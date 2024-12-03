package Task12.Task12_E;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int nodesNum = in.nextInt();
    List<Integer> nodes = IntStream.range(1, nodesNum + 1).boxed().toList();
    List<SimpleEdge<Integer>> edges1 = new ArrayList<>();
    for (int i = 0; i < nodesNum - 1; i++) {
      edges1.add(new SimpleEdge<>(in.nextInt(), in.nextInt()));
    }
    List<SimpleEdge<Integer>> edges2 = new ArrayList<>();
    for (int i = 0; i < nodesNum - 1; i++) {
      edges2.add(new SimpleEdge<>(in.nextInt(), in.nextInt()));
    }
    DecodableTree<Integer, SimpleEdge<Integer>> tree1 =
        new DecodableTree<>(nodes, edges1, nodesNum);
    DecodableTree<Integer, SimpleEdge<Integer>> tree2 =
        new DecodableTree<>(nodes, edges2, nodesNum);
    List<Integer> pruferCode1 = tree1.getPruferCode();
    List<Integer> pruferCode2 = tree2.getPruferCode();

    HashMap<Integer, Integer> namesChange = new HashMap<>();
    HashSet<Integer> notMentioned1 = new HashSet<>(nodes);
    HashSet<Integer> notMentioned2 = new HashSet<>(nodes);
    boolean passed = true;
    for (int i = 0; i < nodesNum - 2; i++) {
      Integer from = pruferCode2.get(i);
      Integer to = pruferCode1.get(i);
      notMentioned2.remove(from);
      notMentioned1.remove(to);
      Integer expected = namesChange.get(from);
      if (expected == null) {
        namesChange.put(from, to);
      } else if (!expected.equals(to)) {
        passed = false;
        break;
      }
    }

    if (!passed) {
      System.out.println(-1);
      return;
    }

    StringBuilder answer = new StringBuilder();
    for (int i = 1; i <= nodesNum; i++) {
      Integer converted = namesChange.get(i);
      if (converted == null) {
        converted = notMentioned1.stream().findAny().get();
        notMentioned1.remove(converted);
      }
      answer.append(converted).append("\n");
    }

    System.out.println(answer);
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

class Tree<V, E extends Edge<V>> extends Graph<V, E> {
  protected final V root;

  public Tree(List<V> vertices, List<E> edges, V root) {
    super(vertices, edges);
    this.root = root;
  }

  public V getRoot() {
    return root;
  }
}

class UnorderedTree<V, E extends Edge<V>> extends Tree<V, E> {
  public UnorderedTree(List<V> vertices, List<E> edges, V root) {
    super(vertices, edges, root);
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

class DecodableTree<V extends Comparable<V>, E extends Edge<V>> extends UnorderedTree<V, E> {
  public DecodableTree(List<V> vertices, List<E> edges, V root) {
    super(vertices, edges, root);
  }

  // root must be the biggest value
  public List<V> getPruferCode() {
    List<V> code = new ArrayList<>();
    pruferDFSInfo info = parentPruferDFS();
    PriorityQueue<V> queue = new PriorityQueue<>(info.leaves);
    for (int i = 0; i < vertices.size() - 2; i++) {
      V currentLeaf = queue.poll();
      V parent = info.parentsInfo.get(currentLeaf);
      code.add(parent);

      info.activeConnected.compute(parent, (k, v) -> v - 1);
      if (info.activeConnected.get(parent) == 1) {
        queue.add(parent);
      }
    }

    return code;
  }

  class pruferDFSInfo {
    public HashMap<V, V> parentsInfo;
    public HashMap<V, Integer> activeConnected;
    public List<V> leaves;

    public pruferDFSInfo(
        HashMap<V, V> parentsInfo, HashMap<V, Integer> activeConnected, List<V> leaves) {
      this.parentsInfo = parentsInfo;
      this.activeConnected = activeConnected;
      this.leaves = leaves;
    }
  }

  private pruferDFSInfo parentPruferDFS() {
    HashMap<V, V> parentsInfo = new HashMap<>();
    HashMap<V, Integer> activeConnected = new HashMap<>();
    List<V> leaves = new ArrayList<>();
    parentPruferDFSIteration(root, null, parentsInfo, activeConnected, leaves);

    return new pruferDFSInfo(parentsInfo, activeConnected, leaves);
  }

  private void parentPruferDFSIteration(
      V from,
      V traceBack,
      HashMap<V, V> parentsInfo,
      HashMap<V, Integer> activeConnected,
      List<V> leaves) {
    var edges = edgesMap.getOrDefault(from, new ArrayList<>());
    for (E edge : edges) {
      if (edge.getTo() == traceBack) continue;
      parentsInfo.put(edge.getTo(), from);
      activeConnected.put(from, edges.size());
      parentPruferDFSIteration(edge.getTo(), from, parentsInfo, activeConnected, leaves);
    }
    if (edges.size() == 1) leaves.add(from);
  }
}

// https://habr.com/ru/articles/91283/
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
