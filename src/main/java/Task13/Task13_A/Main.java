package Task13.Task13_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  private static void test() {
    Random random = new Random();
    int limit = 10;
    while (true) {
      Hull2D hull = new Hull2D();
      int dotsNumber = random.nextInt(1, 10);
      List<Point> dots = new ArrayList<>();
      for (int i = 0; i < dotsNumber; i++) {
        dots.add(new Point(random.nextInt(-limit, limit), random.nextInt(-limit, limit)));
        hull.addPoint(dots.get(dots.size() - 1));
      }

      int requestsNumber = random.nextInt(1, 2);
      for (int i = 0; i < requestsNumber; i++) {
        if (random.nextBoolean()) {
          Line touchLine = new Line(random.nextInt(-limit, limit), random.nextInt(-limit, limit), 0);
          Point touchPoint = hull.getAngleTouch(touchLine.getAngle());
          Point trueMax = null;
          long countedMax1 = Long.MIN_VALUE;
          for (Point dot : dots) {
            if (countedMax1 < touchLine.xMul * dot.x + touchLine.yMul * dot.y) {
              countedMax1 = Math.round(touchLine.xMul * dot.x + touchLine.yMul * dot.y);
              trueMax = dot;
            }
          }
          long countedMax2 = Math.round(touchLine.applyPoint(touchPoint));
          if (countedMax1 != countedMax2) {
            System.out.println("ERROR");
          }
        } else {
        }
      }
    }
  }

  public static void main(String[] args) {
    test();
    Hull2D hull = new Hull2D();
    int dotsNumber = in.nextInt();
    for (int i = 0; i < dotsNumber; i++) {
      hull.addPoint(new Point(in.nextInt(), in.nextInt()));
    }

    int requestsNumber = in.nextInt();
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestsNumber; i++) {
      String method = in.nextString(4);
      if (method.equals("get")) {
        Line touchLine = new Line(in.nextInt(), in.nextInt(), 0);
        Point touchPoint = hull.getAngleTouch(touchLine.getAngle());
        answer.append(Math.round(touchLine.applyPoint(touchPoint))).append('\n');
      } else {
        hull.addPoint(new Point(in.nextInt(), in.nextInt()));
      }
    }
    System.out.println(answer);
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

class GeometryMethods {
  public static Point originPoint = new Point(0, 0);
  public static Double calculationBoundingBox = (double) Long.MAX_VALUE;
  public static Line boxD = new Line(0, 1, calculationBoundingBox);
  public static Line boxU = new Line(0, -1, calculationBoundingBox);
  public static Line boxR = new Line(-1, 0, calculationBoundingBox);
  public static Line boxL = new Line(1, 0, calculationBoundingBox);

  public static boolean equalDoubles(double d1, double d2) {
    return Math.abs(d1 - d2) < 1e-30;
  }

  public static double dotProduct(Vector vector1, Vector vector2) {
    return vector1.x * vector2.x + vector1.y * vector2.y;
  }

  // cross product CW from vector 1 to vector 2
  public static double crossProduct(Vector vector1, Vector vector2) {
    return vector1.x * vector2.y - vector1.y * vector2.x;
  }

  public static Point linesIntersection(Line line1, Line line2) {
    var coeff = line1.xMul * line2.yMul - line2.xMul * line1.yMul;
    if (equalDoubles(coeff, 0.0)) {
      return null;
    }
    return new Point(
        (line1.yMul * line2.c - line2.yMul * line1.c) / coeff,
        (line1.c * line2.xMul - line2.c * line1.xMul) / coeff);
  }

  public static Optional<Point> segmentsIntersection(Point p1, Point p2, Point p3, Point p4) {
    Line line1 = new Line(p1, p1.vectorToPoint(p2));
    Line line2 = new Line(p3, p3.vectorToPoint(p4));
    int posInfo1 = line1.CWLocation(p3) * line1.CWLocation(p4);
    int posInfo2 = line2.CWLocation(p1) * line2.CWLocation(p2);
    if (posInfo1 == -1 && posInfo2 == -1) {
      return Optional.of(GeometryMethods.linesIntersection(line1, line2));
    }
    if (posInfo1 == 0 && posInfo2 == -1) {
      return Optional.of(line1.CWLocation(p3) == 0 ? p3 : p4);
    }
    if (posInfo2 == 0 && posInfo1 == -1) {
      return Optional.of(line2.CWLocation(p1) == 0 ? p1 : p2);
    }
    return Optional.empty();
  }
}

