package Task03_E;

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

class DecTree {
  private static Random random = new Random();
  private Node root;

  private static class Node {
    public int pos;
    public Node left = null;
    public Node right = null;
    public int min;
    public int max;
    public int maxLength;
    public int overlaps;
    public long priority;

    public Node(int pos, int factor) {
      this.pos = pos;
      this.min = pos;
      this.max = pos;
      this.maxLength = 0;
      this.overlaps = factor;
      this.priority = random.nextLong();
    }
  }

  private static class Pair {
    Node first;
    Node second;

    public Pair(Node first, Node second) {
      this.first = first;
      this.second = second;
    }
  }

  private void updateVal(Node node) {
    if (node != null) {
      node.min = Math.min(getMin(node.left), node.pos);
      node.max = Math.max(getMax(node.right), node.pos);
      node.maxLength = Math.max(getMaxLength(node.left), getMaxLength(node.right));
      if (node.right != null) {
        node.maxLength = Math.max(node.maxLength, getMin(node.right) - node.pos);
      }
      if (node.left != null) {
        node.maxLength = Math.max(node.maxLength, node.pos - getMax(node.left));
      }
    }
  }

  private int getMaxLength(Node node) {
    return node == null ? 0 : node.maxLength;
  }

  private int getMax(Node node) {
    return node == null ? Integer.MIN_VALUE : node.max;
  }

  private int getMin(Node node) {
    return node == null ? Integer.MAX_VALUE : node.min;
  }

  private Pair split(Node node, int key) {
    if (node == null) {
      return new Pair(null, null);
    }
    // replace Node children so that all are in one side to the key
    // and another element in Pair is on the other side ALL
    // works by value (as a search tree)
    // left - <=
    // right - >
    if (node.pos > key) {
      Pair pair = split(node.left, key);
      node.left = pair.second;
      updateVal(node);
      return new Pair(pair.first, node);
    } else {
      Pair pair = split(node.right, key);
      node.right = pair.first;
      updateVal(node);
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
      updateVal(less);
      return less;
    } else {
      bigger.left = merge(less, bigger.left);
      updateVal(bigger);
      return bigger;
    }
  }

  public Node find(int value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.pos == value) {
        return tmp;
      }
      if (tmp.pos > value) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return null;
  }

  public void alterPoint(int pos, int factor) {
    Node node = find(pos);
    if (node == null) {
      insertPoint(pos, factor);
    } else {
      node.overlaps += factor;
      if (node.overlaps == 0) {
        deletePoint(node.pos);
      }
    }
  }

  private void insertPoint(int pos, int factor) {
    Node newNode = new Node(pos, factor);
    Pair pair = split(root, pos);
    root = merge(merge(pair.first, newNode), pair.second);
  }

  private void deletePoint(int pos) {
    Pair pair = split(root, pos);
    Pair leftPair = split(pair.first, pos - 1);
    root = merge(leftPair.first, pair.second);
  }

  public int getMaxLength() {
    return root.maxLength;
  }
}

public class Main {
  private static class Dot {
    public final int x;
    public final int y;
    public int start;

    public Dot(int x, int y, int start) {
      this.x = x;
      this.y = y;
      this.start = start;
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int stations = in.nextInt();
    List<Dot> stationList = new ArrayList<>();
    int tracks = in.nextInt();
    int trackLength = in.nextInt();
    DecTree treeOnSegments = new DecTree();
    treeOnSegments.alterPoint(0, 1);
    treeOnSegments.alterPoint(trackLength, 1);

    for (int i = 0; i < stations; i++) {
      int x = in.nextInt(); // (0, trackLength)
      int y1 = in.nextInt(); // <y2
      int y2 = in.nextInt(); // >y1
      stationList.add(new Dot(x, y1, 1));
      stationList.add(new Dot(x, y2 + 1, -1));
    }
    stationList.sort(Comparator.comparingInt(v -> v.y));
    stationList.add(new Dot(0, Integer.MAX_VALUE, 0));

    int stationIndex = 0;
    for (int i = 0; i <= tracks; i++) {
      // get all dots at current level - i
      HashMap<Integer, Dot> newDots = new HashMap<>();
      while (stationList.get(stationIndex).y == i) {
        Dot dot = stationList.get(stationIndex);
        if (newDots.containsKey(dot.x)) {
          newDots.get(dot.x).start += dot.start;
        } else {
          newDots.put(dot.x, new Dot(dot.x, dot.y, dot.start));
        }
        stationIndex++;
      }
      for (Dot dot : newDots.values()) {
        treeOnSegments.alterPoint(dot.x, dot.start);
      }
      System.out.println(treeOnSegments.getMaxLength());
    }
  }
}
