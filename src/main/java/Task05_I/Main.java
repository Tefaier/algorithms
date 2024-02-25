package Task05_I;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

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

  static record Edge(short from, short to) {
  }

  static class DFS {
    public final Graph graph;
    public final DoubleConExplorer explorer;
    private short[] checkMemory;

    public DFS(Graph graph, DoubleConExplorer graphExplorer) {
      this.graph = graph;
      this.explorer = graphExplorer;
      this.checkMemory = new short[graph.getSize()];
      Arrays.fill(this.checkMemory, (short) 0);
      explorer.prepareGraph(graph);
    }

    public void startDFS() {
      Short vertex = explorer.getNextUnexplored();
      if (vertex == null) {
        return;
      }
      explorer.startExploring(vertex);
      dfsOnStack();
      explorer.finishExploring(vertex);
    }

    private void dfsOnStack() {
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
            explorer.exploreWhite(next);
            break;
          } else if (status == VertexStatus.Gray) {
            explorer.exploreGray(next);
          } else {
            explorer.exploreBlack(next);
          }
        }

        if (isLast(currentIndex)) {
          explorer.endVertex(currentIndex);
        }
      }
    }

    private boolean isLast(Short vertex) {
      return vertex.equals(explorer.getActiveVert());
    }
  }

  static class Graph {
    public List<List<Short>> connectionList = new ArrayList<>();
    private short size;

    public Graph() {

    }

    public Graph(short size) {
      this.size = size;
      for (int i = 0; i < size; i++) {
        connectionList.add(new ArrayList<>());
      }
    }

    public Short getSize() {
      return size;
    }

    public List<Short> getConnected(Short vertex) {
      return connectionList.get(vertex);
    }

    public void addEdge(Edge edge, boolean orientated) {
      connectionList.get(edge.from()).add(edge.to());
      if (!orientated) {
        connectionList.get(edge.to()).add(edge.from());
      }
    }
  }

  static class DoubleConExplorer {
    private static class BridgeMemory {
      public int stackDepth;
      public int counter = 1;
      public boolean merged = false;

      public BridgeMemory(int stackDepth) {
        this.stackDepth = stackDepth;
      }

      public void swallow(BridgeMemory bridgeMemory) {
        this.counter += bridgeMemory.counter;
        this.merged = true;
      }
    }

    private Stack<BridgeMemory> bridgeMemories = new Stack<>();

    private Graph graph;

    // state of each index
    private short timer = 0;
    private short[] inTime;
    private short[] upTime;

    private Stack<Short> visitStack = new Stack<>();
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Short lastCheckedIndex = -1;

    public void prepareGraph(Graph graph) {
      this.graph = graph;
      for (int i = 0; i < graph.getSize(); i++) {
        vertexStatuses.add(VertexStatus.White);
      }
      inTime = new short[graph.getSize()];
      upTime = new short[graph.getSize()];
      Arrays.fill(inTime, (short) 0);
      Arrays.fill(upTime, (short) 0);
    }

    public Short getActiveVert() {
      return visitStack.isEmpty() ? null : visitStack.peek();
    }

    public Short getNextUnexplored() {
      for (short i = (short) (lastCheckedIndex + 1); i < vertexStatuses.size(); i++) {
        if (vertexStatuses.get(i) == VertexStatus.White) {
          lastCheckedIndex = i;
          return i;
        }
      }
      lastCheckedIndex = (short) (vertexStatuses.size() - 1);
      return null;
    }

    public VertexStatus getVertexStatus(Short vertex) {
      return vertexStatuses.get(vertex);
    }

    public void setVertexStatus(Short vertex, VertexStatus status) {
      vertexStatuses.set(vertex, status);
    }

    public void startExploring(Short vertex) {
      exploreWhite(vertex);
    }

    public void finishExploring(Short vertex) {
      return;
    }

    public void exploreWhite(Short vertex) {
      setVertexStatus(vertex, VertexStatus.Gray);

      visitStack.add(vertex);

      ++timer;
      inTime[vertex] = timer;
      upTime[vertex] = timer;
    }

    public void exploreGray(Short vertex) {
      if (wentBack(vertex)) {
        return;
      }
      upTime[getActiveVert()] = (short) Math.min(upTime[getActiveVert()], inTime[vertex]);
    }

    private boolean wentBack(Short vertex) {
      return visitStack.size() > 1 && visitStack.get(visitStack.size() - 2).equals(vertex);
    }

    public void exploreBlack(Short vertex) {
      return;
    }

    public void endVertex(Short vertex) {
      setVertexStatus(vertex, VertexStatus.Black);
      visitStack.pop();
      Short active = getActiveVert();
      if (active == null) {
        return;
      }
      upTime[active] = (short) Math.min(upTime[active], upTime[vertex]);
      if (upTime[vertex] > inTime[active]) {
        // bridge located
        passBridge();
      }
      pushMemory();
    }

    public boolean isFinished() {
      return (lastCheckedIndex == vertexStatuses.size() - 1 && vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black);
    }

    private void passBridge() {
      // correlates with appropriate vertex index in stack
      int depth = visitStack.size();
      if (bridgeMemories.empty() || bridgeMemories.peek().stackDepth < depth) {
        bridgeMemories.add(new BridgeMemory(depth));
      } else {
        bridgeMemories.peek().merged = false;
      }
    }

    private void pushMemory() {
      int depth = visitStack.size();
      if (bridgeMemories.size() > 1 && bridgeMemories.get(bridgeMemories.size() - 2).stackDepth == depth - 1) {
        bridgeMemories.get(bridgeMemories.size() - 2).swallow(bridgeMemories.pop());
      } else if (!bridgeMemories.isEmpty() && bridgeMemories.peek().stackDepth == depth) {
        bridgeMemories.peek().stackDepth--;
      }
    }

    public int calculateAnswer() {
      return bridgeMemories.isEmpty() ?
          0 :
          (
              Math.ceilDiv(
                  (bridgeMemories.peek().counter +
                      (bridgeMemories.peek().merged ? 0 : 1))
                  , 2)
          );
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    short vertexes = (short) in.nextInt();
    int edges = in.nextInt();
    Graph graph = new Graph(vertexes);
    for (int i = 0; i < edges; i++) {
      graph.addEdge(new Edge((short) (in.nextInt() - 1), (short) (in.nextInt() - 1)), false);
    }
    DoubleConExplorer doubleConExplorer = new DoubleConExplorer();
    DFS dfs = new DFS(graph, doubleConExplorer);
    while (!doubleConExplorer.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(doubleConExplorer.calculateAnswer());
  }
}
