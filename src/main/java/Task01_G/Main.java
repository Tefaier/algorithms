package Task01_G;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

  static class Parser {

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

  // stat K starts from 1, returns value of element that will be at this position
  // from starts inclusive, to end inclusive
  public static long getStatK(long[] arr, int k, int from, int to) {
    if (to - from == 1) {
      // == to because k is index + 1 and here to = from + 1, so if != to then it's k = to + 1 ONLY
      return k == to ? Math.min(arr[from], arr[to]) : Math.max(arr[from], arr[to]);
    } else if (from == to) {
      return arr[from];
    } else {
      // int pivot = getMiddle(Arrays.copyOfRange(arr, from, to + 1));
      long pivot = getMiddle(arr, from, to);
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
  public static int partition(long[] arr, long pivot, int from, int to) {
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
  public static long getMiddle(long[] arr, int from, int to) {
    // int[] answer = new int[(int) Math.ceil((to + 1 - from) / 5f)];
    ArrayList<Long> answer = new ArrayList<>();
    for (int i = from; i <= to; i += 5) {
      answer.add(getMedium(arr, i, Math.min(i + 4, to)));
      // answer[i / 5] = getMedium(arr, i, i + 4);
    }
    int mid = (int) Math.ceil(answer.size() / 2f);
    return getStatK(answer.stream().mapToLong(i -> i).toArray(), mid, 0, answer.size() - 1);
  }

  public static long getMedium(long[] arr, int from, int to) {
    // int limit = Math.min(to + 1, arr.length);
    bubbleSort(arr, from, to);
    return arr[(to + from) / 2];
  }

  public static void bubbleSort(long[] arr, int from, int to) {
    for (int i = from; i <= to; i++) {
      for (int j = i + 1; j <= to; j++) {
        if (arr[i] > arr[j]) {
          swap(arr, i, j);
        }
      }
    }
  }

  public static void swap(long[] arr, int i1, int i2) {
    long temp = arr[i1];
    arr[i1] = arr[i2];
    arr[i2] = temp;
  }

  public static void quickSort(long[] arr, int l, int r) {
    if (l < r) {
      int m = (l + r) / 2;
      // already made partition
      long pivot = getStatK(arr, m, l, r);
      int pos1 = 0;
      while (arr[pos1] < pivot) {
        pos1++;
      }
      int pos2 = pos1;
      while (arr[pos2] == pivot && pos2 < r) {
        pos2++;
      }

      quickSort(arr, l, pos1 - 1);
      quickSort(arr, pos2, r);
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);

    int number = in.nextInt();

    long[] array = new long[number];
    for (int i = 0; i < number; i++) {
      array[i] = in.nextLong();
    }

    quickSort(array, 0, number - 1);
    System.out.println(
        Arrays.stream(array).<String>mapToObj(Long::toString).collect(Collectors.joining(" ")));
  }
}
