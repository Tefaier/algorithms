package Task11.Task11_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int commandsNum = in.nextInt();
    int sixth = in.nextInt();
    int maximumChanges = in.nextInt();

    List<Integer> values = new ArrayList<>(sixth * 6);
    for (int i = 0; i < sixth * 6; i++) {
      values.add(in.nextInt());
    }
    values.sort(Integer::compareTo);

    List<List<Integer>> history = new ArrayList<>(maximumChanges);
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < commandsNum; i++) {
      var historyPart = new ArrayList<Integer>(6);
      for (int j = 1; j <= 6; j++) {
        historyPart.add(values.get(j * sixth - 1));
      }
      history.add(historyPart);

      var command = new Integer[]{in.nextInt(), in.nextInt(), in.nextInt()};
      if (command[0] == 1) {
        // 0 1 2 3
        int from = command[1];
        int to = command[2];
        int fromPos = binarySearch(from, values);
        int toPos = binarySearch(to, values);
        toPos = toPos < 0 ? -1 * toPos - 1 : toPos;
        if (fromPos == toPos) {
          values.set(fromPos, to);
          continue;
        }
        if (toPos > fromPos) {
          toPos -= 1;
          for (int j = fromPos; j < toPos; j++) {
            values.set(j, values.get(j + 1));
          }
          values.set(toPos, to);
        } else {
          for (int j = toPos + 1; j <= fromPos; j++) {
            values.set(j, values.get(j - 1));
          }
          values.set(toPos, to);
        }
      } else {
        int toPast = command[1];
        int pointer = command[2];
        result.append(history.get(history.size() - 1 - toPast).get(pointer - 1)).append('\n');
      }
    }
    System.out.println(result);
  }

  private static int binarySearch(int key, List<Integer> arr) {
    return binarySearch(-1, arr.size(), key, arr);
  }

  // index of first key or bigger element
  private static int binarySearch(int left, int right, int key, List<Integer> arr) {
    while (left + 1 < right) {
      int mid = (left + right) / 2;
      if (arr.get(mid) < key) {
        left = mid;
      } else {
        right = mid;
      }
    }

    if (right < arr.size() && arr.get(right) == key) {
      return right;
    } else {
      return -1 - right;
    }
  }

}

//https://habr.com/ru/articles/91283/
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
