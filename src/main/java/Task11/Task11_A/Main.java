package Task11.Task11_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int commandsNum = in.nextInt();
    int sixth = in.nextInt();
    int maximumChanges = in.nextInt() + 1;

    int[] values = new int[sixth * 6];
    for (int i = 0; i < sixth * 6; i++) {
      values[i] = in.nextInt();
    }
    values = Arrays.stream(values).sorted().toArray();

    int[][] history = new int[maximumChanges][6];
    StringBuilder result = new StringBuilder();
    int historyPointer = 0;
    for (int j = 1; j <= 6; j++) {
      history[historyPointer][j - 1] = values[j * sixth - 1];
    }

    for (int i = 0; i < commandsNum; i++) {
      var command = in.nextInt();
      if (command == 1) {
        int from = in.nextInt();
        int to = in.nextInt();
        int pointer = binarySearch(-1, values.length, from, values);
        if (to < from) {
          while (pointer > 0 && values[pointer - 1] > to) {
            values[pointer] = values[pointer - 1];
            --pointer;
          }
        } else if (to > from) {
          while (pointer < values.length - 1 && values[pointer + 1] < to) {
            values[pointer] = values[pointer + 1];
            ++pointer;
          }
        }
        values[pointer] = to;

        historyPointer = (historyPointer + 1) % maximumChanges;
        for (int j = 1; j <= 6; j++) {
          history[historyPointer][j - 1] = values[j * sixth - 1];
        }
      } else {
        int toPast = in.nextInt();
        int pointer = in.nextInt();
        result
            .append(
                history[(historyPointer - toPast + maximumChanges) % maximumChanges][pointer - 1])
            .append('\n');
      }
    }
    System.out.print(result);
  }

  // index of first key or bigger element
  private static int binarySearch(int left, int right, int key, int[] arr) {
    while (left + 1 < right) {
      int mid = (left + right) / 2;
      if (arr[mid] < key) {
        left = mid;
      } else {
        right = mid;
      }
    }

    return right;
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
