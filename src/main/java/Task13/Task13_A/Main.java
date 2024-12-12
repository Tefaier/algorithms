package Task13.Task13_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int dotsNumber = in.nextInt();
    Point[] initialPoints = new Point[dotsNumber];
    for (int i = 0; i < dotsNumber; i++) {
      initialPoints[i] = new Point(in.nextInt(), in.nextInt());
    }
    var envelops = GeoM.buildEnvelops(GeoM.buildConvexHull(initialPoints));
    Hull2D hull = new Hull2D(envelops.get(0), envelops.get(1));

    int requestsNumber = in.nextInt();
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestsNumber; i++) {
      String method = in.nextString(4);
      if (method.equals("get")) {
        Line touchLine = new Line(in.nextInt(), in.nextInt(), 0);
        Point touchPoint = hull.getAngleTouch(touchLine.getAngle());
        answer.append(touchLine.applyPoint(touchPoint)).append('\n');
      } else {
        hull.addPoint(new Point(in.nextInt(), in.nextInt()));
      }
    }
    System.out.println(answer);
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

  public Vector vectorToPoint(Point otherPoint) {
    return new Vector(otherPoint.x - x, otherPoint.y - y);
  }
}

class Line {
  final long xMul;
  final long yMul;
  final long c;
  final Vector lineVector;

  public Line(Point through, Vector with) {
    this(through, -with.y, with.x);
  }

  public Line(Point through, long xMul, long yMul) {
    this(xMul, yMul, -(xMul * through.x + yMul * through.y));
  }

  public Line(long xMul, long yMul, long c) {
    this.xMul = xMul;
    this.yMul = yMul;
    this.c = c;
    this.lineVector = new Vector(yMul, -xMul);
  }

  public long applyPoint(Point point) {
    return xMul * point.x + yMul * point.y + c;
  }

  // if rotation from line vector towards point is clock-wise
  // towards positive c of the line
  public int CWLocation(Point point) {
    var appliedValue = applyPoint(point);
    return appliedValue == 0 ? 0 : (appliedValue > 0 ? 1 : -1);
  }

  public double getAngle() {
    return Math.atan2(lineVector.y, lineVector.x);
  }
}

class GeoM {
  public static boolean segmentsIntersection(Point p1, Point p2, Point p3, Point p4) {
    Line line1 = new Line(p1, p1.vectorToPoint(p2));
    Line line2 = new Line(p3, p3.vectorToPoint(p4));
    int posInfo1 = line1.CWLocation(p3) * line1.CWLocation(p4);
    int posInfo2 = line2.CWLocation(p1) * line2.CWLocation(p2);
    if (posInfo1 <= 0 && posInfo2 <= 0) {
      return true;
    }
    return false;
  }

  public static long crossProduct(Point p11, Point p12, Point p21, Point p22) {
    return (p12.x - p11.x) * (p22.y - p21.y) - (p12.y - p11.y) * (p22.x - p21.x);
  }

  public static long crossProduct(Vector vector1, Vector vector2) {
    return vector1.x * vector2.y - vector1.y * vector2.x;
  }

  public static List<Point> buildConvexHull(Point[] points) {
    List<Point> conv = new ArrayList<>();
    // outlier outcomes
    if (points.length == 1) {
      conv.add(points[0]);
      return conv;
    }
    if (points.length == 2) {
      conv.add(points[0]);
      conv.add(points[1]);
      return conv;
    }

    // get bottom -> right
    int s = 0;
    for (int i = 1; i < points.length; i++) {
      if (points[i].y < points[s].y || (points[i].y == points[s].y && points[i].x > points[s].x)) {
        s = i;
      }
    }
    conv.add(points[s]);
    points[s] = points[0];
    points[0] = conv.get(0);

    // sort
    Point point0 = points[0];
    Arrays.sort(
        points,
        (p1, p2) -> {
          if (p1 == point0) return -1;
          if (p2 == point0) return 1;
          long result = crossProduct(point0, p1, point0, p2);
          if (result != 0) return result > 0 ? -1 : 1;
          return Long.compare(
              point0.vectorToPoint(p1).magnitude2(), point0.vectorToPoint(p2).magnitude2());
        });

    // get second conv point
    int target = 1;
    while ((target < points.length - 1)
        && crossProduct(points[0], points[target + 1], points[0], points[1]) == 0) {
      target++;
    }
    conv.add(points[target]);
    if (target < points.length - 1) {
      conv.add(points[target + 1]);
    }

    // go through other
    for (int i = target + 2; i < points.length; i++) {
      while (crossProduct(
          conv.get(conv.size() - 2),
          conv.get(conv.size() - 1),
          conv.get(conv.size() - 1),
          points[i])
          <= 0) {
        conv.remove(conv.size() - 1);
        if (conv.size() == 1) {
          break;
        }
      }
      conv.add(points[i]);
    }

    return conv;
  }

