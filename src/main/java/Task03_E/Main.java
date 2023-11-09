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

  private Node root;

  private static class Node {
    public int l;
    public int r;
    public Node left = null;
    public Node right = null;
    public final int length;

    public Node(int l, int r) {
      this.l = l;
      this.r = r;
      this.length = r - l;
    }

    // segment contains value in [l, r)
    public boolean contains(int val) {
      return val >= l && val < r;
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

  public DecTree(int l, int r) {
    root = new Node(l, r);
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
    if (node.l > key) {
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
    if (less.length > bigger.length) {
      less.right = merge(less.right, bigger);
      return less;
    } else {
      bigger.left = merge(less, bigger.left);
      return bigger;
    }
  }

  public Node find(int value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.contains(value)) {
        return tmp;
      }
      if (tmp.l > value) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return null;
  }

  public void deleteBorder(int value) {
    Node nodeToDelete = find(value); // right one
    Node nodeToDelete2 = find(value - 1); // left one
    Node replaceNode = new Node(nodeToDelete2.l, nodeToDelete.r);
    Pair pair = split(root, nodeToDelete.r - 1);
    Pair leftPair = split(pair.first, nodeToDelete2.l - 1);
    root = merge(merge(leftPair.first, replaceNode), pair.second);
  }

  public void addBorder(int value) {
    Node nodeToDelete = find(value);
    Node replaceNode = new Node(value, nodeToDelete.r); // right one
    Node replaceNode2 = new Node(nodeToDelete.l, value); // left one
    Pair pair = split(root, nodeToDelete.r - 1);
    Pair leftPair = split(pair.first, nodeToDelete.l - 1);
    root = merge(merge(leftPair.first, merge(replaceNode2, replaceNode)), pair.second);
  }

  public int getMaxLength() {
    return root.length;
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
    DecTree treeOnSegments = new DecTree(0, trackLength);

    for (int i = 0; i < stations; i++) {
      int x = in.nextInt(); // (0, trackLength)
      int y1 = in.nextInt(); // <y2
      int y2 = in.nextInt(); // >y1
      stationList.add(new Dot(x, y1, 1));
      stationList.add(new Dot(x, y2, -1));
    }
    stationList.sort(Comparator.comparingInt(v -> v.y));
    stationList.add(new Dot(0, Integer.MAX_VALUE, 0));

    int stationIndex = 0;
    Set<Integer> positionsToClear = new HashSet<>();
    HashMap<Integer, Dot> newDots = new HashMap<>();
    HashMap<Integer, Dot> activeDots = new HashMap<>();
    for (int i = 0; i <= tracks; i++) {
      // get all dots at current level - i
      positionsToClear = new HashSet<>();
      newDots = new HashMap<>();
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
        if (activeDots.containsKey(dot.x)) {
          // change value or eradication if needed
          activeDots.get(dot.x).start += dot.start;
          if (activeDots.get(dot.x).start == 0) {
            activeDots.remove(dot.x);
            positionsToClear.add(dot.x);
          }
        } else {
          // add to active segments
          activeDots.put(dot.x, new Dot(dot.x, dot.y, dot.start));
          treeOnSegments.addBorder(dot.x);
        }
      }
      System.out.println(treeOnSegments.getMaxLength());
      // clear eradicated stations after finishing with track
      for (Integer toClear : positionsToClear) {
        treeOnSegments.deleteBorder(toClear);
      }
    }
  }
}
