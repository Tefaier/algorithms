package Task04_E;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

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

  // main x, sub y
  static class SegmentTree2DEmpty {
    private class SegmentTree {
      int[] tree;
      int height;
      int arrOffset;
      int leaves;

      public SegmentTree(int y_size) {
        height = (int) (Math.ceil(Math.log(y_size) / Math.log(2)));
        arrOffset = (1 << height) - 1;
        tree = new int[(1 << (height + 1)) - 1];
        leaves = tree.length - arrOffset;
      }

      private int getSum(int v, int left, int right, int qLeft, int qRight) {
        if (right <= qLeft || qRight <= left) {
          return 0;
        } else if (qLeft <= left && right <= qRight) {
          return tree[v];
        } else {
          int mid = (left + right) / 2;
          return getSum(2 * v + 1, left, mid, qLeft, qRight)
              + getSum(2 * v + 2, mid, right, qLeft, qRight);
        }
      }

      public int getSum(int qLeft, int qRight) {
        return getSum(0, 0, leaves, qLeft, qRight);
      }

      public void updateAtIndex(int index, int value) {
        int treeIndex = arrOffset + index;
        do {
          tree[treeIndex] += value;
          treeIndex = (treeIndex - 1) / 2;
        } while (treeIndex > 0);
      }
    }

    SegmentTree[] trees;
    int height;
    int arrOffset;
    int leaves;
    int totalSize = 0;

    public SegmentTree2DEmpty(int x_size, int y_size) {
      height = (int) (Math.ceil(Math.log(x_size) / Math.log(2)));
      arrOffset = (1 << height) - 1;
      trees = new SegmentTree[(1 << (height + 1)) - 1];
      leaves = trees.length - arrOffset;
      for (int i = 0; i < trees.length; i++) {
        trees[i] = new SegmentTree(y_size);
      }
    }

    private int getSum(int v, int left, int right, int qLeft_x, int qRight_x, int qLeft_y, int qRight_y) {
      if (right <= qLeft_x || qRight_x <= left) {
        return 0;
      } else if (qLeft_x <= left && right <= qRight_x) {
        return trees[v].getSum(qLeft_y, qRight_y);
      } else {
        int mid = (left + right) / 2;
        return getSum(2 * v + 1, left, mid, qLeft_x, qRight_x, qLeft_y, qRight_y)
            + getSum(2 * v + 2, mid, right, qLeft_x, qRight_x, qLeft_y, qRight_y);
      }
    }

    public int getSum(int qLeft_x, int qLeft_y) {
      return getSum(0, 0, leaves, qLeft_x, Integer.MAX_VALUE, qLeft_y, Integer.MAX_VALUE);
    }

    public void updateAtIndex(int index_x, int index_y, int value) {
      totalSize++;
      int treeIndex = arrOffset + index_x;
      do {
        trees[treeIndex].updateAtIndex(index_y, value);
        treeIndex = (treeIndex - 1) / 2;
      } while (treeIndex > 0);
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int xLimit = in.nextInt();
    int yLimit = in.nextInt();
    SegmentTree2DEmpty tree = new SegmentTree2DEmpty(xLimit, yLimit);
    int flowersNumber = in.nextInt();
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
    for (int i = 0; i < requests; i++) {
      int type = in.nextInt();
      if (type == 1) {
        int x1 = in.nextInt();
        int y1 = in.nextInt();
        int x2 = in.nextInt();
        int y2 = in.nextInt();
        tree.updateAtIndex(x1, y1, 1);
        tree.updateAtIndex(x2, y2, -1);
      } else {
        int x = in.nextInt();
        int y = in.nextInt();
        int sum = tree.getSum(x, y);
        System.out.println(Boolean.logicalXor(flowers.containsKey(x) && flowers.get(x).contains(y), ((tree.totalSize + sum) % 2 == 1)) ? "YES" : "NO");
      }
    }
  }
}
