package Task09.Task09_C.ForReview;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// O(|N|^2 * |L|), N - number of words, L - maximum length of word, so 10^11 in given task

public class Main {
  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    int wordNum = Integer.parseInt(in.nextLine());
    List<String> words = new ArrayList<>();

    for (int i = 0; i < wordNum; ++i) {
      words.add(in.nextLine());
    }

    List<Integer> values = new ArrayList<>();
    for (int w1 = 0; w1 < wordNum; w1++) {
      for (int w2 = 0; w2 < wordNum; w2++) {
        if (w1 == w2) continue;
        String concatedWord = words.get(w1) + words.get(w2);
        if (StringHandler.palindromCheck(concatedWord)) {
          values.add(w1 + 1);
          values.add(w2 + 1);
        }
      }
    }

    System.out.println(values.size() / 2);
    for (int i = 0; i < values.size() / 2; ++i) {
      System.out.println(values.get(i * 2) + " " + values.get(i * 2 + 1));
    }
  }
}

class StringHandler {
  public static boolean palindromCheck(String string) {
    boolean answer = true;
    for (int i = 0; i < string.length() / 2; ++i) {
      if (string.charAt(i) != string.charAt(string.length() - 1 - i)) {
        answer = false;
        break;
      }
    }

    return answer;
  }
}
