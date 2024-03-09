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

record Edge(int from, int to, long price, long time) {
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
  private static final Parser in = new Parser(System.in);
  private static final long inf = Long.MAX_VALUE;
  private static long cost;

  static class UnitTime implements Comparable<UnitTime> {
    public int source;
    public int vertex;
    public long time;
    public long price;

    public UnitTime(int source, int vertex, long time, long price) {
      this.source = source;
      this.vertex = vertex;
      this.time = time;
      this.price = price;
    }

    @Override
    public int compareTo(UnitTime o) {
      return (int) (time - o.time);
    }
  }

  static class UnitPrice implements Comparable<UnitPrice> {
    public int source;
    public int vertex;
    public int[] path;
    public long time;
    public long price;

    public UnitPrice(int source, int vertex, int[] path, long time, long price) {
      this.source = source;
      this.vertex = vertex;
      this.path = path;
      this.time = time;
      this.price = price;
    }

    @Override
    public int compareTo(UnitPrice o) {
      long diff = price - o.price;
      return (int) (diff == 0 ? time - o.time : diff);
    }
  }

  private static int[] tryReachMinCostTimeLimit(int start, int target, Graph graph, long timeLimit) {
    cost = inf;
    long[] price = new long[graph.getVertexCount()];
    Arrays.fill(price, inf);
    // from the target
    long[] minTime = new long[graph.getVertexCount()];
    Arrays.fill(minTime, inf);
    long[] timeAchieved = new long[graph.getVertexCount()];
    Arrays.fill(timeAchieved, inf);
    int[] path = new int[0];

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
    queuePrice.add(new UnitPrice(-1, start, new int[]{start}, 0, 0));
    UnitPrice unitPrice;
    while ((unitPrice = queuePrice.poll()) != null) {
      if (
          price[unitPrice.vertex] != inf // уже посещали
              && timeAchieved[unitPrice.vertex] <= unitPrice.time
      ) continue;

      if (unitPrice.vertex == target && price[target] > unitPrice.price) {
        path = unitPrice.path;
        cost = unitPrice.price;
      }

      price[unitPrice.vertex] = unitPrice.price;
      timeAchieved[unitPrice.vertex] = unitPrice.time;

      for (Edge edge : graph.getConnected(unitPrice.vertex)) {
        if (unitPrice.time + edge.time() + minTime[edge.to()] <= timeLimit) {
          int[] newPath = Arrays.copyOf(unitPrice.path, unitPrice.path.length + 1);
          newPath[newPath.length - 1] = edge.to();
          queuePrice.add(new UnitPrice(unitPrice.vertex, edge.to(), newPath, unitPrice.time + edge.time(), unitPrice.price + edge.price()));
        }
      }
    }

    return path;
  }

  public static void main(String[] args) {
    int roomNum = in.nextInt();
    long edgeNum = in.nextInt();
    long timeLimit = in.nextInt();
    Graph graph = new Graph(roomNum);
    for (long j = 0; j < edgeNum; j++) {
      graph.addEdge(new Edge(in.nextInt() - 1, in.nextInt() - 1, in.nextInt(), in.nextInt()));
    }
    int start = 0;
    int target = roomNum - 1;

    if (start == target) {
      System.out.println("0\n1\n1");
      return;
    }

    var path = tryReachMinCostTimeLimit(start, target, graph, timeLimit);

    if (path.length == 0 || cost == inf) {
      System.out.print(-1);
      return;
    }

    System.out.println(cost);
    System.out.println(path.length);
    StringBuilder pathString = new StringBuilder();
    for (int i = 0; i < path.length; ++i) {
      pathString.append(path[i] + 1).append(" ");
    }
    System.out.print(pathString);
  }
}
