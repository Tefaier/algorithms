import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class DecTree {
  private static Random random = new Random();

  private Node root = null;

  private static class Node {
    int value = 0;
    public Node left = null;
    public Node right = null;
    private long priority;

    public Node(int value) {
      this.value = value;
      this.priority = random.nextLong();
    }
  }

  private static class Pair {
    Node first = null;
    Node second = null;

    public Pair(Node first, Node second) {
      this.first = first;
      this.second = second;
    }
  }

  private Pair split(Node node, int key) {
    if (node == null) {
      return new Pair(null, null);
    }
    // replace Node children so that all are in one side to the key
    // and another element in Pair is on the other side ALL
    // works by value (as a search tree)
    if (node.value > key) {
      Pair pair = split(node.left, key);
      node.left = pair.second;
      return new Pair(pair.first, node);
    } else {
      Pair pair = split(node.right, key);
      node.right = pair.first;
      return new Pair(node, pair.second);
    }
  }

  // less and bigger in terms of value
  private Node merge(Node less, Node bigger) {
    if (less == null) {
      return bigger;
    }
    if (bigger == null) {
      return less;
    }
    // works by priority (as a binary heap)
    if (less.priority > bigger.priority) {
      less.right = merge(less.right, bigger);
      return less;
    } else {
      bigger.left = merge(less, bigger.left);
      return bigger;
    }
  }

  public void insert(int value) {
    Node node = new Node(value);
    Pair pair = split(root, value);
    root = merge(merge(pair.first, node), pair.second);
  }

  private void print(Node node) {
    int leftValue = -1;
    int rightValue = -1;
    if (node.left != null) {
      print(node.left);
      leftValue = node.left.value;
    }
    if (node.right != null) {
      print(node.right);
      rightValue = node.right.value;
    }
    System.out.println(node.value + " value|priority " + node.priority);
    System.out.println(leftValue + " left|right " + rightValue + "\n");
  }

  public void print() {
    print(root);
  }
}

//https://habr.com/ru/articles/91283/
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

class Heap<T extends Number & Comparable<T>> {
  ArrayList<T> arr;

  public Heap(ArrayList<T> arr) {
    this.arr = arr;
    heapify();
  }

  public Heap() {
    this.arr = new ArrayList<>();
  }

  private void siftUp(int index) {
    if (index == 0) return;
    int parent = (index - 1) / 2;
    if (arr.get(parent).compareTo(arr.get(index)) > 0) {
      T tmp = arr.get(parent);
      arr.set(parent, arr.get(index));
      arr.set(index, tmp);
      siftUp(parent);
    }
  }

  private void siftDown(int index) {
    int left = 2 * index + 1;
    int right = 2 * index + 2;
    if (right > arr.size()) return;

    if (right == arr.size()) {
      right = left;
    }

    int imin = arr.get(left).compareTo(arr.get(right)) < 0 ? left : right;
    if (arr.get(index).compareTo(arr.get(imin)) > 0) {
      T tmp = arr.get(imin);
      arr.set(imin, arr.get(index));
      arr.set(index, tmp);
      siftDown(imin);
    }
  }

  public void add(T value) {
    arr.add(value);
    siftUp(arr.size() - 1);
  }

  public T top() {
    return arr.get(0);
  }

  public T getMin() {
    T tmp = arr.get(0);
    arr.set(0, arr.get(arr.size() - 1));
    arr.remove(arr.size() - 1);
    siftDown(0);
    return tmp;
  }

  public boolean isEmpty() {
    return arr.size() == 0;
  }

  private void heapify() {
    for (int i = arr.size() / 2; i >= 0; i--) {
      siftDown(i);
    }
  }
}

class Tree {
  private Node root = null;
  private int size = 0;

  private static class Node {
    int value = 0;
    Node left = null;
    Node right = null;
    Node parent = null;

    public Node(int value) {
      this.value = value;
    }
  }

