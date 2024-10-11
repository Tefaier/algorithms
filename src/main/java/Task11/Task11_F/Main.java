package Task11.Task11_F;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int valuesNum = in.nextInt();

    int[] values = new int[valuesNum];
    for (int i = 0; i < valuesNum; i++) {
      values[i] = in.nextInt();
    }

    ArrayMethods.timSort(values);
    System.out.print(
        Arrays.stream(values).mapToObj(Integer::toString).collect(Collectors.joining(" ")));
  }
}

class ArrayMethods {
  private static final int minRunThr = 32;

  private record runInfo(int left, int size) {
  }

  private static Stack<runInfo> getRuns(int[] arr) {
    var answer = new Stack<runInfo>();
    int pointer = 1;
    int previousElement = arr[0];
    int previousStart = 0;
    while (pointer < arr.length) {
      if (arr[pointer] < previousElement) {
        int size = pointer - previousStart;
        if (size < minRunThr) {
          insertionSort(arr, previousStart, previousStart + minRunThr - 1);
          pointer = Math.min(arr.length - 1, previousStart + minRunThr - 1);
          size = pointer - previousStart + 1;
        }
        answer.add(new runInfo(previousStart, size));
        previousStart = ++pointer;
        if (pointer == arr.length) break;
      }
      previousElement = arr[pointer++];
    }

    if (previousStart != arr.length) {
      answer.add(new runInfo(previousStart, arr.length - previousStart));
    }

    return answer;
  }

  private static void insertionSort(int[] arr, int left, int right) {
    right = Math.min(arr.length - 1, right);
    for (int i = left + 1; i <= right; i++) {
      int temp = arr[i];
      int j = i - 1;
      while (j >= left && arr[j] > temp) {
        arr[j + 1] = arr[j];
        --j;
      }
      arr[j + 1] = temp;
    }
  }

  private static void merge(int[] arr, runInfo one, runInfo two) {
    runInfo left = one.left < two.left ? one : two;
    runInfo right = one.left < two.left ? two : one;
    merge(arr, left.left, left.left + left.size - 1, right.left + right.size - 1);
  }

  private static void merge(int[] arr, int leftStart, int leftFinish, int rightFinish) {
    int len1 = leftFinish - leftStart + 1;
    int[] left = Arrays.copyOfRange(arr, leftStart, leftFinish + 1);
    int len2 = rightFinish - leftFinish;
    int[] right = Arrays.copyOfRange(arr, leftFinish + 1, rightFinish + 1);

    int pointerL = 0;
    int pointerR = 0;
    int pointerW = leftStart;

    while (pointerL < len1 && pointerR < len2) {
      if (left[pointerL] <= right[pointerR]) {
        arr[pointerW++] = left[pointerL++];
      } else {
        arr[pointerW++] = right[pointerR++];
      }
    }

    while (pointerL < len1) {
      arr[pointerW++] = left[pointerL++];
    }
    while (pointerR < len2) {
      arr[pointerW++] = right[pointerR++];
    }
  }

  public static void timSort(int[] arr) {
    var runs = getRuns(arr);

    while (runs.size() > 2) {
      var runX = runs.pop();
      var runY = runs.pop();
      var runZ = runs.pop();

      if (runX.size > runY.size + runZ.size && runY.size > runZ.size) {
        break;
      }
      merge(arr, runY, runX.size < runZ.size ? runX : runZ);

      if (runX.size < runZ.size) {
        runs.add(runZ);
        runs.add(new runInfo(runY.left, runY.size + runX.size));
      } else {
        runs.add(new runInfo(runZ.left, runY.size + runZ.size));
        runs.add(runX);
      }
    }

    while (runs.size() >= 2) {
      var run1 = runs.pop();
      var run2 = runs.pop();
      merge(arr, run2, run1);
      runs.add(new runInfo(run2.left, run1.size + run2.size));
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
