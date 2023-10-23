package Task01_E;

import java.io.DataInputStream;
import java.io.InputStream;

public class Main {

  static class Parser {

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

  static class Tree {
    private Node root = null;
    private int size = 0;

    private static class Node {
      int value = 0;
      Node left = null;
      Node right = null;
      Node parent = null;

      public Node(int value, Node parent) {
        this.value = value;
        this.parent = parent;
      }
    }

    public void add(int value) {
      if (root == null) {
        root = new Node(value, null);
        size++;
        return;
      }
      Node tmp = root;
      while (tmp != null) {
        /*
        if (tmp.value == value) {
          break;
        }
        */
        if (tmp.value > value) {
          if (tmp.left == null) {
            tmp.left = new Node(value, tmp);
            size++;
            return;
          }
          tmp = tmp.left;
        } else {
          if (tmp.right == null) {
            tmp.right = new Node(value, tmp);
            size++;
            return;
          }
          tmp = tmp.right;
        }
      }
    }

    public boolean find(int value) {
      Node tmp = root;
      while (tmp != null) {
        if (tmp.value == value) {
          return true;
        }
        if (tmp.value > value) {
          tmp = tmp.left;
        } else {
          tmp = tmp.right;
        }
      }
      return false;
    }

    public String getMin() {
      if (root == null) {
        return "error";
      }
      Node tmp = root;
      while (tmp.left != null) {
        tmp = tmp.left;
      }
      return Integer.toString(tmp.value);
    }

    public String getMax() {
      if (root == null) {
        return "error";
      }
      Node tmp = root;
      while (tmp.right != null) {
        tmp = tmp.right;
      }
      return Integer.toString(tmp.value);
    }

    public String extractMin() {
      if (root == null) {
        return "error";
      }
      Node tmp = root;
      while (tmp.left != null) {
        tmp = tmp.left;
      }
      if (tmp.parent == null) {
        root = tmp.right;
        if (root != null) {
          root.parent = null;
        }
      } else {
        tmp.parent.left = tmp.right;
        if (tmp.right != null) {
          tmp.right.parent = tmp.parent;
        }
      }
      size--;
      return Integer.toString(tmp.value);
    }

    public String extractMax() {
      if (root == null) {
        return "error";
      }
      Node tmp = root;
      while (tmp.right != null) {
        tmp = tmp.right;
      }
      if (tmp.parent == null) {
        root = tmp.left;
        if (root != null) {
          root.parent = null;
        }
      } else {
        tmp.parent.right = tmp.left;
        if (tmp.left != null) {
          tmp.left.parent = tmp.parent;
        }
      }
      size--;
      return Integer.toString(tmp.value);
    }

    public int size() {
      return size;
    }

    public void clear() {
      root = null;
      size = 0;
    }

    public void print() {
      print(root);
    }

    private void print(Node node) {
      if (node == null) return;
      print(node.left);
      System.out.print(node.value + " ");
      print(node.right);
    }
  }

  public static void main(String[] args) {
    Parser parser = new Parser(System.in);
    Tree tree = new Tree();
    int commandsNumber = parser.nextInt();
    for (int i = 0; i < commandsNumber; i++) {
      String command = parser.nextString(12);
      switch (command) {
        case "insert" -> {
          tree.add(parser.nextInt());
          System.out.println("ok");
        }
        case "extract_max" -> {
          System.out.println(tree.extractMax());
        }
        case "extract_min" -> {
          System.out.println(tree.extractMin());
        }
        case "get_max" -> {
          System.out.println(tree.getMax());
        }
        case "get_min" -> {
          System.out.println(tree.getMin());
        }
        case "size" -> {
          System.out.println(tree.size());
        }
        case "clear" -> {
          tree.clear();
          System.out.println("ok");
        }
      }
    }
  }
}
