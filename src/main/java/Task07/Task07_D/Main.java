package Task07.Task07_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;

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

class DSU {
  private int size;
  private int active;

  private int[] linkList;
  private int[] sizeList;

  public DSU(int size) {
    this.size = size;
    active = size;
    linkList = new int[size];
    sizeList = new int[size];
    for (int i = 0; i < size; i++) {
      linkList[i] = i;
      sizeList[i] = 1;
    }
  }

  public int compNumber() {
    return active;
  }

  public int find(int v) {
    return goToRoot(v);
  }

  // returns true if they were in one component
  public boolean union(int v1, int v2) {
    int part1 = find(v1);
    int part2 = find(v2);

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
    active--;
  }
}

class Edge implements Comparable<Edge> {
  public int from;
  public int to;
  public int weight;

  public Edge(int from, int to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  @Override
  public int compareTo(Edge o) {
    return weight - o.weight;
  }
}

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int vertexNum = in.nextInt();

    PriorityQueue<Edge> sortedEdges = new PriorityQueue<>();
    for (int x = 0; x < vertexNum; x++) {
      for (int y = 0; y < vertexNum; y++) {
        if (y <= x) {
          in.nextInt();
          continue;
        }
        sortedEdges.add(new Edge(x, y, in.nextInt()));
      }
    }
    for (int x = 0; x < vertexNum; x++) {
      sortedEdges.add(new Edge(vertexNum, x, in.nextInt()));
    }

    DSU dsu = new DSU(vertexNum + 1);
    int weightCounter = 0;

    while (dsu.compNumber() > 1) {
      Edge currentEdge = sortedEdges.poll();
      if (!dsu.union(currentEdge.from, currentEdge.to)) {
        weightCounter += currentEdge.weight;
      }
    }

    System.out.println(weightCounter);
  }
}
