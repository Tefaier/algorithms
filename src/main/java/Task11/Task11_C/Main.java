package Task11.Task11_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int valuesNum = in.nextInt();

    List<Integer> values = new ArrayList<>(valuesNum);
    for (int i = 0; i < valuesNum; i++) {
      values.add(in.nextInt());
    }

    SegmentTree tree = new SegmentTree(values);
    StringBuilder result = new StringBuilder();
    int requestNum = in.nextInt();
    for (int i = 0; i < requestNum; i++) {
      String command = in.nextString(5);
      if (command.equals("U")) {
        tree.changeValue(in.nextInt() - 1, in.nextInt());
      } else {
        result.append(tree.getRange(in.nextInt() - 1, in.nextInt() - 1)).append('\n');
      }
    }
    System.out.print(result);
  }
}

class SegmentTree {
  private class Node {
    public Map<Integer, Integer> valueCounter;
    public Node left;
    public Node right;
    public int rangeFrom;
    public int rangeTo;
    public long sum;

    public Node(int rangeFrom, int rangeTo) {
      this.valueCounter = new HashMap<>();
      this.rangeFrom = rangeFrom;
      this.rangeTo = rangeTo;
      this.sum = 0;
    }

    public Node(Integer value, int rangeFrom, int rangeTo) {
      this.valueCounter = new HashMap<>();
      valueCounter.put(value, 1);
      this.rangeFrom = rangeFrom;
      this.rangeTo = rangeTo;
      this.sum = value;
    }

    public void combine(Node left, Node right) {
      if (left != null) {
        valueCounter.putAll(left.valueCounter);
        sum = left.sum;
      }
      for (Map.Entry<Integer, Integer> entry : right.valueCounter.entrySet()) {
        if (entry.getValue() == 0) continue;
        if (valueCounter.getOrDefault(entry.getKey(), 0) == 0) sum += entry.getKey();
        valueCounter.put(
            entry.getKey(), valueCounter.getOrDefault(entry.getKey(), 0) + entry.getValue());
      }
    }

    public void replaceValue(Integer from, Integer to) {
      valueCounter.put(from, valueCounter.get(from) - 1);
      if (valueCounter.get(from) == 0) sum -= from;
      if (valueCounter.getOrDefault(to, 0) == 0) sum += to;
      valueCounter.put(to, valueCounter.getOrDefault(to, 0) + 1);
    }
  }

  private Node root;
  private int size;
  private List<Integer> underlineArray;

  SegmentTree(List<Integer> array) {
    underlineArray = array;
    size = array.size();
    root = buildRange(array, 0, size - 1);
  }

  private Node buildRange(List<Integer> array, int from, int to) {
    Node node;
    if (from != to) {
      int mid = (from + to) / 2;
      node = new Node(from, to);
      node.left = buildRange(array, from, mid);
      node.right = buildRange(array, mid + 1, to);
      node.combine(node.left, node.right);
    } else {
      node = new Node(array.get(from), from, to);
    }
    return node;
  }

  public Long getRange(int l, int r) {
    Node rangeNode = new Node(0, 0);
    getRange(l, r, root, rangeNode);
    return rangeNode.sum;
  }

  private void getRange(int rl, int rr, Node cNode, Node collectNode) {
    if (cNode.rangeTo <= rr && cNode.rangeFrom >= rl) {
      collectNode.combine(null, cNode);
      return;
    }
    if (cNode.rangeTo < rl || cNode.rangeFrom > rr) return;
    getRange(rl, rr, cNode.left, collectNode);
    getRange(rl, rr, cNode.right, collectNode);
  }

  public void changeValue(int index, int newValue) {
    int eraseValue = underlineArray.get(index);
    underlineArray.set(index, newValue);

    Node referenceNode = root;
    while (referenceNode.rangeFrom != referenceNode.rangeTo) {
      referenceNode.replaceValue(eraseValue, newValue);
      if (index > referenceNode.left.rangeTo) {
        referenceNode = referenceNode.right;
      } else {
        referenceNode = referenceNode.left;
      }
    }
    referenceNode.replaceValue(eraseValue, newValue);
  }
}

// https://habr.com/ru/articles/91283/
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
