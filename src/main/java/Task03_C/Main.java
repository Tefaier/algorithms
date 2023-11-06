package Task03_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

class AVLTree {
  private Node root = null;
  private int size = 0;

  public static class Node {
    int value = 0;
    int height = 0;
    Node left = null;
    Node right = null;
    String content = "";

    public Node(int value, String content) {
      this.value = value;
      this.content = content;
    }
  }

  private void updateHeight(Node node) {
    node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
  }

  private int getHeight(Node node) {
    return node == null ? 0 : node.height;
  }

  // can be negative
  private int getDisbalance(Node node) {
    return (node == null) ? 0 : getHeight(node.right) - getHeight(node.left);
  }

  private Node rightRotate(Node top) {
    Node newTop = top.left;
    Node bottom = newTop.right;
    newTop.right = top;
    top.left = bottom;
    updateHeight(top);
    updateHeight(newTop);
    return newTop;
  }

  private Node leftRotate(Node top) {
    Node newTop = top.right;
    Node bottom = newTop.left;
    newTop.left = top;
    top.right = bottom;
    updateHeight(top);
    updateHeight(newTop);
    return newTop;
  }

  private Node rebalance(Node target) {
    updateHeight(target);
    int balance = getDisbalance(target);
    if (balance > 1) {
      if (getHeight(target.right.right) > getHeight(target.right.left)) {
        // much more at right-right
        target = leftRotate(target);
      } else {
        // much more at right-left
        target.right = rightRotate(target.right);
        target = leftRotate(target);
      }
    } else if (balance < -1) {
      if (getHeight(target.left.left) > getHeight(target.left.right))
        // much more at left-left
        target = rightRotate(target);
      else {
        // much more at left-right
        target.left = leftRotate(target.left);
        target = rightRotate(target);
      }
    }
    return target;
  }

  // rebalances from right to left
  private void rebalanceChain(List<Node> chain) {
    for (int i = chain.size() - 1; i >= 0; i--) {
      rebalance(chain.get(i));
    }
  }

  // returns if overlap was located
  public boolean add(int value, String content) {
    if (root == null) {
      root = new Node(value, content);
    }
    Node tmp = root;
    List<Node> chain = new ArrayList<>();
    chain.add(tmp);
    while (tmp != null) {
      if (tmp.value == value) {
        return false;
      }
      if (tmp.value > value) {
        if (tmp.left == null) {
          tmp.left = new Node(value, content);
          size++;
        }
        tmp = tmp.left;
        chain.add(tmp);
      } else {
        if (tmp.right == null) {
          tmp.right = new Node(value, content);
          size++;
        }
        tmp = tmp.right;
        chain.add(tmp);
      }
    }
    rebalanceChain(chain);
    return true;
  }

  public Node find(int value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value == value) {
        return tmp;
      }
      if (tmp.value > value) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return null;
  }
}

public class Main {
  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    AVLTree treeOnNicknames = new AVLTree();
    AVLTree treeOnCarNames = new AVLTree();
    int participants = in.nextInt();
    for (int i = 0; i < participants; i++) {
      String nickname = in.nextString(100);
      String carName = in.nextString(100);
      treeOnNicknames.add(nickname.hashCode(), carName);
      treeOnCarNames.add(carName.hashCode(), nickname);
    }

    int requests = in.nextInt();
    for (int i = 0; i < requests; i++) {
      int request = in.nextString(100).hashCode();
      AVLTree.Node node = treeOnNicknames.find(request);
      if (node != null) {
        System.out.println(node.content);
      } else {
        System.out.println(treeOnCarNames.find(request).content);
      }
    }
  }
}
