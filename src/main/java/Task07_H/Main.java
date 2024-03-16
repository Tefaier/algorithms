package Task07_H;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

class LCA {
  private class LCAVertex {
    private LCAVertex parent;
    private int depth;
    private int weightFromRoot;
    private int vertIndex;
    private int indexInDfs;

    public LCAVertex(LCAVertex parent, int depth, int weightFromRoot, int vertIndex) {
      this.parent = parent;
      this.depth = depth;
      this.weightFromRoot = weightFromRoot;
      this.vertIndex = vertIndex;
      this.indexInDfs = -1;
    }
  }

  private LCAVertex[] references;
  private ArrayList<Integer> dfsList = new ArrayList<>();
  private int[] treeList;
  private Graph graph;
  private int rootIndex;

  public LCA(Graph graph, int rootIndex) {
    this.rootIndex = rootIndex;
    this.graph = graph;
    buildLCA();
  }

  private void buildLCA() {
    references = new LCAVertex[graph.getVertexCount()];
    buildDFS();
    treeList = new int[dfsList.size() * 4 + 1];
    Arrays.fill(treeList, -1);
    buildTree(1, 0, dfsList.size() - 1);

    for (int i = 0; i < dfsList.size(); i++) {
      int index = dfsList.get(i);
      if (references[index].indexInDfs == -1) references[index].indexInDfs = i;
    }
  }

  private void buildDFS() {
    int[] visitedStory = new int[graph.getVertexCount()];
    boolean[] used = new boolean[graph.getVertexCount()];
    Stack<Integer> stack = new Stack<>();

    stack.add(rootIndex);
    dfsList.add(rootIndex);
    used[rootIndex] = true;
    references[rootIndex] = new LCAVertex(null, 0, 0, rootIndex);
    while (!stack.empty()) {
      int cur = stack.peek();
      while (visitedStory[cur] < graph.getEdges(cur).size()) {
        Edge edge = graph.getEdges(cur).get(visitedStory[cur]);
        ++visitedStory[cur];
        if (!used[edge.to]) {
          LCAVertex from = references[edge.from];
          stack.add(edge.to);
          dfsList.add(edge.to);
          used[edge.to] = true;
          references[edge.to] = new LCAVertex(from, from.depth + 1, from.weightFromRoot + 1, edge.to);
          break;
        }
      }
      if (cur == stack.peek()) {
        stack.pop();
        if (!stack.isEmpty()) dfsList.add(stack.peek());
      }
    }
  }

  private void buildTree(int index, int left, int right) {
    if (left == right)
      // treeList contains indexes of vertex with minimum height on its region
      treeList[index] = dfsList.get(left);
    else {
      int middle = (left + right) >> 1;
      buildTree(index * 2, left, middle);
      buildTree(index * 2 + 1, middle + 1, right);
      if (getHeight(treeList[index * 2]) < getHeight(treeList[index * 2 + 1]))
        treeList[index] = treeList[index * 2];
      else
        treeList[index] = treeList[index * 2 + 1];
    }
  }

  public int getHeight(int index) {
    return references[index].depth;
  }

  private int getDFSindex(int vertex) {
    return references[vertex].indexInDfs;
  }

  public int getDistance(int v1, int v2) {
    if (v1 == v2) {
      return 0;
    }
    return references[v1].depth + references[v2].depth - 2 * references[getLCA(v1, v2)].depth;
  }

  public int getLCA(int v1, int v2) {
    int left = getDFSindex(v1);
    int right = getDFSindex(v2);
    return minByTree(1, 0, dfsList.size() - 1, Math.min(left, right), Math.max(left, right));
  }

  private int minByTree(int index, int currentLeft, int currentRight, int requestLeft, int requestRight) {
    if (currentLeft == requestLeft && currentRight == requestRight)
      return treeList[index];
    int middle = (currentLeft + currentRight) >> 1;
    // go left
    if (requestRight <= middle)
      return minByTree(index * 2, currentLeft, middle, requestLeft, requestRight);
    // go right
    if (requestLeft > middle)
      return minByTree(index * 2 + 1, middle + 1, currentRight, requestLeft, requestRight);
    // split
    int ans1 = minByTree(index * 2, currentLeft, middle, requestLeft, middle);
    int ans2 = minByTree(index * 2 + 1, middle + 1, currentRight, middle + 1, requestRight);
    // priority on minimum height
    return getHeight(ans1) < getHeight(ans2) ? ans1 : ans2;
  }
}

class DSU {
  private int size;
  private int active;

  private int[] linkList;
  private int[] sizeList;

  public DSU(int size) {
    this.size = size;
    active = size;
    linkList = new int[size];
    sizeList = new int[size];
    for (int i = 0; i < size; i++) {
      linkList[i] = i;
      sizeList[i] = 1;
    }
  }

