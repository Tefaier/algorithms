package Task05_B;

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

  static class DFS<V, E extends Edge, G extends GraphExplorer<V, E>> {
    public final Graph<V, E> graph;
    public final G explorer;
    private int[] checkMemory;

    public DFS(Graph<V, E> graph, G graphExplorer) {
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
          Integer next = connected.get(checkMemory[currentIndex]);
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

  static class Graph<V, E extends Edge> {
    public List<List<Integer>> connectionList = new ArrayList<>();
    // vertexes serves as a backup from index to original information
    // however edges must use indexes of vertexes
    public List<V> vertexes;
    public List<E> edges;

    public Graph() {

    }

    public Graph(List<E> edges, List<V> vertexes) {
      this.vertexes = vertexes;
      this.edges = edges;
      for (int i = 0; i < vertexes.size(); i++) {
        connectionList.add(new ArrayList<>());
      }
      for (Edge edge : edges) {
        connectionList.get(edge.from()).add(edge.to());
        if (!edge.orientated()) {
          connectionList.get(edge.to()).add(edge.from());
        }
      }
    }

    public List<Integer> getConnected(Integer vertex) {
      return connectionList.get(vertex);
    }

    public int getVertexCount() {
      return vertexes.size();
    }

    public int getEdgesCount() {
      return edges.size();
    }
  }

  static record Edge(Integer from, Integer to, boolean orientated) {
  }

  static interface GraphExplorer<V, E extends Edge> {
    public Stack<Integer> getVisitStack();

    public Integer getRandomUnexplored();

    public void prepareGraph(Graph<V, E> graph);

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


  static class ClustersSearch<V, E extends Edge> implements GraphExplorer<V, E> {
    public StringBuilder answer = new StringBuilder();
    public StringBuilder lastCluster;
    public int lastClusterSize = 0;
    public int clusterNumber = 0;

    private Graph<V, E> graph;

    // state of each index
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Integer lastCheckedIndex = -1;

    @Override
    public Stack<Integer> getVisitStack() {
      return null;
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
    public void prepareGraph(Graph<V, E> graph) {
      this.graph = graph;
      for (int i = 0; i < graph.getVertexCount(); i++) {
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
      clusterNumber++;
      lastClusterSize = 0;
      lastCluster = new StringBuilder();
    }

    @Override
    public void finishExploring(Integer vertex) {
      answer.append(lastClusterSize).append('\n').append(lastCluster).append('\n');
    }

    @Override
    public void exploreWhite(Integer vertex) {
      lastClusterSize++;
      lastCluster.append(graph.vertexes.get(vertex)).append(" ");
    }

    @Override
    public void exploreGray(Integer vertex) {
      return;
    }

    @Override
    public void exploreBlack(Integer vertex) {
      return;
    }

    @Override
    public void endVertex(Integer vertex) {
      return;
    }

    @Override
    public boolean isFinished() {
      return lastCheckedIndex == vertexStatuses.size() - 1;
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    List<Integer> vertexesList = IntStream.rangeClosed(1, vertexes).boxed().toList();
    List<Edge> edgesList = new ArrayList<>();
    for (int i = 0; i < edges; i++) {
      edgesList.add(new Edge(in.nextInt() - 1, in.nextInt() - 1, false));
    }
    Graph<Integer, Edge> graph = new Graph<>(edgesList, vertexesList);
    ClustersSearch<Integer, Edge> clustersSearch = new ClustersSearch<>();
    DFS<Integer, Edge, ClustersSearch<Integer, Edge>> dfs = new DFS<>(graph, clustersSearch);
    dfs.initExplorer();
    while (!clustersSearch.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(clustersSearch.clusterNumber);
    System.out.println(clustersSearch.answer);
  }
}
