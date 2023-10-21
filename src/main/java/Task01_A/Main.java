package Task01_A;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
  public static int binarySearch(int left, int right, int key, int[] arr, boolean first) {
    while (left + 1 < right) {
      int mid = (left + right) / 2;
      if ((first && arr[mid] < key) || (!first && arr[mid] <= key)) {
        left = mid;
      } else {
        right = mid;
      }
    }

    if (first) {
      if (right < arr.length && arr[right] == key) {
        return right;
      } else {
        return -1 - right;
      }
    } else {
      if (left >= 0 && arr[left] == key) {
        return left;
      } else {
        return -1 - left;
      }
    }
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    ArrayList<Integer> values = new ArrayList<>();
    ArrayList<Integer> answer = new ArrayList<>();
    ArrayList<Integer> sorted = new ArrayList<>();

    int valuesNumber = input.nextInt();

    for (int i = 0; i < valuesNumber; i++) {
      values.add(input.nextInt());
    }

    for (int i = valuesNumber - 1; i > -1; i--) {

    }
  }
}
