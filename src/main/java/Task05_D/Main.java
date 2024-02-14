package Task05_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  static class Parser {

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

  static enum VertexStatus {White, Gray, Black}

  static class DFS<V, E extends GraphExplorer<V>> {
    public final Graph<V> graph;
    public final E explorer;
    private int[] checkMemory;

    public DFS(Graph<V> graph, E graphExplorer) {
      this.graph = graph;
      this.explorer = graphExplorer;
      this.checkMemory = new int[graph.vertexes.size()];
      Arrays.fill(this.checkMemory, 0);
    }

    public void initExplorer() {
      explorer.prepareGraph(graph);
    }

    public void startDFS() {
      Short vertex = explorer.getRandomUnexplored();
      if (vertex == null) {
        return;
      }
      explorer.startExploring(vertex);
      dfsOnStack();
      explorer.finishExploring(vertex);
    }

    public void startDFS(Short vertex) {
      explorer.startExploring(vertex);
      dfsOnStack();
      explorer.finishExploring(vertex);
    }

    public void dfsOnStack() {
      while (!explorer.getVisitStack().isEmpty()) {
        if (explorer.isFinished()) {
          break;
        }

        Short currentIndex = explorer.getVisitStack().peek();
        List<Short> connected = graph.getConnected(currentIndex);
        while (checkMemory[currentIndex] < connected.size()) {
          short next = connected.get(checkMemory[currentIndex]);
          ++checkMemory[currentIndex];
          VertexStatus status = explorer.getVertexStatus(next);
          if (status == VertexStatus.White) {
            explorer.setVertexStatus(next, VertexStatus.Gray);
            explorer.exploreWhite(next);
            break;
          } else if (status == VertexStatus.Gray) {
            explorer.exploreGray(next);
          } else {
            explorer.exploreBlack(next);
          }
        }
        if (currentIndex.equals(explorer.getVisitStack().peek())) {
          explorer.setVertexStatus(currentIndex, VertexStatus.Black);
          explorer.endVertex(currentIndex);
        }
      }
    }
  }

  static interface GraphExplorer<V> {
    public Stack<Short> getVisitStack();

    public Short getRandomUnexplored();

    public void prepareGraph(Graph<V> graph);

    public VertexStatus getVertexStatus(Short vertex);

    public void setVertexStatus(Short vertex, VertexStatus status);

    public void startExploring(Short vertex);

    public void finishExploring(Short vertex);

    public void exploreWhite(Short vertex);

    public void exploreGray(Short vertex);

    public void exploreBlack(Short vertex);

    public void endVertex(Short vertex);

    public boolean isFinished();
  }

  static class Graph<V> {
    public List<List<Short>> connectionList = new ArrayList<>();
    // vertexes serves as a backup from index to original information
    // however edges must use indexes of vertexes
    public List<V> vertexes;

    public Graph() {

    }

    public Graph(List<Edge> edges, List<V> vertexes, boolean orientated) {
      this.vertexes = vertexes;
      for (int i = 0; i < vertexes.size(); i++) {
        connectionList.add(new ArrayList<>());
      }
      for (Edge edge : edges) {
        connectionList.get(edge.from()).add(edge.to());
        if (!orientated) {
          connectionList.get(edge.to()).add(edge.from());
        }
      }
    }

    public List<Short> getConnected(Short vertex) {
      return connectionList.get(vertex);
    }

    public void addVert(V vertex) {
      vertexes.add(vertex);
    }

    public void addEdge(Edge edge, boolean orientated) {
      connectionList.get(edge.from()).add(edge.to());
      if (!orientated) {
        connectionList.get(edge.to()).add(edge.from());
      }
    }

    public Graph<V> getReversed() {
      Graph<V> newGraph = new Graph<V>(new ArrayList<>(), vertexes, true);
      for (short i = 0; i < connectionList.size(); i++) {
        for (Short index : connectionList.get(i)) {
          newGraph.addEdge(new Edge(index, i), true);
        }
      }
      return newGraph;
    }
  }

  static record Edge(Short from, Short to) {
  }

  static class TopolSortSearch<V> implements GraphExplorer<V> {
    public List<Short> outOrder = new ArrayList<>();

    private Graph<V> graph;
    private Stack<Short> visitStack = new Stack<>();

    // state of each index
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Short lastCheckedIndex = -1;

    @Override
    public Stack<Short> getVisitStack() {
      return visitStack;
    }

    @Override
    public Short getRandomUnexplored() {
      for (short i = (short) (lastCheckedIndex + 1); i < vertexStatuses.size(); i++) {
        if (vertexStatuses.get(i) == VertexStatus.White) {
          lastCheckedIndex = i;
          return i;
        }
      }
      lastCheckedIndex = (short) (vertexStatuses.size() - 1);
      return null;
    }

    @Override
    public void prepareGraph(Graph<V> graph) {
      this.graph = graph;
      for (int i = 0; i < graph.vertexes.size(); i++) {
        vertexStatuses.add(VertexStatus.White);
      }
    }

    @Override
    public VertexStatus getVertexStatus(Short vertex) {
      return vertexStatuses.get(vertex);
    }

    @Override
    public void setVertexStatus(Short vertex, VertexStatus status) {
      vertexStatuses.set(vertex, status);
    }

    @Override
    public void startExploring(Short vertex) {
      exploreWhite(vertex);
      setVertexStatus(vertex, VertexStatus.Gray);
    }

    @Override
    public void finishExploring(Short vertex) {
      return;
    }

    @Override
    public void exploreWhite(Short vertex) {
      visitStack.add(vertex);
    }

    @Override
    public void exploreGray(Short vertex) {
      return;
    }

    @Override
    public void exploreBlack(Short vertex) {
      return;
    }

    @Override
    public void endVertex(Short vertex) {
      visitStack.pop();
      outOrder.add(vertex);
    }

    @Override
    public boolean isFinished() {
      return
          (lastCheckedIndex == vertexStatuses.size() - 1 &&
              vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black);
    }
  }

  static class ConnectedSearch<V> implements GraphExplorer<V> {
    public Short[] vertComponent;
    public List<Short> toGoOrder;

    private Graph<V> graph;
    private Stack<Short> visitStack = new Stack<>();

    // state of each index
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Short lastCheckedIndex = -1;
    private Short currentComponent = 0;

    @Override
    public Stack<Short> getVisitStack() {
      return visitStack;
    }

    @Override
    public Short getRandomUnexplored() {
      for (short i = (short) (lastCheckedIndex - 1); i >= 0; i--) {
        if (vertexStatuses.get(toGoOrder.get(i)) == VertexStatus.White) {
          lastCheckedIndex = i;
          return toGoOrder.get(i);
        }
      }
      lastCheckedIndex = (short) (0);
      return null;
    }

    @Override
    public void prepareGraph(Graph<V> graph) {
      this.graph = graph;
      for (int i = 0; i < graph.vertexes.size(); i++) {
        vertexStatuses.add(VertexStatus.White);
      }
      vertComponent = new Short[vertexStatuses.size()];
      Arrays.fill(vertComponent, (short) 0);
      lastCheckedIndex = (short) vertexStatuses.size();
    }

    @Override
    public VertexStatus getVertexStatus(Short vertex) {
      return vertexStatuses.get(vertex);
    }

    @Override
    public void setVertexStatus(Short vertex, VertexStatus status) {
      vertexStatuses.set(vertex, status);
    }

    @Override
    public void startExploring(Short vertex) {
      ++currentComponent;
      exploreWhite(vertex);
      setVertexStatus(vertex, VertexStatus.Gray);
    }

    @Override
    public void finishExploring(Short vertex) {
      return;
    }

    @Override
    public void exploreWhite(Short vertex) {
      visitStack.add(vertex);
      vertComponent[vertex] = currentComponent;
    }

    @Override
    public void exploreGray(Short vertex) {
      return;
    }

    @Override
    public void exploreBlack(Short vertex) {
      return;
    }

    @Override
    public void endVertex(Short vertex) {
      visitStack.pop();
    }

    @Override
    public boolean isFinished() {
      return
          (lastCheckedIndex == 0 &&
              vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black);
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    List<Short> vertexesList = IntStream.rangeClosed(1, vertexes).boxed().map(Integer::shortValue).toList();
    List<Edge> edgesList = new ArrayList<>();
    for (int i = 0; i < edges; i++) {
      edgesList.add(new Edge((short) (in.nextInt() - 1), (short) (in.nextInt() - 1)));
    }
    Graph<Short> graph = new Graph<>(edgesList, vertexesList, true);
    TopolSortSearch<Short> topolSortSearch = new TopolSortSearch<>();
    DFS<Short, TopolSortSearch<Short>> dfs = new DFS<>(graph, topolSortSearch);
    dfs.initExplorer();
    while (!topolSortSearch.isFinished()) {
      dfs.startDFS();
    }
    Graph<Short> graphReversed = graph.getReversed();
    ConnectedSearch<Short> connectedSearch = new ConnectedSearch<>();
    connectedSearch.toGoOrder = topolSortSearch.outOrder;
    DFS<Short, ConnectedSearch<Short>> dfs2 = new DFS<>(graphReversed, connectedSearch);
    dfs2.initExplorer();
    while (!connectedSearch.isFinished()) {
      dfs2.startDFS();
    }
    System.out.println(connectedSearch.currentComponent);
    System.out.print(Arrays.stream(connectedSearch.vertComponent).map(Object::toString).collect(Collectors.joining(" ")));
  }
}
