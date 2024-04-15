package Task06.Task06_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private ArrayList<Edge> edges;

  public Graph(int vertexCount) {
    this.vertexCount = vertexCount;
    edges = new ArrayList<>();
  }

  public int getVertexCount() {
    return vertexCount;
  }

  public void addEdge(Edge edge) {
    edges.add(edge);
  }

  public List<Edge> getEdges() {
    return edges;
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static int infinity = 30000;

  private static int[] fordBelman(int start, Graph graph) {
    int[] distances = new int[graph.getVertexCount()];
    Arrays.fill(distances, infinity);
    distances[start] = 0;

    for (int k = 0; k < graph.getVertexCount(); k++) {
      boolean relaxed = false;
      for (Edge edge : graph.getEdges()) {
        if (distances[edge.from()] != infinity) {
          if (distances[edge.to()] > distances[edge.from()] + edge.weight()) {
            distances[edge.to()] = distances[edge.from()] + edge.weight();
            relaxed = true;
          }
        }
      }
      if (!relaxed) break;
    }

    return distances;
  }

  public static void main(String[] args) {
    int roomNum = in.nextInt();
    int edgeNum = in.nextInt();
    Graph graph = new Graph(roomNum);
    for (int j = 0; j < edgeNum; j++) {
      graph.addEdge(new Edge(in.nextInt() - 1, in.nextInt() - 1, in.nextInt()));
    }
    System.out.println(Arrays.stream(fordBelman(0, graph)).mapToObj(Integer::toString).collect(Collectors.joining(" ")));
  }
}
