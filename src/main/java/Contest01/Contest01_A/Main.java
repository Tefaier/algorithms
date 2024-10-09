package Contest01.Contest01_A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    char commaSign = ',';

    ArrayList<Integer> commaPos = new ArrayList<>();

    var words = Arrays.stream(in.nextLine().replace(" ,", ",").replace(",", ", ").split(" ")).toList();
    int maxLength = 0;
    for (int i = 0; i < words.size(); i++) {
      if (words.get(i).isEmpty()) continue;
      boolean hasComa = words.get(i).charAt(words.get(i).length() - 1) == commaSign;
      if (hasComa) {
        commaPos.add(i);
      }
      maxLength = Math.max(maxLength, words.get(i).length() - (hasComa ? 1 : 0));
    }

    StringBuilder answer = new StringBuilder();
    int lengthCounter = 0;
    int stringLimit = maxLength * 3;
    for (int i = 0; i < words.size(); i++) {
      String targetWord = words.get(i);
      if (targetWord.isEmpty()) continue;
      if (targetWord.length() + lengthCounter + (lengthCounter == 0 ? 0 : 1) <= stringLimit) {
        if (lengthCounter != 0) {
          lengthCounter += 1;
          answer.append(' ');
        }
        lengthCounter += targetWord.length();
        answer.append(targetWord);
      } else {
        lengthCounter = targetWord.length();
        answer.append('\n').append(targetWord);
      }
    }

    System.out.println(answer);
  }
}
