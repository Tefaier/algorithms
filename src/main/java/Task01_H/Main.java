package Task01_H;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  // stat K starts from 1, returns value of element that will be at this position
  // from starts inclusive, to end inclusive
  public static int getStatK(int[] arr, int k, int from, int to) {
    if (to - from == 1) {
      // == to because k is index + 1 and here to = from + 1, so if != to then it's k = to + 1 ONLY
      return k == to ? Math.min(arr[from], arr[to]) : Math.max(arr[from], arr[to]);
    } else if (from == to) {
      return arr[from];
    } else {
      // int pivot = getMiddle(Arrays.copyOfRange(arr, from, to + 1));
      int pivot = getMiddle(arr, from, to);
      // element including which after all >= pivot
      int pivotPos = partition(arr, pivot, from, to);
      // element including which after all > pivot
      int pivotsEnd = partition(arr, pivot + 1, pivotPos, to);
      if (k <= pivotPos) {
        return getStatK(arr, k, from, pivotPos - 1);
      } else if (k <= pivotsEnd) {
        return pivot;
      } else {
        return getStatK(arr, k, pivotsEnd, to);
      }
    }
  }

  // returns index at which pivot is (to the left all <, to the right (including) all >=)
  // also changes provided arr
  public static int partition(int[] arr, int pivot, int from, int to) {
    int i = (from - 1);
    for (int j = from; j <= to; j++) {
      if (arr[j] < pivot) {
        i++;
        swap(arr, i, j);
      }
    }
    // swap(arr, i + 1, to);
    return (i + 1);
  }

  // from and to inclusive
  public static int getMiddle(int[] arr, int from, int to) {
    // int[] answer = new int[(int) Math.ceil((to + 1 - from) / 5f)];
    ArrayList<Integer> answer = new ArrayList<>();
    for (int i = from; i <= to; i += 5) {
      answer.add(getMedium(arr, i, Math.min(i + 4, to)));
      // answer[i / 5] = getMedium(arr, i, i + 4);
    }
    int mid = (int) Math.ceil(answer.size() / 2f);
    return getStatK(answer.stream().mapToInt(i -> i).toArray(), mid, 0, answer.size() - 1);
  }

  public static int getMedium(int[] arr, int from, int to) {
    // int limit = Math.min(to + 1, arr.length);
    bubbleSort(arr, from, to);
    return arr[(to + from) / 2];
  }

  public static void bubbleSort(int[] arr, int from, int to) {
    for (int i = from; i <= to; i++) {
      for (int j = i + 1; j <= to; j++) {
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

    System.out.println(getStatK(array, stat, 0, array.length - 1));
  }
}