  // hull order is CW from the lowest point
  public static List<List<Point>> buildEnvelops(List<Point> hull) {
    List<List<Point>> envelops = new ArrayList<>();
    envelops.add(new ArrayList<>());
    envelops.add(new ArrayList<>());

    Point leftTop = hull.get(0);
    int leftTopIndex = 0;
    Point leftBottom = hull.get(0);
    int leftBottomIndex = 0;
    for (int i = 1; i < hull.size(); i++) {
      Point inCheck = hull.get(i);
      if (inCheck.x < leftTop.x) {
        leftTop = inCheck;
        leftTopIndex = i;
        leftBottom = inCheck;
        leftBottomIndex = i;
      } else if (inCheck.x == leftTop.x && inCheck.y > leftTop.y) {
        leftTop = inCheck;
        leftTopIndex = i;
      } else if (inCheck.x == leftTop.x && inCheck.y < leftBottom.y) {
        leftBottom = inCheck;
        leftBottomIndex = i;
      }
    }

    envelops.get(0).add(leftBottom);
    envelops.get(1).add(leftTop);

    Point prevPoint = leftBottom;
    for (int i = 1; i < hull.size(); i++) {
      Point inCheck = hull.get((leftBottomIndex + i + hull.size()) % hull.size());
      if (inCheck.x > prevPoint.x) {
        envelops.get(0).add(inCheck);
        prevPoint = inCheck;
      } else {
        break;
      }
    }

    prevPoint = leftTop;
    for (int i = 1; i < hull.size(); i++) {
      Point inCheck = hull.get((leftTopIndex - i + hull.size()) % hull.size());
      if (inCheck.x > prevPoint.x) {
        envelops.get(1).add(inCheck);
        prevPoint = inCheck;
      } else {
        break;
      }
    }

    return envelops;
  }
}

class DecTree {
  private static Random random = new Random();

  private Node root = null;
  private int size = 0;

  private class Node {
    Long value;
    Point load;
    public Node left = null;
    public Node right = null;
    public int size = 0;
    private final long priority;

