package Task12.Task12_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

public class Main {
  static int gcd(int n1, int n2) {
    if (n2 == 0) {
      return n1;
    }
    return gcd(n2, n1 % n2);
  }

  static class MegaTriangle {
    public int[] breaks;

    public MegaTriangle(int side1, int side2, int side3) {
      int[] arr = new int[]{side1, side2, side3};
      arr = Arrays.stream(arr).sorted().toArray();
      side1 = arr[0];
      side2 = arr[1];
      side3 = arr[2];
      int gcd1 = gcd(side1, side2);
      int gcd2 = gcd(side1, side3);
      breaks = new int[]{side1 / gcd1, side2 / gcd1, side1 / gcd2, side3 / gcd2};
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MegaTriangle that = (MegaTriangle) o;
      return Arrays.equals(breaks, that.breaks);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(breaks);
    }
  }

  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int trianglesNum = in.nextInt();
    HashSet<MegaTriangle> triangleGroups = new HashSet<>();
    int counter = 0;
    for (int i = 0; i < trianglesNum; i++) {
      MegaTriangle newTriangle = new MegaTriangle(in.nextInt(), in.nextInt(), in.nextInt());
      counter += triangleGroups.add(newTriangle) ? 1 : 0;
    }
    System.out.println(counter);
  }
}

// https://habr.com/ru/articles/91283/
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

  public int nextLong() {
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