  public int compNumber() {
    return active;
  }

  public int find(int v) {
    return goToRoot(v);
  }

  // returns true if they were in one component
  public boolean union(int v1, int v2) {
    int part1 = find(v1);
    int part2 = find(v2);

    if (part1 == part2) {
      return true;
    } else {
      if (sizeList[part1] < sizeList[part2])
        mergeParts(part1, part2);
      else
        mergeParts(part2, part1);
      return false;
    }
  }

  private int goToRoot(int v) {
    int active = v;
    ArrayList<Integer> visited = new ArrayList<>();
    while (active != linkList[active]) {
      visited.add(active);
      active = linkList[active];
    }

    for (int i = 0; i < visited.size() - 1; i++) {
      linkList[visited.get(i)] = active;
    }

    return active;
  }

  private void mergeParts(int from, int to) {
    linkList[from] = to;
    sizeList[to] += sizeList[from];
    active--;
  }
}

class Graph {
  public List<Edge> edges = new ArrayList<>();
  public List<List<Edge>> connectionList = new ArrayList<>();
  // vertexes serves as a backup from index to original information
  // however edges must use indexes of vertexes
  public int[] checkMemory;
  private List<List<Integer>> edgesMemory = new ArrayList<>();
  private int counter = 0;
  private int vertexCount;

  public Graph() {

  }

  public Graph(int vertexCount) {
    this.vertexCount = vertexCount;
    for (int i = 0; i < vertexCount; i++) {
      connectionList.add(new ArrayList<>());
      edgesMemory.add(new ArrayList<>());
    }
  }

  public int getVertexCount() {
    return vertexCount;
  }

  // use checkMemory - returns the edge that was looked last in the dfs from some vertex
  public Integer getEdgeIndex(int from) {
    return edgesMemory.get(from).get(checkMemory[from] - 1);
  }

  public List<Edge> getEdges(Integer vertex) {
    return connectionList.get(vertex);
  }

  public void addEdge(Edge edge, boolean orientated) {
    ++counter;
    edges.add(edge);
    connectionList.get(edge.from()).add(edge);
    edgesMemory.get(edge.from()).add(counter);
    if (!orientated) {
      Edge reversed = new Edge(edge.to, edge.from);
      edges.add(reversed);
      connectionList.get(reversed.from()).add(reversed);
      edgesMemory.get(reversed.from()).add(counter);
    }
  }
}

enum VertexStatus {White, Gray, Black}

class DFS<V, E extends GraphExplorer<V>> {
  public final Graph graph;
  public final E explorer;
  private int[] checkMemory;

