package Task06_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
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

record Edge(int from, int to, int price, int time) {
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
  private static int inf = Integer.MAX_VALUE;
  private static int cost;

  static class Unit implements Comparable {
    public int source;
    public int vertex;
    public int time;
    public int price;

    public Unit(int source, int vertex, int time, int price) {
      this.source = source;
      this.vertex = vertex;
      this.time = time;
      this.price = price;
    }

    @Override
    public int compareTo(Object o) {
      if (o instanceof Unit) {
        int difference = time - ((Unit) o).time;
        return difference;
      }
      return 0;
    }
  }

  private static int[] tryReachMinCostTimeLimit(int start, int target, Graph graph, int timeLimit) {
    if (start == target) return new int[]{-1};

    int[] price = new int[graph.getVertexCount()];
    Arrays.fill(price, inf);
    int[] parents = new int[graph.getVertexCount()];
    Arrays.fill(parents, -1);

    PriorityQueue<Unit> queue = new PriorityQueue<>();
    queue.add(new Unit(-1, start, 0, 0));
    // rules
    // target is to minimise cost
    // but there is also limit on time

    Unit unit;
    while ((unit = queue.poll()) != null) {
      // because inf is MAX_VALUE
      if (unit.price >= price[unit.vertex]) continue;
      // queue is sorted using time, so it means that we came to the vertex with higher price lower time

      price[unit.vertex] = unit.price;
      parents[unit.vertex] = unit.source;

      for (Edge edge : graph.getEdges(unit.vertex)) {
        if (unit.time + edge.time() <= timeLimit) {
          int to = (edge.to() == unit.vertex) ? edge.from() : edge.to();
          queue.add(new Unit(unit.vertex, to, unit.time + edge.time(), unit.price + edge.price()));
        }
      }
    }

    cost = price[target];
    return parents;
  }

  public static void main(String[] args) {
    int roomNum = in.nextInt();
    int edgeNum = in.nextInt();
    int timeLimit = in.nextInt();
    Graph graph = new Graph(roomNum);
    for (int j = 0; j < edgeNum; j++) {
      graph.addEdge(new Edge(in.nextInt() - 1, in.nextInt() - 1, in.nextInt(), in.nextInt()));
    }
    int start = 0;
    int target = roomNum - 1;
    if (start == target) {
      System.out.println("0\n1\n1");
    } else {
      var parents = tryReachMinCostTimeLimit(start, target, graph, timeLimit);
      if (parents[target] == -1) {
        System.out.println(-1);
      } else {
        System.out.println(cost);
        Stack<Integer> chain = new Stack<>();
        int cursor = target;
        while (true) {
          chain.add(cursor);
          cursor = parents[cursor];
          if (cursor == -1) {
            break;
          }
        }
        System.out.println(chain.size());
        while (!chain.isEmpty()) {
          System.out.print(chain.pop() + 1 + " ");
        }
      }
    }
  }
}
