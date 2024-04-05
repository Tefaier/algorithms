package Task08_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int xDimension = in.nextInt();
    int yDimension = in.nextInt();
    int mapSize = xDimension * yDimension;
    int blockedNum = in.nextInt();
    int blockableNum = in.nextInt();
    boolean[][] blockedMap = new boolean[xDimension][yDimension];
    boolean[][] blockableMap = new boolean[xDimension][yDimension];

    for (int i = 0; i < blockedNum; i++) {
      blockedMap[in.nextInt() - 1][in.nextInt() - 1] = true;
    }

    for (int i = 0; i < blockableNum; i++) {
      blockableMap[in.nextInt() - 1][in.nextInt() - 1] = true;
    }

    int sourceX = in.nextInt() - 1;
    int sourceY = in.nextInt() - 1;
    int targetX = in.nextInt() - 1;
    int targetY = in.nextInt() - 1;
    int sourceIndex = (sourceX * yDimension + sourceY) * 2 + 1;
    int targetIndex = (targetX * yDimension + targetY) * 2;

    ArrayList<BoardEdge<Integer>> edges = new ArrayList<>();
    for (int x = 0; x < xDimension; x++) {
      for (int y = 0; y < yDimension; y++) {
        if (blockedMap[x][y]) continue;
        int topPos = (x * yDimension + y) * 2;
        if (x != sourceX || y != sourceY) {
          edges.add(new BoardEdge<>(topPos, topPos + 1, blockableMap[x][y] ? 1 : 10000, true, x + 1, y + 1));
        }

        if (x < xDimension - 1 && !blockedMap[x + 1][y]) {
          edges.add(new BoardEdge<>(topPos + 1, ((x + 1) * yDimension + y) * 2, 10000, false, x + 1, y + 1));
        }
        if (y < yDimension - 1 && !blockedMap[x][y + 1]) {
          edges.add(new BoardEdge<>(topPos + 1, (x * yDimension + y + 1) * 2, 10000, false, x + 1, y + 1));
        }
        if (x > 0 && !blockedMap[x - 1][y]) {
          edges.add(new BoardEdge<>(topPos + 1, ((x - 1) * yDimension + y) * 2, 10000, false, x + 1, y + 1));
        }
        if (y > 0 && !blockedMap[x][y - 1]) {
          edges.add(new BoardEdge<>(topPos + 1, (x * yDimension + y - 1) * 2, 10000, false, x + 1, y + 1));
        }
      }
    }

    Net net = new Net(IntStream.range(0, mapSize * 2).boxed().toList(), edges, true);
    GraphHandler.setup(mapSize * 2);
    long flow = EdmondKarp(net, sourceIndex, targetIndex);
    if (flow >= 10000) {
      System.out.println(-1);
      return;
    }

    boolean[] reached = new boolean[mapSize * 2];
    GraphHandler.setup(mapSize * 2);
    GraphHandler.bfs(net, sourceIndex, targetIndex, (edge) -> edge.getAwailableFlow() > 0);
    var visit = GraphHandler.usedEdges;
    for (int i = 0; i < mapSize * 2; i++) {
      reached[i] = visit.get(i) != null;
    }
    reached[sourceIndex] = true;

    ArrayList<BoardEdge<Integer>> chosenEdges = new ArrayList<>();
    for (int i = 0; i < edges.size(); i++) {
      var edge = edges.get(i);
      if ((reached[edge.getFrom()] ^ reached[edge.getTo()]) && edge.statePass) {
        chosenEdges.add(edge);
      }
    }

    System.out.println(chosenEdges.size());
    System.out.println(chosenEdges.stream().map(edge -> edge.x + " " + edge.y).collect(Collectors.joining("\n")));
  }

  private static long EdmondKarp(Net net, int from, int to) {
    long maxFlow = 0;
    while (true) {
      var result = constructPath(GraphHandler.bfs(net, from, to, (edge) -> edge.getAwailableFlow() > 0), to);
      if (result == null) return maxFlow;
      maxFlow += net.pushFlowByPath(result);
      if (maxFlow >= 10000) {
        return maxFlow;
      }
    }
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

class BoardEdge<V> extends WeightedEdge<V> {
  public boolean statePass;
  public int x;
  public int y;

  public BoardEdge(V from, V to, int weight, boolean statePass, int x, int y) {
    super(from, to, weight);
    this.statePass = statePass;
    this.x = x;
    this.y = y;
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
  protected List<BoardEdge<Integer>> initialEdges;
  protected List<NetEdge> edges = new ArrayList<>();
  protected ArrayList<List<NetEdge>> connectionList = new ArrayList<>();

  public Net(List<Integer> vertices, List<BoardEdge<Integer>> edges, boolean orientated) {
    this.vertices = vertices;
    this.initialEdges = edges;
    this.orientated = orientated;
    for (int i = 0; i <= vertices.size(); i++) {
      connectionList.add(new ArrayList<>());
    }
    edges.forEach(this::addEdge);
  }

  public void addEdge(BoardEdge<Integer> edge) {
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
  public static ArrayList<Net.NetEdge> usedEdges;

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