  public DFS(Graph graph, E graphExplorer) {
    this.graph = graph;
    this.explorer = graphExplorer;
    this.checkMemory = new int[graph.getVertexCount()];
    Arrays.fill(this.checkMemory, 0);
    graph.checkMemory = this.checkMemory;
    initExplorer();
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
    Integer currentIndex;
    while ((currentIndex = explorer.getActiveVert()) != null) {
      if (explorer.isFinished()) {
        break;
      }

      List<Edge> connected = graph.getEdges(currentIndex);
      while (checkMemory[currentIndex] < connected.size()) {
        int next = connected.get(checkMemory[currentIndex]).to;
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

interface GraphExplorer<V> {
  public Integer getActiveVert();

  public Integer getRandomUnexplored();

  public void prepareGraph(Graph graph);

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

class Edge {
  int from;
  int to;
  boolean isABridge = false;

  public Edge(int from, int to) {
    this.from = from;
    this.to = to;
  }

  public int from() {
    return from;
  }

  public int to() {
    return to;
  }
}

class BridgeExplorer<V> implements GraphExplorer<V> {
  public List<Integer> bridgesIndexes = new ArrayList<>();
  //private Set<Integer> bridgesBlackList = new HashSet<>();

  private Graph graph;
  private Stack<Integer> visitStack = new Stack<>();
  private Stack<VertexStatus> statusStack = new Stack<>();

  // state of each index
  private int[] inTime;
  private int[] upTime;
  private List<VertexStatus> vertexStatuses = new ArrayList<>();
  private Integer lastCheckedIndex = -1;
  private int timer = 0;

  @Override
  public Integer getActiveVert() {
    return visitStack.isEmpty() ? null : visitStack.peek();
  }

  @Override
  public Integer getRandomUnexplored() {
    for (int i = (lastCheckedIndex + 1); i < vertexStatuses.size(); i++) {
      if (vertexStatuses.get(i) == VertexStatus.White) {
        lastCheckedIndex = i;
        return i;
      }
    }
    lastCheckedIndex = (vertexStatuses.size() - 1);
    return null;
  }

  @Override
  public void prepareGraph(Graph graph) {
    this.graph = graph;
    for (int i = 0; i < graph.getVertexCount(); i++) {
      vertexStatuses.add(VertexStatus.White);
    }
    inTime = new int[graph.getVertexCount()];
    upTime = new int[graph.getVertexCount()];
    Arrays.fill(inTime, 0);
    Arrays.fill(upTime, 0);
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
    exploreWhite(vertex);
    setVertexStatus(vertex, VertexStatus.Gray);
  }

  @Override
  public void finishExploring(Integer vertex) {
    return;
  }

  @Override
  public void exploreWhite(Integer vertex) {
    visitStack.add(vertex);
    statusStack.add(VertexStatus.White);
    ++timer;
    inTime[vertex] = timer;
    upTime[vertex] = timer;
  }

  @Override
  public void exploreGray(Integer vertex) {
    if (visitStack.size() > 1 && Objects.equals(vertex, visitStack.elementAt(visitStack.size() - 2))) {
      incrementStatus();
      return;
    }
    upTime[getActiveVert()] = Math.min(upTime[getActiveVert()], inTime[vertex]);
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
  public void exploreBlack(Integer vertex) {
    return;
    // upTime[getActiveVert()] =  Math.min(upTime[getActiveVert()], inTime[vertex]);
  }

  @Override
  public void endVertex(Integer vertex) {
    visitStack.pop();
    VertexStatus status = statusStack.pop();
    Integer active = getActiveVert();
    if (active == null) {
      return;
    }
    upTime[active] = Math.min(upTime[active], upTime[vertex]);
    if (upTime[vertex] > inTime[active] && status == VertexStatus.Gray) {
      int bridgeIndex = graph.getEdgeIndex(active);
      bridgesIndexes.add(bridgeIndex - 1);
    }
  }

  @Override
  public boolean isFinished() {
    return
        (lastCheckedIndex == vertexStatuses.size() - 1 &&
            vertexStatuses.get(lastCheckedIndex) == VertexStatus.Black);
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

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    int safeRoom = in.nextInt() - 1;
    Graph graph = new Graph(vertexes);
    for (int i = 0; i < edges; i++) {
      graph.addEdge(new Edge((in.nextInt() - 1), (in.nextInt() - 1)), false);
    }
    BridgeExplorer<Integer> bridgeExplorer = new BridgeExplorer<>();
    DFS<Integer, BridgeExplorer<Integer>> dfs = new DFS<>(graph, bridgeExplorer);
    dfs.startDFS();

    for (int i = 0; i < bridgeExplorer.bridgesIndexes.size(); i++) {
      graph.edges.get(bridgeExplorer.bridgesIndexes.get(i) * 2).isABridge = true;
      graph.edges.get(bridgeExplorer.bridgesIndexes.get(i) * 2 + 1).isABridge = true;
    }

    DSU dsu = new DSU(vertexes);

    int[] visitedStory = new int[vertexes];
    boolean[] used = new boolean[vertexes];
    Stack<Integer> stack = new Stack<>();

    stack.add(0);
    used[0] = true;
    while (!stack.empty()) {
      int cur = stack.peek();
      while (visitedStory[cur] < graph.getEdges(cur).size()) {
        Edge edge = graph.getEdges(cur).get(visitedStory[cur]);
        ++visitedStory[cur];
        if (!used[edge.to]) {
          stack.add(edge.to);
          used[edge.to] = true;
          if (!edge.isABridge) {
            dsu.union(edge.from, edge.to);
          }
          break;
        }
      }
      if (cur == stack.peek()) {
        stack.pop();
      }
    }

    int[] vertProxy = new int[vertexes];
    HashMap<Integer, Integer> seenComp = new HashMap<>();
    int counter = 0;
    for (int i = 0; i < vertexes; i++) {
      int comp = dsu.find(i);
      if (seenComp.containsKey(comp)) {
        vertProxy[i] = seenComp.get(comp);
        continue;
      }
      seenComp.put(comp, counter);
      vertProxy[i] = counter;
      counter++;
    }

    Graph mergedGraph = new Graph(counter);
    for (int i = 0; i < bridgeExplorer.bridgesIndexes.size(); i++) {
      Edge from = graph.edges.get(bridgeExplorer.bridgesIndexes.get(i) * 2);
      mergedGraph.addEdge(new Edge(vertProxy[from.from], vertProxy[from.to]), false);
    }

    LCA lca = new LCA(mergedGraph, vertProxy[safeRoom]);

    int requests = in.nextInt();
    for (int i = 0; i < requests; i++) {
      System.out.println(lca.getHeight(lca.getLCA(vertProxy[in.nextInt() - 1], vertProxy[in.nextInt() - 1])));
    }
  }
}

