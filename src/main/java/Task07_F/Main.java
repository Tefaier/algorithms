package Task07_F;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int vertexNum = in.nextInt();

    UnorderedGraph<Integer, SimpleEdge<Integer>> graph = new UnorderedGraph<>(IntStream.range(1, vertexNum + 1).boxed().toList(), new ArrayList<>());
    for (int i = 0; i < vertexNum - 1; i++) {
      graph.addEdge(new SimpleEdge<>(in.nextInt(), in.nextInt()));
    }

    LCA<Integer> lca = new LCA<>(graph);

    int requestNum = in.nextInt();
    for (int i = 0; i < requestNum; i++) {
      System.out.println(lca.getDistance(in.nextInt(), in.nextInt()));
    }
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

interface Edge<V> {
  V getFrom();

  V getTo();

  <E extends Edge<V>> E reversed();
}

class SimpleEdge<V> implements Edge<V> {
  private V from;
  private V to;

  public SimpleEdge(V from, V to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public V getFrom() {
    return from;
  }

  @Override
  public V getTo() {
    return to;
  }

  @Override
  public SimpleEdge<V> reversed() {
    return new SimpleEdge<>(getTo(), getFrom());
  }
}

class Graph<V, E extends Edge<V>> {
  protected int edgesNum = 0;
  protected List<V> vertices;
  protected HashMap<V, List<E>> edgesMap = new HashMap<>();

  public Graph(List<V> vertices, List<E> edges) {
    this.vertices = vertices;
    for (E edge : edges) {
      ++edgesNum;
      edgesMap.putIfAbsent(edge.getFrom(), new ArrayList<>());
      edgesMap.get(edge.getFrom()).add(edge);
    }
  }

  public void addEdge(E edge) {
    ++edgesNum;
    edgesMap.putIfAbsent(edge.getFrom(), new ArrayList<>());
    edgesMap.get(edge.getFrom()).add(edge);
  }

  public List<E> getConnected(V vertex) {
    return edgesMap.getOrDefault(vertex, new ArrayList<>());
  }

  public int getVertexCount() {
    return vertices.size();
  }

  public int getEdgeCount() {
    return edgesNum;
  }

  public List<V> getVertices() {
    return vertices;
  }
}

class UnorderedGraph<V, E extends Edge<V>> extends Graph<V, E> {
  public UnorderedGraph(List<V> vertices, List<E> edges) {
    super(vertices, edges);
    for (E edge : edges) {
      edgesMap.putIfAbsent(edge.getTo(), new ArrayList<>());
      edgesMap.get(edge.getTo()).add(edge.reversed());
    }
  }

  @Override
  public void addEdge(E edge) {
    super.addEdge(edge);
    edgesMap.putIfAbsent(edge.getTo(), new ArrayList<>());
    edgesMap.get(edge.getTo()).add(edge.reversed());
  }
}

class LCA<V> {
  private class LCAVertex<V> {
    private LCAVertex<V> parent;
    private int depth;
    private int weightFromRoot;
    private int vertIndex;
    private int indexInDfs;
    public V value;

    public LCAVertex(LCAVertex<V> parent, int depth, int weightFromRoot, int vertIndex, V value) {
      this.parent = parent;
      this.depth = depth;
      this.weightFromRoot = weightFromRoot;
      this.vertIndex = vertIndex;
      this.indexInDfs = -1;
      this.value = value;
    }
  }

  private LCAVertex<V>[] references;
  private ArrayList<Integer> dfsList = new ArrayList<>();
  private int[] treeList;
  private Graph<V, ?> graph;
  private int rootIndex;

  private HashMap<V, Integer> positionMap = new HashMap<>();

  public LCA(Graph<V, ?> graph) {
    this.graph = graph;
    for (int i = 0; i < graph.getVertexCount(); i++) {
      positionMap.put(graph.vertices.get(i), i);
    }
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
    V randomElement = graph.getVertices().get(new Random().nextInt(graph.getVertexCount()));
    rootIndex = positionMap.get(randomElement);
    int[] visitedStory = new int[graph.getVertexCount()];
    boolean[] used = new boolean[graph.getVertexCount()];
    Stack<Integer> stack = new Stack<>();

    stack.add(rootIndex);
    dfsList.add(rootIndex);
    used[rootIndex] = true;
    references[rootIndex] = new LCAVertex<>(null, 0, 0, rootIndex, randomElement);
    while (!stack.empty()) {
      int current = stack.peek();
      while (visitedStory[current] < graph.getConnected(references[current].value).size()) {
        Edge<V> edge = graph.getConnected(references[current].value).get(visitedStory[current]);
        ++visitedStory[current];
        int toIndex = positionMap.get(edge.getTo());
        if (!used[toIndex]) {
          LCAVertex<V> from = references[positionMap.get(edge.getFrom())];
          stack.add(positionMap.get(edge.getTo()));
          dfsList.add(positionMap.get(edge.getTo()));
          used[toIndex] = true;
          references[toIndex] = new LCAVertex<>(from, from.depth + 1, from.weightFromRoot + 1, toIndex, edge.getTo());
          break;
        }
      }
      if (current == stack.peek()) {
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

  public int getDistance(V v1, V v2) {
    int index1 = positionMap.get(v1);
    int index2 = positionMap.get(v2);
    if (index1 == index2) {
      return 0;
    }
    return references[index1].depth + references[index2].depth - 2 * references[getLCA(index1, index2)].depth;
  }

  private int getLCA(int v1, int v2) {
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
