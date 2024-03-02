package Task05_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

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

enum VertexStatus {White, Gray, Black;}

interface Edge<V> {
  V from();

  V to();
}

class SimpleEdge<V> implements Edge<V> {
  private V from;
  private V to;

  public SimpleEdge(V from, V to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public V from() {
    return from;
  }

  @Override
  public V to() {
    return to;
  }
}

class Graph<V, E extends Edge<V>> {
  private int edgesNum = 0;
  public List<V> vertexes;
  public HashMap<V, List<E>> edgesMap = new HashMap<>();

  public Graph(List<V> vertexes, List<E> edges) {
    this.vertexes = vertexes;
    for (E edge : edges) {
      ++edgesNum;
      edgesMap.putIfAbsent(edge.from(), new ArrayList<>());
      edgesMap.get(edge.from()).add(edge);
    }
  }

  public List<E> getConnected(V vertex) {
    return edgesMap.getOrDefault(vertex, new ArrayList<>());
  }

  public int getVertexCount() {
    return vertexes.size();
  }

  public int getEdgeCount() {
    return edgesNum;
  }
}

interface GraphExplorer<V, E extends Edge<V>> {
  public Stack<V> getVisitStack();

  public V getRandomUnexplored();

  public VertexStatus getVertexStatus(V vertex);

  public void setVertexStatus(V vertex, VertexStatus status);

  public void startExploring(V vertex);

  public void finishExploring(V vertex);

  public void exploreWhite(E edge);

  public void exploreGray(E edge);

  public void exploreBlack(E edge);

  public void endVertex(V vertex);

  public boolean isFinished();
}

class ClustersSearch<V, E extends Edge<V>> implements GraphExplorer<V, E> {
  public StringBuilder answer = new StringBuilder();
  public StringBuilder lastCluster;
  public int lastClusterSize = 0;
  public int clusterNumber = 0;

  private final Graph<V, E> graph;
  private final Stack<V> visitStack = new Stack<>();

  // state of each index
  private final HashMap<V, VertexStatus> vertexStatuses = new HashMap<>();
  private int lastCheckedIndex = -1;

  public ClustersSearch(Graph<V, E> graph) {
    this.graph = graph;
    prepareGraph();
  }

  public void prepareGraph() {
    for (int i = 0; i < graph.getVertexCount(); i++) {
      vertexStatuses.put(graph.vertexes.get(i), VertexStatus.White);
    }
  }

  @Override
  public Stack<V> getVisitStack() {
    return visitStack;
  }

  @Override
  public V getRandomUnexplored() {
    for (int i = lastCheckedIndex + 1; i < vertexStatuses.size(); i++) {
      if (vertexStatuses.get(graph.vertexes.get(i)) == VertexStatus.White) {
        lastCheckedIndex = i;
        return graph.vertexes.get(i);
      }
    }
    lastCheckedIndex = vertexStatuses.size() - 1;
    return null;
  }

  @Override
  public VertexStatus getVertexStatus(V vertex) {
    return vertexStatuses.get(vertex);
  }

  @Override
  public void setVertexStatus(V vertex, VertexStatus status) {
    vertexStatuses.put(vertex, status);
  }

  @Override
  public void startExploring(V vertex) {
    clusterNumber++;
    lastClusterSize = 1;
    lastCluster = new StringBuilder();
    lastCluster.append(vertex.toString()).append(" ");
    setVertexStatus(vertex, VertexStatus.Gray);
    visitStack.add(vertex);
  }

  @Override
  public void finishExploring(V vertex) {
    answer.append(lastClusterSize).append('\n').append(lastCluster).append('\n');
  }

  @Override
  public void exploreWhite(E edge) {
    setVertexStatus(edge.to(), VertexStatus.Gray);
    visitStack.add(edge.to());
    lastClusterSize++;
    lastCluster.append(edge.to().toString()).append(" ");
  }

  @Override
  public void exploreGray(E edge) {
    return;
  }

  @Override
  public void exploreBlack(E edge) {
    return;
  }

  @Override
  public void endVertex(V vertex) {
    visitStack.pop();
  }

  @Override
  public boolean isFinished() {
    return lastCheckedIndex == vertexStatuses.size() - 1;
  }
}

class DFS<V, E extends Edge<V>, G extends GraphExplorer<V, E>> {
  public final Graph<V, E> graph;

  public final G explorer;

  private final Map<V, Integer> checkMemory = new HashMap<>();

  public DFS(Graph<V, E> graph, G graphExplorer) {
    this.graph = graph;
    this.explorer = graphExplorer;
    for (int i = 0; i < graph.vertexes.size(); i++) {
      checkMemory.put(graph.vertexes.get(i), 0);
    }
  }

  public void startDFS() {
    V vertex = explorer.getRandomUnexplored();
    if (vertex == null) {
      return;
    }
    explorer.startExploring(vertex);
    dfsOnStack();
    explorer.finishExploring(vertex);
  }

  public void dfsOnStack() {
    while (!explorer.getVisitStack().isEmpty()) {
      if (explorer.isFinished()) {
        break;
      }

      V currentVert = explorer.getVisitStack().peek();
      List<E> outgoingEdges = graph.getConnected(currentVert);
      while (checkMemory.get(currentVert) < outgoingEdges.size()) {
        E edge = outgoingEdges.get(checkMemory.get(currentVert));
        checkMemory.replace(currentVert, checkMemory.get(currentVert) + 1);
        VertexStatus status = explorer.getVertexStatus(edge.to());
        if (status == VertexStatus.White) {
          explorer.exploreWhite(edge);
          break;
        } else if (status == VertexStatus.Gray) {
          explorer.exploreGray(edge);
        } else {
          explorer.exploreBlack(edge);
        }
      }
      if (currentVert.equals(explorer.getVisitStack().peek())) {
        explorer.setVertexStatus(currentVert, VertexStatus.Black);
        explorer.endVertex(currentVert);
      }
    }
  }
}


public class Main {
  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    List<Integer> vertexesList = IntStream.rangeClosed(1, vertexes).boxed().toList();
    List<SimpleEdge<Integer>> edgesList = new ArrayList<>();
    for (int i = 0; i < edges; i++) {
      int v1 = in.nextInt();
      int v2 = in.nextInt();
      edgesList.add(new SimpleEdge<>(v1, v2));
      edgesList.add(new SimpleEdge<>(v2, v1));
    }
    Graph<Integer, SimpleEdge<Integer>> graph = new Graph<>(vertexesList, edgesList);
    ClustersSearch<Integer, SimpleEdge<Integer>> clustersSearch = new ClustersSearch<>(graph);
    DFS<Integer, SimpleEdge<Integer>, ClustersSearch<Integer, SimpleEdge<Integer>>> dfs = new DFS<>(graph, clustersSearch);
    while (!clustersSearch.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(clustersSearch.clusterNumber);
    System.out.println(clustersSearch.answer);
  }
}
