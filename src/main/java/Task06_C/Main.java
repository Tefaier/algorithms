package Task06_C;

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

record Edge(int from, int to, int price, int time) {
}


class Graph {
  private int vertexCount;
  public List<List<Edge>> connectionList = new ArrayList<>();

  public Graph(int vertexCount) {
    this.vertexCount = vertexCount;
    for (int i = 0; i < vertexCount; i++) {
      connectionList.add(new ArrayList<>());
    }
  }

  public void addEdge(Edge edge) {
    connectionList.get(edge.from()).add(edge);
    connectionList.get(edge.to()).add(new Edge(edge.to(), edge.from(), edge.price(), edge.time()));
  }

  public int getVertexCount() {
    return vertexCount;
  }

  public List<Edge> getConnected(Integer vertex) {
    return connectionList.get(vertex);
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static int inf = Integer.MAX_VALUE;
  private static int cost;

  static class UnitTime implements Comparable {
    public int source;
    public int vertex;
    public int time;
    public int price;

    public UnitTime(int source, int vertex, int time, int price) {
      this.source = source;
      this.vertex = vertex;
      this.time = time;
      this.price = price;
    }

    @Override
    public int compareTo(Object o) {
      if (o instanceof UnitTime) {
        int diff = time - ((UnitTime) o).time;
        return diff == 0 ? price - ((UnitTime) o).price : diff;
      }
      return 0;
    }
  }

  static class UnitPrice extends UnitTime {

    public UnitPrice(int source, int vertex, int time, int price) {
      super(source, vertex, time, price);
    }

    @Override
    public int compareTo(Object o) {
      if (o instanceof UnitTime) {
        return price - ((UnitTime) o).price;
      }
      return 0;
    }
  }

  private static int[] tryReachMinCostTimeLimit(int start, int target, Graph graph, int timeLimit) {
    cost = inf;
    int[] price = new int[graph.getVertexCount()];
    Arrays.fill(price, inf);
    // from the target
    int[] minTime = new int[graph.getVertexCount()];
    Arrays.fill(minTime, inf);
    int[] parents = new int[graph.getVertexCount()];
    Arrays.fill(parents, -1);

    PriorityQueue<UnitTime> queueTime = new PriorityQueue<>();
    queueTime.add(new UnitTime(-1, target, 0, 0));
    // rules
    // target is to minimise cost
    // but there is also limit on time

    UnitTime unit;
    while ((unit = queueTime.poll()) != null) {
      if (minTime[unit.vertex] != inf) continue;
      minTime[unit.vertex] = unit.time;
      for (Edge edge : graph.getConnected(unit.vertex)) {
        queueTime.add(new UnitTime(0, edge.to(), unit.time + edge.time(), 0));
      }
    }

    PriorityQueue<UnitPrice> queuePrice = new PriorityQueue<>();
    queuePrice.add(new UnitPrice(-1, start, 0, 0));
    while ((unit = queuePrice.poll()) != null) {
      if (price[unit.vertex] != inf) continue;

      price[unit.vertex] = unit.price;
      parents[unit.vertex] = unit.source;

      for (Edge edge : graph.getConnected(unit.vertex)) {
        if (unit.time + edge.time() + minTime[edge.to()] <= timeLimit) {
          queuePrice.add(new UnitPrice(unit.vertex, edge.to(), unit.time + edge.time(), unit.price + edge.price()));
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
      return;
    }

    var parents = tryReachMinCostTimeLimit(start, target, graph, timeLimit);

    if (parents[target] == -1 || cost == inf) {
      System.out.println(-1);
      return;
    }

    System.out.println(cost);
    List<Integer> chain = new ArrayList<>();
    int cursor = target;
    while (true) {
      chain.add(cursor);
      cursor = parents[cursor];
      if (cursor == -1) {
        break;
      }
    }
    System.out.println(chain.size());
    for (int i = chain.size() - 1; i >= 0; --i) {
      System.out.print(chain.get(i) + 1 + " ");
    }
  }
}
