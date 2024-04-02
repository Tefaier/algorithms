package Task08_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {

  }

  private static int FordFarkenson() {

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
  protected List<NetEdge> edges;
  protected HashMap<V, List<NetEdge>> edgesMap = new HashMap<>();

  public Net(List<V> vertices, List<? extends WeightedEdge<V>> edges, boolean orientated) {
    this.vertices = vertices;
    this.initialEdges = edges;
    this.orientated = orientated;
    edges.forEach(this::addEdge);
  }

  public <E extends WeightedEdge<V>> void addEdge(E edge) {
    // forward
    edgesMap.putIfAbsent(edge.getFrom(), new ArrayList<>());
    edgesMap.get(edge.getFrom()).add(new NetEdge(edge.getFrom(), edge.getTo(), edge.getWeight(), 0, edgesNum));
    // backward
    edgesMap.putIfAbsent(edge.getTo(), new ArrayList<>());
    edgesMap.get(edge.getTo()).add(new NetEdge(edge.getTo(), edge.getFrom(), orientated ? 0 : edge.getWeight(), 0, edgesNum));
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

enum VertexColors {White, Gray, Black;}

interface GraphVisitor<V, E extends Edge<V>> {
  public Stack<V> getVisitStack();

  public V getNextUnexplored(List<V> vertices);

  public VertexColors getVertexStatus(V vertex);

  public void setVertexStatus(V vertex, VertexColors status);

  public void startExploring(V vertex);

  public void finishExploring(V vertex);

  public void exploreWhite(E edge);

  public void exploreGray(E edge);

  public void exploreBlack(E edge);

  public void endVertex(V vertex);

  public boolean isFinished();

}

class PathSearchVisitor<V, E extends Edge<V>> implements GraphVisitor<V, E> {
  public List<E> path = new ArrayList<>();
  private final Stack<V> visitStack = new Stack<>();
  // state of each index
  private final HashMap<V, VertexColors> vertexColors = new HashMap<>();

  private V source;
  private V target;
  private boolean isFinished = false;

  public PathSearchVisitor(V source, V target) {
    this.source = source;
    this.target = target;
  }

  @Override
  public Stack<V> getVisitStack() {
    return visitStack;
  }

  @Override
  public V getNextUnexplored(List<V> vertices) {
    return source;
  }

  @Override
  public VertexColors getVertexStatus(V vertex) {
    return vertexColors.getOrDefault(vertex, VertexColors.White);
  }

  @Override
  public void setVertexStatus(V vertex, VertexColors status) {
    vertexColors.put(vertex, status);
  }

  @Override
  public void startExploring(V vertex) {
    setVertexStatus(vertex, VertexColors.Gray);
    visitStack.add(vertex);
  }

  @Override
  public void finishExploring(V vertex) {
    isFinished = true;
  }

  @Override
  public void exploreWhite(E edge) {
    setVertexStatus(edge.getTo(), VertexColors.Gray);
    visitStack.add(edge.getTo());
    path.add(edge);
    if (edge.getTo() == target) {
      isFinished = true;
    }
  }

  @Override
  public void exploreGray(E edge) {
  }

  @Override
  public void exploreBlack(E edge) {
  }

  @Override
  public void endVertex(V vertex) {
    setVertexStatus(vertex, VertexColors.Black);
    visitStack.pop();
    if (!isFinished) {
      path.remove(path.size() - 1);
    }
  }

  @Override
  public boolean isFinished() {
    return isFinished;
  }

}

class GraphHandler {
  public static <V, E extends Edge<V>, G extends GraphVisitor<V, E>> void dfs(Graph<V, E> graph, G graphVisitor, boolean persist) {
    do {
      V vertex = graphVisitor.getNextUnexplored(graph.getVertices());
      if (vertex == null) {
        return;
      }
      graphVisitor.startExploring(vertex);
      GraphHandler.recursiveDfs(graph, graphVisitor, vertex);
      graphVisitor.finishExploring(vertex);
    } while (persist && !graphVisitor.isFinished());
  }

  private static <V, E extends Edge<V>, G extends GraphVisitor<V, E>> void recursiveDfs(Graph<V, E> graph, G visitor, V vertex) {
    List<E> outgoingEdges = graph.getConnected(vertex);
    for (var edge : outgoingEdges) {
      if (visitor.isFinished()) return;
      VertexColors status = visitor.getVertexStatus(edge.getTo());
      if (status == VertexColors.White) {
        visitor.exploreWhite(edge);
        recursiveDfs(graph, visitor, edge.getTo());
      } else if (status == VertexColors.Gray) {
        visitor.exploreGray(edge);
      } else {
        visitor.exploreBlack(edge);
      }
    }

    visitor.endVertex(vertex);
  }
}
