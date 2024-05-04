package Task10.Task10_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    String str = in.nextString(10010);
    SuffixAutomate automate = new SuffixAutomate();
    automate.addString(str);
    automate.calc();
    System.out.println(automate.count());
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
  public static int alphabetSize = 'z' - 'a' + 1;
  public static int alphabetStart = 'a';

  static class AutomateNode {
    public int length;
    public AutomateNode link;
    public int maxToEnd = -100000;
    public int minToEnd = 100000;
    public TreeMap<Integer, AutomateNode> next = new TreeMap<>();

    public AutomateNode(int length, AutomateNode link) {
      this.length = length;
      this.link = link;
    }

    public int diff() {
      return maxToEnd - minToEnd;
    }

    public void tryNewMin(int min) {
      if (min < minToEnd) minToEnd = min;
    }

    public void tryNewMax(int max) {
      if (max > maxToEnd) maxToEnd = max;
    }
  }

  public List<AutomateNode> nodes = new ArrayList<>();
  private int fullNodeIndex = 0;

  public SuffixAutomate() {
    nodes.add(new AutomateNode(0, null));
  }

  public void addString(String str) {
    for (int i = 0; i < str.length(); i++) {
      addLetter(str.charAt(i));
    }
  }

  public void addLetter(int character) {
    int newFullIndex = nodes.size();

    nodes.add(new AutomateNode(nodes.get(fullNodeIndex).length + 1, nodes.get(0)));
    AutomateNode newFullNode = nodes.get(newFullIndex);

    AutomateNode pointer = nodes.get(fullNodeIndex);
    while (pointer != null && !pointer.next.containsKey(character)) {
      pointer.next.put(character, newFullNode);
      pointer = pointer.link;
    }

    if (pointer == null) {
      fullNodeIndex = newFullIndex;
      return;
    }

    AutomateNode toSplit = pointer.next.get(character);
    if (pointer.length + 1 == toSplit.length) {
      newFullNode.link = toSplit;
    } else {
      nodes.add(new AutomateNode(pointer.length + 1, toSplit.link));
      AutomateNode cloneNode = nodes.get(nodes.size() - 1);
      cloneNode.next.putAll(toSplit.next);

      while (pointer != null && pointer.next.getOrDefault(character, null) == toSplit) {
        pointer.next.put(character, cloneNode);
        pointer = pointer.link;
      }

      newFullNode.link = cloneNode;
      toSplit.link = cloneNode;
    }

    fullNodeIndex = newFullIndex;
  }

  public void calc() {
    nodes.get(fullNodeIndex).maxToEnd = 0;
    nodes.get(fullNodeIndex).minToEnd = 0;
    for (int i = nodes.size() - 1; i >= 0; --i) {
      AutomateNode currentNode = nodes.get(i);
      for (AutomateNode node : currentNode.next.values()) {
        currentNode.tryNewMin(node.minToEnd + 1);
        currentNode.tryNewMax(node.maxToEnd + 1);
      }
      if (currentNode.link != null) {
        currentNode.link.tryNewMin(currentNode.minToEnd);
        currentNode.link.tryNewMax(currentNode.maxToEnd);
      }
    }
    nodes.get(0).maxToEnd = 0;
    nodes.get(0).minToEnd = 0;
  }

  public long count() {
    long counter = 0;
    Queue<Pair> queue = new ArrayDeque<>();
    queue.add(new Pair(nodes.get(0), 0));

    Pair pair = null;
    while ((pair = queue.poll()) != null) {
      if (pair.length() <= pair.node().diff()) ++counter;
      for (AutomateNode node : pair.node().next.values()) {
        queue.add(new Pair(node, pair.length() + 1));
      }
    }
    return --counter;
  }
}

record Pair(SuffixAutomate.AutomateNode node, Integer length) {
}
