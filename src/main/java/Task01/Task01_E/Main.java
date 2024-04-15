package Task01.Task01_E;

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

  static class MiniMax {
    static class IntWithData implements Comparable {
      public int locationInMin;
      public int locationInMax;
      public final int value;

      IntWithData(int value) {
        this.value = value;
      }

      @Override
      public int compareTo(Object o) {
        if (o instanceof IntWithData) {
          return value - ((IntWithData) o).value;
        } else {
          return 0;
        }
      }
    }

    static class HeapMax {
      ArrayList<IntWithData> arr;
      MiniMax controller;

      public HeapMax(MiniMax controller) {
        this.arr = new ArrayList<>();
        this.controller = controller;
      }

      public int size() {
        return arr.size();
      }

      private void siftUp(int index) {
        if (index == 0) {
          return;
        }
        int parent = (index - 1) / 2;
        if (arr.get(parent).compareTo(arr.get(index)) < 0) {
          IntWithData tmp = arr.get(parent);
          arr.set(parent, arr.get(index));
          arr.set(index, tmp);
          arr.get(parent).locationInMax = parent;
          arr.get(index).locationInMax = index;
          siftUp(parent);
        }
      }

      private void siftDown(int index) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        if (right > arr.size()) {
          return;
        }
        if (right == arr.size()) {
          right = left;
        }
        int imax = arr.get(left).compareTo(arr.get(right)) > 0 ? left : right;
        if (arr.get(index).compareTo(arr.get(imax)) < 0) {
          IntWithData tmp = arr.get(imax);
          arr.set(imax, arr.get(index));
          arr.set(index, tmp);
          arr.get(imax).locationInMax = imax;
          arr.get(index).locationInMax = index;
          siftDown(imax);
        }
      }

      public void add(IntWithData unit) {
        arr.add(unit);
        unit.locationInMax = arr.size() - 1;
        siftUp(arr.size() - 1);
      }

      public IntWithData top() {
        return arr.get(0);
      }

      public boolean isEmpty() {
        return arr.size() == 0;
      }

      public void clear() {
        arr = new ArrayList<>();
      }

      public void removeUnit(IntWithData unit) {
        int index = unit.locationInMax;
        if (index == arr.size() - 1) {
          arr.remove(arr.size() - 1);
          return;
        }
        unit.locationInMax = -1;
        IntWithData tmp = arr.get(arr.size() - 1);
        arr.set(index, tmp);
        tmp.locationInMax = index;
        arr.remove(arr.size() - 1);
        if (index == 0) {
          siftDown(index);
        } else {
          int parent = (index - 1) / 2;
          if (arr.get(parent).compareTo(arr.get(index)) < 0) {
            siftUp(index);
          } else {
            siftDown(index);
          }
        }
      }
    }

    static class HeapMin {
      ArrayList<IntWithData> arr;
      MiniMax controller;

      public HeapMin(MiniMax controller) {
        this.arr = new ArrayList<>();
        this.controller = controller;
      }

      public int size() {
        return arr.size();
      }

      private void siftUp(int index) {
        if (index == 0) {
          return;
        }
        int parent = (index - 1) / 2;
        if (arr.get(parent).compareTo(arr.get(index)) > 0) {
          IntWithData tmp = arr.get(parent);
          arr.set(parent, arr.get(index));
          arr.set(index, tmp);
          arr.get(parent).locationInMin = parent;
          arr.get(index).locationInMin = index;
          siftUp(parent);
        }
      }

      private void siftDown(int index) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        if (right > arr.size()) {
          return;
        }
        if (right == arr.size()) {
          right = left;
        }
        int imin = arr.get(left).compareTo(arr.get(right)) < 0 ? left : right;
        if (arr.get(index).compareTo(arr.get(imin)) > 0) {
          IntWithData tmp = arr.get(imin);
          arr.set(imin, arr.get(index));
          arr.set(index, tmp);
          arr.get(imin).locationInMin = imin;
          arr.get(index).locationInMin = index;
          siftDown(imin);
        }
      }

      public void add(IntWithData unit) {
        arr.add(unit);
        unit.locationInMin = arr.size() - 1;
        siftUp(arr.size() - 1);
      }

      public IntWithData top() {
        return arr.get(0);
      }

      public boolean isEmpty() {
        return arr.size() == 0;
      }

      public void clear() {
        arr = new ArrayList<>();
      }

      public void removeUnit(IntWithData unit) {
        int index = unit.locationInMin;
        if (index == arr.size() - 1) {
          arr.remove(arr.size() - 1);
          return;
        }
        unit.locationInMin = -1;
        IntWithData tmp = arr.get(arr.size() - 1);
        arr.set(index, tmp);
        tmp.locationInMin = index;
        arr.remove(arr.size() - 1);
        if (index == 0) {
          siftDown(index);
        } else {
          int parent = (index - 1) / 2;
          if (arr.get(parent).compareTo(arr.get(index)) > 0) {
            siftUp(index);
          } else {
            siftDown(index);
          }
        }
      }
    }

    private HeapMin heapMin;
    private HeapMax heapMax;

    MiniMax() {
      heapMin = new HeapMin(this);
      heapMax = new HeapMax(this);
    }

    public int size() {
      return heapMin.size();
    }

    public void clear() {
      heapMax.clear();
      heapMin.clear();
    }

    public void add(int value) {
      IntWithData intWithData = new IntWithData(value);
      heapMin.add(intWithData);
      heapMax.add(intWithData);
    }

    public String getMin() {
      if (heapMin.size() > 0) {
        return Integer.toString(heapMin.top().value);
      }
      return "error";
    }

    public String getMax() {
      if (heapMax.size() > 0) {
        return Integer.toString(heapMax.top().value);
      }
      return "error";
    }

    public String extractMin() {
      if (heapMin.size() > 0) {
        IntWithData unit = heapMin.top();
        heapMin.removeUnit(unit);
        heapMax.removeUnit(unit);
        return Integer.toString(unit.value);
      }
      return "error";
    }

    public String extractMax() {
      if (heapMax.size() > 0) {
        IntWithData unit = heapMax.top();
        heapMin.removeUnit(unit);
        heapMax.removeUnit(unit);
        return Integer.toString(unit.value);
      }
      return "error";
    }
  }

  public static void main(String[] args) {
    Parser parser = new Parser(System.in);
    MiniMax miniMax = new MiniMax();
    int commandsNumber = parser.nextInt();
    for (int i = 0; i < commandsNumber; i++) {
      String command = parser.nextString(12);
      switch (command) {
        case "insert" -> {
          miniMax.add(parser.nextInt());
          System.out.println("ok");
        }
        case "extract_max" -> {
          System.out.println(miniMax.extractMax());
        }
        case "extract_min" -> {
          System.out.println(miniMax.extractMin());
        }
        case "get_max" -> {
          System.out.println(miniMax.getMax());
        }
        case "get_min" -> {
          System.out.println(miniMax.getMin());
        }
        case "size" -> {
          System.out.println(miniMax.size());
        }
        case "clear" -> {
          miniMax.clear();
          System.out.println("ok");
        }
      }
    }
  }
}
