package Task03_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

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

public class Main {

  private static class Node {
    public final int value;
    public final int priority;
    public final int order;
    public Node left = null;
    public Node right = null;
    public Node parent = null;

    public Node(int order, int value, int priority) {
      this.value = value;
      this.priority = priority;
      this.order = order;
    }


    @Override
    public String toString() {
      return Integer.toString(parent != null ? parent.order : 0) + " " +
          Integer.toString(left != null ? left.order : 0) + " " +
          Integer.toString(right != null ? right.order : 0);
    }
  }

  private static void positionNode(Node newNode, Node lastNode) {
    Node processNode = lastNode;
    if (processNode == null) {
      return;
    }
    while (processNode.parent != null && processNode.priority > newNode.priority) {
      processNode = processNode.parent;
    }
    // no parent or founded a place to locate
    if (processNode.priority < newNode.priority) {
      if (processNode.right != null) {
        newNode.left = processNode.right;
        newNode.left.parent = newNode;
      }
      processNode.right = newNode;
      newNode.parent = processNode;
    } else {
      // no parent
      newNode.left = processNode;
      processNode.parent = newNode;
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int cars = in.nextInt();
    Node lastNode = null;
    Node[] allNodes = new Node[cars];

    for (int i = 0; i < cars; i++) {
      int value = in.nextInt();
      int priority = in.nextInt();
      Node node = new Node(i + 1, value, priority);
      positionNode(node, lastNode);

      lastNode = node;
      allNodes[i] = node;
    }

    System.out.println("YES");
    System.out.print(Arrays.stream(allNodes).map(Node::toString).collect(Collectors.joining("\n")));
  }
}
