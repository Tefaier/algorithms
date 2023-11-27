package Task04_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

  static class SparseTable {
    private class Unit {
      private class Stat implements Comparable {
        public final int value;
        public final int index;

        public Stat(int value, int index) {
          this.value = value;
          this.index = index;
        }

        @Override
        public int compareTo(Object o) {
          if (o instanceof Stat) {
            return value - ((Stat) o).value;
          }
          return 0;
        }
      }

      public final Stat first;
      public final Stat second;

      public Unit(Unit unit1, Unit unit2) {
        var sorted =
            Stream.of(unit1.first, unit1.second, unit2.first, unit2.second).sorted().toList();
        first = sorted.get(0);
        second = sorted.get((sorted.get(0).index == sorted.get(1).index) ? 2 : 1);
      }

      public Unit(int first, int firstIndex, int second, int secondIndex) {
        this.first = new Stat(first, firstIndex);
        this.second = new Stat(second, secondIndex);
      }
    }

    private List<Unit>[] table;

    public SparseTable(int[] arr) {
      table = new List[arr.length];
      for (int i = 0; i < arr.length; i++) {
        table[i] = new ArrayList<Unit>();
        table[i].add(new Unit(arr[i], i, arr[i], i));
      }
      int height = (int) (Math.ceil(Math.log(arr.length) / Math.log(2)));
      for (int level = 1; level < height; level++) {
        buildLevel(level);
      }
    }

    // levels 0 -> ... (level 0 builded in constructor's body)
    private void buildLevel(int level) {
      int length = 1 << level;
      for (int i = 0; i <= table.length - length; i++) {
        table[i].add(new Unit(table[i].get(level - 1), table[i + (length >> 1)].get(level - 1)));
      }
    }

    // l and r are inclusive and start from 0
    public int request(int l, int r) {
      int height = (int) (Math.floor(Math.log(r - l) / Math.log(2)));
      return new Unit(table[l].get(height), table[r + 1 - (1 << height)].get(height)).second.value;
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int volume = in.nextInt();
    int requests = in.nextInt();
    int[] values = new int[volume];
    for (int i = 0; i < volume; i++) {
      values[i] = in.nextInt();
    }
    SparseTable table = new SparseTable(values);
    for (int i = 0; i < requests; i++) {
      int l = in.nextInt();
      int r = in.nextInt();
      System.out.println(table.request(l - 1, r - 1));
    }
  }
}
