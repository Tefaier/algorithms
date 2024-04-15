package Task04.Task04_G;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Random;

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

  static class TreeImplicit {
    public static Random random = new Random();

    private class Node {
      int value;
      int min;
      int size = 1;
      boolean isReversed = false;
      long priority = random.nextLong();
      Node left;
      Node right;

      public Node(int value) {
        this.value = value;
        this.min = value;
      }
    }

    private Node root;

    private static void push(Node node) {
      if (node.isReversed) {
        node.isReversed = false;
        var tmp = node.left;
        node.left = node.right;
        node.right = tmp;

        if (node.left != null) {
          node.left.isReversed ^= true;
        }
        if (node.right != null) {
          node.right.isReversed ^= true;
        }
      }
    }

    private static int size(Node node) {
      return node == null ? 0 : node.size;
    }

    private static int stat(Node node) {
      return node == null ? Integer.MAX_VALUE : node.min;
    }

    private static void update(Node node) {
      if (node == null) {
        return;
      }
      node.size = size(node.left) + size(node.right) + 1;
      node.min = Math.min(Math.min(stat(node.left), stat(node.right)), node.value);
    }

    private record Pair(Node first, Node second) {
    }

    // splits putting in left size of count
    private Pair split(Node node, int count) {
      if (node == null) {
        return new Pair(null, null);
      }
      push(node);
      if (size(node.left) + 1 <= count) {
        Pair q = split(node.right, count - size(node.left) - 1);
        node.right = q.first;
        update(node);
        // update(q.second);
        return new Pair(node, q.second);
      } else {
        Pair q = split(node.left, count);
        node.left = q.second;
        update(node);
        // update(q.first);
        return new Pair(q.first, node);
      }
    }

    private Node merge(Node less, Node bigger) {
      if (less == null) {
        push(bigger);
        return bigger;
      }
      if (bigger == null) {
        push(less);
        return less;
      }
      push(less);
      push(bigger);
      if (less.priority > bigger.priority) {
        less.right = merge(less.right, bigger);
        update(less);
        return less;
      } else {
        bigger.left = merge(less, bigger.left);
        update(bigger);
        return bigger;
      }
    }

    // works by indexes from 0!, both inclusive
    public Node ctrlx(int l, int r) {
      Pair q1 = split(root, r + 1);
      Pair q2 = split(q1.first, l);
      root = merge(q2.first, q1.second);
      return q2.second;
    }

    // works by indexes from 0!, k index will be start of inserted
    public void ctrlv(Node node, int k) {
      Pair q = split(root, k);
      root = merge(q.first, merge(node, q.second));
    }

    public void insert(int value, int position) {
      ctrlv(new Node(value), position);
    }

    // inclusive, starts from 0
    public void reverse(int l, int r) {
      Pair q1 = split(root, r + 1);
      Pair q2 = split(q1.first, l);
      q2.second.isReversed ^= true;
      root = merge(merge(q2.first, q2.second), q1.second);
    }

    public int minRequest(int l, int r) {
      Pair q1 = split(root, r + 1);
      Pair q2 = split(q1.first, l);
      int answer = q2.second.min;
      root = merge(merge(q2.first, q2.second), q1.second);
      return answer;
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int volume = in.nextInt();
    int requests = in.nextInt();
    TreeImplicit tree = new TreeImplicit();
    for (int i = 0; i < volume; i++) {
      tree.insert(in.nextInt(), i);
    }
    for (int i = 0; i < requests; i++) {
      int type = in.nextInt();
      if (type == 1) {
        tree.reverse(in.nextInt() - 1, in.nextInt() - 1);
      } else {
        System.out.println(tree.minRequest(in.nextInt() - 1, in.nextInt() - 1));
      }
    }
  }
}
