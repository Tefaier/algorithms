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

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int xLimit = in.nextInt();
    int yLimit = in.nextInt();
    // SegmentTree2DEmpty tree = new SegmentTree2DEmpty(xLimit, yLimit);
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
    for (int i = 0; i < requests; i++) {
      int type = in.nextInt();
      if (type == 1) {
        int x1 = in.nextInt();
        int y1 = in.nextInt();
        int x2 = in.nextInt();
        int y2 = in.nextInt();
        // tree.updateAtIndex(x1, y1, 1);
        // tree.updateAtIndex(x2, y2, -1);
      } else {
        int x = in.nextInt();
        int y = in.nextInt();
        // int sum = tree.getSum(x, y);
      }
    }
    // flowers.containsKey(x) && flowers.get(x).contains(y)
  }
}
