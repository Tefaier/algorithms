package Contest01.Contest01_B;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
  private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
  //private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    /*
    ArrayList<String> lines = new ArrayList<>();
    while (true) {
      lines.add(in.nextLine());
      if (Objects.equals(lines.get(lines.size() - 1), "")) break;
    }

     */
    var lines = in.lines().toList();
    int wordsNum = Integer.parseInt(Arrays.stream(lines.get(0).split(" ")).toList().get(0));
    int requestNum = Integer.parseInt(Arrays.stream(lines.get(0).split(" ")).toList().get(1));
    List<String> dict = new ArrayList<>(lines.subList(1, wordsNum + 1));
    List<String> requests = new ArrayList<>(lines.subList(wordsNum + 1, wordsNum + requestNum + 1));

    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requests.size(); i++) {
      var values = Arrays.stream(requests.get(i).split(" ")).toList();

      int order = Integer.parseInt(values.get(0));
      String key = values.get(1);

      var bounds = getBounds(dict, key);
      if (bounds == null || bounds.to() - bounds.from() + 1 < order) {
        answer.append("-1").append('\n');
      } else {
        answer.append(bounds.from() + order).append('\n');
      }
    }

    System.out.println(answer);
  }

  private static Pair getBounds(List<String> dict, String key) {
    int l = -1;
    int r = dict.size() - 1;
    while (true) {
      if (r == l + 1) break;
      int m = (r + l) / 2;
      if (startsWith(dict.get(m), key) > 0) {
        l = m;
      } else {
        r = m;
      }
    }

    int leftFound = r;
    if (startsWith(dict.get(r), key) != 0) {
      return null;
    }

    l = r;
    r = dict.size();
    while (true) {
      if (r == l + 1) break;
      int m = (r + l) / 2;
      if (startsWith(dict.get(m), key) >= 0) {
        l = m;
      } else {
        r = m;
      }
    }
    int rightFound = l;
    return new Pair(leftFound, rightFound);
  }

  private static int startsWith(String word, String key) {
    for (int i = 0; i < Math.min(key.length(), word.length()); i++) {
      if (word.charAt(i) != key.charAt(i)) return key.charAt(i) - word.charAt(i);
    }
    return key.length() > word.length() ? 1 : 0;
  }
}

record Pair(int from, int to) {
}
