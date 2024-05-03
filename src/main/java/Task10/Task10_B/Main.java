package Task10.Task10_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int numCount = in.nextInt();
    int maximum = in.nextInt();
    SuffixAutomate.alphabetSize = maximum;
    SuffixAutomate automate = new SuffixAutomate();
    for (int i = 0; i < numCount; i++) {
      automate.addLetter(in.nextInt() - 1);
    }
    var result = automate.calc();
    System.out.println(result.value());
    System.out.println(result.length());
    System.out.println(result.string());
  }
}

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

class SuffixAutomate {
  public static int alphabetSize = '1';
  public static int alphabetStart = '1';

  static class AutomateNode {
    public int length;
    public int link;
    public int previousLongest;
    public int previousLongestNext;
    public int counter = 1;
    public Integer[] next = new Integer[alphabetSize];

    public AutomateNode(int length, int link, int previousLongest, int previousLongestNext) {
      this.length = length;
      this.link = link;
      this.previousLongest = previousLongest;
      this.previousLongestNext = previousLongestNext;
    }
  }

  public List<AutomateNode> nodes = new ArrayList<>();
  private int fullNodeIndex = 0;

  public SuffixAutomate() {
    nodes.add(new AutomateNode(0, -1, -1, -1));
  }

  public void addLetter(int character) {
    int newFullIndex = nodes.size();

    nodes.add(new AutomateNode(nodes.get(fullNodeIndex).length + 1, 0, fullNodeIndex, character));

    int pointer = fullNodeIndex;
    while (pointer != -1 && nodes.get(pointer).next[character] == null) {
      nodes.get(pointer).next[character] = newFullIndex;
      pointer = nodes.get(pointer).link;
    }

    if (pointer == -1) {
      fullNodeIndex = newFullIndex;
      return;
    }

    int toSplit = nodes.get(pointer).next[character];
    if (nodes.get(pointer).length + 1 == nodes.get(toSplit).length) {
      nodes.get(newFullIndex).link = toSplit;
    } else {
      int cloneIndex = nodes.size();
      nodes.add(new AutomateNode(nodes.get(pointer).length + 1, nodes.get(toSplit).link, pointer, character));
      nodes.get(cloneIndex).next = Arrays.copyOf(nodes.get(toSplit).next, nodes.get(toSplit).next.length);

      while (pointer != -1 && nodes.get(pointer).next[character] == toSplit) {
        nodes.get(pointer).next[character] = cloneIndex;
        pointer = nodes.get(pointer).link;
      }

      nodes.get(newFullIndex).link = cloneIndex;
      nodes.get(toSplit).link = cloneIndex;
    }

    fullNodeIndex = newFullIndex;
  }

  public Answer calc() {
    AtomicReference<AutomateNode> greatestNode = new AtomicReference<>();
    AtomicInteger greatestValue = new AtomicInteger(-1);
    nodes.stream().sorted((elem1, elem2) -> elem2.length - elem1.length).forEachOrdered(elem -> {
      if (elem.link != -1) {
        nodes.get(elem.link).counter += elem.counter;
      }
      if (elem.counter * elem.length >= greatestValue.get()) {
        greatestValue.set(elem.counter * elem.length);
        greatestNode.set(elem);
      }
    });
    List<Integer> values = new ArrayList<>();
    var cursor = greatestNode.get();
    while (cursor.previousLongestNext != -1) {
      values.add(cursor.previousLongestNext + 1);
      cursor = nodes.get(cursor.previousLongest);
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = values.size() - 1; i >= 0; --i) {
      stringBuilder.append(values.get(i)).append(" ");
    }

    return new Answer(greatestNode.get().length, greatestNode.get().counter, greatestValue.get(), stringBuilder);
  }
}

record Answer(int length, int count, int value, StringBuilder string) {
}