interface WalkableKey<T> extends Comparable<WalkableKey<T>> {
  public T getVal();

  public WalkableKey<T> prevVal();

  public WalkableKey<T> nextVal();
}

class WalkableInteger implements WalkableKey<Integer> {
  public int val;

  public WalkableInteger(Point point) {
    this((int) Math.round(point.x));
  }

  public WalkableInteger(int val) {
    this.val = val;
  }

  @Override
  public Integer getVal() {
    return val;
  }

  @Override
  public WalkableInteger prevVal() {
    return new WalkableInteger(
        val == Integer.MIN_VALUE ? Integer.MIN_VALUE : val - 1);
  }

  @Override
  public WalkableInteger nextVal() {
    return new WalkableInteger(
        val == Integer.MAX_VALUE ? Integer.MAX_VALUE : val + 1);
  }

  @Override
  public int compareTo(WalkableKey<Integer> o) {
    return Integer.compare(val, o.getVal());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WalkableInteger that = (WalkableInteger) o;
    return val == that.val;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(val);
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

  public int getSize() {
    return getSize(root);
  }

  private int getSize(Node node) {
    return node == null ? 0 : node.size;
  }

  public void insert(WalkableKey<K> value, L load) {
    if (find(value)) {
      Node tmp = root;
      while (tmp != null) {
        if (tmp.value.equals(value)) {
          tmp.load = load;
          return;
        }
        if (tmp.value.compareTo(value) > 0) {
          tmp = tmp.left;
        } else {
          tmp = tmp.right;
        }
      }
    }
    size++;
    Node node = new Node(value, load);
    Pair pair = split(root, value);
    root = merge(merge(pair.first, node), pair.second);
  }

  public boolean find(WalkableKey<K> value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value.equals(value)) {
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

  // delete by range of values, inclusive
  public void deleteRange(WalkableKey<K> valueFrom, WalkableKey<K> valueTo) {
    Pair pair = split(root, valueTo);
    Pair leftPair = split(pair.first, valueFrom.prevVal());
    root = merge(leftPair.first, pair.second);
    size -= leftPair.second == null ? 0 : leftPair.second.size;
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

  public L getKth(int k) {
    if (size <= k || k < 0) {
      return null;
    }
    Node tmp = root;
    int toAdd = 0;
    while (tmp != null) {
      int index = toAdd + getSize(tmp.left);
      if (index == k) {
        return tmp.load;
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
  record BinarySearchResult(int point1Index, Point point1, Point point2) {
  }

  DecTree<Integer, Point> upperBound = new DecTree<>();
  DecTree<Integer, Point> lowerBound = new DecTree<>();

  private BinarySearchResult getCoverSegment(DecTree<Integer, Point> bound, double x) {
    int l = 0;
    int r = bound.getSize();
    while (r - l > 1) {
      int m = (l + r) / 2;
      if (bound.getKth(m).x >= x) {
        r = m;
      } else {
        l = m;
      }
    }
    if (l == 0) {
      Point p0 = bound.getKth(0);
      if (p0.x > x) {
        return new BinarySearchResult(-1, null, null);
      }
      return new BinarySearchResult(0, p0, bound.getKth(1));
    }
    if (l == bound.getSize() - 1) {
      return new BinarySearchResult(bound.getSize(), null, null);
    }
    return new BinarySearchResult(l, bound.getKth(l), bound.getKth(l + 1));
  }

  // git inclusive range where to search for break point
  private BinarySearchResult getL(
      DecTree<Integer, Point> bound, Point point, int l, int r, boolean upper) {
    if (r - l <= 1) {
      if (r == l) return new BinarySearchResult(l, bound.getKth(l), null);
      Point l1 = bound.getKth(l);
      Point l2 = bound.getKth(l + 1);
      Line segmentLine = new Line(l1, l1.vectorToPoint(l2));
      int location = segmentLine.CWLocation(point);
      if (upper ? location >= 0 : location <= 0) return new BinarySearchResult(l, l1, l2);
      return new BinarySearchResult(r, l2, null);
    }
    int m = (l + r) / 2;
    Point m1 = bound.getKth(m);
    Point m2 = bound.getKth(m + 1);
    Line segmentLine = new Line(m1, m1.vectorToPoint(m2));
    int location = segmentLine.CWLocation(point);
    if (location == 0) {
      return new BinarySearchResult(m, m1, m2);
    } else if (upper ? location < 0 : location > 0) {
      return getL(bound, point, m, r, upper);
    } else {
      return getL(bound, point, l, m, upper);
    }
  }

  // git inclusive range where to search for break point
  private BinarySearchResult getR(
      DecTree<Integer, Point> bound, Point point, int l, int r, boolean upper) {
    if (r - l <= 1) {
      if (r == l) return new BinarySearchResult(l, bound.getKth(l), null);
      Point r1 = bound.getKth(r - 1);
      Point r2 = bound.getKth(r);
      Line segmentLine = new Line(r1, r1.vectorToPoint(r2));
      int location = segmentLine.CWLocation(point);
      if (upper ? location >= 0 : location <= 0) return new BinarySearchResult(r, r2, null);
      return new BinarySearchResult(l, r1, r2);
    }
    int m = (l + r) / 2;
    Point m1 = bound.getKth(m - 1);
    Point m2 = bound.getKth(m);
    Line segmentLine = new Line(m1, m1.vectorToPoint(m2));
    int location = segmentLine.CWLocation(point);
    if (location == 0) {
      return new BinarySearchResult(m, m2, null);
    } else if (upper ? location >= 0 : location <= 0) {
      return getR(bound, point, m, r, upper);
    } else {
      return getR(bound, point, l, m, upper);
    }
  }

  public void addPoint(Point point) {
    if (lowerBound.getSize() == 0) {
      lowerBound.insert(new WalkableInteger(point), point);
      upperBound.insert(new WalkableInteger(point), point);
      return;
    }
    if (isInside(point)) return;
    if (lowerBound.getSize() == 1) {
      if (lowerBound.getKth(0).x == point.x) {
        if (lowerBound.getKth(0).y > point.y) {
          lowerBound.insert(new WalkableInteger(point), point);
        }
        if (upperBound.getKth(0).y < point.y) {
          upperBound.insert(new WalkableInteger(point), point);
        }
      } else {
        lowerBound.insert(new WalkableInteger(point), point);
        upperBound.insert(new WalkableInteger(point), point);
      }
      return;
    }
    var coverLower = getCoverSegment(lowerBound, point.x);
    var coverUpper = getCoverSegment(upperBound, point.x);
    if (coverLower.point1Index == -1) {
      var toKeepL = getR(lowerBound, point, 0, lowerBound.getSize() - 1, false);
      var toKeepU = getR(upperBound, point, 0, upperBound.getSize() - 1, true);
      lowerBound.deleteRange(
          new WalkableInteger(Integer.MIN_VALUE),
          new WalkableInteger(toKeepL.point1).prevVal());
      upperBound.deleteRange(
          new WalkableInteger(Integer.MIN_VALUE),
          new WalkableInteger(toKeepU.point1).prevVal());
      lowerBound.insert(new WalkableInteger(point), point);
      upperBound.insert(new WalkableInteger(point), point);
    } else if (coverLower.point1Index == lowerBound.getSize()) {
      var toKeepL = getL(lowerBound, point, 0, lowerBound.getSize() - 1, false);
      var toKeepU = getL(upperBound, point, 0, upperBound.getSize() - 1, true);
      lowerBound.deleteRange(
          new WalkableInteger(toKeepL.point1).nextVal(),
          new WalkableInteger(Integer.MAX_VALUE));
      upperBound.deleteRange(
          new WalkableInteger(toKeepU.point1).nextVal(),
          new WalkableInteger(Integer.MAX_VALUE));
      lowerBound.insert(new WalkableInteger(point), point);
      upperBound.insert(new WalkableInteger(point), point);
    } else {
      boolean isAbove =
          GeometryMethods.segmentsIntersection(
                  coverLower.point1, coverLower.point2, point, new Point(point.x, Integer.MIN_VALUE))
              .isPresent();
      if (isAbove) {
        var toKeepU1 = getL(upperBound, point, 0, coverUpper.point1Index, true);
        var toKeepU2 =
            getR(upperBound, point, coverUpper.point1Index + 1, upperBound.getSize() - 1, true);
        upperBound.deleteRange(
            new WalkableInteger(toKeepU1.point1).nextVal(),
            new WalkableInteger(toKeepU2.point1).prevVal());
        if (toKeepU1.point1.x == point.x) upperBound.delete(new WalkableInteger(toKeepU1.point1));
        if (toKeepU2.point1.x == point.x) upperBound.delete(new WalkableInteger(toKeepU2.point1));
        upperBound.insert(new WalkableInteger(point), point);
      } else {
        var toKeepL1 = getL(lowerBound, point, 0, coverLower.point1Index, false);
        var toKeepL2 =
            getR(lowerBound, point, coverLower.point1Index + 1, lowerBound.getSize() - 1, false);
        lowerBound.deleteRange(
            new WalkableInteger(toKeepL1.point1).nextVal(),
            new WalkableInteger(toKeepL2.point1).prevVal());
        if (toKeepL1.point1.x == point.x) lowerBound.delete(new WalkableInteger(toKeepL1.point1));
        if (toKeepL2.point1.x == point.x) lowerBound.delete(new WalkableInteger(toKeepL2.point1));
        lowerBound.insert(new WalkableInteger(point), point);
      }
    }
  }

  public boolean isInside(Point point) {
    if (lowerBound.getSize() == 0) return false;
    if (lowerBound.getSize() == 1)
      return GeometryMethods.equalDoubles(
          lowerBound.getKth(0).vectorToPoint(point).magnitude2(), 0.0);
    var coverLower = getCoverSegment(lowerBound, point.x);
    var coverUpper = getCoverSegment(upperBound, point.x);
    if (coverLower.point1Index == -1 || coverLower.point1Index == lowerBound.getSize()) {
      return false;
    }
    boolean cond1 =
        GeometryMethods.segmentsIntersection(
                coverLower.point1, coverLower.point2, point, new Point(point.x, Integer.MIN_VALUE))
            .isPresent();
    boolean cond2 =
        GeometryMethods.segmentsIntersection(
                coverUpper.point1, coverUpper.point2, point, new Point(point.x, Integer.MAX_VALUE))
            .isPresent();
    return cond1 && cond2;
  }

  private BinarySearchResult searchTouchVertex(
      DecTree<Integer, Point> bound, double angle, boolean upper) {
    int l = 0;
    int r = bound.getSize();
    while (r - l > 1) {
      int m = (l + r) / 2;
      Point mPoint = bound.getKth(m);
      Point from = bound.getKth(m - 1);
      double angleBreak =
          upper
              ? new Line(from, from.vectorToPoint(mPoint)).getAngle()
              : new Line(mPoint, mPoint.vectorToPoint(from)).getAngle();
      if (upper
          ? angle > angleBreak
          : (angleBreak > 0
          ? (angle < angleBreak && angle > 0)
          : (angle > 0 || angle < angleBreak))) {
        r = m;
      } else {
        l = m;
      }
    }
    return new BinarySearchResult(l, bound.getKth(l), null);
  }

  public Point getAngleTouch(double angle) {
    if (angle >= Math.PI / -2 && angle <= Math.PI / 2) {
      return searchTouchVertex(upperBound, angle, true).point1;
    } else {
      return searchTouchVertex(lowerBound, angle, false).point1;
    }
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
