package Task05_F;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
    public final articExplorer explorer;
    private short[] checkMemory;

    public DFS(Graph graph, articExplorer graphExplorer) {
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
            explorer.setVertexStatus(next, VertexStatus.Gray);
            explorer.exploreWhite(next);
            break;
          } else if (status == VertexStatus.Gray) {
            explorer.exploreGray(next);
          } else {
            explorer.exploreBlack(next);
          }
        }

        if (isLast(currentIndex)) {
          explorer.setVertexStatus(currentIndex, VertexStatus.Black);
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

  static class articExplorer {
    public Set<Short> artPoints = new HashSet<>();
    //private Set<Integer> bridgesBlackList = new HashSet<>();

    private Graph graph;

    // state of each index
    private short timer = 0;
    private short[] inTime;
    private short[] upTime;

    private Stack<Short> visitStack = new Stack<>();
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Short lastCheckedIndex = -1;

    private Short sourceVert;
    private boolean secondWhite;

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
      sourceVert = vertex;
      secondWhite = false;
      exploreWhite(vertex);
    }

    public void finishExploring(Short vertex) {
      return;
    }

    public void exploreWhite(Short vertex) {
      setVertexStatus(vertex, VertexStatus.Gray);

      if (sourceVert.equals(getActiveVert()) && secondWhite) {
        artPoints.add((short) (sourceVert + 1));
      }
      visitStack.add(vertex);

      ++timer;
      inTime[vertex] = timer;
      upTime[vertex] = timer;
    }

    public void exploreGray(Short vertex) {
      upTime[getActiveVert()] = (short) Math.min(upTime[getActiveVert()], inTime[vertex]);
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
      if (active.equals(sourceVert)) {
        // back to source
        secondWhite = true;
      } else if (upTime[vertex] >= inTime[active]) {
        // back not to source and passed
        artPoints.add((short) (active + 1));
      }
    }

    public boolean isFinished() {
      return (lastCheckedIndex == vertexStatuses.size() - 1 && vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black);
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
    articExplorer articExplorer = new articExplorer();
    DFS dfs = new DFS(graph, articExplorer);
    while (!articExplorer.isFinished()) {
      dfs.startDFS();
    }
    System.out.println(articExplorer.artPoints.size());
    System.out.print(articExplorer.artPoints.stream().sorted().map(Objects::toString).collect(Collectors.joining("\n")));
  }
}
