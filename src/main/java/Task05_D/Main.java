package Task05_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertices = in.nextInt();
    int edges = in.nextInt();
    List<Short> verticesList = IntStream.rangeClosed(1, vertices).boxed().map(Integer::shortValue).toList();
    List<SimpleEdge<Short>> edgesList = new ArrayList<>();
    List<SimpleEdge<Short>> edgesListReversed = new ArrayList<>();
    for (int i = 0; i < edges; i++) {
      int v1 = in.nextInt();
      int v2 = in.nextInt();
      edgesList.add(new SimpleEdge<>((short) v1, (short) v2));
      edgesListReversed.add(new SimpleEdge<>((short) v2, (short) v1));
    }

    Graph<Short, SimpleEdge<Short>> graph = new Graph<>(verticesList, edgesList);
    TopSortSearch<Short, SimpleEdge<Short>> topSortSearch = new TopSortSearch<>();
    GraphHandler.dfs(graph, topSortSearch, true);

    Graph<Short, SimpleEdge<Short>> graphReversed = new Graph<>(verticesList, edgesListReversed);
    ConnectedSearch<Short, SimpleEdge<Short>> connectedSearch = new ConnectedSearch<>(topSortSearch.outOrder);
    GraphHandler.dfs(graphReversed, connectedSearch, true);

    System.out.println(connectedSearch.getComponentCount());
    System.out.print(graph.vertices.stream().map(vert -> connectedSearch.vertComponent.get(vert).toString()).collect(Collectors.joining(" ")));
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

enum VertexColors {White, Gray, Black;}

interface Edge<V> {


  V getFrom();

  V getTo();

  <E extends Edge<V>> E reversed();

}

class SimpleEdge<V> implements Edge<V> {

  private V from;

  private V to;

  public SimpleEdge(V from, V to) {
    this.from = from;
    this.to = to;
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
  public SimpleEdge<V> reversed() {
    return new SimpleEdge<>(getTo(), getFrom());
  }

}

class Graph<V, E extends Edge<V>> {
  private int edgesNum = 0;
  public List<V> vertices;

  public HashMap<V, List<E>> edgesMap = new HashMap<>();

  public Graph(List<V> vertices, List<E> edges) {
    this.vertices = vertices;
    for (E edge : edges) {
      ++edgesNum;
      edgesMap.putIfAbsent(edge.getFrom(), new ArrayList<>());
      edgesMap.get(edge.getFrom()).add(edge);
    }
  }

  public List<E> getConnected(V vertex) {
    return edgesMap.getOrDefault(vertex, new ArrayList<>());
  }

  public int getVertexCount() {
    return vertices.size();
  }

  public int getEdgeCount() {
    return edgesNum;
  }

}

interface GraphVisitor<V, E extends Edge<V>> {

  public void initVertex(V vertex);

  public Stack<V> getVisitStack();

  public V getRandomUnexplored(List<V> vertices);

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

class TopSortSearch<V, E extends Edge<V>> implements GraphVisitor<V, E> {

  public List<V> outOrder = new ArrayList<>();

  private final Stack<V> visitStack = new Stack<>();
  // state of each index
  private final HashMap<V, VertexColors> vertexColors = new HashMap<>();

  private int lastCheckedIndex = -1;

  @Override
  public void initVertex(V vertex) {
    vertexColors.put(vertex, VertexColors.White);
  }

  @Override
  public Stack<V> getVisitStack() {
    return visitStack;
  }

  @Override
  public V getRandomUnexplored(List<V> vertices) {
    for (int i = lastCheckedIndex + 1; i < vertexColors.size(); i++) {
      if (vertexColors.get(vertices.get(i)) == VertexColors.White) {
        lastCheckedIndex = i;
        return vertices.get(i);
      }
    }
    lastCheckedIndex = vertexColors.size() - 1;
    return null;
  }

  @Override
  public VertexColors getVertexStatus(V vertex) {
    return vertexColors.get(vertex);
  }

  @Override
  public void setVertexStatus(V vertex, VertexColors status) {
    vertexColors.put(vertex, status);
  }

  @Override
  public void startExploring(V vertex) {
    visitStack.add(vertex);
    setVertexStatus(vertex, VertexColors.Gray);
  }

  @Override
  public void finishExploring(V vertex) {

  }

  @Override
  public void exploreWhite(E edge) {
    setVertexStatus(edge.getTo(), VertexColors.Gray);
    visitStack.add(edge.getTo());
  }

  @Override
  public void exploreGray(E edge) {

  }

  @Override
  public void exploreBlack(E edge) {

  }

  @Override
  public void endVertex(V vertex) {
    visitStack.pop();
    outOrder.add(vertex);
  }

