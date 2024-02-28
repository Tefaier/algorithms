package Task06_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

class Parser {

  private final int BUFFER_SIZE = 1 << 16;
  private DataInputStream din;
  private byte[] buffer;
  private int bufferPointer, bytesRead;

  public Parser(InputStream in) {
    din = new DataInputStream(in);
    buffer = new byte[BUFFER_SIZE];
    bufferPointer = bytesRead = 0;
  }

  public String nextString(int maxSize) {
    byte[] ch = new byte[maxSize];
    int point = 0;
    try {
      byte c = read();
      while (c == ' ' || c == '\n' || c == '\r') {
        c = read();
      }
      while (c != ' ' && c != '\n' && c != '\r') {
        ch[point++] = c;
        c = read();
      }
    } catch (Exception e) {
    }
    return new String(ch, 0, point);
  }

  public int nextInt() {
    int ret = 0;
    boolean neg;
    try {
      byte c = read();
      while (c <= ' ') {
        c = read();
      }
      neg = c == '-';
      if (neg) {
        c = read();
      }
      do {
        ret = ret * 10 + c - '0';
        c = read();
      } while (c > ' ');

      if (neg) {
        return -ret;
      }
    } catch (Exception e) {
    }
    return ret;
  }

  public long nextLong() {
    long ret = 0;
    boolean neg;
    try {
      byte c = read();
      while (c <= ' ') {
        c = read();
      }
      neg = c == '-';
      if (neg) {
        c = read();
      }
      do {
        ret = ret * 10 + c - '0';
        c = read();
      } while (c > ' ');

      if (neg) {
        return -ret;
      }
    } catch (Exception e) {
    }
    return ret;
  }

  private void fillBuffer() {
    try {
      bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
    } catch (Exception e) {
    }
    if (bytesRead == -1) buffer[0] = -1;
  }

  private byte read() {
    if (bufferPointer == bytesRead) {
      fillBuffer();
    }
    return buffer[bufferPointer++];
  }
}

record Edge(int from, int to, int weight) {
}

class Graph {
  private int vertexCount;
  private ArrayList<Edge>[] connectionList;

  public Graph(int vertexCount) {
    this.vertexCount = vertexCount;
    connectionList = new ArrayList[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      connectionList[i] = new ArrayList<>();
    }
  }

  public int getVertexCount() {
    return vertexCount;
  }

  public void addEdge(Edge edge) {
    connectionList[edge.from()].add(edge);
    connectionList[edge.to()].add(edge);
  }

  public List<Edge> getEdges(int from) {
    return connectionList[from];
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static int infinity = 2009000999;

  private static int[] deixtra(int start, Graph graph) {
    int[] distances = new int[graph.getVertexCount()];
    Arrays.fill(distances, infinity);
    distances[start] = 0;

    class Unit implements Comparable {
      public int vertex;

      public Unit(int vertex) {
        this.vertex = vertex;
      }

      @Override
      public int compareTo(Object o) {
        if (o instanceof Unit) {
          return distances[vertex] - distances[((Unit) o).vertex];
        }
        return 0;
      }
    }

    PriorityQueue<Unit> queue = new PriorityQueue<>();
    queue.add(new Unit(start));

    Unit unit;
    while ((unit = queue.poll()) != null) {
      for (Edge edge : graph.getEdges(unit.vertex)) {
        int to = edge.to();
        if (to == unit.vertex) {
          to = edge.from();
        }
        int newDist = distances[unit.vertex] + edge.weight();
        if (newDist < distances[to]) {
          distances[to] = newDist;
          queue.add(new Unit(to));
        }
      }
    }
    return distances;
  }

  public static void main(String[] args) {
    int tries = in.nextInt();
    for (int i = 0; i < tries; i++) {
      int roomNum = in.nextInt();
      int edgeNum = in.nextInt();
      Graph graph = new Graph(roomNum);
      for (int j = 0; j < edgeNum; j++) {
        graph.addEdge(new Edge(in.nextInt(), in.nextInt(), in.nextInt()));
      }
      System.out.println(Arrays.stream(deixtra(in.nextInt(), graph)).mapToObj(Integer::toString).collect(Collectors.joining(" ")));
    }
  }
}
