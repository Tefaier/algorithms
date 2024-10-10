package Task11.Task11_E;

/*
  private static void test() {
    while (true) {
      Random random = new Random();
      int length1 = random.nextInt(100) + 1;
      int length2 = random.nextInt(100) + 1;

      Integer[] arr = new Integer[length1 + length2];
      Integer[] arr1 = new Integer[length1];
      for (int i = 0; i < length1; i++) {
        arr1[i] = random.nextInt(10000) - 5000;
        arr[i] = arr1[i];
      }

      Integer[] arr2 = new Integer[length2];
      for (int i = 0; i < length2; i++) {
        arr2[i] = random.nextInt(10000) - 5000;
        arr[i + arr1.length] = arr2[i];
      }

      Arrays.sort(arr);
      ArrayMethods.merge(arr1, arr2);
      for (int i = 0; i < length1; i++) {
        if (!Objects.equals(arr[i], arr1[i])) {
          System.out.print("ERROR");
        }
      }
      for (int i = 0; i < length2; i++) {
        if (!Objects.equals(arr[i + length1], arr2[i])) {
          System.out.print("ERROR");
        }
      }
    }
  }
 */

import java.io.DataInputStream;
import java.io.InputStream;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int length1 = in.nextInt();
    int length2 = in.nextInt();

    Integer[] arr1 = new Integer[length1];
    for (int i = 0; i < length1; i++) {
      arr1[i] = in.nextInt();
    }

    Integer[] arr2 = new Integer[length2];
    for (int i = 0; i < length2; i++) {
      arr2[i] = in.nextInt();
    }

    ArrayMethods.merge(arr1, arr2);

    for (int i : arr1) {
      System.out.print(i + " ");
    }
    for (int i : arr2) {
      System.out.print(i + " ");
    }
  }

  private static <T extends Comparable<T>> T get(T[] arr) {
    return arr[0];
  }
}

class ArrayMethods {
  private static class ArrayProxy<T> {
    public T[] arr1;
    public T[] arr2;
    public int length;

    public ArrayProxy(T[] arr1, T[] arr2) {
      this.arr1 = arr1;
      this.arr2 = arr2;
      length = arr1.length + arr2.length;
    }

    public T get(int index) {
      if (index >= arr1.length) {
        return arr2[index - arr1.length];
      } else {
        return arr1[index];
      }
    }

    public void set(int index, T value) {
      if (index >= arr1.length) {
        arr2[index - arr1.length] = value;
      } else {
        arr1[index] = value;
      }
    }

    public void swap(int index1, int index2) {
      T val = get(index1);
      set(index1, get(index2));
      set(index2, val);
    }
  }

  public static <T extends Comparable<T>> void merge(T[] arrLeft, T[] arrRight) {
    ArrayProxy<T> proxy = new ArrayProxy<>(arrLeft, arrRight);
    merge(proxy);
  }

  private static <T extends Comparable<T>> void merge(ArrayProxy<T> arr) {
    int middle = (arr.length + 1) / 2;
    forcePartSort(arr, middle, arr.length - 1, arr.length - middle, -1);
    headSortAdd(arr, (middle / 2) - 1, middle);
    swipeElement(arr, 0, true, arr.length);
  }

  // limit is excluded
  private static <T extends Comparable<T>> void swipeElement(
      ArrayProxy<T> arr, int index, boolean toRight, int limit) {
    int newIndex;
    while ((newIndex = toRight ? index + 1 : index - 1) != limit) {
      if (arr.get(index).compareTo(arr.get(newIndex)) * (toRight ? 1 : -1) > 0) {
        arr.swap(index, newIndex);
        index = newIndex;
      } else {
        break;
      }
    }
  }

  // right is finish of part to sort
  // knownSorted is start of part to merge with
  private static <T extends Comparable<T>> void headSortAdd(
      ArrayProxy<T> arr, int right, int knownSorted) {
    if (knownSorted <= 1) return;
    forcePartSort(arr, 0, right, right + 1, 1);
    outerMerge(arr, 0, right, knownSorted, arr.length - 1, right + 1);
    swipeElement(arr, arr.length - 1, false, right);
    headSortAdd(arr, (right - 1) / 2, right + 1);
  }

  private static <T extends Comparable<T>> void forcePartSort(
      ArrayProxy<T> arr, int left, int right, int initialRange, int useDirection) {
    if (left == right) return;
    int m = (left + right) / 2;
    int leftNew = left + initialRange * useDirection;
    int middleNew = m + 1 + initialRange * useDirection;
    int rightNew = right + initialRange * useDirection;
    forcePartSort(arr, leftNew, middleNew - 1, initialRange, useDirection * -1);
    forcePartSort(arr, middleNew, rightNew, initialRange, useDirection * -1);
    outerMerge(arr, leftNew, middleNew - 1, middleNew, rightNew, left);
  }

  private static <T extends Comparable<T>> void outerMerge(
      ArrayProxy<T> arr, int left1, int right1, int left2, int right2, int writeStart) {
    int pointerL = left1;
    int pointerR = left2;
    int pointerW = writeStart;

    while (pointerL <= right1 && pointerR <= right2) {
      if (arr.get(pointerL).compareTo(arr.get(pointerR)) <= 0) {
        arr.swap(pointerW++, pointerL++);
      } else {
        arr.swap(pointerW++, pointerR++);
      }
    }

    while (pointerL <= right1) {
      arr.swap(pointerW++, pointerL++);
    }
    while (pointerR <= right2) {
      arr.swap(pointerW++, pointerR++);
    }
  }
}

// https://habr.com/ru/articles/91283/
class Parser {
  private final int BUFFER_SIZE = 1 << 16;
  private DataInputStream din;
  private byte[] buffer;
  private int bufferPointer, bytesRead;

  public Parser(InputStream in) {
    din = new DataInputStream(in);
    buffer = new byte[BUFFER_SIZE];
    bufferPointer = bytesRead = 0;
  }

  public String nextString(int maxSize) {
    byte[] ch = new byte[maxSize];
    int point = 0;
    try {
      byte c = read();
      while (c == ' ' || c == '\n' || c == '\r') {
        c = read();
      }
      while (c != ' ' && c != '\n' && c != '\r') {
        ch[point++] = c;
        c = read();
      }
    } catch (Exception e) {
    }
    return new String(ch, 0, point);
  }

  public int nextInt() {
    int ret = 0;
    boolean neg;
    try {
      byte c = read();
      while (c <= ' ') {
        c = read();
      }
      neg = c == '-';
      if (neg) {
        c = read();
      }
      do {
        ret = ret * 10 + c - '0';
        c = read();
      } while (c > ' ');

      if (neg) {
        return -ret;
      }
    } catch (Exception e) {
    }
    return ret;
  }

  public long nextLong() {
    long ret = 0;
    boolean neg;
    try {
      byte c = read();
      while (c <= ' ') {
        c = read();
      }
      neg = c == '-';
      if (neg) {
        c = read();
      }
      do {
        ret = ret * 10 + c - '0';
        c = read();
      } while (c > ' ');

      if (neg) {
        return -ret;
      }
    } catch (Exception e) {
    }
    return ret;
  }

  private void fillBuffer() {
    try {
      bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
    } catch (Exception e) {
    }
    if (bytesRead == -1) buffer[0] = -1;
  }

  private byte read() {
    if (bufferPointer == bytesRead) {
      fillBuffer();
    }
    return buffer[bufferPointer++];
  }
}
