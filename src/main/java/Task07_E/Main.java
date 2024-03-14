package Task07_E;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

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

class Edge implements Comparable<Edge> {
  public int from;
  public int to;
  public int weight;

  public Edge(int from, int to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  @Override
  public int compareTo(Edge o) {
    return o.weight - weight;
  }
}

class Graph {
  private int vertexCount;
  private ArrayList<Edge>[] connectionList;

  public Graph(int vertexCount) {
    this.vertexCount = vertexCount;
    connectionList = new ArrayList[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      connectionList[i] = new ArrayList<>();
    }
  }

  public int getVertexCount() {
    return vertexCount;
  }

  public void addEdge(Edge edge) {
    connectionList[edge.from].add(edge);
    connectionList[edge.to].add(new Edge(edge.to, edge.from, edge.weight));
  }

  public List<Edge> getEdges(int from) {
    return connectionList[from];
  }
}

class LCA {
  private class LCAVertex {
    private LCAVertex parent;
    private int depth;
    private int weightFromRoot;
    private int vertIndex;

    public LCAVertex(LCAVertex parent, int depth, int weightFromRoot, int vertIndex) {
      this.parent = parent;
      this.depth = depth;
      this.weightFromRoot = weightFromRoot;
      this.vertIndex = vertIndex;
    }
  }

  private LCAVertex[] references;
  private Graph graph;
  private int rootIndex;

  public LCA(Graph graph) {
    this.graph = graph;
    this.references = new LCAVertex[graph.getVertexCount()];
    buildLCA();
  }

  private void buildLCA() {
    rootIndex = 0;
    references[0] = new LCAVertex(null, 0, 0, 0);

    Stack<Integer> stack = new Stack<>();
    stack.add(0);
    int[] visitedStory = new int[graph.getVertexCount()];
    boolean[] used = new boolean[graph.getVertexCount()];
    used[0] = true;
    while (!stack.empty()) {
      int cur = stack.peek();
      while (visitedStory[cur] < graph.getEdges(cur).size()) {
        Edge edge = graph.getEdges(cur).get(visitedStory[cur]);
        ++visitedStory[cur];
        if (!used[edge.to]) {
          used[edge.to] = true;
          stack.add(edge.to);
          LCAVertex from = references[edge.from];
          references[edge.to] = new LCAVertex(from, from.depth + 1, from.weightFromRoot + edge.weight, edge.to);
          break;
        }
      }
      if (cur == stack.peek()) {
        stack.pop();
      }
    }
  }

  public int getMinInPath(int v1, int v2) {
    int min = Integer.MAX_VALUE;
    LCAVertex vLCA1 = references[v1];
    LCAVertex vLCA2 = references[v2];

    // move to same depth
    if (vLCA1.depth > vLCA2.depth) {
      while (vLCA1.depth != vLCA2.depth) {
        min = Math.min(min, vLCA1.weightFromRoot - vLCA1.parent.weightFromRoot);
        vLCA1 = vLCA1.parent;
      }
    }
    if (vLCA2.depth > vLCA1.depth) {
      while (vLCA2.depth != vLCA1.depth) {
        min = Math.min(min, vLCA2.weightFromRoot - vLCA2.parent.weightFromRoot);
        vLCA2 = vLCA2.parent;
      }
    }

    // move together
    while (vLCA1 != vLCA2) {
      min = Math.min(min, vLCA1.weightFromRoot - vLCA1.parent.weightFromRoot);
      vLCA1 = vLCA1.parent;
      min = Math.min(min, vLCA2.weightFromRoot - vLCA2.parent.weightFromRoot);
      vLCA2 = vLCA2.parent;
    }

    return min;
  }

  /*
  public int getDistance(int v1, int v2) {
    return references[v1].weightFromRoot + references[v2].weightFromRoot - references[getLCA(v1, v2)].weightFromRoot;
  }

  public int getLCA(int v1, int v2) {
    LCAVertex vLCA1 = references[v1];
    LCAVertex vLCA2 = references[v2];
    if (vLCA1.depth > vLCA2.depth) vLCA1 = moveUp(vLCA1, vLCA2.depth);
    if (vLCA2.depth > vLCA1.depth) vLCA2 = moveUp(vLCA2, vLCA1.depth);
    while (vLCA1 != vLCA2) {
      vLCA1 = vLCA1.parent;
      vLCA2 = vLCA2.parent;
    }
    return vLCA1.vertIndex;
  }

  private LCAVertex moveUp(LCAVertex v, int toDepth) {
    LCAVertex current = v;
    while (current.depth != toDepth) {
      current = current.parent;
    }
    return current;
  }
   */
}

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int vertexNum = in.nextInt();
    int edgeNum = in.nextInt();
    int requestNum = in.nextInt();

    PriorityQueue<Edge> sortedEdges = new PriorityQueue<>();
    for (int i = 0; i < edgeNum; i++) {
      sortedEdges.add(new Edge(in.nextInt() - 1, in.nextInt() - 1, in.nextInt()));
    }
    // priority is for edges of higher weight

    Graph graph = new Graph(vertexNum);
    DSU dsu = new DSU(vertexNum);
    int weightCounter = 0;

    while (dsu.compNumber() > 1) {
      Edge currentEdge = sortedEdges.poll();
      if (!dsu.union(currentEdge.from, currentEdge.to)) {
        graph.addEdge(currentEdge);
      }
    }

    LCA lca = new LCA(graph);

    for (int i = 0; i < requestNum; i++) {
      System.out.println(lca.getMinInPath(in.nextInt() - 1, in.nextInt() - 1));
    }
  }
}

