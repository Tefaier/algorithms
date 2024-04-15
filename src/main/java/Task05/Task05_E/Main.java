package Task05.Task05_E;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
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
    private short[] checkMemory;

    public DFS(Graph<V> graph, E graphExplorer) {
      this.graph = graph;
      this.explorer = graphExplorer;
      this.checkMemory = new short[graph.getSize()];
      Arrays.fill(this.checkMemory, (short) 0);
      graph.checkMemory = this.checkMemory;
      initExplorer();
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
      Short currentIndex;
      while ((currentIndex = explorer.getActiveVert()) != null) {
        if (explorer.isFinished()) {
          break;
        }

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
        if (currentIndex.equals(explorer.getActiveVert())) {
          explorer.setVertexStatus(currentIndex, VertexStatus.Black);
          explorer.endVertex(currentIndex);
        }
      }
    }
  }

  static interface GraphExplorer<V> {
    public Short getActiveVert();

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
    public short[] checkMemory;
    private List<List<Integer>> edgesMemory = new ArrayList<>();
    private int counter = 0;
    private short size;

    public Graph() {

    }

    public Graph(short size) {
      this.size = size;
      for (int i = 0; i < size; i++) {
        connectionList.add(new ArrayList<>());
        edgesMemory.add(new ArrayList<>());
      }
    }

    public Short getSize() {
      return size;
    }

    // use checkMemory - returns the edge that was looked last in the dfs from some vertex
    public Integer getEdgeIndex(short from) {
      return edgesMemory.get(from).get(checkMemory[from] - 1);
    }

    public List<Short> getConnected(Short vertex) {
      return connectionList.get(vertex);
    }

    public void addEdge(Edge edge, boolean orientated) {
      ++counter;
      connectionList.get(edge.from()).add(edge.to());
      edgesMemory.get(edge.from()).add(counter);
      if (!orientated) {
        connectionList.get(edge.to()).add(edge.from());
        edgesMemory.get(edge.to()).add(counter);
      }
    }
  }

  static class Edge {
    short from;
    short to;

    public Edge(short from, short to) {
      this.from = (short) Math.min(from, to);
      this.to = (short) Math.max(from, to);
    }

    public short from() {
      return from;
    }

    public short to() {
      return to;
    }
  }

  static class BridgeExplorer<V> implements GraphExplorer<V> {
    public List<Integer> bridgesIndexes = new ArrayList<>();
    //private Set<Integer> bridgesBlackList = new HashSet<>();

    private Graph<V> graph;
    private Stack<Short> visitStack = new Stack<>();
    private Stack<VertexStatus> statusStack = new Stack<>();

    // state of each index
    private short[] inTime;
    private short[] upTime;
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Short lastCheckedIndex = -1;
    private short timer = 0;

    @Override
    public Short getActiveVert() {
      return visitStack.isEmpty() ? null : visitStack.peek();
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
      for (int i = 0; i < graph.getSize(); i++) {
        vertexStatuses.add(VertexStatus.White);
      }
      inTime = new short[graph.getSize()];
      upTime = new short[graph.getSize()];
      Arrays.fill(inTime, (short) 0);
      Arrays.fill(upTime, (short) 0);
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
      statusStack.add(VertexStatus.White);
      ++timer;
      inTime[vertex] = timer;
      upTime[vertex] = timer;
    }

    @Override
    public void exploreGray(Short vertex) {
      if (visitStack.size() > 1 && Objects.equals(vertex, visitStack.elementAt(visitStack.size() - 2))) {
        incrementStatus();
        return;
      }
      upTime[getActiveVert()] = (short) Math.min(upTime[getActiveVert()], inTime[vertex]);
    }

    private void incrementStatus() {
      VertexStatus status = statusStack.pop();
      if (status == VertexStatus.White) {
        statusStack.add(VertexStatus.Gray);
      } else {
        statusStack.add(VertexStatus.Black);
      }
    }

    @Override
    public void exploreBlack(Short vertex) {
      return;
      // upTime[getActiveVert()] = (short) Math.min(upTime[getActiveVert()], inTime[vertex]);
    }

    @Override
    public void endVertex(Short vertex) {
      visitStack.pop();
      VertexStatus status = statusStack.pop();
      Short active = getActiveVert();
      if (active == null) {
        return;
      }
      upTime[active] = (short) Math.min(upTime[active], upTime[vertex]);
      if (upTime[vertex] > inTime[active] && status == VertexStatus.Gray) {
        int bridgeIndex = graph.getEdgeIndex(active);
        bridgesIndexes.add(bridgeIndex);
        /*
        if (!bridgesBlackList.contains(bridgeIndex)) {
          bridgesIndexes.add(bridgeIndex);
        }
         */
      }
    }

    @Override
    public boolean isFinished() {
      return
          (lastCheckedIndex == vertexStatuses.size() - 1 &&
              vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black);
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    short vertexes = (short) in.nextInt();
    int edges = in.nextInt();
    Graph<Short> graph = new Graph<>(vertexes);
    for (int i = 0; i < edges; i++) {
      graph.addEdge(new Edge((short) (in.nextInt() - 1), (short) (in.nextInt() - 1)), false);
    }
    BridgeExplorer<Short> bridgeExplorer = new BridgeExplorer<>();
    DFS<Short, BridgeExplorer<Short>> dfs = new DFS<>(graph, bridgeExplorer);
    while (!bridgeExplorer.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(bridgeExplorer.bridgesIndexes.size());
    System.out.print(bridgeExplorer.bridgesIndexes.stream().sorted().map(Objects::toString).collect(Collectors.joining(" ")));
  }
}
