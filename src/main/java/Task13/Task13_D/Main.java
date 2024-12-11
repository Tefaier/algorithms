package Task13.Task13_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  private static void test() {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat df = new DecimalFormat("", symbols);
    Random random = new Random();
    while (true) {
      int boundingX = random.nextInt(2, 100);
      int boundingY = random.nextInt(2, 100);
      int towersNum = random.nextInt(1, 10);
      List<Point> towers = new ArrayList<>(towersNum);
      for (int i = 0; i < towersNum; i++) {
        Point newPoint = new Point(random.nextInt(1, boundingX), random.nextInt(1, boundingY));
        for (int j = 0; j < towers.size(); j++) {
          if (GeometryMethods.equalDoubles(newPoint.x, towers.get(j).x) && GeometryMethods.equalDoubles(newPoint.y, towers.get(j).y)) {
            newPoint = null;
            break;
          }
        }
        if (newPoint == null) continue;
        towers.add(newPoint);
      }
      towersNum = towers.size();

      Line OX1 = new Line(0.0, 1.0, 0.0);
      Line OX2 = new Line(0.0, -1.0, boundingY);
      Line OY1 = new Line(1.0, 0.0, 0.0);
      Line OY2 = new Line(-1.0, 0.0, boundingX);
      List<Line> boundingBox = new ArrayList<>();
      boundingBox.add(OX1);
      boundingBox.add(OX2);
      boundingBox.add(OY1);
      boundingBox.add(OY2);

      StringBuilder result = new StringBuilder();
      for (int from = 0; from < towersNum; from++) {
        Point fromT = towers.get(from);
        List<Line> middlePerps = new ArrayList<>(towersNum + 4);
        middlePerps.addAll(boundingBox);
        for (int to = 0; to < towersNum; to++) {
          if (to == from) continue;
          Point toT = towers.get(to);
          Vector normal = toT.vectorToPoint(fromT);
          middlePerps.add(
              new Line(
                  normal.x * 2,
                  normal.y * 2,
                  -normal.x * (fromT.x + toT.x) - normal.y * (fromT.y + toT.y)));
        }
        // output points are in CCW order
        var output = GeometryMethods.intersectLinesAsHalfplanes(middlePerps, false, false);
        // because it creates cyclic intersection and it is possible to have the point being created
        if (GeometryMethods.equalDoubles(
            output.get(output.size() - 1).vectorToPoint(output.get(output.size() - 2)).magnitude2(),
            0.0)) {
          output.remove(output.size() - 1);
        }
        double minX = boundingX;
        double minY = boundingY;
        int minIndex = 0;
        for (int i = 0; i < output.size(); i++) {
          Point compared = output.get(i);
          if (compared.x < minX || (compared.x == minX && compared.y < minY)) {
            minIndex = i;
            minX = compared.x;
            minY = compared.y;
          }
        }
        result.append(output.size());
        for (int i = 0; i < output.size(); i++) {
          Point point = output.get((minIndex + i) % output.size());
          double x = GeometryMethods.equalDoubles(point.x, 0.0) ? 0.0 : point.x;
          double y = GeometryMethods.equalDoubles(point.y, 0.0) ? 0.0 : point.y;
          result.append(' ').append(df.format(x)).append(' ').append(df.format(y));
        }
        result.append('\n');
      }
      System.out.println(result);
      System.out.println("-----");
    }
  }

  public static void main(String[] args) {
    // test();
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat df = new DecimalFormat("", symbols);
    int boundingX = in.nextInt();
    int boundingY = in.nextInt();
    int towersNum = in.nextInt();
    List<Point> towers = new ArrayList<>(towersNum);
    for (int i = 0; i < towersNum; i++) {
      towers.add(new Point(in.nextInt(), in.nextInt()));
    }

    Line OX1 = new Line(0.0, 1.0, 0.0);
    Line OX2 = new Line(0.0, -1.0, boundingY);
    Line OY1 = new Line(1.0, 0.0, 0.0);
    Line OY2 = new Line(-1.0, 0.0, boundingX);
    List<Line> boundingBox = new ArrayList<>();
    boundingBox.add(OX1);
    boundingBox.add(OX2);
    boundingBox.add(OY1);
    boundingBox.add(OY2);

    StringBuilder result = new StringBuilder();
    for (int from = 0; from < towersNum; from++) {
      Point fromT = towers.get(from);
      List<Line> middlePerps = new ArrayList<>(towersNum + 4);
      middlePerps.addAll(boundingBox);
      for (int to = 0; to < towersNum; to++) {
        if (to == from) continue;
        Point toT = towers.get(to);
        Vector normal = toT.vectorToPoint(fromT);
        middlePerps.add(
            new Line(
                normal.x,
                normal.y,
                -normal.x * (fromT.x + toT.x) / 2 - normal.y * (fromT.y + toT.y) / 2));
      }
      // output points are in CCW order
      var output = GeometryMethods.intersectLinesAsHalfplanes(middlePerps, false, false);
      // because it creates cyclic intersection and it is possible to have the point being created
      if (GeometryMethods.equalDoubles(
          output.get(output.size() - 1).vectorToPoint(output.get(output.size() - 2)).magnitude2(),
          0.0)) {
        output.remove(output.size() - 1);
      }
      double minX = boundingX;
      double minY = boundingY;
      int minIndex = 0;
      for (int i = 0; i < output.size(); i++) {
        Point compared = output.get(i);
        if (compared.x < minX || (compared.x == minX && compared.y < minY)) {
          minIndex = i;
          minX = compared.x;
          minY = compared.y;
        }
      }
      result.append(output.size());
      for (int i = 0; i < output.size(); i++) {
        Point point = output.get((minIndex + i) % output.size());
        double x = GeometryMethods.equalDoubles(point.x, 0.0) ? 0.0 : point.x;
        double y = GeometryMethods.equalDoubles(point.y, 0.0) ? 0.0 : point.y;
        result.append(' ').append(df.format(x)).append(' ').append(df.format(y));
      }
      result.append('\n');
    }
    System.out.println(result);
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
  private static MathContext bidDecimalContext = new MathContext(10, RoundingMode.HALF_UP);
  public static Point originPoint = new Point(0, 0);
  public static Double calculationBoundingBox = (double) Long.MAX_VALUE;
  public static Line boxD = new Line(0, 1, calculationBoundingBox);
  public static Line boxU = new Line(0, -1, calculationBoundingBox);
  public static Line boxR = new Line(-1, 0, calculationBoundingBox);
  public static Line boxL = new Line(1, 0, calculationBoundingBox);

  public static boolean equalDoubles(double d1, double d2) {
    return Math.abs(d1 - d2) < 1e-30;
  }

  public static double distancePointToLine2(Point point, Line line) {
    return Math.pow(line.applyPoint(point), 2.0)
        / (Math.pow(line.xMul, 2.0) + Math.pow(line.yMul, 2.0));
  }

  public static double dotProduct(Vector vector1, Vector vector2) {
    return vector1.x * vector2.x + vector1.y * vector2.y;
  }

  // cross product CW from vector 1 to vector 2
  public static double crossProduct(Vector vector1, Vector vector2) {
    return vector1.x * vector2.y - vector1.y * vector2.x;
  }

  public static Point linesIntersection(Line line1, Line line2) {
    return linesIntersection(line1, line2, false);
  }

  public static Point linesIntersection(Line line1, Line line2, boolean increasedPrecision) {
    var coeff = line1.xMul * line2.yMul - line2.xMul * line1.yMul;
    if (equalDoubles(coeff, 0.0)) {
      return null;
    }
    if (increasedPrecision) {
      BigDecimal partUp1 = new BigDecimal(line1.yMul * line2.c - line2.yMul * line1.c);
      BigDecimal partUp2 = new BigDecimal(line1.c * line2.xMul - line2.c * line1.xMul);
      BigDecimal divisor = new BigDecimal(coeff);
      return new Point(
          partUp1.divide(divisor, bidDecimalContext).doubleValue(),
          partUp2.divide(divisor, bidDecimalContext).doubleValue());
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
    return linesIntersection(line, forward ? boxD : boxU);
  }

  public static boolean outOfBoundingBox(Point point) {
    return Math.max(Math.abs(point.x), Math.abs(point.y)) > calculationBoundingBox;
  }

  public static ArrayList<Point> createIntersectionPoints(
      ArrayList<Line> group, boolean withInfinities, boolean cyclic, boolean precise) {
    ArrayList<Point> points = new ArrayList<>();
    if (withInfinities) points.add(makeLineInfiniteCut(group.get(0), false));
    for (int i = 1; i < group.size(); i++) {
      points.add(linesIntersection(group.get(i - 1), group.get(i), precise));
    }
    if (cyclic && group.size() > 1)
      points.add(linesIntersection(group.get(0), group.get(group.size() - 1), precise));
    if (withInfinities) points.add(makeLineInfiniteCut(group.get(group.size() - 1), true));
    return points;
  }

  public static ArrayList<Point> intersectLinesAsHalfplanes(
      List<Line> lines, boolean expectGoodInput, boolean simpleOutput) {
    if (!expectGoodInput) {
      lines.add(boxD);
      lines.add(boxU);
      lines.add(boxR);
      lines.add(boxL);
      lines.sort(Comparator.comparingDouble(Line::getAngle));
    }

    LinkedList<Line> activeLines = new LinkedList<>();
    for (Line line : lines) {
      if (activeLines.size() >= 1) {
        // check it is parallel to the last in list
        if (linesIntersection(activeLines.get(activeLines.size() - 1), line) == null) {
          int location = line.CWLocation(activeLines.get(activeLines.size() - 1).getSamplePoint());
          if (location <= 0) {
            if (dotProduct(activeLines.get(activeLines.size() - 1).lineVector, line.lineVector)
                < 0) {
              // opposite direction
              return new ArrayList<>();
            }
          } else {
            continue;
          }
        }
      }

      if (activeLines.size() == 0) {
        activeLines.addLast(line);
        continue;
      }
      // logic based on if above
      if (activeLines.size() == 1) {
        if (linesIntersection(activeLines.get(activeLines.size() - 1), line) == null) {
          activeLines.pollLast();
        }
        activeLines.addLast(line);
        continue;
      }

      // now not parallel to the last for sure
      Point cut =
          linesIntersection(
              activeLines.get(activeLines.size() - 1), activeLines.get(activeLines.size() - 2));
      while (line.CWLocation(cut) <= 0) {
        activeLines.pollLast();
        // check on creation of single point intersection
        if (crossProduct(activeLines.getLast().lineVector, line.lineVector) <= 0.0)
          return new ArrayList<>();
        if (activeLines.size() == 1) {
          break;
        }
        cut =
            linesIntersection(
                activeLines.get(activeLines.size() - 1), activeLines.get(activeLines.size() - 2));
      }

      // can't be 0 here
      if (activeLines.size() == 1) {
        activeLines.addLast(line);
        continue;
      }

      cut = linesIntersection(activeLines.get(0), activeLines.get(1));
      while (line.CWLocation(cut) <= 0) {
        activeLines.pollFirst();
        // can't be single point anymore after check in previous while
        if (activeLines.size() == 1) {
          return new ArrayList<>();
        }
        cut = linesIntersection(activeLines.get(0), activeLines.get(1));
      }

      activeLines.addLast(line);
    }

    if (simpleOutput) {
      ArrayList<Point> output = new ArrayList<>();
      output.add(activeLines.getFirst().getSamplePoint());
      return output;
    }

    return createIntersectionPoints(new ArrayList<>(activeLines), false, true, true);
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
