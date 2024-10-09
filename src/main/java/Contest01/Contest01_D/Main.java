package Contest01.Contest01_D;

import java.util.*;

public class Main {
  private static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    int actionsNum = in.nextInt();
    in.nextLine();
    String actionList = in.nextLine();
    List<Integer> compressedActions = new ArrayList<>();
    List<Boolean> singleChange = new ArrayList<>();
    boolean lastRight = true;
    boolean multiple1 = false;
    boolean multiple2 = false;
    int counter = 0;
    for (int i = 0; i < actionsNum; i++) {
      if (actionList.charAt(i) != 'F') {
        if (counter != 0) {
          compressedActions.add(counter);
          counter = 0;
        }
        lastRight = actionList.charAt(i) == 'R';
        multiple2 = multiple1;
        multiple1 = true;
        continue;
      }
      if (counter == 0) singleChange.add(!multiple2);
      counter += lastRight ? 1 : -1;
    }
    if (counter != 0) {
      compressedActions.add(counter);
    }

    int usualAction = compressedActions.stream().reduce(0, Integer::sum);

    Set<Integer> possibleChanges = new HashSet<>();
    if (compressedActions.isEmpty()) {
      System.out.println((actionList.contains("R") ? 1 : 0) + (actionList.contains("L") ? 1 : 0));
      return;
    }

    // due to redirection inside
    int max = compressedActions.stream().max(Integer::compareTo).get();
    int min = compressedActions.stream().min(Integer::compareTo).get();
    for (int i = 0; i < max; i++) {
      possibleChanges.add(-1 - 2 * i);
    }
    for (int i = 0; i < min * -1; i++) {
      possibleChanges.add(1 + 2 * i);
    }
    // due to direction repeat
    if (max > 0) possibleChanges.add(-1);
    if (min < 0) possibleChanges.add(1);
    // due to full redirection
    for (int i = 1; i < compressedActions.size(); i++) {
      possibleChanges.add(compressedActions.get(i) * -2);
    }
    if (actionList.charAt(0) != 'F') {
      possibleChanges.add(compressedActions.get(0) * -2);
    }
    // due to extra forward
    boolean looksRight = true;
    boolean safeRightAdd = false;
    boolean safeLeftAdd = false;
    int pos = 0;
    int compressedCounter = 0;
    while (pos < actionList.length()) {
      if (looksRight && actionList.charAt(pos) == 'R') {
        possibleChanges.add(1);
        safeRightAdd = true;
      }
      if (!looksRight && actionList.charAt(pos) == 'L') {
        possibleChanges.add(-1);
        safeLeftAdd = true;
      }
      if (safeLeftAdd && safeRightAdd && compressedCounter > 0) break;
      if (actionList.charAt(pos) != 'F') {
        ++pos;
      } else {
        if (compressedCounter == 0 && pos > 0 && actionList.charAt(pos - 1) == 'L' && (pos == 1 || actionList.charAt(pos - 2) == 'R')) {
          possibleChanges.add(1 + compressedActions.get(0) * -2);
        }
        pos += Math.abs(compressedActions.get(compressedCounter));
        compressedCounter++;
      }
    }
    // due to compressed moves merge
    for (int i = 1; i < compressedActions.size(); i++) {
      if (singleChange.get(i)) {
        if (compressedActions.get(i) > 0 ^ compressedActions.get(i - 1) > 0) {
          possibleChanges.add(compressedActions.get(i) * -1 + (compressedActions.get(i - 1) > 0 ? 1 : 0));
        }
      }
    }


    System.out.println(possibleChanges.size());
  }
}
