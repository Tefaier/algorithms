package Task03.Task03_F;

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
    public int value;
    public Node left = null;
    public Node right = null;
    public long sum;
    private long priority;

    public Node(int value) {
      this.value = value;
      this.sum = value;
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
      updateVal(pair.first);
      updateVal(node);
      return new Pair(pair.first, node);
    } else {
      Pair pair = split(node.right, key);
      node.right = pair.first;
      updateVal(node);
      updateVal(pair.second);
      return new Pair(node, pair.second);
    }
  }

  // less and bigger in terms of value
  private Node merge(Node less, Node bigger) {
    if (less == null) {
      updateVal(bigger);
      return bigger;
    }
    if (bigger == null) {
      updateVal(less);
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

  private void updateVal(Node node) {
    if (node != null) {
      node.sum = node.value + getVal(node.left) + getVal(node.right);
    }
  }

  private long getVal(Node node) {
    return node == null ? 0 : node.sum;
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

  public long getSum(int from, int to) {
    Pair pairLeft = split(root, from - 1);
    Pair pairRight = split(pairLeft.second, to);
    long answer = pairRight.first == null ? 0 : pairRight.first.sum;
    merge(merge(pairLeft.first, pairRight.first), pairRight.second);
    return answer;
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
