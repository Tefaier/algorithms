package Task01.Task01_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;

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

  public static class IntWithOperation {
    public long value;
    public final long operation;

    public IntWithOperation(long value, long operation) {
      this.value = value;
      this.operation = operation;
    }
  }

  public static class Heap {
    ArrayList<IntWithOperation> arr;
    int operations;

    public Heap() {
      this.arr = new ArrayList<>();
      this.operations = 0;
    }

    private void siftUp(int index) {
      if (index == 0) {
        return;
      }
      int parent = (index - 1) / 2;
      if (arr.get(parent).value - arr.get(index).value > 0) {
        IntWithOperation tmp = arr.get(parent);
        arr.set(parent, arr.get(index));
        arr.set(index, tmp);
        siftUp(parent);
      }
    }

    private void siftDown(int index) {
      int left = 2 * index + 1;
      int right = 2 * index + 2;
      if (left >= arr.size()) {
        return;
      }

      if (right == arr.size()) {
        right = left;
      }

      int imin = arr.get(left).value - arr.get(right).value > 0 ? right : left;
      if (arr.get(index).value - arr.get(imin).value > 0) {
        IntWithOperation tmp = arr.get(imin);
        arr.set(imin, arr.get(index));
        arr.set(index, tmp);
        siftDown(imin);
      }
    }

    public void add(Long value) {
      arr.add(new IntWithOperation(value, operations));
      siftUp(arr.size() - 1);
    }

    public Long getMin() {
      return arr.get(0).value;
    }

    public void extractMin() {
      arr.set(0, arr.get(arr.size() - 1));
      arr.remove(arr.size() - 1);
      siftDown(0);
    }

    public void adjustValue(long operation, long change) {
      int index = getOperated(operation);
      arr.get(index).value -= change;
      siftUp(index);
    }

    public void recordOperation() {
      operations++;
    }

    private int getOperated(long operation) {
      // returns index of element in the arr
      for (int i = 0; i < arr.size(); i++) {
        if (arr.get(i).operation == operation) {
          return i;
        }
      }
      throw new RuntimeException("Lol");
    }
  }

  public static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    Heap heap = new Heap();
    long operations = in.nextLong();
    for (long i = 0; i < operations; i++) {
      String command = in.nextString(100);
      heap.recordOperation();
      switch (command) {
        case "insert":
          heap.add(in.nextLong());
          break;
        case "decreaseKey":
          heap.adjustValue(in.nextLong(), in.nextLong());
          break;
        case "getMin":
          System.out.println(heap.getMin());
          break;
        case "extractMin":
          heap.extractMin();
          break;
        default:
          break;
      }
    }
  }
}
