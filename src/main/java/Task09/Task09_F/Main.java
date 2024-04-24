package Task09.Task09_F;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int peopleNum = in.nextInt();
    int minGroupSize = in.nextInt();
    TrieDoubled trie = new TrieDoubled();
    for (int i = 0; i < peopleNum; ++i) {
      trie.insert(in.nextString(1000001));
    }

    var result = trie.getOverlapGroupsCount(minGroupSize);
    int requestNum = in.nextInt();
    for (int i = 0; i < requestNum; ++i) {
      int overlap = in.nextInt();
      System.out.println(overlap >= result.length ? 0 : result[overlap]);
    }
  }
}

class TrieDoubled {
  public final int alphabetSize = '9' - '0' + 1;
  public final int alphabetStart = '0';

  public class TrieNode {
    public boolean isTerminal = false;
    public int subTreeSize = 0;
    public final Map<Integer, TrieNode> next = new TreeMap<>();
    public int depth = 0;
  }

  public TrieNode root = new TrieNode();
  private int maxWordSize = 0;

  public void insert(String word) {
    maxWordSize = Math.max(maxWordSize, word.length());
    root.subTreeSize += 1;
    var contextNode = root;

    for (int i = 0; i < word.length(); ++i) {
      var charIndex = calculateIndex(word.charAt(i), word.charAt(word.length() - i - 1));

      contextNode.next.putIfAbsent(charIndex, new TrieNode());

      contextNode = contextNode.next.get(charIndex);
      contextNode.subTreeSize += 1;
      contextNode.depth = i + 1;
    }

    contextNode.isTerminal = true;
  }

  public int[] getOverlapGroupsCount(Integer minGroupSize) {
    int[] answer = new int[maxWordSize + 1];
    Queue<TrieNode> queue = new ArrayDeque<>();
    queue.add(root);

    TrieNode node = null;
    while ((node = queue.poll()) != null) {
      if (node.subTreeSize >= minGroupSize) {
        answer[node.depth] += 1;
        queue.addAll(node.next.values());
      }
    }
    return answer;
  }

  private int calculateIndex(char c1, char c2) {
    return c2 + (c1 - alphabetStart) * alphabetSize;
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