  public void add(int value) {
    if (root == null) {
      root = new Node(value);
    }
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value == value) {
        break;
      }
      if (tmp.value > value) {
        if (tmp.left == null) {
          tmp.left = new Node(value);
          size++;
        }
        tmp = tmp.left;
      } else {
        if (tmp.right == null) {
          tmp.right = new Node(value);
          size++;
        }
        tmp = tmp.right;
      }
    }
  }

  public boolean find(int value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value == value) {
        return true;
      }
      if (tmp.value > value) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return false;
  }

  public int size() {
    return size;
  }

  public void print() {
    print(root);
  }

  private void print(Node node) {
    if (node == null) return;
    print(node.left);
    System.out.print(node.value + " ");
    print(node.right);
  }
}

class SegmentTree {
  int[] arr;
  int[] tree;
  int size = 1;

  public SegmentTree(int[] array) {
    arr = array.clone();
    while (size < arr.length) {
      size *= 2;
    }
    tree = new int[size * 2];
    build(0, 0, size);
  }

  // left included, right excluded
  private void build(int v, int left, int right) {
    if (left + 1 == right) {
      // points at the leave node that covers one element from initial array
      if (v >= tree.length || left >= arr.length) {
        // out of range so the value that can't effect calculation of parent
        tree[v] = Integer.MAX_VALUE;
      } else {
        // inherit value from array
        tree[v] = arr[left];
      }
    } else {
      int mid = (left + right) / 2;
      build(2 * v + 1, left, mid);
      build(2 * v + 2, mid, right);

      // math function for this tree
      tree[v] = Math.min(tree[2 * v + 1], tree[2 * v + 2]);
    }
  }

  private int getMin(int v, int left, int right, int qLeft, int qRight) {
    if (right <= qLeft || qRight <= left) {
      return Integer.MAX_VALUE;
    } else if (qLeft <= left && right <= qRight) {
      return tree[v];
    } else {
      int mid = (left + right) / 2;
      return Math.min(getMin(2 * v + 1, left, mid, qLeft, qRight), getMin(2 * v + 2, mid, right, qLeft, qRight));
    }
  }

  public int getMin(int qLeft, int qRight) {
    return getMin(0, 0, size, qLeft, qRight);
  }

}

class TreeImplicit {
  public static Random random = new Random();

  private class Node {
    int value = 0;
    long priority = random.nextLong();
    Node left;
    Node right;
    int size = 1;

    public Node(int value) {
      this.value = value;
    }
  }


  private Node root;

  private static int size(Node node) {
    return node == null ? 0 : node.size;
  }

  private static void upDate(Node node) {
    if (node == null) return;
    node.size = size(node.left) + size(node.right) + 1;
  }

  private record Pair(Node first, Node second) {
  }

  private Pair split(Node node, int count) {
    if (node == null) {
      return new Pair(null, null);
    }
    if (size(node.left) + 1 <= count) {
      Pair q = split(node.right, count - size(node.left) - 1);
      node.right = q.first;
      upDate(node);
      upDate(q.second);
      return new Pair(node, q.second);
    } else {
      Pair q = split(node.left, count);
      node.left = q.second;
      upDate(node);
      upDate(q.first);
      return new Pair(q.first, node);
    }


  }

  private Node merge(Node less, Node bigger) {
    if (less == null) {
      return bigger;
    }
    if (bigger == null) {
      return less;
    }
    if (less.priority > bigger.priority) {
      less.right = merge(less.right, bigger);
      upDate(less);
      return less;
    } else {
      bigger.left = merge(less, bigger.left);
      upDate(bigger);
      return bigger;
    }

  }

  public Node ctrlx(int l, int r) {
    Pair q1 = split(root, r);
    Pair q2 = split(q1.first, l);
    root = merge(q2.first, q1.second);
    return q2.second;
  }

  public void ctrlv(Node node, int k) {
    Pair q = split(root, k);
    root = merge(q.first, merge(node, q.second));
  }

  public void moveToBegin(int l, int r) {
    Node tmp = ctrlx(l, r);
    ctrlv(tmp, 0);
  }

  public void insert(int value, int position) {
    ctrlv(new Node(value), position);
  }

  private void print(Node node) {
    if (node == null) {
      return;
    }
    print(node.left);
    System.out.print(node.value + " ");
    print(node.right);
  }

  public void print() {
    print(root);
  }


}

class FFTThing {
  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    int sizeOfPolinom1 = in.nextInt();
    long[] polinom1 = new long[sizeOfPolinom1 + 1];
    for (int i = 0; i <= sizeOfPolinom1; i++) {
      polinom1[i] = in.nextInt();
    }

