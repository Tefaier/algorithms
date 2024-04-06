package Task08_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  private static void test() {
    int iteration = 0;
    Random random = new Random();
    float maxSeconds = 0;
    while (true) {
      iteration++;
      Timestamp timeStart = new Timestamp(System.currentTimeMillis());
      int stationsNum = random.nextInt(2, 20);
      int edgeNum = random.nextInt(1, 100);
      GraphHandler.setup(stationsNum + 1);

      ArrayList<WeightedEdge<Integer>> edges = new ArrayList<>();
      for (int i = 0; i < edgeNum; i++) {
        int from = random.nextInt(1, stationsNum + 1);
        int to = random.nextInt(1, stationsNum + 1);
        if (from == to) continue;
        edges.add(new WeightedEdge<>(from, to, random.nextInt(1, 100)));
      }
      Net net = new Net(IntStream.range(1, stationsNum + 1).boxed().toList(), edges, true);
      long max = dinic(net, 1, stationsNum);

      Timestamp timeFinish = new Timestamp(System.currentTimeMillis());
      float seconds = Math.round((timeFinish.getTime() - timeStart.getTime())) / 1000f;
      maxSeconds = Math.max(maxSeconds, seconds);
      if (seconds < 0.01) {
        System.out.print("+");
        continue;
      }
      System.out.println();
      System.out.println("Iteration: " + iteration + " duration: " + seconds + " flow: " + max + " input: " + stationsNum + " " + edgeNum); //+ " <" + edges.stream().map(edge -> edge.getFrom() + " " + edge.getTo() + " " + edge.getWeight()).collect(Collectors.joining(" | ")) + ">");
    }
  }

  public static void main(String[] args) {
    test();
    int stationsNum = in.nextInt();
    int edgeNum = in.nextInt();
    GraphHandler.setup(stationsNum + 1);

    ArrayList<WeightedEdge<Integer>> edges = new ArrayList<>();
    for (int i = 0; i < edgeNum; i++) {
      edges.add(new WeightedEdge<>(in.nextInt(), in.nextInt(), in.nextInt()));
    }
    Net net = new Net(IntStream.range(1, stationsNum + 1).boxed().toList(), edges, true);
    long max = dinic(net, 1, stationsNum);

    System.out.println(max);
    for (int i = 0; i < net.edges.size(); i += 2) {
      System.out.println(net.edges.get(i).getFlow());
    }
  }

  private static long dinic(Net net, int from, int to) {
    long maxFlow = 0;
    while (true) {
      var result = dinicIteration(net, from, to);
      if (result == 0) return maxFlow;
      maxFlow += result;
    }
  }

  private static long dinicIteration(Net net, int from, int to) {
    long answer = 0;

    // didn't reach target
    if (!GraphHandler.bfs(net, from, to, (edge) -> edge.getAwailableFlow() > 0)) {
      return answer;
    }

    while (true) {
      GraphHandler.dfsFlowEdges.clear();
      int delta = GraphHandler.dfsFlow(
          net,
          from,
          to,
          (edge) ->
              (
                  GraphHandler.heights[edge.getTo()] == GraphHandler.heights[edge.getFrom()] + 1
                      && edge.getAwailableFlow() > 0
              ),
          Integer.MAX_VALUE);
      if (delta == 0) break;

      net.pushFlowByPath(GraphHandler.dfsFlowEdges);
      answer += delta;
    }

    return answer;
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
  protected List<WeightedEdge<Integer>> initialEdges;
  protected List<NetEdge> edges = new ArrayList<>();
  protected ArrayList<List<NetEdge>> connectionList = new ArrayList<>();

  public Net(List<Integer> vertices, List<WeightedEdge<Integer>> edges, boolean orientated) {
    this.vertices = vertices;
    this.initialEdges = edges;
    this.orientated = orientated;
    for (int i = 0; i <= vertices.size(); i++) {
      connectionList.add(new ArrayList<>());
    }
    edges.forEach(this::addEdge);
  }

  public void addEdge(WeightedEdge<Integer> edge) {
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
  public static int[] heights;
  public static ArrayList<Net.NetEdge> dfsFlowEdges = new ArrayList<>();

  public static void setup(int values) {
    iteration = 0;
    iterationVisit = new int[values];
    heights = new int[values];
  }

  public static int dfsFlow(Net net, int from, int to, Predicate<Net.NetEdge> limit, int min) {
    if (min == 0) return 0;
    if (from == to) return min;

    for (var edge : net.getConnected(from)) {
      if (!limit.test(edge)) continue;

      int delta = dfsFlow(net, edge.getTo(), to, limit, Math.min(min, edge.getAwailableFlow()));

      if (delta > 0) {
        dfsFlowEdges.add(edge);
        return delta;
      }
    }
    return 0;
  }

  public static boolean bfs(Graph<Integer, Net.NetEdge> graph, int from, int to, Predicate<Net.NetEdge> limit) {
    Arrays.fill(heights, 0);
    iteration++;
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(from);
    iterationVisit[from] = iteration;
    boolean reachedEnd = false;

    Integer vertex;
    while ((vertex = queue.poll()) != null) {
      for (var edge : graph.getConnected(vertex)) {
        if (!limit.test(edge)) continue;
        // check first visit in this iteration
        if (iterationVisit[edge.getTo()] < iteration) {
          heights[edge.getTo()] = heights[edge.getFrom()] + 1;
          iterationVisit[edge.getTo()] = iteration;
          queue.add(edge.getTo());
          if (edge.getTo() == to) {
            reachedEnd = true;
          }
        }
      }
    }
    return reachedEnd;
  }
}
