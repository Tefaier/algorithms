package Task11.Task11_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int valuesNum = in.nextInt();

    List<Integer> values = new ArrayList<>(valuesNum);
    for (int i = 0; i < valuesNum; i++) {
      values.add(in.nextInt());
    }
    PersistentArray<Integer> arr = new PersistentArray<>(values);

    int requestNum = in.nextInt();
    for (int i = 0; i < requestNum; i++) {
      String command = in.nextString(10);
      if (command.equals("create")) {
        int fromVersion = in.nextInt();
        int changeIndex = in.nextInt() - 1;
        int changeTo = in.nextInt();
        arr.changeValue(fromVersion, changeIndex, changeTo);
      } else {
        int fromVersion = in.nextInt();
        int index = in.nextInt() - 1;
        System.out.println(arr.getElement(fromVersion, index));
      }
    }
  }
}

class PersistentArray<T> {
  private class Node {
    public T value;
    public Node left;
    public Node right;
    public int rangeFrom;
    public int rangeTo;

    public Node(int rangeFrom, int rangeTo) {
      this.value = null;
      this.rangeFrom = rangeFrom;
      this.rangeTo = rangeTo;
    }

    public Node(T value, int rangeFrom, int rangeTo) {
      this.value = value;
      this.rangeFrom = rangeFrom;
      this.rangeTo = rangeTo;
    }
  }

  private List<Node> roots = new ArrayList<>();
  private int size;

  PersistentArray(List<T> array) {
    size = array.size();
    roots.add(buildRange(array, 0, array.size() - 1));
  }

  private Node buildRange(List<T> array, int from, int to) {
    Node node = new Node(from, to);
    if (from != to) {
      int mid = (from + to) / 2;
      node.left = buildRange(array, from, mid);
      node.right = buildRange(array, mid + 1, to);
    } else {
      node.value = array.get(from);
    }
    return node;
  }

  public T getElement(int version, int index) {
    Node activeNode = roots.get(version - 1);
    while (activeNode.rangeFrom != activeNode.rangeTo) {
      if (index > activeNode.left.rangeTo) {
        activeNode = activeNode.right;
      } else {
        activeNode = activeNode.left;
      }
    }
    return activeNode.value;
  }

  public void changeValue(int version, int index, T newValue) {
    Node newNode = new Node(0, size - 1);
    Node referenceNode = roots.get(version - 1);
    roots.add(newNode);

    while (newNode.rangeFrom != newNode.rangeTo) {
      if (index > referenceNode.left.rangeTo) {
        newNode.left = referenceNode.left;
        newNode.right = new Node(referenceNode.right.rangeFrom, referenceNode.right.rangeTo);
        newNode = newNode.right;
        referenceNode = referenceNode.right;
      } else {
        newNode.right = referenceNode.right;
        newNode.left = new Node(referenceNode.left.rangeFrom, referenceNode.left.rangeTo);
        newNode = newNode.left;
        referenceNode = referenceNode.left;
      }
    }
    newNode.value = newValue;
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