    int sizeOfPolinom2 = in.nextInt();
    long[] polinom2 = new long[sizeOfPolinom2 + 1];
    for (int i = 0; i <= sizeOfPolinom2; i++) {
      polinom2[i] = in.nextInt();
    }

    Polinom parsedPolinom1 = new Polinom(polinom1);
    Polinom parsedPolinom2 = new Polinom(polinom2);

    Polinom multiplyPolinom = multiplyPolinomsInCoolWay(
        parsedPolinom1,
        parsedPolinom2
    );

    System.out.println("(" + parsedPolinom1 + ") * (" + parsedPolinom2 + ")" + " = " + multiplyPolinom);
  }

  public static Polinom multiplyPolinomsInCoolWay(Polinom polinom1, Polinom polinom2) {
    int size = 1;
    while (size < Math.max(
        polinom1.values.length,
        polinom2.values.length)
    ) size <<= 1;
    size <<= 1;

    Complex[] complexValues1 = new Complex[size];
    Complex[] complexValues2 = new Complex[size];
    for (int i = 0; i < size; i++) {
      complexValues1[i] = new Complex(i < polinom1.values.length ? polinom1.values[i] : 0, 0);
      complexValues2[i] = new Complex(i < polinom2.values.length ? polinom2.values[i] : 0, 0);
    }

    fft(complexValues1, false);
    fft(complexValues2, false);

    // simple values combination
    Complex[] combinesValues = new Complex[size];
    for (int i = 0; i < size; i++) {
      combinesValues[i] = complexValues1[i];
      combinesValues[i].multiply(complexValues2[i]);
    }

    fft(combinesValues, true);

    long[] multipliers = new long[
        polinom1.values.length
            + polinom2.values.length
            - 1];
    for (int i = 0; i < multipliers.length; i++) {
      multipliers[i] = Math.round(combinesValues[i].real);
    }

    return new Polinom(multipliers);
  }

  public static void fft(Complex[] values, boolean reversed) {
    int size = values.length;
    int sizeHalved = size / 2;
    if (size == 1) return;

    Complex[] even = new Complex[sizeHalved];
    for (int k = 0; k < sizeHalved; k++) {
      even[k] = values[2 * k];
    }

    Complex[] odd = new Complex[sizeHalved];
    for (int k = 0; k < sizeHalved; k++) {
      odd[k] = values[2 * k + 1];
    }

    fft(even, reversed);
    fft(odd, reversed);

    Complex w = new Complex(1, 0);
    double angleToAdd = 2 * Math.PI / size * (reversed ? -1 : 1);
    Complex wAdd = new Complex(Math.cos(angleToAdd), Math.sin(angleToAdd));
    for (int i = 0; i < sizeHalved; i++) {
      values[i] = new Complex(w.real, w.imaginary);
      values[i].multiply(odd[i]);
      values[i].add(even[i]);
      w.multiply(wAdd);
    }

    w = new Complex(1, 0);
    for (int i = sizeHalved; i < size; i++) {
      Complex val = new Complex(w.real, w.imaginary);
      val.multiply(odd[i - sizeHalved]);
      values[i] = new Complex(even[i - sizeHalved].real, even[i - sizeHalved].imaginary);
      values[i].substract(val);
      w.multiply(wAdd);
    }

    if (reversed) {
      for (int i = 0; i < size; i++) {
        values[i].simpleDivide(2);
      }
    }
  }
}

class Complex {
  public double real;
  public double imaginary;

  public Complex(double real, double imaginary) {
    this.real = real;
    this.imaginary = imaginary;
  }

  public void multiply(Complex z) {
    double newReal = real * z.real - imaginary * z.imaginary;
    double newImaginary = real * z.imaginary + imaginary * z.real;
    real = newReal;
    imaginary = newImaginary;
  }

  public void simpleDivide(double x) {
    real /= x;
    imaginary /= x;
  }

  public void add(Complex z) {
    real += z.real;
    imaginary += z.imaginary;
  }

  public void substract(Complex z) {
    real -= z.real;
    imaginary -= z.imaginary;
  }
}

class Polinom {
  public long[] values;

  public Polinom(long[] values) {
    this.values = values;
  }

