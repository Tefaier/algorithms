package Task01_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

  static class IntWithInverses {
    public int value;
    public int inverses;
    public final int initialIndex;

    IntWithInverses(int value, int index) {
      this.value = value;
      this.inverses = 0;
      this.initialIndex = index;
    }

    public IntWithInverses copy() {
      IntWithInverses copy = new IntWithInverses(value, initialIndex);
      copy.inverses = inverses;
      return copy;
    }
  }

  public static void mergeSort(IntWithInverses[] arr, int[] indexer, int l, int r) {
    if (l >= r) {
      return;
    }
    int m = (l + r) / 2;
    mergeSort(arr, indexer, l, m);
    mergeSort(arr, indexer, m + 1, r);
    merge(arr, indexer, l, m, r);
  }

  public static void merge(IntWithInverses[] arr, int[] indexer, int l, int m, int r) {
    int size1 = m - l + 1;
    int size2 = r - m;
    IntWithInverses[] arr1 = new IntWithInverses[size1]; // l -> m
    IntWithInverses[] arr2 = new IntWithInverses[size1]; // m + 1 -> r
    for (int i = 0; i < size1; ++i) {
      arr1[i] = arr[l + i].copy();
    }
    for (int j = 0; j < size2; ++j) {
      arr2[j] = arr[m + 1 + j].copy();
    }

    int pointer1 = 0;
    int pointer2 = 0;
    int pointerMain = l;

    while (pointer1 < size1 && pointer2 < size2) {
      if (arr1[pointer1].value <= arr2[pointer2].value) {
        arr[pointerMain] = arr1[pointer1];
        pointer1++;
      } else {
        // inversion detected
        arr2[pointer2].inverses += size1 - pointer1;
        arr[pointerMain] = arr2[pointer2];
        pointer2++;
      }
      indexer[arr[pointerMain].initialIndex] = pointerMain;
      pointerMain++;
    }

    while (pointer1 < size1) {
      arr[pointerMain] = arr1[pointer1];
      indexer[arr[pointerMain].initialIndex] = pointerMain;
      pointer1++;
      pointerMain++;
    }

    while (pointer2 < size2) {
      arr[pointerMain] = arr2[pointer2];
      indexer[arr[pointerMain].initialIndex] = pointerMain;
      pointer2++;
      pointerMain++;
    }
    return;
  }

  public static void main(String[] args) {
    // through merge sort
    Parser input = new Parser(System.in);
    int valuesNumber = input.nextInt();

    IntWithInverses[] values = new IntWithInverses[valuesNumber];
    // shows on which place stays element that was at index as it's in orderHolder
    int[] orderHolder = new int[valuesNumber];

    for (int i = 0; i < valuesNumber; i++) {
      values[i] = new IntWithInverses(input.nextInt(), i);
      orderHolder[i] = i;
    }

    mergeSort(values, orderHolder, 0, valuesNumber - 1);
    System.out.println(
        IntStream.range(0, valuesNumber)
            .map(i -> orderHolder[i] - i + values[orderHolder[i]].inverses)
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(" ")));
  }
}
