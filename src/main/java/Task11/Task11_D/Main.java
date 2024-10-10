package Task11.Task11_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int valuesNum = in.nextInt();

    List<Integer> values = new ArrayList<>(valuesNum);
    for (int i = 0; i < valuesNum; i++) {
      values.add(in.nextInt());
    }

    UnstableListSystem tree = new UnstableListSystem(values);
    StringBuilder result = new StringBuilder();
    int requestNum = in.nextInt();
    for (int i = 0; i < requestNum; i++) {
      String command = in.nextString(7);
      if (command.equals("merge")) {
        tree.merge(in.nextInt() - 1, in.nextInt() - 1);
        result.append(tree.getTopValue()).append('\n');
      } else if (command.equals("head")) {
        tree.splitTail(in.nextInt() - 1);
        result.append(tree.getPreTopValue()).append('\n').append(tree.getTopValue()).append('\n');
      } else {
        tree.splitHead(in.nextInt() - 1);
        result.append(tree.getPreTopValue()).append('\n').append(tree.getTopValue()).append('\n');
      }
    }
    System.out.print(result);
  }
}

class UnstableListSystem {
  private static final int normalizer = (int) (1e9 + 7);

  private class Node {
    public int value = 0;
    public int sum = 0;
    public Node left;
    public Node right;

    public Node() {
    }

    public Node(int value) {
      this.value = value;
      this.sum = value;
    }

    public void updateValue() {
      this.sum = this.value + (left == null ? 0 : left.sum) + (right == null ? 0 : right.sum);
      this.sum %= normalizer;
    }
  }

  private List<Node> roots = new ArrayList<>();
  private int size;

  UnstableListSystem(List<Integer> array) {
    size = array.size();
    buildRange(array);
  }

  private void buildRange(List<Integer> array) {
    array.forEach((elem) -> roots.add(new Node(elem)));
  }

  public int getTopValue() {
    return roots.get(roots.size() - 1).sum;
  }

  public int getPreTopValue() {
    return roots.get(roots.size() - 2).sum;
  }

  public void merge(int index1, int index2) {
    Node newNode = new Node();
    newNode.left = roots.get(index1);
    newNode.right = roots.get(index2);
    newNode.updateValue();
    roots.add(newNode);
  }

  public void splitTail(int index) {
    Node referenceNode = roots.get(index);
    Node newMain = new Node();
    Node newNode = newMain;
    Stack<Node> newNodes = new Stack<>();
    int tailValue;

    while (true) {
      newNode.value = referenceNode.value;
      newNodes.add(newNode);
      if (referenceNode.left != null) {
        newNode.right = referenceNode.right;
        newNode.left = new Node();
        newNode = newNode.left;
        referenceNode = referenceNode.left;
      } else {
        // referenceNode is tail
        tailValue = referenceNode.value;
        newNodes.pop();
        Node floatingNode = referenceNode.right;
        while (!newNodes.empty()
            && newNodes.peek().value == 0
            && (newNodes.peek().right == null || floatingNode == null)) {
          if (floatingNode == null) floatingNode = newNodes.peek().right;
          newNodes.pop();
        }
        if (newNodes.empty()) {
          newMain = floatingNode;
        } else {
          newNodes.peek().left = floatingNode;
        }
        while (!newNodes.empty()) {
          newNodes.pop().updateValue();
        }
        break;
      }
    }

    roots.add(new Node(tailValue));
    roots.add(newMain);
  }

  public void splitHead(int index) {
    Node referenceNode = roots.get(index);
    Node newMain = new Node();
    Node newNode = newMain;
    Stack<Node> newNodes = new Stack<>();
    int headValue;

    while (true) {
      newNode.value = referenceNode.value;
      newNodes.add(newNode);
      if (referenceNode.right != null) {
        newNode.left = referenceNode.left;
        newNode.right = new Node();
        newNode = newNode.right;
        referenceNode = referenceNode.right;
      } else {
        // referenceNode is tail
        headValue = referenceNode.value;
        newNodes.pop();
        Node floatingNode = referenceNode.left;
        while (!newNodes.empty()
            && newNodes.peek().value == 0
            && (newNodes.peek().left == null || floatingNode == null)) {
          if (floatingNode == null) floatingNode = newNodes.peek().left;
          newNodes.pop();
        }
        if (newNodes.empty()) {
          newMain = floatingNode;
        } else {
          newNodes.peek().right = floatingNode;
        }
        while (!newNodes.empty()) {
          newNodes.pop().updateValue();
        }
        break;
      }
    }

    roots.add(newMain);
    roots.add(new Node(headValue));
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