  @Override
  public String toString() {
    String[] parts = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      parts[i] = values[i] + "*x^" + (values.length - i - 1);
    }
    return Arrays.stream(parts).collect(Collectors.joining(" + "));
  }
}

public class Algorithms {
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
      Integer vertex = explorer.getRandomUnexplored();
      if (vertex == null) {
        return;
      }
      explorer.startExploring(vertex);
      dfs(vertex);
      explorer.finishExploring(vertex);
    }

    public void startDFS(Integer vertex) {
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

  static interface GraphExplorer<V> {
    public Integer getRandomUnexplored();

    public void prepareGraph(Graph<V> graph);

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

    public void addVert(V vertex) {
      vertexes.add(vertex);
    }

    public void addEdge(Edge edge, boolean orientated) {
      connectionList.get(edge.from()).add(edge.to());
      if (!orientated) {
        connectionList.get(edge.to()).add(edge.from());
      }
    }
  }

  static record Edge(Integer from, Integer to) {
  }

  static class ClustersSearch<V> implements GraphExplorer<V> {
    public StringBuilder answer = new StringBuilder();
    public StringBuilder lastCluster;
    public int lastClusterSize = 0;
    public int clusterNumber = 0;

    private Graph<V> graph;

    // state of each index
    private List<VertexStatus> vertexStatuses = new ArrayList<>();
    private Integer lastCheckedIndex = -1;

    @Override
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

    @Override
    public void prepareGraph(Graph<V> graph) {
      this.graph = graph;
      for (int i = 0; i < graph.vertexes.size(); i++) {
        vertexStatuses.add(VertexStatus.White);
      }
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
      clusterNumber++;
      lastClusterSize = 0;
      lastCluster = new StringBuilder();
    }

    @Override
    public void finishExploring(Integer vertex) {
      answer.append(lastClusterSize).append('\n').append(lastCluster).append('\n');
    }

    @Override
    public void exploreWhite(Integer vertex) {
      lastClusterSize++;
      lastCluster.append(graph.vertexes.get(vertex)).append(" ");
    }

    @Override
    public void exploreGray(Integer vertex) {
      return;
    }

    @Override
    public void exploreBlack(Integer vertex) {
      return;
    }

    @Override
    public void endVertex(Integer vertex) {
      return;
    }

    @Override
    public boolean isFinished() {
      return lastCheckedIndex == vertexStatuses.size() - 1;
    }
  }

  public enum MathActions {
    Plus,
    Substract,
    Mult,
    Divide
  }

  public static long mod;

  public static int step = 10;

  public static Scanner in = new Scanner(System.in);

  public static boolean checkCoverage(int[] positions, int number, int length, boolean max) {
    int covered_coord = positions[0] + length;
    number--;
    for (int i = 1; i < positions.length; i++) {
      if (positions[i] > covered_coord) {
        covered_coord = positions[i] + length;
        number--;
        if (number < 0) {
          // to cover more segments when available needed
          return max;
        }
      }
    }
    return max ? number == 0 : true;
  }

  public static boolean checkCoverageWithBin(int[] positions, int number, int length) {
    int covered_coord = positions[0] + length;
    int location = -1;
    while (number > 0) {
      number--;
      location = binarySearch(location, positions.length, covered_coord, positions, false); // segment is used
      if (Math.abs(location) >= positions.length - (location >= 0 ? 1 : 0)) { // all are covered
        break;
      }
      covered_coord = positions[Math.abs(location) + (location >= 0 ? 1 : 0)] + length;
    }
    return Math.abs(location) >= positions.length - (location >= 0 ? 1 : 0);
  }

  public static int binarySearch(int key, int[] arr, boolean first) {
    return binarySearch(-1, arr.length, key, arr, first);
  }

  public static int binarySearch(int left, int right, int key, int[] arr, boolean first) {
    while (left + 1 < right) {
      int mid = (left + right) / 2;
      if ((first && arr[mid] < key) || (!first && arr[mid] <= key)) {
        left = mid;
      } else {
        right = mid;
      }
    }

    if (first) {
      if (right < arr.length && arr[right] == key) {
        return right;
      } else {
        return -1 - right;
      }
    } else {
      if (left >= 0 && arr[left] == key) {
        return left;
      } else {
        return -1 - left;
      }
    }
  }

  public static double binarySolution(Function<Double, Double> func, double min_v, double max_v, double eps) {
    if (eps == 0) {
      eps = 0.000001d;
    }
    double mid = 0;
    int act_times = (int) Math.log(Math.log(2) * (max_v - min_v) / eps);
    for (int i = 0; i < act_times; i++) {
      mid = (min_v + max_v) / 2;
      if (func.apply(min_v) * func.apply(mid) > 0) {
        min_v = mid;
      } else {
        max_v = mid;
      }
    }
    return mid;
  }

  public static ArrayList<Integer> primesFind(int max) {
    ArrayList<Integer> primes = new ArrayList<>();
    boolean[] located = new boolean[max];
    for (int i = 2; i < max; i++) {
      if (!located[i]) {
        primes.add(i);
        for (int j = i + i; j < max; j += i) {
          located[j] = true;
        }
      }
    }
    return primes;
  }

  public static ArrayList<Integer> breakOnPrimes(int number) {
    ArrayList<Integer> factors = new ArrayList<>();
    for (int i = 2; i * i <= number; i++) {
      if (number % i == 0) {
        number /= i;
        factors.add(i);
        i--;
      }
    }
    factors.add(number);
    return factors;
  }

  public static long calculateWithMod(long mod, long digit1, long digit2, MathActions action) {
    long ans = 0;
    Algorithms.mod = mod;
    switch (action) {
      case Plus -> {
        digit1 %= mod;
        digit2 %= mod;
        ans = (digit1 + digit2) % mod;
        return (ans + mod) % mod;
      }
      case Substract -> {
        digit1 %= mod;
        digit2 %= mod;
        ans = (digit1 - digit2) % mod;
        return (ans + mod) % mod;
      }
      case Mult -> {
        digit1 %= mod;
        digit2 %= mod;
        ans = (digit1 * digit2) % mod;
        return (ans + mod) % mod;
      }
      case Divide -> {
        return calculateWithMod(mod, digit1, binPow(digit2, mod - 2), MathActions.Mult);
      }
      default -> {
        return ans;
      }
    }
  }

  public static long calculateWithMod(long digit1, long digit2, MathActions action) {
    return calculateWithMod(mod, digit1, digit2, action);
  }

  public static long binPow(long value, long pow) {
    if (pow == 0) {
      return 1;
    }
    if (pow % 2 == 0) {
      return binPow(calculateWithMod(value, value, MathActions.Mult), pow / 2);
    }
    return calculateWithMod(
        binPow(calculateWithMod(value, value, MathActions.Mult), (pow - 1) / 2),
        value,
        MathActions.Mult);
  }

  public static int[] smallestPrimes(int max) {
    int[] delPrimes = new int[max + 1];
    for (int i = 0; i <= max; i++) {
      delPrimes[i] = i;
    }

    for (int i = 2; i <= max; i++) {
      for (int j = i + i; j <= max; j += i) {
        delPrimes[j] = Math.min(delPrimes[j], i);
      }
    }
    return delPrimes;
  }

  public static long sumAlg1(int[] primes) { // gives sum of smallest simple primes of not primes from 2 to N
    long ans = 0;
    for (int i = 2; i < primes.length; i++) {
      if (primes[i] != i) {
        ans += primes[i];
      }
    }
    return ans;
  }

  public static int findSmallest(int[] digits, int left, int right, int min) {
    int smallest = digits[left - 1];
    for (int i = 0; i < left - 1; i++) {
      smallest = Math.min(smallest, digits[i]);
      if (smallest == min) {
        return min;
      }
    }
    for (int i = right - 1; i < digits.length; i++) {
      smallest = Math.min(smallest, digits[i]);
      if (smallest == min) {
        return min;
      }
    }
    return smallest;
  }

  public static void TaskC_0() {
    System.out.println(binarySolution((value) -> value * value - 2, 0, 10, 0));
  }

  public static void Task00_1() {
    int candidats = in.nextInt();
    int[] speeds = new int[candidats];
    int[] minLeft = new int[candidats + 1];
    int[] minRight = new int[candidats + 1];
    minLeft[0] = Integer.MAX_VALUE;
    for (int i = 0; i < candidats; i++) {
      speeds[i] = in.nextInt();
      minLeft[i + 1] = Math.min(minLeft[i], speeds[i]);
    }
    minRight[0] = Integer.MAX_VALUE;
    for (int i = 0; i < candidats; i++) {
      minRight[i + 1] = Math.min(minRight[i], speeds[candidats - i - 1]);
    }
    int trials = in.nextInt();
    for (int i = 0; i < trials; i++) {
      System.out.println(Math.min(minLeft[in.nextInt()], minRight[candidats - in.nextInt() + 1]));
    }
  }

  public static void Task00_2() {
    int mandr = in.nextInt();
    double[] sounds = new double[mandr];
    double[] sum = new double[mandr + 1];
    sum[0] = 0;
    in.nextLine();
    String[] line = in.nextLine().split(" ");
    for (int i = 0; i < mandr; i++) {
      sounds[i] = Math.log10(Float.parseFloat(line[i]));
      sum[i + 1] = sum[i] + sounds[i];
    }
    int trials = in.nextInt();
    for (int i = 0; i < trials; i++) {
      int left = in.nextInt();
      int right = in.nextInt();
      double val = Math.pow(10, (sum[right + 1] - sum[left]) / (right - left + 1));
      System.out.printf("%.6f\n", val);
    }
  }

  public static void Task00_3() {
    int points = in.nextInt();
    int[] positions = new int[points];
    int number = in.nextInt();

    for (int i = 0; i < points; i++) {
      positions[i] = in.nextInt();
    }
    Arrays.sort(positions);

    int max = positions[points - 1] - positions[0]; // distance between the furthest ones
    int min = -1; // set as minimum so that 0 is reachable

    if (number >= points || max == 0) { // enough points or all at one position
      System.out.println(0);
      return;
    }

    if (number == 1) {
      System.out.println(max);
      return;
    }

    while (min + 1 < max) {
      int mid = (max + min) / 2;
      if (checkCoverageWithBin(positions, number, mid)) {
        max = mid;
      } else {
        min = mid;
      }
    }
    System.out.println(max);
  }

  public static void Task00_4() {
    int points = in.nextInt(); // >=3
    int[] positions = new int[points];
    int number = in.nextInt(); // >=2
    int min = Integer.MAX_VALUE; // minimum distance between adjacent
    positions[0] = in.nextInt();
    for (int i = 1; i < points; i++) {
      positions[i] = in.nextInt();
      min = Math.min(min, positions[i] - positions[i - 1]);
    }
    //positions = Arrays.stream(positions).sorted().toArray(); // already sorted
    min--;
    int max = positions[points - 1] - positions[0]; // maximum distance

    if (number >= points || max == 0) {
      System.out.println(min);
      return;
    }

    while (min + 1 < max) {
      int mid = (max + min) / 2;
      if (!checkCoverage(positions, number, mid, true)) {
        max = mid;
      } else {
        min = mid;
      }
    }
    System.out.println(max);
  }

  public static void Task00_5() {
    int count = in.nextInt();
    int[] numbers = new int[count];
    //int max = Integer.MIN_VALUE;
    for (int i = 0; i < count; i++) {
      numbers[i] = in.nextInt();
      //max = Math.max(max, numbers[i]);
    }

    for (int val : numbers) {
      var res = breakOnPrimes(val);
      for (int i = 0; i < res.size(); i++) {
        System.out.print(res.get(i) + (i == res.size() - 1 ? "\n" : " "));
      }
    }

  }

  public static void Task00_6() {
    long a = in.nextLong();
    long b = in.nextLong();
    long c = in.nextLong();
    long d = in.nextLong();
    long mod = 1000000007;

    long val1 = calculateWithMod(mod, a, d, MathActions.Mult);
    long val2 = calculateWithMod(mod, b, c, MathActions.Mult);
    long val3 = calculateWithMod(mod, val1, val2, MathActions.Plus);
    long val4 = calculateWithMod(mod, b, d, MathActions.Mult);
    System.out.println(calculateWithMod(mod, val3, val4, MathActions.Divide));
  }

  public static void Task00_7() {
    int num = in.nextInt();
    System.out.println(sumAlg1(smallestPrimes(num)));
  }

  public static void main(String[] args) {
    Task00_3();
  }
}
