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

  static class DFS<V> {
    public final Graph<V> graph;
    public final ClustersSearch<V> explorer;

    public DFS(Graph<V> graph, ClustersSearch<V> graphExplorer) {
      this.graph = graph;
      this.explorer = graphExplorer;
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
      dfs(vertex);
      explorer.finishExploring(vertex);
    }

    public void dfs(Integer vertex) {
      explorer.setVertexStatus(vertex, VertexStatus.Gray);
      explorer.exploreWhite(vertex);
      for (Integer next : graph.getConnected(vertex)) {
        if (explorer.isFinished()) {
          break;
        }
        switch (explorer.getVertexStatus(next)) {
          case White -> {
            dfs(next);
          }
          case Gray -> {
            explorer.exploreGray(next);
          }
          case Black -> {
            explorer.exploreBlack(next);
          }
        }
      }
      explorer.setVertexStatus(vertex, VertexStatus.Black);
      explorer.endVertex(vertex);
    }
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
  }

  static record Edge(Integer from, Integer to) {
  }

  static class ClustersSearch<V> {
    public StringBuilder answer = new StringBuilder();
    public StringBuilder lastCluster;
    public int lastClusterSize = 0;
    public int clusterNumber = 0;

    private Graph<V> graph;

    // state of each index
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Integer lastCheckedIndex = -1;

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

    public void prepareGraph(Graph<V> graph) {
      this.graph = graph;
      for (int i = 0; i < graph.vertexes.size(); i++) {
        vertexStatuses.add(VertexStatus.White);
      }
    }

    public VertexStatus getVertexStatus(Integer vertex) {
      return vertexStatuses.get(vertex);
    }

    public void setVertexStatus(Integer vertex, VertexStatus status) {
      vertexStatuses.set(vertex, status);
    }

    public void startExploring(Integer vertex) {
      clusterNumber++;
      lastClusterSize = 0;
      lastCluster = new StringBuilder();
    }

    public void finishExploring(Integer vertex) {
      answer.append(lastClusterSize).append('\n').append(lastCluster).append('\n');
    }

    public void exploreWhite(Integer vertex) {
      lastClusterSize++;
      lastCluster.append(graph.vertexes.get(vertex)).append(" ");
    }

    public void exploreGray(Integer vertex) {
      return;
    }

    public void exploreBlack(Integer vertex) {
      return;
    }

    public void endVertex(Integer vertex) {
      return;
    }

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
      edgesList.add(new Edge(in.nextInt() - 1, in.nextInt() - 1));
    }
    Graph<Integer> graph = new Graph<>(edgesList, vertexesList, false);
    ClustersSearch<Integer> clustersSearch = new ClustersSearch<>();
    DFS<Integer> dfs = new DFS<>(graph, clustersSearch);
    dfs.initExplorer();
    while (!clustersSearch.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(clustersSearch.clusterNumber);
    System.out.println(clustersSearch.answer);
  }
}
