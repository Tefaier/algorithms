package Task13.Task13_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
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

  public static Point linesIntersection(Line line1, Line line2) {
    var coeff = line1.xMul * line2.yMul - line2.xMul * line1.yMul;
    if (equalDoubles(coeff, 0.0)) {
      return null;
    }
    return new Point((line1.yMul * line2.c - line2.yMul * line1.c) / coeff, (line1.c * line2.xMul - line2.c * line1.xMul) / coeff);
  }

  public static Optional<Point> segmentsIntersection(Point p1, Point p2, Point p3, Point p4) {
    Line line1 = new Line(p1, p1.vectorToPoint(p2));
    Line line2 = new Line(p3, p3.vectorToPoint(p4));
    int posInfo1 = line1.CWLocation(p3) * line1.CWLocation(p4);
    int posInfo2 = line2.CWLocation(p1) * line2.CWLocation(p2);
    if (posInfo1 == -1 && posInfo2 == -1) {
      return Optional.of(GeometryMethods.linesIntersection(line1, line2));
    }
    if (posInfo1 == 0) {
      return Optional.of(line1.CWLocation(p3) == 0 ? p3 : p4);
    }
    if (posInfo2 == 0) {
      return Optional.of(line2.CWLocation(p1) == 0 ? p1 : p2);
    }
    return Optional.empty();
  }

  // lines must be sorted
  // returns them as subsequence
  public static ArrayList<Line> createArc(ArrayList<Line> group) {
    ArrayList<Line> stack = new ArrayList<>();
    for (Line line : group) {
      while (stack.size() >= 2 && line.CWLocation(linesIntersection(stack.get(stack.size() - 1), stack.get(stack.size() - 2))) < 0) {
        stack.remove(stack.size() - 1);
      }
      stack.add(line);
    }
    return stack;
  }

  private static Point makeLineInfiniteCut(Line line, boolean forward) {
    double angle = line.getAngle();
    if (angle > -Math.PI / 4 && angle <= Math.PI / 4) {
      return linesIntersection(line, forward ? boxR : boxL);
    }
    if (angle > Math.PI / 4 && angle <= 3 * Math.PI / 4) {
      return linesIntersection(line, forward ? boxU : boxD);
    }
    if (angle > 3 * Math.PI / 4 || angle <= -3 * Math.PI / 4) {
      return linesIntersection(line, forward ? boxL : boxR);
    }
    return linesIntersection(line, forward ? boxL : boxR);
  }

  public static ArrayList<Point> createIntersectionPoints(ArrayList<Line> group, boolean withInfinities) {
    ArrayList<Point> points = new ArrayList<>();
    if (withInfinities) points.add(makeLineInfiniteCut(group.get(0), false));
    for (int i = 1; i < group.size(); i++) {
      points.add(linesIntersection(group.get(i - 1), group.get(i)));
    }
    if (withInfinities) points.add(makeLineInfiniteCut(group.get(group.size() - 1), true));
    return points;
  }

  // bottom, top in increasing angle order
  // expected to have no useless bounding box lines?
  public static ArrayList<Point> mergeArcs(ArrayList<Line> bottom, ArrayList<Line> top) {
    ArrayList<Point> pointsBottom = createIntersectionPoints(bottom, true);
    ArrayList<Point> pointsTop = createIntersectionPoints(top, true);
    Collections.reverse(pointsTop);
    ArrayList<Point> newPointsBottom = new ArrayList<>();
    ArrayList<Point> newPointsTop = new ArrayList<>();
    int i = 1;
    int j = 1;
    int intersectionsNumber = 0;
    while (i < pointsBottom.size() && j < pointsTop.size() && intersectionsNumber < 2) {
      var intersection = segmentsIntersection(pointsBottom.get(i), pointsBottom.get(i - 1), pointsTop.get(j), pointsTop.get(j - 1));
      if (intersection.isPresent() && intersection.get() != pointsBottom.get(i - 1) && intersection.get() != pointsTop.get(j - 1)) {
        ++intersectionsNumber;
        newPointsBottom.add(intersection.get());
        newPointsTop.add(intersection.get());
      }
      if (pointsBottom.get(i).x < pointsTop.get(j).x) {
        if (intersectionsNumber == 1 && pointsBottom.get(i) != newPointsBottom.get(newPointsBottom.size() - 1)) {
          newPointsBottom.add(pointsBottom.get(i));
        }
        i++;
      } else {
        if (intersectionsNumber == 1 && pointsTop.get(i) != newPointsTop.get(newPointsBottom.size() - 1)) {
          newPointsTop.add(pointsTop.get(j));
        }
        j++;
      }
    }
    ArrayList<Point> ans = new ArrayList<>();
    for (int k = 0; k < newPointsTop.size(); k++) {
      ans.add(newPointsTop.get(i));
    }
    // the same point of intersection is ignored
    for (int k = newPointsBottom.size() - 2; k > 0; k--) {
      ans.add(newPointsBottom.get(k));
    }
    return ans;
  }

  public static ArrayList<Point> intersectLines(List<Line> lines) {
    lines.sort((line1, line2) -> Double.compare(line1.getAngle(), line2.getAngle()));
    ArrayList<Line> group1 = new ArrayList<>();
    ArrayList<Line> group2 = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).getAngle() < Math.PI / 2) {
        group1.add(lines.get(i));
      } else {
        group2.add(lines.get(i));
      }
    }
    ArrayList<Line> bottom = createArc(group1);
    ArrayList<Line> top = createArc(group2);
    return mergeArcs(bottom, top);
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
