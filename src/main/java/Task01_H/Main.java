package Task01_H;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

  // stat K starts from 1, returns value of element that will be at this position
  public static int getStatK(int[] arr, int k) {
    if (arr.length == 2) {
      return k == 1 ? Math.min(arr[0], arr[1]) : Math.max(arr[0], arr[1]);
    } else if (arr.length == 1) {
      return arr[0];
    } else {
      int pivotPos = partition(arr, 0, arr.length - 1);
      if (k <= pivotPos) {
        return getStatK(Arrays.copyOfRange(arr, 0, pivotPos), k);
      } else {
        return getStatK(Arrays.copyOfRange(arr, pivotPos, arr.length), k - pivotPos + 1);
      }
    }
  }

  // returns index at which pivot is (to the left all <, to the right (including) all >=)
  // also changes provided arr
  public static int partition(int[] arr, int from, int to) {
    int pivot = getMiddle(Arrays.copyOfRange(arr, from, to + 1));

    int i = (from - 1);
    for (int j = from; j <= to; j++) {
      if (arr[j] < pivot) {
        i++;
        swap(arr, i, j);
      }
    }
    //swap(arr, i + 1, to);
    return (i + 1);
  }

  public static int getMiddle(int[] arr) {
    int[] middles = getFivesMiddle(arr);
    return getStatK(middles, middles.length / 2 + 1);
  }

  public static int[] getFivesMiddle(int[] arr) {
    int[] answer = new int[(int) Math.ceil(arr.length / 5f)];
    for (int i = 0; i < arr.length; i += 5) {
      answer[i / 5] = getMedium(arr, i, i + 4);
    }
    return answer;
  }

  public static int getMedium(int[] arr, int from, int to) {
    int limit = Math.min(to + 1, arr.length);
    int[] unit = Arrays.copyOfRange(arr, from, limit);
    bubbleSort(unit);
    return unit[unit.length / 2];
  }

  public static void bubbleSort(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
      for (int j = i + 1; j < arr.length; j++) {
        if (arr[i] > arr[j]) {
          swap(arr, i, j);
        }
      }
    }
  }

  public static void swap(int[] arr, int i1, int i2) {
    int temp = arr[i1];
    arr[i1] = arr[i2];
    arr[i2] = temp;
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    int mod = 10004321;

    int number = input.nextInt();
    int stat = input.nextInt();
    int value1 = input.nextInt();
    int value2 = input.nextInt();

    int[] array = new int[number];
    array[0] = value1;
    array[1] = value2;
    for (int i = 2; i < number; i++) {
      array[i] = (array[i - 1] * 123 + array[i - 2] * 45) % mod;
    }

    System.out.print(getStatK(array, stat));
  }
}
