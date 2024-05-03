package Task10.Task10_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    String str = in.nextString(10000);
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
    public int link;
    public int maxToEnd = -1;
    public int minToEnd = 10000000;
    public Integer[] next = new Integer[alphabetSize];

    public AutomateNode(int length, int link) {
      this.length = length;
      this.link = link;
    }

    public int diff() {
      return maxToEnd - minToEnd;
    }
  }

  public List<AutomateNode> nodes = new ArrayList<>();
  private int fullNodeIndex = 0;

  public SuffixAutomate() {
    nodes.add(new AutomateNode(0, -1));
  }

  public void addString(String str) {
    for (int i = 0; i < str.length(); i++) {
      addLetter(StringHandler.normaliseAlphabet(str.charAt(i)));
    }
  }

  public void addLetter(int character) {
    int newFullIndex = nodes.size();

    nodes.add(new AutomateNode(nodes.get(fullNodeIndex).length + 1, 0));

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
      nodes.add(new AutomateNode(nodes.get(pointer).length + 1, nodes.get(toSplit).link));
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

  public boolean checkContains(String str) {
    boolean answer = true;
    Integer cursor = 0;
    for (int i = 0; i < str.length(); i++) {
      if ((cursor = nodes.get(cursor).next[StringHandler.normaliseAlphabet(str.charAt(i))]) == null) {
        answer = false;
        break;
      }
    }
    return answer;
  }

  public Integer traverse(int from, int character) {
    return nodes.get(from).next[StringHandler.normaliseAlphabet(character)];
  }

  public void calc() {
    nodes.get(nodes.size() - 1).maxToEnd = 0;
    nodes.get(nodes.size() - 1).minToEnd = 0;
    for (int i = nodes.size() - 1; i >= 0; --i) {
      AutomateNode currentNode = nodes.get(i);
      int min = currentNode.minToEnd;
      int max = currentNode.maxToEnd;
      for (Integer integer : currentNode.next) {
        if (integer == null) continue;
        min = Math.min(min, nodes.get(integer).minToEnd + 1);
        max = Math.max(max, nodes.get(integer).maxToEnd + 1);
      }
      currentNode.minToEnd = min;
      currentNode.maxToEnd = max;
      if (currentNode.link != -1) {
        nodes.get(currentNode.link).minToEnd = Math.min(nodes.get(currentNode.link).minToEnd, currentNode.minToEnd);
        nodes.get(currentNode.link).maxToEnd = Math.max(nodes.get(currentNode.link).maxToEnd, currentNode.maxToEnd);
      }
    }
    nodes.get(0).maxToEnd = 0;
    nodes.get(0).minToEnd = 0;
  }

  public long count() {
    long counter = 0;
    Queue<Pair> queue = new ArrayDeque<>();
    queue.add(new Pair(0, 0));

    Pair node = null;
    while ((node = queue.poll()) != null) {
      if (node.length() <= nodes.get(node.index()).diff()) ++counter;
      for (Integer i : nodes.get(node.index()).next) {
        if (i != null) queue.add(new Pair(i, node.length() + 1));
      }
    }
    return --counter;
  }
}

class StringHandler {
  private static int alpStart = 'a';
  private static int capStart = 'A';

  public static int normaliseAlphabet(int character) {
    return character < alpStart ? character - capStart : character - alpStart;
  }
}

record Pair(Integer index, Integer length) {
}
