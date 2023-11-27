package Task04_H;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.stream.IntStream;

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

  static class SegmentTree {
    static class DecTree {
      private static Random random = new Random();

      private Node root = null;

      private static class Node {
        int value = 0;
        public Node left = null;
        public Node right = null;
        public int size = 1;
        public int overlaps = 1;
        private long priority;

        public Node(int value) {
          this.value = value;
          this.priority = random.nextLong();
        }

        public Node(int value, int overlaps) {
          this.value = value;
          this.overlaps = overlaps;
          this.priority = random.nextLong();
        }

        private Node(Node node) {
          this.value = node.value;
          this.left = getCopy(node.left);
          this.right = getCopy(node.right);
          this.size = node.size;
          this.overlaps = node.overlaps;
          this.priority = node.priority;
        }

        public static Node getCopy(Node node) {
          if (node == null) {
            return null;
          }
          return new Node(node);
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

      public DecTree(int[] arr, int l, int r) {
        r = Math.min(arr.length - 1, r);
        if (l > r) {
          return;
        }
        Stack<Node> parentChain = new Stack<>();
        this.root = new Node(arr[l]);
        parentChain.push(this.root);
        for (int i = l + 1; i <= r; i++) {
          Node node = new Node(arr[i]);
          Node lastNode = null;
          while (!parentChain.empty() && node.priority > parentChain.peek().priority) {
            lastNode = parentChain.pop();
            updateSize(lastNode);
          }
          node.left = lastNode;
          updateSize(node);
          if (parentChain.empty()) {
            root = node;
          } else {
            parentChain.peek().right = node;
            updateSize(parentChain.peek());
          }
          parentChain.push(node);
        }
        while (!parentChain.empty()) {
          updateSize(parentChain.pop());
        }
      }

      public DecTree() {
        this.root = null;
      }

      public DecTree(int val) {
        this.root = new Node(val);
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
        insert(value, 1);
      }

      private void insert(int value, int overlaps) {
        Node located = find(value);
        if (located != null) {
          alterOverlap(located, overlaps);
          return;
        }

        Node node = new Node(value, overlaps);
        Pair pair = split(root, value);
        root = merge(merge(pair.first, node), pair.second);
      }

      public void delete(int value) {
        Node located = find(value);
        if (located == null) {
          return;
        } else if (located.overlaps != 1) {
          alterOverlap(located, -1);
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
        node.size += byVal;
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

      public int sizeBetween(int lower, int upper) {
        Pair pair = split(root, upper);
        Pair leftPair = split(pair.first, lower - 1);
        int answer = (leftPair.second == null) ? 0 : leftPair.second.size;
        root = merge(merge(leftPair.first, leftPair.second), pair.second);
        return answer;
      }
    }

    int[] arr;
    DecTree[] tree;
    int height;
    int arrOffset;
    int leaves;

    public SegmentTree(int[] array) {
      // no cloning
      arr = array;
      height = (int) (Math.ceil(Math.log(arr.length) / Math.log(2)));
      arrOffset = (1 << height) - 1;
      tree = new DecTree[(1 << (height + 1)) - 1];
      leaves = tree.length - arrOffset;
      build(0, 0, leaves);
    }

    // left included, right excluded
    private void build(int v, int left, int right) {
      if (left >= arr.length) {
        // out of range so the value that can't effect calculation of parent
        return;
      }
      if (left + 1 == right) {
        // inherit value from array (even taken with negative, from 0)
        tree[v] = new DecTree(arr[left]);
      } else {
        int mid = (left + right) / 2;
        build(2 * v + 1, left, mid);
        build(2 * v + 2, mid, right);
        merge(left, mid, right);

        tree[v] = new DecTree(arr, left, right - 1);
      }
    }

    private int qLeft;
    private int qRight;
    private int lower;
    private int upper;

    // left inclusive, right exclusive
    private int getCount(int v, int left, int right) {
      if (right <= qLeft || qRight < left) {
        return 0;
      } else if (qLeft <= left && right - 1 <= qRight) {
        return tree[v].sizeBetween(lower, upper);
      } else {
        int mid = (left + right) / 2;
        return getCount(2 * v + 1, left, mid) + getCount(2 * v + 2, mid, right);
      }
    }

    // all are inclusive, indexes start from 0
    public int getCount(int qLeft, int qRight, int lower, int upper) {
      this.qLeft = qLeft;
      this.qRight = qRight;
      this.lower = lower;
      this.upper = upper;
      return getCount(0, 0, leaves);
    }

    public void updateAtIndex(int index, int value) {
      int treeIndex = arrOffset + index;
      int from = tree[treeIndex].root.value;
      while (treeIndex > 0) {
        tree[treeIndex].delete(from);
        tree[treeIndex].insert(value);
        treeIndex = (treeIndex - 1) >> 1;
      }
      tree[0].delete(from);
      tree[0].insert(value);
    }

    private void merge(int l, int m, int r) {
      r = Math.min(arr.length, r);
      if (m >= arr.length) {
        return;
      }
      int size1 = m - l;
      int size2 = r - m;
      int[] arr1 = Arrays.copyOfRange(arr, l, m); // l -> m - 1
      int[] arr2 = Arrays.copyOfRange(arr, m, r); // m -> r - 1

      int pointer1 = 0;
      int pointer2 = 0;
      int pointerMain = l;

      while (pointer1 < size1 && pointer2 < size2) {
        if (arr1[pointer1] <= arr2[pointer2]) {
          arr[pointerMain] = arr1[pointer1];
          pointer1++;
        } else {
          arr[pointerMain] = arr2[pointer2];
          pointer2++;
        }
        pointerMain++;
      }

      while (pointer1 < size1) {
        arr[pointerMain] = arr1[pointer1];
        pointer1++;
        pointerMain++;
      }

      while (pointer2 < size2) {
        arr[pointerMain] = arr2[pointer2];
        pointer2++;
        pointerMain++;
      }
    }
  }

  private static Random random = new Random();

  private static void test() {
    while (true) {
      int volume = random.nextInt(2, 100000);
      SegmentTree tree = new SegmentTree(IntStream.range(1, volume).toArray());
      System.out.println(volume);
    }
  }

  public static void main(String[] args) {
    // test();
    Parser in = new Parser(System.in);
    int volume = in.nextInt();
    int requests = in.nextInt();
    int[] values = new int[volume];
    for (int i = 0; i < volume; i++) {
      values[i] = in.nextInt();
    }
    SegmentTree tree = new SegmentTree(values);
    for (int i = 0; i < requests; i++) {
      String type = in.nextString(4);
      if (type.charAt(0) == 'G') {
        System.out.println(
            tree.getCount(in.nextInt() - 1, in.nextInt() - 1, in.nextInt(), in.nextInt()));
      } else {
        tree.updateAtIndex(in.nextInt() - 1, in.nextInt());
      }
    }
  }
}
