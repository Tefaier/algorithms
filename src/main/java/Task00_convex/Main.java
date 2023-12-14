package Task00_convex;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
  record Point(long x, long y) {
    @Override
    public String toString() {
      return x + " " + y;
    }
  }

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int pointsNumber = in.nextInt();
    Point[] points = new Point[pointsNumber];
    for (int i = 0; i < pointsNumber; i++) {
      points[i] = new Point(in.nextInt(), in.nextInt());
    }
    List<Point> conv = getConvex(points);

    System.out.println(conv.size());
    System.out.println(conv.stream().map(Point::toString).collect(Collectors.joining("\n")));
  }

  private static List<Point> getConvex(Point[] points) {
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
    for (int i = points.length - 1; i > 1; i--) {
      for (int j = 1; j < i; j++) {
        if (vect(points[0], points[j], points[0], points[j + 1]) < 0 ||
            (vect(points[0], points[j], points[0], points[j + 1]) == 0 &&
                dist2(points[0], points[j]) > dist2(points[0], points[j + 1]))
        ) {
          Point tmp = points[j];
          points[j] = points[j + 1];
          points[j + 1] = tmp;
        }
      }
    }

    // get second conv point
    int target = 1;
    while ((target < points.length - 1) && vect(points[0], points[target + 1], points[0], points[1]) == 0) {
      target++;
    }
    conv.add(points[target]);
    if (target < points.length - 1) {
      conv.add(points[target + 1]);
    }

    // go through other
    for (int i = target + 2; i < points.length; i++) {
      while (vect(conv.get(conv.size() - 2), conv.get(conv.size() - 1), conv.get(conv.size() - 1), points[i]) <= 0) {
        conv.remove(conv.size() - 1);
        if (conv.size() == 1) {
          break;
        }
      }
      conv.add(points[i]);
    }

    return conv;
  }

  private static long vect(Point a1, Point a2, Point b1, Point b2) {
    return (a2.x - a1.x) * (b2.y - b1.y) - (b2.x - b1.x) * (a2.y - a1.y);
  }

  private static long dist2(Point a, Point b) {
    return (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
  }
}
