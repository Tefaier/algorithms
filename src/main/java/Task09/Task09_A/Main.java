package Task09.Task09_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    int wordNum = in.nextInt();
    in.nextLine();
    String[] words = in.nextLine().split(" ");
    StringBuilder base = new StringBuilder(words[0]);
    for (int i = 1; i < words.length; ++i) {
      var result = StringHandler.prefixFunction(base, words[i], true);

      base.append(words[i].substring(result[result.length - 1]));
    }
    System.out.println(base);
  }
}

class StringHandler {
  public static int[] prefixFunction(StringBuilder suffixString, String prefixString, boolean endPriotity) {
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
}