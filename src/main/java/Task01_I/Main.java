package Task01_I;

import java.util.*;

public class Main {

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    int number = input.nextInt();

    Long[] values = new Long[number];
    for (int i = 0; i < values.length; i++) {
      values[i] = input.nextLong();
    }

    ArrayDeque[] bitValues = new ArrayDeque[256];
    for (int i = 0; i < bitValues.length; i++) {
      bitValues[i] = new ArrayDeque<Long>();
    }

    for (int k = 0; k < 8; k++) {
      for (int i = 0; i < values.length; i++) {
        int bucket = (int) (values[i] >> (k * 8)) & 255;
        bitValues[bucket].addLast(values[i]);
      }
      int j = 0;
      for (int i = 0; i < bitValues.length; i++) {
        while (!bitValues[i].isEmpty()) {
          values[j] = (long) bitValues[i].removeFirst();
          j++;
        }
      }
    }

    for (int i = 0; i < values.length; i++) {
      System.out.println(values[i]);
    }
  }
}
