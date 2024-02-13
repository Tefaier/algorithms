package Task05_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
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
      Integer vertex = explorer.getRandomUnexplored();
      if (vertex == null) {
        return;
      }
      explorer.startExploring(vertex);
      dfsOnStack();
      explorer.finishExploring(vertex);
    }

    public void startDFS(Integer vertex) {
      explorer.startExploring(vertex);
      dfsOnStack();
      explorer.finishExploring(vertex);
    }

    public void dfsOnStack() {
      while (!explorer.getVisitStack().isEmpty()) {
        if (explorer.isFinished()) {
          break;
        }

        Integer currentIndex = explorer.getVisitStack().peek();
        List<Integer> connected = graph.getConnected(currentIndex);
        while (checkMemory[currentIndex] < connected.size()) {
          int next = connected.get(checkMemory[currentIndex]);
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
    public Stack<Integer> getVisitStack();

    public Integer getRandomUnexplored();

    public void prepareGraph(Graph<V> graph);

    public VertexStatus getVertexStatus(Integer vertex);

    public void setVertexStatus(Integer vertex, VertexStatus status);

    public void startExploring(Integer vertex);

    public void finishExploring(Integer vertex);

    public void exploreWhite(Integer vertex);

    public void exploreGray(Integer vertex);

    public void exploreBlack(Integer vertex);

    public void endVertex(Integer vertex);

    public boolean isFinished();
  }

  static class Graph<V> {
    public List<List<Integer>> connectionList = new ArrayList<>();
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

    public List<Integer> getConnected(Integer vertex) {
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
      for (int i = 0; i < connectionList.size(); i++) {
        for (Integer index : connectionList.get(i)) {
          newGraph.addEdge(new Edge(index, i), true);
        }
      }
      return newGraph;
    }
  }

  static record Edge(Integer from, Integer to) {
  }

  static class TopolSortSearch<V> implements GraphExplorer<V> {
    public List<Integer> outOrder = new ArrayList<>();

    private Graph<V> graph;
    private boolean cycleDetected = false;
    private Stack<Integer> visitStack = new Stack<>();

    // state of each index
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Integer lastCheckedIndex = -1;

    @Override
    public Stack<Integer> getVisitStack() {
      return visitStack;
    }

    @Override
    public Integer getRandomUnexplored() {
      for (int i = lastCheckedIndex + 1; i < vertexStatuses.size(); i++) {
        if (vertexStatuses.get(i) == VertexStatus.White) {
          lastCheckedIndex = i;
          return i;
        }
      }
      lastCheckedIndex = vertexStatuses.size() - 1;
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
    public VertexStatus getVertexStatus(Integer vertex) {
      return vertexStatuses.get(vertex);
    }

    @Override
    public void setVertexStatus(Integer vertex, VertexStatus status) {
      vertexStatuses.set(vertex, status);
    }

    @Override
    public void startExploring(Integer vertex) {
      visitStack.add(vertex);
    }

    @Override
    public void finishExploring(Integer vertex) {
      return;
    }

    @Override
    public void exploreWhite(Integer vertex) {
      visitStack.add(vertex);
    }

    @Override
    public void exploreGray(Integer vertex) {
      cycleDetected = true;
    }

    @Override
    public void exploreBlack(Integer vertex) {
      return;
    }

    @Override
    public void endVertex(Integer vertex) {
      visitStack.pop();
      outOrder.add(vertex);
    }

    @Override
    public boolean isFinished() {
      return
          (lastCheckedIndex == vertexStatuses.size() - 1 &&
              vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black) ||
              cycleDetected;
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    List<Integer> vertexesList = IntStream.rangeClosed(1, vertexes).boxed().toList();
    List<Edge> edgesList = new ArrayList<>();
    for (int i = 0; i < edges; i++) {
      edgesList.add(new Edge(in.nextInt() - 1, in.nextInt() - 1));
    }
    Graph<Integer> graph = new Graph<>(edgesList, vertexesList, true);
    TopolSortSearch<Integer> topolSortSearch = new TopolSortSearch<>();
    DFS<Integer, TopolSortSearch<Integer>> dfs = new DFS<>(graph, topolSortSearch);
    dfs.initExplorer();
    while (!topolSortSearch.isFinished()) {
      dfs.startDFS();
    }
    if (topolSortSearch.cycleDetected) {
      System.out.print(-1);
      return;
    }
    for (int i = topolSortSearch.outOrder.size() - 1; i >= 0; i--) {
      System.out.print(graph.vertexes.get(topolSortSearch.outOrder.get(i)) + " ");
    }
  }
}
