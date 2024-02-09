package Task04_E;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
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

  static class DecTree {
    private static Random random = new Random();

    private Node root = null;

    private static class Node {
      int value = 0;
      public Node left = null;
      public Node right = null;
      public int size = 0;
      public int overlaps = 1;
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
        // updateSize(pair.first);
        updateSize(node);
        return new Pair(pair.first, node);
      } else {
        Pair pair = split(node.right, key);
        node.right = pair.first;
        updateSize(node);
        // updateSize(pair.second);
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
        node.size = node.overlaps + getSize(node.left) + getSize(node.right);
      }
    }

    private int getSize(Node node) {
      return node == null ? 0 : node.size;
    }

    public void insert(int value) {
      Node located = find(value);
      if (located != null) {
        alterOverlap(located, 1);
        return;
      }

      Node node = new Node(value);
      Pair pair = split(root, value);
      root = merge(merge(pair.first, node), pair.second);
    }

    public void delete(int value, int times) {
      Node located = find(value);
      if (located == null) {
        return;
      } else if (located.overlaps != times) {
        alterOverlap(located, times * -1);
        return;
      }

      Pair pair = split(root, value);
      Pair leftPair = split(pair.first, value - 1);
      root = merge(leftPair.first, pair.second);
    }

    private void alterOverlap(Node node, int byVal) {
      Pair pair = split(root, node.value);
      Pair leftPair = split(pair.first, node.value - 1);
      node.overlaps += byVal;
      root = merge(merge(leftPair.first, node), pair.second);
    }

    private Node find(int value) {
      Node tmp = root;
      while (tmp != null) {
        if (tmp.value == value) {
          return tmp;
        }
        if (tmp.value > value) {
          tmp = tmp.left;
        } else {
          tmp = tmp.right;
        }
      }
      return null;
    }

    public int getOrder(int value) {
      Pair q = split(root, value);
      int answer = getSize(q.first);
      root = merge(q.first, q.second);
      return answer;
    }

    public int getVolume() {
      return getSize(root);
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int xLimit = in.nextInt();
    int yLimit = in.nextInt();
    int flowersNumber = in.nextInt();
    // hashmap by x, then y
    HashMap<Integer, HashSet<Integer>> flowers = new HashMap<>();
    int requests = in.nextInt();
    for (int i = 0; i < flowersNumber; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      if (flowers.containsKey(x)) {
        flowers.get(x).add(y);
      } else {
        flowers.put(x, new HashSet<>());
        flowers.get(x).add(y);
      }
    }
    DecTree x1 = new DecTree();
    DecTree x2 = new DecTree();
    DecTree y1 = new DecTree();
    DecTree y2 = new DecTree();
    for (int i = 0; i < requests; i++) {
      int type = in.nextInt();
      if (type == 1) {
        x1.insert(in.nextInt());
        y1.insert(in.nextInt());
        x2.insert(in.nextInt());
        y2.insert(in.nextInt());
      } else {
        int x = in.nextInt();
        int y = in.nextInt();
        int sum =
            x1.getOrder(x - 1)
                + x2.getOrder(x - 1)
                + y1.getOrder(y - 1)
                + y2.getOrder(y - 1)
                + x1.getVolume();
        boolean result =
            flowers.containsKey(x) && flowers.get(x).contains(y) ? sum % 2 == 0 : sum % 2 == 1;
        System.out.println(result ? "YES" : "NO");
      }
    }
  }
}
