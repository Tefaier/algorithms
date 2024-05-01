package Task10.Task10_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {

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
  public final int alphabetSize = 'z' - 'a' + 1;
  public final int alphabetStart = 'a';

  static class AutomateNode {
    public int lenght;
    public int link;
    public TreeMap<Integer, Integer> next = new TreeMap<>();

    public AutomateNode(int lenght, int link) {
      this.lenght = lenght;
      this.link = link;
    }
  }

  public List<AutomateNode> nodes = new ArrayList<>();
  private int fullNodeIndex = 0;

  public SuffixAutomate() {
    nodes.add(new AutomateNode(0, -1));
  }

  public void addLetter(char character) {
    int newFullIndex = nodes.size();

    nodes.add(new AutomateNode(nodes.get(fullNodeIndex).lenght + 1, 0));

    int pointer = fullNodeIndex;
    while (pointer != -1 && !nodes.get(pointer).next.containsKey((int) character)) {
      nodes.get(pointer).next.put((int) character, newFullIndex);
      pointer = nodes.get(pointer).link;
    }

    if (pointer == -1) {
      fullNodeIndex = newFullIndex;
      return;
    }

    int toSplit = nodes.get(pointer).next.get((int) character);
    if (nodes.get(pointer).lenght + 1 == nodes.get(toSplit).lenght) {
      nodes.get(newFullIndex).link = toSplit;
    } else {
      int cloneIndex = nodes.size();
      nodes.add(new AutomateNode(nodes.get(pointer).lenght + 1, nodes.get(toSplit).link));
      nodes.get(cloneIndex).next.putAll(nodes.get(toSplit).next);

      while (pointer != -1 && nodes.get(pointer).next.getOrDefault((int) character, -1) == toSplit) {
        nodes.get(pointer).next.put((int) character, cloneIndex);
        pointer = nodes.get(pointer).link;
      }

      nodes.get(newFullIndex).link = cloneIndex;
      nodes.get(toSplit).link = cloneIndex;
    }

    fullNodeIndex = newFullIndex;
  }
}
