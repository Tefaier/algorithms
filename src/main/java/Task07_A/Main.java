package Task07_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

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

class DSUSum {
  private int size;
  private int active;

  private int[] linkList;
  private int[] sizeList;
  private int[] valueList;

  public DSUSum(int size) {
    this.size = size;
    active = size;
    linkList = new int[size];
    sizeList = new int[size];
    valueList = new int[size];
    for (int i = 0; i < size; i++) {
      linkList[i] = i;
      sizeList[i] = 1;
      valueList[i] = 0;
    }
  }

  public int compNumber() {
    return active;
  }

  public int getValue(int component) {
    return valueList[component];
  }

  public int find(int v) {
    return goToRoot(v);
  }

  // returns true if they were in one component
  public boolean union(int v1, int v2, int value) {
    int part1 = find(v1);
    int part2 = find(v2);

    valueList[part1] += value;
    if (part1 == part2) {
      return true;
    } else {
      if (sizeList[part1] < sizeList[part2])
        mergeParts(part1, part2);
      else
        mergeParts(part2, part1);
      return false;
    }
  }

  private int goToRoot(int v) {
    int active = v;
    ArrayList<Integer> visited = new ArrayList<>();
    while (active != linkList[active]) {
      visited.add(active);
      active = linkList[active];
    }

    for (int i = 0; i < visited.size() - 1; i++) {
      linkList[visited.get(i)] = active;
    }

    return active;
  }

  private void mergeParts(int from, int to) {
    linkList[from] = to;
    sizeList[to] += sizeList[from];
    valueList[to] += valueList[from];
    active--;
  }
}

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int netizens = in.nextInt();
    int requestNum = in.nextInt();
    DSUSum dsu = new DSUSum(netizens);
    for (int i = 0; i < requestNum; i++) {
      int requestType = in.nextInt();
      if (requestType == 1) {
        dsu.union(in.nextInt() - 1, in.nextInt() - 1, in.nextInt());
      } else {
        System.out.println(dsu.getValue(dsu.find(in.nextInt() - 1)));
      }
    }
  }
}
