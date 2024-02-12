package Task05_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public DFS(Graph<V> graph, E graphExplorer) {
      this.graph = graph;
      this.explorer = graphExplorer;
    }

    public void initExplorer() {
      explorer.prepareGraph(graph);
    }

    public void startDFS() {
      V vertex = explorer.getRandomUnexplored();
      explorer.startExploring(vertex);
      dfs(vertex);
    }

    public void startDFS(V vertex) {
      explorer.startExploring(vertex);
      dfs(vertex);
    }

    public void dfs(V vertex) {
      explorer.setVertexStatus(vertex, VertexStatus.Gray);
      explorer.startVertex(vertex);
      for (V next : graph.getConnected(vertex)) {
        if (explorer.getVertexStatus(next) == VertexStatus.White) {
          dfs(next);
        }
      }
      explorer.setVertexStatus(vertex, VertexStatus.Black);
    }
  }

  static interface GraphExplorer<V> {
    public V getRandomUnexplored();

    public void prepareGraph(Graph<V> graph);

    public VertexStatus getVertexStatus(V vertex);

    public void setVertexStatus(V vertex, VertexStatus status);

    public void startExploring(V vertex);

    public void startVertex(V vertex);

    public void exploreOld(V vertex);

    public void exploreFinished(V vertex);

    public void endVertex(V vertex);

    public boolean isFinished();
  }

  static class Graph<V> {
    public HashMap<V, Set<V>> connectionList = new HashMap<>();
    public List<V> vertexes;

    public Graph() {

    }

    public Graph(List<Edge<V>> edges, List<V> vertexes, boolean orientated) {
      this.vertexes = vertexes;
      vertexes.forEach(vert -> connectionList.put(vert, new HashSet<>()));
      for (Edge<V> edge : edges) {
        connectionList.get(edge.from()).add(edge.to());
        if (!orientated) {
          connectionList.get(edge.to()).add(edge.from());
        }
      }
    }

    public Set<V> getConnected(V vertex) {
      return connectionList.get(vertex);
    }
  }

  static record Edge<V>(V from, V to) {
  }

  static class ClustersSearch<V> implements GraphExplorer<V> {
    public StringBuilder answer = new StringBuilder();
    public StringBuilder lastCluster;
    public int lastClusterSize = 0;
    public int clusterNumber = 0;

    private Set<V> unexplored = new HashSet<>();
    private HashMap<V, VertexStatus> vertexStatuses = new HashMap<>();

    @Override
    public V getRandomUnexplored() {
      return unexplored.isEmpty() ? null : unexplored.stream().findAny().get();
    }

    @Override
    public void prepareGraph(Graph<V> graph) {
      unexplored.addAll(graph.connectionList.keySet());
    }

    @Override
    public VertexStatus getVertexStatus(V vertex) {
      return vertexStatuses.getOrDefault(vertex, VertexStatus.White);
    }

    @Override
    public void setVertexStatus(V vertex, VertexStatus status) {
      vertexStatuses.put(vertex, status);
    }

    @Override
    public void startExploring(V vertex) {
      if (lastClusterSize != 0) {
        answer.append(lastClusterSize).append('\n').append(lastCluster).append('\n');
      }
      clusterNumber++;
      lastClusterSize = 0;
      lastCluster = new StringBuilder();
      vertexStatuses = new HashMap<>();
    }

    @Override
    public void startVertex(V vertex) {
      lastClusterSize++;
      lastCluster.append(vertex).append(" ");
      unexplored.remove(vertex);
    }

    @Override
    public void exploreOld(V vertex) {
      return;
    }

    @Override
    public void exploreFinished(V vertex) {
      return;
    }

    @Override
    public void endVertex(V vertex) {
      return;
    }

    @Override
    public boolean isFinished() {
      return unexplored.isEmpty();
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    List<Integer> vertexesList = IntStream.rangeClosed(1, vertexes).boxed().toList();
    List<Edge<Integer>> edgesList = new ArrayList<>();
    for (int i = 0; i < edges; i++) {
      edgesList.add(new Edge<>(in.nextInt(), in.nextInt()));
    }
    Graph<Integer> graph = new Graph<>(edgesList, vertexesList, false);
    ClustersSearch<Integer> clustersSearch = new ClustersSearch<>();
    DFS<Integer, ClustersSearch<Integer>> dfs = new DFS<>(graph, clustersSearch);
    dfs.initExplorer();
    while (!clustersSearch.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(clustersSearch.clusterNumber);
    clustersSearch.answer.append(clustersSearch.lastClusterSize).append('\n').append(clustersSearch.lastCluster);
    System.out.println(clustersSearch.answer);
  }
}
