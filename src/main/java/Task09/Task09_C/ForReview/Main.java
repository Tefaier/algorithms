package Task09.Task09_C.ForReview;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
  private static final int MAX_WORD_SIZE = 11;
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int wordNum = in.nextInt();
    List<String> words = new ArrayList<>();
    TrieSuffixLeftPalindrom trie = new TrieSuffixLeftPalindrom();

    String word;
    for (int i = 0; i < wordNum; ++i) {
      word = in.nextString(MAX_WORD_SIZE);
      words.add(word);
      trie.insert(word);
    }

    List<Integer> values = new ArrayList<>();
    for (int wNum = 1; wNum <= wordNum; ++wNum) {
      for (Integer concatNumber : trie.getRightConcatPalindroms(words.get(wNum - 1))) {
        if (concatNumber == wNum) continue;
        values.add(wNum);
        values.add(concatNumber);
      }
    }

    System.out.println(values.size() / 2);
    for (int i = 0; i < values.size() / 2; ++i) {
      System.out.println(values.get(i * 2) + " " + values.get(i * 2 + 1));
    }
  }
}

class TrieSuffixLeftPalindrom implements Graph<TrieSuffixLeftPalindrom.TrieNode, TrieSuffixLeftPalindrom.LetterEdge> {
  public final int ALPHABET_SIZE = 'z' - 'a' + 1;
  public final int ALPHABET_START = 'a';

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
    public final TrieNode[] next = new TrieNode[ALPHABET_SIZE];
    public List<Integer> wordsPalindrom = new ArrayList<>();
  }

  public int vertexCounter = 0;
  public int wordCounter = 0;
  public TrieNode root = new TrieNode();

  public void insert(String word) {
    ++wordCounter;
    var contextNode = root;

    for (int i = word.length() - 1; i >= 0; --i) {
      var charIndex = word.charAt(i) - ALPHABET_START;

      if (contextNode.next[charIndex] == null) {
        contextNode.next[charIndex] = new TrieNode();
      }

      contextNode = contextNode.next[charIndex];

      if (StringHandler.isSubPalindrom(word, 0, i - 1)) contextNode.wordsPalindrom.add(wordCounter);
    }

    contextNode.isTerminal = true;
    contextNode.terminatesWord = wordCounter;
  }

  public List<Integer> getRightConcatPalindroms(String forWord) {
    List<Integer> answer = new ArrayList<>();
    TrieNode activeNode = root;
    for (int i = 0; i < forWord.length() - 1; i++) {
      activeNode = activeNode.next[forWord.charAt(i) - ALPHABET_START];
      if (activeNode == null) return answer;
      if (activeNode.isTerminal && StringHandler.isSubPalindrom(forWord, i + 1, forWord.length() - 1))
        answer.add(activeNode.terminatesWord);
    }

    activeNode = activeNode.next[forWord.charAt(forWord.length() - 1) - ALPHABET_START];
    if (activeNode != null) {
      answer.addAll(activeNode.wordsPalindrom);
    }

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
    return vertexCounter - 1;
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