package Task07.Task07_G;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int vertexNum = in.nextInt();
    int requestNum = in.nextInt();

    Graph graph = new Graph(vertexNum);
    for (int i = 1; i < vertexNum; i++) {
      graph.addEdge(new Edge(i, in.nextInt()));
    }

    long a1 = in.nextInt();
    long a2 = in.nextInt();
    long x = in.nextInt();
    long y = in.nextInt();
    long z = in.nextInt();

    LCA lca = new LCA(graph);

    int prevAnswer = 0;
    long counter = 0;
    for (int i = 0; i < requestNum; i++) {
      prevAnswer = lca.getLCA((int) ((a1 + prevAnswer) % vertexNum), (int) a2);
      counter += prevAnswer;
      a1 = (x * a1 + y * a2 + z) % vertexNum;
      a2 = (x * a2 + y * a1 + z) % vertexNum;
    }
    System.out.println(counter);
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

class Edge {
  public int from;
  public int to;

  public Edge(int from, int to) {
    this.from = from;
    this.to = to;
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
    connectionList[edge.to].add(new Edge(edge.to, edge.from));
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

  public LCA(Graph graph) {
    this.graph = graph;
    rootIndex = 0;
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

  private int getHeight(int index) {
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
