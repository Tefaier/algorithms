package Task06_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

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

  static class Unit implements Comparable {
    public int vertex;
    public long distance;
    public boolean isAVirus;

    public Unit(int vertex, long distance, boolean isAVirus) {
      this.vertex = vertex;
      this.distance = distance;
      this.isAVirus = isAVirus;
    }

    @Override
    public int compareTo(Object o) {
      if (o instanceof Unit) {
        long difference = distance - ((Unit) o).distance;
        return (difference == 0) ? (isAVirus ? -1 : 1) : (int) difference;
      }
      return 0;
    }
  }

  private static long tryReachWithDeixtra(int start, int target, Graph graph, List<Integer> virusSources) {
    if (start == target) return 0;
    if (virusSources.contains(start)) return -1;
    long[] distances = new long[graph.getVertexCount()];
    PriorityQueue<Unit> queue = new PriorityQueue<>();
    Arrays.fill(distances, Long.MAX_VALUE);
    distances[start] = 0;
    queue.add(new Unit(start, 0, false));
    virusSources.forEach(source -> {
      distances[source] = 0;
      queue.add(new Unit(source, 0, true));
    });
    Set<Integer> checked = new HashSet<>();
    int aliveCounter = 1;
    // rules
    // what is known - who came where first
    // if only viruses in the queue - break
    // if target reached - break

    Unit unit;
    while ((unit = queue.poll()) != null) {
      if (!unit.isAVirus) aliveCounter--;
      if (checked.contains(unit.vertex)) continue;
      checked.add(unit.vertex);
      for (Edge edge : graph.getEdges(unit.vertex)) {
        int to = edge.to() == unit.vertex ? edge.from() : edge.to();
        long newDist = distances[unit.vertex] + edge.weight();
        if (newDist < distances[to]) {
          distances[to] = newDist;
          if (to == target) return unit.isAVirus ? -1 : newDist;
          queue.add(new Unit(to, newDist, unit.isAVirus));
          if (!unit.isAVirus) aliveCounter++;
        }
      }
      if (aliveCounter == 0) return -1;
    }
    return -1;
  }

  public static void main(String[] args) {
    int roomNum = in.nextInt();
    int edgeNum = in.nextInt();
    int virusesNum = in.nextInt();
    List<Integer> viruses = new ArrayList<>();
    for (int i = 0; i < virusesNum; i++) {
      viruses.add(in.nextInt() - 1);
    }
    Graph graph = new Graph(roomNum);
    for (int j = 0; j < edgeNum; j++) {
      graph.addEdge(new Edge(in.nextInt() - 1, in.nextInt() - 1, in.nextInt()));
    }
    int start = in.nextInt() - 1;
    int target = in.nextInt() - 1;
    System.out.println(tryReachWithDeixtra(start, target, graph, viruses));
  }
}
