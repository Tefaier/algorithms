package Task11.Task11_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int valuesNum = in.nextInt();

    int[] values = new int[valuesNum];
    for (int i = 0; i < valuesNum; i++) {
      values[i] = in.nextInt();
    }

    SquareBreak thing = new SquareBreak(values);
    StringBuilder result = new StringBuilder();
    int requestNum = in.nextInt();
    for (int i = 0; i < requestNum; i++) {
      String command = in.nextString(5);
      if (command.equals("U")) {
        thing.changeIndexValue(in.nextInt() - 1, in.nextInt());
      } else {
        result.append(thing.collectSum(in.nextInt() - 1, in.nextInt() - 1)).append('\n');
      }
    }
    System.out.print(result);
  }
}

class SquareBreak {
  private class Node {
    public Map<Integer, Integer> valueCounter = new TreeMap<>();
    public int leftIndex;
    public int rightIndex;

    public Node(int leftIndex, int rightIndex, int[] arr) {
      this.leftIndex = leftIndex;
      this.rightIndex = rightIndex;
      for (int i = leftIndex; i <= rightIndex; i++) {
        valueCounter.put(arr[i], valueCounter.getOrDefault(arr[i], 0) + 1);
      }
    }

    public void replaceValue(Integer from, Integer to) {
      valueCounter.put(from, valueCounter.get(from) - 1);
      if (valueCounter.get(from) == 0) valueCounter.remove(from);
      valueCounter.put(to, valueCounter.getOrDefault(to, 0) + 1);
    }
  }

  private int[] underlineArray;
  private int[] nodeIndex;
  private Node[] nodes;

  public SquareBreak(int[] arr) {
    underlineArray = arr;
    nodeIndex = new int[underlineArray.length];
    buildNodes();
  }

  private void buildNodes() {
    int nodeSize = (int) Math.sqrt(underlineArray.length);
    int nodesNum = Math.ceilDiv(underlineArray.length, nodeSize);
    nodes = new Node[nodesNum];
    int l = 0;
    int r = l + nodeSize;
    for (int i = 0; i < nodesNum; i++) {
      nodes[i] = new Node(l, r - 1, underlineArray);
      Arrays.fill(nodeIndex, l, r, i);
      l = r;
      r += nodeSize;
      r = Math.min(underlineArray.length, r);
    }
  }

  public void changeIndexValue(int index, int newValue) {
    nodes[nodeIndex[index]].replaceValue(underlineArray[index], newValue);
    underlineArray[index] = newValue;
  }

  public long collectSum(int left, int right) {
    int nodeLeftIndex = nodeIndex[left];
    int nodeRightIndex = nodeIndex[right];
    Node nodeLeft = nodes[nodeLeftIndex];
    Node nodeRight = nodes[nodeRightIndex];
    long sum = 0;
    Set<Integer> metNumbers = new TreeSet<>();

    if (nodeLeft.leftIndex == left && nodeLeft.rightIndex <= right) {
      for (Map.Entry<Integer, Integer> entry : nodeLeft.valueCounter.entrySet()) {
        metNumbers.add(entry.getKey());
        sum += entry.getKey();
      }
    } else {
      for (int i = left; i <= Math.min(nodeLeft.rightIndex, right); i++) {
        if (!metNumbers.contains(underlineArray[i])) {
          metNumbers.add(underlineArray[i]);
          sum += underlineArray[i];
        }
      }
    }

    if (nodeLeftIndex != nodeRightIndex && nodeRight.rightIndex == right) {
      for (Map.Entry<Integer, Integer> entry : nodeRight.valueCounter.entrySet()) {
        if (!metNumbers.contains(entry.getKey())) {
          metNumbers.add(entry.getKey());
          sum += entry.getKey();
        }
      }
    } else if (nodeLeftIndex != nodeRightIndex) {
      for (int i = nodeRight.leftIndex; i <= right; i++) {
        if (!metNumbers.contains(underlineArray[i])) {
          metNumbers.add(underlineArray[i]);
          sum += underlineArray[i];
        }
      }
    }

    for (int i = nodeLeftIndex + 1; i < nodeRightIndex; i++) {
      Node inNode = nodes[i];
      for (Map.Entry<Integer, Integer> entry : inNode.valueCounter.entrySet()) {
        if (!metNumbers.contains(entry.getKey())) {
          metNumbers.add(entry.getKey());
          sum += entry.getKey();
        }
      }
    }

    return sum;
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
