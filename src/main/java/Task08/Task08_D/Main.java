package Task08.Task08_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int stationsNum = in.nextInt();
    int edgeNum = in.nextInt();
    GraphHandler.setup(stationsNum + 1);

    ArrayList<WeightedEdge<Integer>> edges = new ArrayList<>();
    for (int i = 0; i < edgeNum; i++) {
      edges.add(new WeightedEdge<>(in.nextInt(), in.nextInt(), in.nextInt()));
    }
    int maxCapacity = edges.stream().map(WeightedEdge::getWeight).max(Integer::compareTo).get();
    Net net = new Net(IntStream.range(1, stationsNum + 1).boxed().toList(), edges, true);
    long max = EdmondKarp(net, 1, stationsNum, maxCapacity);

    System.out.println(max);
    for (int i = 0; i < net.edges.size(); i += 2) {
      System.out.println(net.edges.get(i).getFlow());
    }
  }

  private static long EdmondKarp(Net net, int from, int to, int capacityLimit) {
    long maxFlow = 0;
    int scale = (int) Math.pow(2, Math.floor(Math.log(capacityLimit) / Math.log(2)));
    List<Net.NetEdge> path;
    while (scale >= 1) {
      int finalScale = scale;
      while ((path = constructPath(GraphHandler.bfs(net, from, to, (edge) -> edge.getAwailableFlow() >= finalScale), to)) != null) {
        maxFlow += net.pushFlowByPath(path);
      }
      scale /= 2;
    }
    return maxFlow;
  }

  private static List<Net.NetEdge> constructPath(List<Net.NetEdge> sourceList, int from) {
    if (sourceList == null) {
      return null;
    }
    ArrayList<Net.NetEdge> path = new ArrayList<>();
    Integer current = from;
    while (sourceList.get(current) != null) {
      var edge = sourceList.get(current);
      path.add(edge);
      current = edge.getFrom();
    }
    return path;
  }
}

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

interface Edge<V> {
  V getFrom();

  V getTo();

  <E extends Edge<V>> E reversed();
}

class WeightedEdge<V> implements Edge<V> {
  private V from;
  private V to;
  private int weight;

  public WeightedEdge(V from, V to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  @Override
  public V getFrom() {
    return from;
  }

  @Override
  public V getTo() {
    return to;
  }

  public int getWeight() {
    return weight;
  }

  @Override
  public WeightedEdge<V> reversed() {
    return new WeightedEdge<>(getTo(), getFrom(), weight);
  }
}

interface Graph<V, E extends Edge<V>> {
  public List<E> getConnected(V vertex);

  public int getVertexCount();

  public int getEdgeCount();

  public List<V> getVertices();
}

class Net implements Graph<Integer, Net.NetEdge> {
  public class NetEdge implements Edge<Integer> {
    private Integer from;
    private Integer to;
    private int capacity;
    private int flow;
    private int index;

    public NetEdge(Integer from, Integer to, int capacity, int flow, int index) {
      this.from = from;
      this.to = to;
      this.capacity = capacity;
      this.flow = flow;
      this.index = index;
    }

    public int getAwailableFlow() {
      return capacity - flow;
    }

    @Override
    public Integer getFrom() {
      return from;
    }

    @Override
    public Integer getTo() {
      return to;
    }

    public int getFlow() {
      return flow;
    }

    @Override
    public <E extends Edge<Integer>> E reversed() {
      return null;
    }
  }

  protected int edgesNum = 0;
  protected boolean orientated;
  protected List<Integer> vertices;
  protected List<? extends WeightedEdge<Integer>> initialEdges;
  protected List<NetEdge> edges = new ArrayList<>();
  protected ArrayList<List<NetEdge>> connectionList = new ArrayList<>();

  public Net(List<Integer> vertices, List<? extends WeightedEdge<Integer>> edges, boolean orientated) {
    this.vertices = vertices;
    this.initialEdges = edges;
    this.orientated = orientated;
    for (int i = 0; i <= vertices.size(); i++) {
      connectionList.add(new ArrayList<>());
    }
    edges.forEach(this::addEdge);
  }

  public <E extends WeightedEdge<Integer>> void addEdge(E edge) {
    // forward
    edges.add(new NetEdge(edge.getFrom(), edge.getTo(), edge.getWeight(), 0, edgesNum));
    connectionList.get(edge.getFrom()).add(edges.get(edges.size() - 1));
    // backward
    edges.add(new NetEdge(edge.getTo(), edge.getFrom(), orientated ? 0 : edge.getWeight(), 0, edgesNum));
    connectionList.get(edge.getTo()).add(edges.get(edges.size() - 1));
    ++edgesNum;
  }

  public List<NetEdge> getConnected(Integer vertex) {
    return connectionList.get(vertex);
  }

  public int getVertexCount() {
    return vertices.size();
  }

  public int getEdgeCount() {
    return edgesNum * 2;
  }

  public List<Integer> getVertices() {
    return vertices;
  }

  public int pushFlowByPath(List<NetEdge> path) {
    int minFlow = Integer.MAX_VALUE;
    for (int i = 0; i < path.size(); i++) {
      minFlow = Math.min(minFlow, path.get(i).getAwailableFlow());
    }
    // apply flow
    for (int i = 0; i < path.size(); i++) {
      path.get(i).flow += minFlow;
      getReversedEdge(path.get(i)).flow -= minFlow;
    }
    return minFlow;
  }

  private NetEdge getReversedEdge(NetEdge edge) {
    return edges.get(edges.get(edge.index * 2) == edge ? edge.index * 2 + 1 : edge.index * 2);
  }
}

class GraphHandler {
  private static int iteration = 0;
  private static int[] iterationVisit;
  private static ArrayList<Net.NetEdge> usedEdges;

  public static void setup(int values) {
    iterationVisit = new int[values];
    usedEdges = new ArrayList<>();
    for (int i = 0; i < values; i++) {
      usedEdges.add(null);
    }
  }

  public static ArrayList<Net.NetEdge> bfs(Graph<Integer, Net.NetEdge> graph, int from, int to, Predicate<Net.NetEdge> limit) {
    iteration++;
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(from);
    iterationVisit[from] = iteration;

    Integer vertex;
    while ((vertex = queue.poll()) != null) {
      for (var edge : graph.getConnected(vertex)) {
        if (!limit.test(edge)) continue;
        if (iterationVisit[edge.getTo()] < iteration) {
          usedEdges.set(edge.getTo(), edge);
          usedEdges.set(edge.getTo(), edge);
          iterationVisit[edge.getTo()] = iteration;
          queue.add(edge.getTo());
        }
        if (edge.getTo() == to) {
          return usedEdges;
        }
      }
    }
    return null;
  }
}
