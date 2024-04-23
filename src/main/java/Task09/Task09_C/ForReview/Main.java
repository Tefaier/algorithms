package Task09.Task09_C.ForReview;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// O(|N|^2 * |L|), N - number of words, L - maximum length of word, so 10^11 in given task

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int wordNum = in.nextInt();
    List<String> words = new ArrayList<>();

    for (int i = 0; i < wordNum; ++i) {
      words.add(in.nextString(11));
    }
  }
}

class TrieSuffixLeftPalindrom implements Graph<TrieSuffixLeftPalindrom.TrieNode, TrieSuffixLeftPalindrom.LetterEdge> {
  public final int alphabetSize = 'z' - 'a' + 1;
  public final int alphabetStart = 'a';

  public class LetterEdge implements Edge<TrieNode> {
    public TrieNode from;
    public TrieNode to;
    public char letter;

    public LetterEdge(TrieNode from, TrieNode to, char letter) {
      this.from = from;
      this.to = to;
      this.letter = letter;
    }

    @Override
    public TrieNode getFrom() {
      return from;
    }

    @Override
    public TrieNode getTo() {
      return to;
    }

    public char getLetter() {
      return letter;
    }

    @Override
    public <E extends Edge<TrieNode>> E reversed() {
      throw new UnsupportedOperationException("Reverse edge doesn't exist for LetterEdge");
    }
  }

  public class TrieNode {
    public boolean isTerminal = false;
    public Integer terminatesWord = null;
    public final TrieNode[] next = new TrieNode[alphabetSize];
    public List<Integer> wordsPalindrom = new ArrayList<>();
  }

  public int vertexCounter = 0;
  public int wordCounter = 0;
  public TrieNode root = new TrieNode();

  public void insert(String word) {
    ++wordCounter;
    var contextNode = root;

    for (int i = word.length() - 1; i >= 0; --i) {
      var charIndex = word.charAt(i) - alphabetStart;

      if (contextNode.next[charIndex] == null) {
        contextNode.next[charIndex] = new TrieNode();
      }

      contextNode = contextNode.next[charIndex];

      if (StringHandler.isSubPalindrom(word, 0, i - 1)) contextNode.wordsPalindrom.add(wordCounter);
    }

    contextNode.isTerminal = true;
    contextNode.terminatesWord = wordCounter;
  }

  public List<Integer> getRightPalindroms(String forWord) {
    List<Integer> answer = new ArrayList<>();


    return answer;
  }

  @Override
  public List<LetterEdge> getConnected(TrieNode vertex) {
    List<LetterEdge> answer = new ArrayList<>();
    for (int i = 0; i < vertex.next.length; i++) {
      if (vertex.next[i] != null) answer.add(new LetterEdge(vertex, vertex.next[i], (char) ('a' + i)));
    }
    return answer;
  }

  @Override
  public int getVertexCount() {
    return vertexCounter;
  }

  @Override
  public int getEdgeCount() {
    throw new UnsupportedOperationException("Trie doesn't provide count of edges");
  }

  @Override
  public List<TrieNode> getVertices() {
    List<TrieNode> nodes = new ArrayList<>();
    fillOutgoing(root, nodes);
    return nodes;
  }

  private void fillOutgoing(TrieNode node, List<TrieNode> nodeList) {
    if (node == null) return;
    nodeList.add(node);
    Arrays.stream(node.next).forEach(n -> fillOutgoing(n, nodeList));
  }
}

class StringHandler {
  // works by indexes in word
  public static boolean isSubPalindrom(String string, int from, int to) {
    while (from < to) {
      if (string.charAt(from) != string.charAt(to)) return false;
      ++from;
      --to;
    }
    return true;
  }
}

interface Edge<V> {
  V getFrom();

  V getTo();

  <E extends Edge<V>> E reversed();
}

interface Graph<V, E extends Edge<V>> {
  public List<E> getConnected(V vertex);

  public int getVertexCount();

  public int getEdgeCount();

  public List<V> getVertices();
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