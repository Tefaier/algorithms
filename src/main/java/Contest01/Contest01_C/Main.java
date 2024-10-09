package Contest01.Contest01_C;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
  private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) {
    var lines = in.lines().toList();
    int segmentsNum = Integer.parseInt(lines.get(0));
    List<Segment> segments = new ArrayList<>();
    for (int i = 0; i < segmentsNum; i++) {
      var parts = lines.get(i + 1).split(" ");
      segments.add(new Segment(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
    }
    segments = segments.stream().sorted(Comparator.comparingInt(Segment::bottomX)).toList();

    int[] rTolMinimum = new int[segmentsNum];
    rTolMinimum[segmentsNum - 1] = Integer.MAX_VALUE;
    int[] lTorMaximum = new int[segmentsNum];
    lTorMaximum[0] = Integer.MIN_VALUE;

    int max = Integer.MIN_VALUE;
    for (int i = 1; i < segmentsNum; ++i) {
      max = Math.max(max, segments.get(i - 1).topX());
      lTorMaximum[i] = max;
    }

    int min = Integer.MAX_VALUE;
    for (int i = segmentsNum - 2; i >= 0; --i) {
      min = Math.min(min, segments.get(i + 1).topX());
      rTolMinimum[i] = min;
    }

    int counter = 0;

    for (int i = 0; i < segmentsNum; ++i) {
      var currentSegment = segments.get(i);
      if ((i > 0 && currentSegment.bottomX() == segments.get(i - 1).bottomX()) || (i < segmentsNum - 1 && currentSegment.bottomX() == segments.get(i + 1).bottomX())) {
        continue;
      }
      if (currentSegment.topX() < rTolMinimum[i] && currentSegment.topX() > lTorMaximum[i]) {
        ++counter;
      }
    }

    System.out.println(counter);
  }
}

record Segment(int bottomX, int topX) {
}
