package Task10.Task10_A;

import java.io.*;
import java.util.*;

public class Main {
  private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {
    SuffixAutomate automate = new SuffixAutomate();
    List<Boolean> answers = new ArrayList<>();
    in.lines().forEachOrdered(str -> {
      switch (str.charAt(0)) {
        case '?' -> {
          Integer cursor = 0;
          for (int i = 2; i < str.length(); i++) {
            cursor = automate.traverse(cursor, str.charAt(i));
            if (cursor == null) {
              break;
            }
          }
          answers.add(cursor != null);
        }
        case 'A' -> {
          for (int i = 2; i < str.length(); i++) {
            automate.addLetter(StringHandler.normaliseAlphabet(str.charAt(i)));
          }
        }
      }
    });
    answers.forEach(val -> System.out.println(val ? "YES" : "NO"));
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
    public Integer[] next = new Integer[alphabetSize];

    public AutomateNode(int length, int link) {
      this.length = length;
      this.link = link;
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
}

class StringHandler {
  private static int alpStart = 'a';
  private static int capStart = 'A';

  public static int normaliseAlphabet(int character) {
    return character < alpStart ? character - capStart : character - alpStart;
  }
}
