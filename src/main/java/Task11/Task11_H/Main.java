package Task11.Task11_H;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class Main {
  private static Parser in = new Parser(System.in);

  private record colouredInfo(int splitIndex, long offset) {
  }

  public static void main(String[] args) {
    // splits [0, splits)
    // splits start at [1 + splitSize * n, splitSize * (n + 1)]
    long number = in.nextLong();
    int colouredNum = in.nextInt();
    int splits = in.nextInt();
    long splitSize = number / splits;

    TreeMap<Integer, Integer> valueToSplitsCountWithIt = new TreeMap<>();
    int[] countColouredInSplit = new int[splits];
    colouredInfo[] colouredInfos = new colouredInfo[colouredNum];

    for (int i = 0; i < colouredNum; i++) {
      long pos = in.nextLong();
      long prevSplitPos = splitSize * ((pos - 1) / splitSize);
      int splitNumber = (int) (prevSplitPos / splitSize);
      colouredInfos[i] = new colouredInfo(splitNumber, pos - prevSplitPos);
      countColouredInSplit[splitNumber]++;
    }
    for (int i = 0; i < countColouredInSplit.length; i++) {
      valueToSplitsCountWithIt.compute(countColouredInSplit[i], (k, v) -> v == null ? 1 : v + 1);
    }
    colouredInfos =
        Arrays.stream(colouredInfos)
            .sorted(Comparator.comparingLong(inf -> inf.offset))
            .toArray(colouredInfo[]::new);

    long bestSlicePos = splitSize;
    long bestSliceCount = 1;
    int bestSliceValue = valueToSplitsCountWithIt.lastKey() - valueToSplitsCountWithIt.firstKey();
    long currentSlicePos = 0;
    boolean bestNow = true;

    for (int i = 0; i < colouredInfos.length; i++) {
      long currentOffset = colouredInfos[i].offset;
      long jumpSize = currentOffset - currentSlicePos;
      currentSlicePos = currentOffset;
      if (bestNow) bestSliceCount += jumpSize - 1;
      while (true) {
        int prevSplitIndex =
            colouredInfos[i].splitIndex > 0 ? colouredInfos[i].splitIndex - 1 : splits - 1;
        var valueDrop = countColouredInSplit[colouredInfos[i].splitIndex];
        var valueIncrease = countColouredInSplit[prevSplitIndex];
        countColouredInSplit[colouredInfos[i].splitIndex] -= 1;
        countColouredInSplit[prevSplitIndex] += 1;
        if (valueIncrease != valueDrop - 1) {
          valueToSplitsCountWithIt.compute(valueDrop, (k, v) -> v == 1 ? null : v - 1);
          valueToSplitsCountWithIt.compute(valueDrop - 1, (k, v) -> v == null ? 1 : v + 1);
          valueToSplitsCountWithIt.compute(valueIncrease, (k, v) -> v == 1 ? null : v - 1);
          valueToSplitsCountWithIt.compute(valueIncrease + 1, (k, v) -> v == null ? 1 : v + 1);
        }

        if (i < colouredInfos.length - 1 && colouredInfos[i + 1].offset == currentOffset) {
          ++i;
        } else {
          break;
        }
      }
      int newValue = valueToSplitsCountWithIt.lastKey() - valueToSplitsCountWithIt.firstKey();
      if (newValue < bestSliceValue) {
        bestSliceValue = newValue;
        bestSlicePos = currentOffset;
        bestSliceCount = 1;
      } else if (newValue == bestSliceValue) {
        ++bestSliceCount;
      }
      bestNow = newValue == bestSliceValue;
    }
    if (bestNow) {
      bestSliceCount += splitSize - currentSlicePos - 1;
    }

    System.out.println(bestSliceValue + " " + (bestSliceCount * splits));
    System.out.println(bestSlicePos);
  }
}
// 0! 1 2! 3 4! 5 6! 7 8! 9 10
// 0! 1 2 3 4 5! 6 7 8 9 10

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
