package Task03_F;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Random;

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

class DecTreeSum {
  private static Random random = new Random();

  private Node root = null;
  private int size = 0;

  private static class Node {
    int value = 0;
    public Node left = null;
    public Node right = null;
    public int size = 0;
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
    // left - <=
    // right - >
    if (node.value > key) {
      Pair pair = split(node.left, key);
      node.left = pair.second;
      updateSize(pair.first);
      updateSize(node);
      return new Pair(pair.first, node);
    } else {
      Pair pair = split(node.right, key);
      node.right = pair.first;
      updateSize(node);
      updateSize(pair.second);
      return new Pair(node, pair.second);
    }
  }

  // less and bigger in terms of value
  private Node merge(Node less, Node bigger) {
    if (less == null) {
      updateSize(bigger);
      return bigger;
    }
    if (bigger == null) {
      updateSize(less);
      return less;
    }
    // works by priority (as a binary heap)
    if (less.priority > bigger.priority) {
      less.right = merge(less.right, bigger);
      updateSize(less);
      return less;
    } else {
      bigger.left = merge(less, bigger.left);
      updateSize(bigger);
      return bigger;
    }
  }

  private void updateSize(Node node) {
    if (node != null) {
      node.size = 1 + getSize(node.left) + getSize(node.right);
    }
  }

  private int getSize(Node node) {
    return node == null ? 0 : node.size;
  }

  public void insert(int value) {
    if (find(value)) {
      return;
    }
    size++;
    Node node = new Node(value);
    Pair pair = split(root, value);
    root = merge(merge(pair.first, node), pair.second);
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

  public void delete(int value) {
    if (!find(value)) {
      return;
    }
    size--;
    Pair pair = split(root, value);
    Pair leftPair = split(pair.first, value - 1);
    root = merge(leftPair.first, pair.second);
  }

  // excludes the value itself
  // depending on bigger will search next bigger or next smaller
  public int getNext(int value, boolean bigger) {
    return getNext(root, value, bigger);
  }

  private int getNext(Node node, int value, boolean bigger) {
    if (node == null) {
      return bigger ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    }
    if (bigger && node.value > value || !bigger && node.value < value) {
      return bigger
          ? Math.min(node.value, getNext(node.left, value, bigger))
          : Math.max(node.value, getNext(node.right, value, bigger));
    } else {
      return bigger ? getNext(node.right, value, bigger) : getNext(node.left, value, bigger);
    }
  }

  public int getKth(int k) {
    if (size <= k || k < 0) {
      return Integer.MIN_VALUE;
    }
    Node tmp = root;
    int toAdd = 0;
    while (tmp != null) {
      int index = toAdd + getSize(tmp.left);
      if (index == k) {
        return tmp.value;
      } else if (index < k) {
        toAdd = index + 1;
        tmp = tmp.right;
      } else {
        tmp = tmp.left;
      }
    }
    throw new RuntimeException("Error with sizes log");
  }

  public long getSum(int from, int to) {

  }
}

public class Main {
  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    DecTreeSum tree = new DecTreeSum();
    int mod = 1000000000;
    int requests = in.nextInt();
    long lastAskResponse = 0;
    for (int i = 0; i < requests; i++) {
      String command = in.nextString(2);
      if (command.equals("+")) {
        int toAdd = in.nextInt();
        if (lastAskResponse != 0) {
          toAdd = (int) ((toAdd + lastAskResponse) % mod);
          lastAskResponse = 0;
        }
        tree.insert(toAdd);
      } else {
        lastAskResponse = tree.getSum(in.nextInt(), in.nextInt());
        System.out.println(lastAskResponse);
      }
    }
  }
}
