package Task08_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int citiesNum = in.nextInt();
    int edgeNum = in.nextInt();

    ArrayList<WeightedEdge<Integer>> edges = new ArrayList<>();
    for (int i = 0; i < edgeNum; i++) {
      edges.add(new WeightedEdge<>(in.nextInt(), in.nextInt(), in.nextInt()));
    }
    Net<Integer> net = new Net<>(IntStream.range(1, citiesNum + 1).boxed().toList(), edges, false);
    EdmondKarp(net, 1, citiesNum);

    FilteredGraph<Integer, Net<Integer>.NetEdge> leftNet = new FilteredGraph<>(net, (edge) -> edge.getAwailableFlow() > 0);
    boolean[] reached = new boolean[citiesNum + 1];
    reached[1] = true;
    for (var key : GraphHandler.bfs(leftNet, 1, citiesNum).keySet()) {
      reached[key] = true;
    }

    ArrayList<Integer> chosenEdges = new ArrayList<>();
    int weightCounter = 0;
    for (int i = 0; i < edges.size(); i++) {
      var edge = edges.get(i);
      if (reached[edge.getFrom()] ^ reached[edge.getTo()]) {
        chosenEdges.add(i + 1);
        weightCounter += edge.getWeight();
      }
    }

    System.out.println(chosenEdges.size() + " " + weightCounter);
    System.out.println(chosenEdges.stream().map(Objects::toString).collect(Collectors.joining(" ")));
  }

  private static <V> int EdmondKarp(Net<V> net, V from, V to) {
    int maxFlow = 0;
    FilteredGraph<V, Net<V>.NetEdge> leftNet = new FilteredGraph<>(net, (edge) -> edge.getAwailableFlow() > 0);
    while (true) {
      var result = constructPath(GraphHandler.bfs(leftNet, from, to), to);
      if (result == null) return maxFlow;
      maxFlow += net.pushFlowByPath(result);
    }
  }

  private static <V> List<Net<V>.NetEdge> constructPath(HashMap<V, Net<V>.NetEdge> sourceMap, V from) {
    if (!sourceMap.containsKey(from)) {
      return null;
    }
    ArrayList<Net<V>.NetEdge> path = new ArrayList<>();
    V current = from;
    while (sourceMap.containsKey(current)) {
      var edge = sourceMap.get(current);
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

class Net<V> implements Graph<V, Net<V>.NetEdge> {
  public class NetEdge implements Edge<V> {
    private V from;
    private V to;
    private int capacity;
    private int flow;
    private int index;

    public NetEdge(V from, V to, int capacity, int flow, int index) {
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
    public V getFrom() {
      return from;
    }

    @Override
    public V getTo() {
      return to;
    }

    @Override
    public <E extends Edge<V>> E reversed() {
      return null;
    }
  }

  protected int edgesNum = 0;
  protected boolean orientated;
  protected List<V> vertices;
  protected List<? extends WeightedEdge<V>> initialEdges;
  protected List<NetEdge> edges = new ArrayList<>();
  protected HashMap<V, List<NetEdge>> edgesMap = new HashMap<>();

  public Net(List<V> vertices, List<? extends WeightedEdge<V>> edges, boolean orientated) {
    this.vertices = vertices;
    this.initialEdges = edges;
    this.orientated = orientated;
    edges.forEach(this::addEdge);
  }

  public <E extends WeightedEdge<V>> void addEdge(E edge) {
    // forward
    edges.add(new NetEdge(edge.getFrom(), edge.getTo(), edge.getWeight(), 0, edgesNum));
    edgesMap.putIfAbsent(edge.getFrom(), new ArrayList<>());
    edgesMap.get(edge.getFrom()).add(edges.get(edges.size() - 1));
    // backward
    edges.add(new NetEdge(edge.getTo(), edge.getFrom(), orientated ? 0 : edge.getWeight(), 0, edgesNum));
    edgesMap.putIfAbsent(edge.getTo(), new ArrayList<>());
    edgesMap.get(edge.getTo()).add(edges.get(edges.size() - 1));
    ++edgesNum;
  }

  public List<NetEdge> getConnected(V vertex) {
    return edgesMap.getOrDefault(vertex, new ArrayList<>());
  }

  public int getVertexCount() {
    return vertices.size();
  }

  public int getEdgeCount() {
    return edgesNum * 2;
  }

  public List<V> getVertices() {
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

class FilteredGraph<V, E extends Edge<V>> implements Graph<V, E> {
  private Graph<V, E> graph;
  private Predicate<E> limit;

  public FilteredGraph(Graph<V, E> graph, Predicate<E> limit) {
    this.graph = graph;
    this.limit = limit;
  }

  public List<E> getConnected(V vertex) {
    return graph.getConnected(vertex).stream().filter(limit).toList();
  }

  @Override
  public int getVertexCount() {
    return graph.getVertexCount();
  }

  @Override
  public int getEdgeCount() {
    return graph.getEdgeCount();
  }

  @Override
  public List<V> getVertices() {
    return graph.getVertices();
  }
}

class GraphHandler {
  public static <V, E extends Edge<V>> HashMap<V, E> bfs(Graph<V, E> graph, V from, V to) {
    Queue<V> queue = new ArrayDeque<>();
    queue.add(from);
    Set<V> visited = new HashSet<>();
    HashMap<V, E> parent = new HashMap<>();
    visited.add(from);

    V vertex;
    while ((vertex = queue.poll()) != null) {
      for (var edge : graph.getConnected(vertex)) {
        if (!visited.contains(edge.getTo())) {
          parent.put(edge.getTo(), edge);
          visited.add(edge.getTo());
          queue.add(edge.getTo());
        }
        if (edge.getTo() == to) {
          return parent;
        }
      }
    }
    return parent;
  }
}
