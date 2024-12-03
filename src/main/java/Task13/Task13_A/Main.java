package Task13.Task13_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  private static int avoidZero(int value) {
    return value == 0 ? 1 : value;
  }

  public static void main(String[] args) {
    int dotsNumber = in.nextInt();
    Point[] dots = new Point[dotsNumber];
    for (int i = 0; i < dotsNumber; i++) {
      dots[i] = new Point(in.nextInt(), in.nextInt());
    }
    NavigableSet<Point> sortByDistance =
        new TreeSet<>(
            (point1, point2) ->
                avoidZero(Double.compare(point1.magnitude2(), point2.magnitude2())));
    sortByDistance.addAll(Arrays.stream(dots).toList());

    int requestsNumber = in.nextInt();
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestsNumber; i++) {
      String method = in.nextString(4);
      if (method.equals("get")) {
        int a = in.nextInt();
        int b = in.nextInt();
        long countedMax = Long.MIN_VALUE;
        double lastDistance2 = 0.0;
        for (Iterator<Point> it = sortByDistance.descendingIterator(); it.hasNext(); ) {
          Point point = it.next();
          Line line = new Line(point, a, b);
          long val = a * point.x + b * point.y;
          if (val > countedMax) {
            countedMax = val;
            lastDistance2 = GeometryMethods.distancePointToLine2(GeometryMethods.originPoint, line);
          }
          if (point.magnitude2() < lastDistance2) {
            break;
          }
        }
        answer.append(countedMax).append('\n');
      } else {
        sortByDistance.add(new Point(in.nextInt(), in.nextInt()));
      }
    }
    System.out.println(answer);
  }
}

class GeometryMethods {
  public static Point originPoint = new Point(0, 0);

  public static boolean equalDoubles(double d1, double d2) {
    return Math.abs(d1 - d2) < 1e-6;
  }

  public static double distancePointToLine2(Point point, Line line) {
    return Math.pow(line.applyPoint(point), 2.0)
        / (Math.pow(line.xMul, 2.0) + Math.pow(line.yMul, 2.0));
  }
}

class Vector {
  final long x;
  final long y;

  public Vector(long x, long y) {
    this.x = x;
    this.y = y;
  }

  public long magnitude2() {
    return x * x + y * y;
  }
}

class Point {
  final long x;
  final long y;

  public Point(long x, long y) {
    this.x = x;
    this.y = y;
  }

  public long magnitude2() {
    return x * x + y * y;
  }
}

class Line {
  final long xMul;
  final long yMul;
  final long c;

  public Line(Point through, Vector with) {
    this(through, with.y, -with.x);
  }

  public Line(Point through, long xMul, long yMul) {
    this(xMul, yMul, -(xMul * through.x + yMul * through.y));
  }

  public Line(long xMul, long yMul, long c) {
    this.xMul = xMul;
    this.yMul = yMul;
    this.c = c;
  }

  public double applyPoint(Point point) {
    return xMul * point.x + yMul * point.y + c;
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