  @Override
  public boolean isFinished() {
    return
        (lastCheckedIndex == vertexColors.size() - 1 && visitStack.isEmpty());
  }

}

class ConnectedSearch<V, E extends Edge<V>> implements GraphVisitor<V, E> {

  public Map<V, Integer> vertComponent = new HashMap<>();
  private List<V> toGoOrder;

  private Stack<V> visitStack = new Stack<>();
  // state of each index
  private final HashMap<V, VertexColors> vertexColors = new HashMap<>();
  private int lastCheckedIndex = -1;

  private int currentComponent = 0;

  public ConnectedSearch(List<V> toGoOrder) {
    this.toGoOrder = toGoOrder;
    this.lastCheckedIndex = toGoOrder.size();
  }

  @Override
  public void initVertex(V vertex) {
    vertexColors.put(vertex, VertexColors.White);
  }


  @Override
  public Stack<V> getVisitStack() {
    return visitStack;
  }

  @Override
  public V getRandomUnexplored(List<V> vertices) {
    for (int i = lastCheckedIndex - 1; i >= 0; --i) {
      if (vertexColors.get(toGoOrder.get(i)) == VertexColors.White) {
        lastCheckedIndex = i;
        return toGoOrder.get(i);
      }
    }
    lastCheckedIndex = 0;
    return null;
  }

  @Override
  public VertexColors getVertexStatus(V vertex) {
    return vertexColors.get(vertex);
  }

  @Override
  public void setVertexStatus(V vertex, VertexColors status) {
    vertexColors.put(vertex, status);
  }

  @Override
  public void startExploring(V vertex) {
    ++currentComponent;
    visitStack.add(vertex);
    setVertexStatus(vertex, VertexColors.Gray);
    vertComponent.put(vertex, currentComponent);
  }

  @Override
  public void finishExploring(V vertex) {
    
  }

  @Override
  public void exploreWhite(E edge) {
    visitStack.add(edge.getTo());
    vertComponent.put(edge.getTo(), currentComponent);
  }

  @Override
  public void exploreGray(E edge) {

  }

  @Override
  public void exploreBlack(E edge) {

  }

  @Override
  public void endVertex(V vertex) {
    visitStack.pop();
  }

  @Override
  public boolean isFinished() {
    return
        (lastCheckedIndex == 0 &&
            vertexColors.get(toGoOrder.get(lastCheckedIndex)) == VertexColors.Black);
  }

  public int getComponentCount() {
    return currentComponent;
  }

}

class GraphHandler {
  public static <V, E extends Edge<V>, G extends GraphVisitor<V, E>> void dfs(Graph<V, E> graph, G graphVisitor, boolean persist) {
    var checkMap = initGraph(graph, graphVisitor);
    do {
      V vertex = graphVisitor.getRandomUnexplored(graph.vertices);
      if (vertex == null) {
        return;
      }
      graphVisitor.startExploring(vertex);
      GraphHandler.dfsOnStack(graph, graphVisitor, checkMap);
      graphVisitor.finishExploring(vertex);
    } while (persist && !graphVisitor.isFinished());
  }

  private static <V, E extends Edge<V>, G extends GraphVisitor<V, E>> Map<V, Integer> initGraph(Graph<V, E> graph, G graphVisitor) {
    Map<V, Integer> lastCheckedEdge = new HashMap<>();
    for (V vertex : graph.vertices) {
      graphVisitor.initVertex(vertex);
      lastCheckedEdge.put(vertex, 0);
    }
    return lastCheckedEdge;
  }

  private static <V, E extends Edge<V>, G extends GraphVisitor<V, E>> void dfsOnStack(Graph<V, E> graph, G visitor, Map<V, Integer> lastCheckedEdge) {
    while (!visitor.getVisitStack().isEmpty()) {
      if (visitor.isFinished()) {
        break;
      }

      V currentVert = visitor.getVisitStack().peek();
      List<E> outgoingEdges = graph.getConnected(currentVert);
      while (lastCheckedEdge.get(currentVert) < outgoingEdges.size()) {
        E edge = outgoingEdges.get(lastCheckedEdge.get(currentVert));
        lastCheckedEdge.replace(currentVert, lastCheckedEdge.get(currentVert) + 1);
        VertexColors status = visitor.getVertexStatus(edge.getTo());
        if (status == VertexColors.White) {
          visitor.exploreWhite(edge);
          break;
        } else if (status == VertexColors.Gray) {
          visitor.exploreGray(edge);
        } else {
          visitor.exploreBlack(edge);
        }
      }
      if (currentVert.equals(visitor.getVisitStack().peek())) {
        visitor.setVertexStatus(currentVert, VertexColors.Black);
        visitor.endVertex(currentVert);
      }
    }
  }
}
