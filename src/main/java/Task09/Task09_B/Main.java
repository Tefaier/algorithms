package Task09.Task09_B;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;

import java.util.*;

public class Main {
  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    StringBuilder pattern = new StringBuilder(in.nextLine());
    StringBuilder text = new StringBuilder(in.nextLine());
    System.out.println(doTask(pattern, text));
  }

  private static long doTask(StringBuilder pattern, StringBuilder text) {
    var result1 = StringHandler.zFunction(text, pattern);
    var result2 = StringHandler.zFunction(text.reverse(), pattern.reverse());
    for (int i = 0; i < result2.length / 2; i++) {
      int temp = result2[i];
      result2[i] = result2[result2.length - 1 - i];
      result2[result2.length - 1 - i] = temp;
    }
    long answer = result1[0] == pattern.length() ? 1 : 0;
    for (int i = 1; i < result1.length; ++i) {
      if (result1[i] == pattern.length()) {
        ++answer;
      }
      int over = Math.min(result1[i], pattern.length() - 1) + Math.min(result2[i - 1], pattern.length() - 1);
      answer += Math.max(0, over + 1 - pattern.length());
    }
    return answer / (pattern.length() / StringHandler.getPeriod(pattern));
  }
}

class StringHandler {
  public static int[] prefixFunction(String suffixString, String prefixString, boolean endPriotity) {
    int[] start = new int[prefixString.length()];

    for (int i = 1; i < start.length; ++i) {
      int current = start[i - 1];
      while (prefixString.charAt(i) != prefixString.charAt(current) && current > 0)
        current = start[current - 1];
      if (prefixString.charAt(i) == prefixString.charAt(current))
        start[i] = current + 1;
    }
    if (suffixString == null) return start;

    int[] end = new int[endPriotity ? Math.min(suffixString.length(), prefixString.length()) : suffixString.length()];
    int from = suffixString.length() - end.length;
    end[0] = suffixString.charAt(from) == prefixString.charAt(0) ? 1 : 0;
    for (int i = from + 1; i < suffixString.length(); ++i) {
      int current = end[i - from - 1];
      while ((current == prefixString.length() || suffixString.charAt(i) != prefixString.charAt(current)) && current > 0)
        current = start[current - 1];
      if (suffixString.charAt(i) == prefixString.charAt(current))
        end[i - from] = current + 1;
    }

    return end;
  }

  public static int[] zFunction(StringBuilder functionString, StringBuilder prefixString) {
    int[] start = new int[prefixString.length()];

    int left = 0;
    int right = 0;
    for (int i = 1; i < start.length; ++i) {
      start[i] = Math.max(0, Math.min(right - i, start[i - left]));
      while (start[i] + i < prefixString.length() && prefixString.charAt(start[i]) == prefixString.charAt(start[i] + i))
        ++start[i];
      if (start[i] + i > right) {
        left = i;
        right = start[i] + i;
      }
    }
    if (functionString == null) return start;

    int[] end = new int[functionString.length()];
    left = 0;
    right = 0;
    for (int i = 0; i < functionString.length(); ++i) {
      end[i] = Math.max(0, Math.min(right - i, (i - left >= start.length) ? 0 : start[i - left]));
      while (end[i] + i < functionString.length() && end[i] < prefixString.length() && prefixString.charAt(end[i]) == functionString.charAt(end[i] + i))
        ++end[i];
      if (end[i] + i > right) {
        left = i;
        right = end[i] + i;
      }
    }
    return end;
  }

  public static int getPeriod(StringBuilder str) {
    int[] z = new int[str.length()];

    int left = 0;
    int right = 0;
    for (int i = 1; i < z.length; ++i) {
      z[i] = Math.max(0, Math.min(right - i, z[i - left]));
      while (z[i] + i < str.length() && str.charAt(z[i]) == str.charAt(z[i] + i))
        ++z[i];
      if (z[i] + i > right) {
        left = i;
        right = z[i] + i;
        if (right == z.length && str.length() % i == 0) {
          return i;
        }
      }
    }
    return z.length;
  }
}