    public Node(Long value, Point load) {
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

  private Pair split(Node node, Long key) {
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

  public void insert(Long value, Point load) {
    if (find(value)) {
      Node tmp = root;
      while (tmp != null) {
        if (tmp.value.equals(value)) {
          tmp.load = load;
          return;
        }
        if (tmp.value > value) {
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

  public boolean find(Long value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value.equals(value)) {
        return true;
      }
      if (tmp.value > value) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return false;
  }

  public void delete(Long value) {
    if (!find(value)) {
      return;
    }
    size--;
    Pair pair = split(root, value);
    Pair leftPair = split(pair.first, value - 1);
    root = merge(leftPair.first, pair.second);
  }

  // delete by range of values, inclusive
  public void deleteRange(Long valueFrom, Long valueTo) {
    Pair pair = split(root, valueTo);
    Pair leftPair = split(pair.first, valueFrom - 1);
    root = merge(leftPair.first, pair.second);
    size -= leftPair.second == null ? 0 : leftPair.second.size;
  }

  public Point getKth(int k) {
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
  private static final long limit = 1500000000;

  record BinarySearchResult(int point1Index, Point point1, Point point2) {
  }

  DecTree upperBound = new DecTree();
  DecTree lowerBound = new DecTree();

  public Hull2D(List<Point> initialLowerBound, List<Point> initialUpperBound) {
    for (Point point : initialLowerBound) {
      lowerBound.insert(point.x, point);
    }
    for (Point point : initialUpperBound) {
      upperBound.insert(point.x, point);
    }
  }

  private BinarySearchResult getCoverSegment(DecTree bound, Long x) {
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

  private Point getL(DecTree bound, Point point, int l, int r, boolean upper) {
    if (r - l <= 1) {
      if (r == l) return bound.getKth(l);
      Point l1 = bound.getKth(l);
      Point l2 = bound.getKth(l + 1);
      Line segmentLine = new Line(l1, l1.vectorToPoint(l2));
      int location = segmentLine.CWLocation(point);
      if (upper ? location >= 0 : location <= 0) return l1;
      return l2;
    }
    int m = (l + r) / 2;
    Point m1 = bound.getKth(m);
    Point m2 = bound.getKth(m + 1);
    Line segmentLine = new Line(m1, m1.vectorToPoint(m2));
    int location = segmentLine.CWLocation(point);
    if (location == 0) {
      return m1;
    } else if (upper ? location < 0 : location > 0) {
      return getL(bound, point, m, r, upper);
    } else {
      return getL(bound, point, l, m, upper);
    }
  }

  private Point getR(DecTree bound, Point point, int l, int r, boolean upper) {
    if (r - l <= 1) {
      if (r == l) return bound.getKth(l);
      Point r1 = bound.getKth(r - 1);
      Point r2 = bound.getKth(r);
      Line segmentLine = new Line(r1, r1.vectorToPoint(r2));
      int location = segmentLine.CWLocation(point);
      if (upper ? location >= 0 : location <= 0) return r2;
      return r1;
    }
    int m = (l + r) / 2;
    Point m1 = bound.getKth(m - 1);
    Point m2 = bound.getKth(m);
    Line segmentLine = new Line(m1, m1.vectorToPoint(m2));
    int location = segmentLine.CWLocation(point);
    if (location == 0) {
      return m2;
    } else if (upper ? location >= 0 : location <= 0) {
      return getR(bound, point, m, r, upper);
    } else {
      return getR(bound, point, l, m, upper);
    }
  }

  public boolean isInside(Point point) {
    if (lowerBound.getSize() == 0) return false;
    if (lowerBound.getSize() == 1)
      return lowerBound.getKth(0).vectorToPoint(point).magnitude2() == 0;
    var coverLower = getCoverSegment(lowerBound, point.x);
    var coverUpper = getCoverSegment(upperBound, point.x);
    if (coverLower.point1Index == -1 || coverLower.point1Index == lowerBound.getSize()) {
      return false;
    }
    boolean cond1 =
        GeoM.segmentsIntersection(
            coverLower.point1, coverLower.point2, point, new Point(point.x, -limit));
    boolean cond2 =
        GeoM.segmentsIntersection(
            coverUpper.point1, coverUpper.point2, point, new Point(point.x, limit));
    return cond1 && cond2;
  }

  public void addPoint(Point point) {
    if (lowerBound.getSize() == 0) {
      lowerBound.insert(point.x, point);
      upperBound.insert(point.x, point);
      return;
    }
    if (lowerBound.getSize() == 1) {
      if (lowerBound.getKth(0).x == point.x) {
        if (lowerBound.getKth(0).y > point.y) {
          lowerBound.insert(point.x, point);
        }
        if (upperBound.getKth(0).y < point.y) {
          upperBound.insert(point.x, point);
        }
      } else {
        lowerBound.insert(point.x, point);
        upperBound.insert(point.x, point);
      }
      return;
    }

    if (isInside(point)) return;

    var coverLower = getCoverSegment(lowerBound, point.x);
    var coverUpper = getCoverSegment(upperBound, point.x);
    if (coverLower.point1Index == -1) {
      var toKeepL = getR(lowerBound, point, 0, lowerBound.getSize() - 1, false);
      var toKeepU = getR(upperBound, point, 0, upperBound.getSize() - 1, true);
      lowerBound.deleteRange(-limit, toKeepL.x - 1);
      upperBound.deleteRange(-limit, toKeepU.x - 1);
      lowerBound.insert(point.x, point);
      upperBound.insert(point.x, point);
    } else if (coverLower.point1Index == lowerBound.getSize()) {
      var toKeepL = getL(lowerBound, point, 0, lowerBound.getSize() - 1, false);
      var toKeepU = getL(upperBound, point, 0, upperBound.getSize() - 1, true);
      lowerBound.deleteRange(toKeepL.x + 1, limit);
      upperBound.deleteRange(toKeepU.x + 1, limit);
      lowerBound.insert(point.x, point);
      upperBound.insert(point.x, point);
    } else {
      boolean isAbove =
          GeoM.segmentsIntersection(
              coverLower.point1, coverLower.point2, point, new Point(point.x, -limit));
      if (isAbove) {
        var toKeepU1 = getL(upperBound, point, 0, coverUpper.point1Index, true);
        var toKeepU2 =
            getR(upperBound, point, coverUpper.point1Index + 1, upperBound.getSize() - 1, true);
        upperBound.deleteRange(toKeepU1.x + 1, toKeepU2.x - 1);
        if (toKeepU1.x == point.x) upperBound.delete(toKeepU1.x);
        if (toKeepU2.x == point.x) upperBound.delete(toKeepU2.x);
        upperBound.insert(point.x, point);
      } else {
        var toKeepL1 = getL(lowerBound, point, 0, coverLower.point1Index, false);
        var toKeepL2 =
            getR(lowerBound, point, coverLower.point1Index + 1, lowerBound.getSize() - 1, false);
        lowerBound.deleteRange(toKeepL1.x + 1, toKeepL2.x - 1);
        if (toKeepL1.x == point.x) lowerBound.delete(toKeepL1.x);
        if (toKeepL2.x == point.x) lowerBound.delete(toKeepL2.x);
        lowerBound.insert(point.x, point);
      }
    }
  }

  private Point searchTouchVertex(DecTree bound, double angle, boolean upper) {
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
    return bound.getKth(l);
  }

  public Point getAngleTouch(double angle) {
    if (angle >= Math.PI / -2 && angle <= Math.PI / 2) {
      return searchTouchVertex(upperBound, angle, true);
    } else {
      return searchTouchVertex(lowerBound, angle, false);
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
