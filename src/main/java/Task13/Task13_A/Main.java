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
          long val = Math.round(a * point.x + b * point.y);
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

  // cross product CW from vector 1 to vector 2
  public static double crossProduct(Vector vector1, Vector vector2) {
    return vector1.x * vector2.y - vector1.y * vector2.x;
  }

  public static double mapTangentTo2pi(double angle, double x) {
    if (angle >= 0 && x >= 0) {
      return angle;
    } else if (x < 0) {
      return Math.PI + angle;
    } else if (angle < 0 && x >= 0) {
      return 2 * Math.PI + angle;
    }
    return 0;
  }

  public static Point linesIntersection(Line line1, Line line2) {
    var coeff = line1.xMul * line2.yMul - line2.xMul * line1.yMul;
    if (equalDoubles(coeff, 0.0)) {
      return null;
    }
    return new Point((line1.yMul * line2.c - line2.yMul * line1.c) / coeff, (line1.c * line2.xMul - line2.c * line1.xMul) / coeff);
  }
}

class Vector {
  final double x;
  final double y;

  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double magnitude2() {
    return x * x + y * y;
  }
}

class Point {
  final double x;
  final double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double magnitude2() {
    return x * x + y * y;
  }

  public Vector vectorToPoint(Point otherPoint) {
    return new Vector(otherPoint.x - x, otherPoint.y - y);
  }
}

class Line {
  final double xMul;
  final double yMul;
  final double c;
  final Vector lineVector;

  public Line(Point through, Vector with) {
    this(through, -with.y, with.x);
  }

  public Line(Point through, double xMul, double yMul) {
    this(xMul, yMul, -(xMul * through.x + yMul * through.y));
  }

  public Line(double xMul, double yMul, double c) {
    this.xMul = xMul;
    this.yMul = yMul;
    this.c = c;
    this.lineVector = new Vector(yMul, -xMul);
  }

  public double applyPoint(Point point) {
    return xMul * point.x + yMul * point.y + c;
  }

  // if rotation from line vector towards point is clock-wise
  // towards positive c of the line
  public int CWLocation(Point point) {
    var appliedValue = applyPoint(point);
    return GeometryMethods.equalDoubles(appliedValue, 0.0) ? 0 : (appliedValue > 0 ? 1 : -1);
  }

  public Point getSamplePoint() {
    if (xMul == 0) {
      return new Point(0, -c / yMul);
    }
    return new Point(-c / xMul, 0);
  }

  public double getAngle() {
    return Math.atan2(lineVector.y, lineVector.x);
  }
}

interface WalkableKey<T> extends Comparable<WalkableKey<T>> {
  public T getVal();

  public WalkableKey<T> prevVal();

  public WalkableKey<T> nextVal();
}

class WalkableLong implements WalkableKey<Long> {
  public long val;

  public WalkableLong(long val) {
    this.val = val;
  }

  @Override
  public Long getVal() {
    return val;
  }

  @Override
  public WalkableLong prevVal() {
    return new WalkableLong(val - 1);
  }

  @Override
  public WalkableLong nextVal() {
    return new WalkableLong(val + 1);
  }

  @Override
  public int compareTo(WalkableKey<Long> o) {
    return Long.compare(val, o.getVal());
  }
}

class DecTree<K, L> {
  private static Random random = new Random();

  private Node root = null;
  private int size = 0;

  private class Node {
    WalkableKey<K> value;
    L load;
    public Node left = null;
    public Node right = null;
    public int size = 0;
    private final long priority;

    public Node(WalkableKey<K> value, L load) {
      this.value = value;
      this.load = load;
      this.priority = random.nextLong();
    }
  }

  private class Pair {
    Node first = null;
    Node second = null;

    public Pair(Node first, Node second) {
      this.first = first;
      this.second = second;
    }
  }

  private Pair split(Node node, WalkableKey<K> key) {
    if (node == null) {
      return new Pair(null, null);
    }
    // replace Node children so that all are in one side to the key
    // and another element in Pair is on the other side ALL
    // works by value (as a search tree)
    // left - <=
    // right - >
    if (node.value.compareTo(key) > 0) {
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

  public void insert(WalkableKey<K> value, L load) {
    if (find(value)) {
      return;
    }
    size++;
    Node node = new Node(value, load);
    Pair pair = split(root, value);
    root = merge(merge(pair.first, node), pair.second);
  }

  public boolean find(WalkableKey<K> value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value == value) {
        return true;
      }
      if (tmp.value.compareTo(value) > 0) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return false;
  }

  public void delete(WalkableKey<K> value) {
    if (!find(value)) {
      return;
    }
    size--;
    Pair pair = split(root, value);
    Pair leftPair = split(pair.first, value.prevVal());
    root = merge(leftPair.first, pair.second);
  }

  // excludes the value itself
  // depending on bigger will search next bigger or next smaller
  public WalkableKey<K> getNext(WalkableKey<K> value, boolean bigger) {
    return getNext(root, value, bigger);
  }

  private WalkableKey<K> getNext(Node node, WalkableKey<K> value, boolean bigger) {
    if (node == null) {
      return null;
    }
    if (bigger && node.value.compareTo(value) > 0 || !bigger && node.value.compareTo(value) < 0) {
      var counterPart = getNext(bigger ? node.left : node.right, value, bigger);
      return (bigger ^ node.value.compareTo(counterPart) > 0) ? node.value : counterPart;
    } else {
      return bigger ? getNext(node.right, value, bigger) : getNext(node.left, value, bigger);
    }
  }

  public WalkableKey<K> getKth(int k) {
    if (size <= k || k < 0) {
      return null;
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
}

class Hull2D {
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
