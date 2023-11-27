package Task04_B;

import java.io.DataInputStream;
import java.io.InputStream;

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
    int[] arr;
    int[] tree;
    int height;
    int arrOffset;
    int leaves;

    public SegmentTree(int[] array) {
      arr = array.clone();
      height = (int) (Math.ceil(Math.log(arr.length) / Math.log(2)));
      arrOffset = (1 << height) - 1;
      tree = new int[(1 << (height + 1)) - 1];
      leaves = tree.length - arrOffset;
      build(0, 0, leaves);
    }

    // left included, right excluded
    private void build(int v, int left, int right) {
      if (left + 1 == right) {
        // points at the leave node that covers one element from initial array
        if (left >= arr.length) {
          // out of range so the value that can't effect calculation of parent
          tree[v] = 0;
        } else {
          // inherit value from array (even taken with negative, from 0)
          tree[v] = ((left & 1) == 0) ? arr[left] * -1 : arr[left];
        }
      } else {
        int mid = (left + right) / 2;
        build(2 * v + 1, left, mid);
        build(2 * v + 2, mid, right);

        // math function for this tree
        tree[v] = (tree[2 * v + 1] + tree[2 * v + 2]);
      }
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
      return getSum(0, 0, leaves, qLeft, qRight) * (((qLeft & 1) == 0) ? -1 : 1);
    }

    public void updateAtIndex(int index, int value) {
      int treeIndex = arrOffset + index;
      int change = (((index & 1) == 0) ? value * -1 : value) - tree[treeIndex];
      do {
        tree[treeIndex] += change;
        treeIndex = (treeIndex - 1) / 2;
      } while (treeIndex > 0);
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int length = in.nextInt();
    int[] likes = new int[length];
    for (int i = 0; i < length; i++) {
      likes[i] = in.nextInt();
    }
    SegmentTree tree = new SegmentTree(likes);
    int requests = in.nextInt();
    for (int i = 0; i < requests; i++) {
      int type = in.nextInt();
      if (type == 0) {
        int index = in.nextInt();
        int newValue = in.nextInt();
        tree.updateAtIndex(index - 1, newValue);
      } else {
        int l = in.nextInt();
        int r = in.nextInt();
        System.out.println(tree.getSum(l - 1, r));
      }
    }
  }
}
