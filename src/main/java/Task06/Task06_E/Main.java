package Task06.Task06_E;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

record Edge(int from, int to, int weight) {
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static int inf = Integer.MAX_VALUE;

  private static void locateCycleOfNW(int[][] graphMatrix, List<Edge> edges) {
    int[] parent = new int[graphMatrix.length];
    Arrays.fill(parent, -1);
    int[] dist = new int[graphMatrix.length];
    boolean[] checked = new boolean[graphMatrix.length];
    Arrays.fill(checked, false);

    // shows vertexes that were suspicious in iteration
    ArrayList<Integer> black_list = new ArrayList<>();
    // pointer shows first not analyzed vertex
    int pointer = 0;
    while (black_list.isEmpty() && pointer < graphMatrix.length) {
      while (pointer < graphMatrix.length && checked[pointer]) pointer++;
      if (pointer == graphMatrix.length) break;

      Arrays.fill(dist, inf);
      dist[pointer] = 0;
      checked[pointer] = true;
      for (int k = 0; k < graphMatrix.length; k++) {
        boolean relaxed = false;
        for (int i = 0; i < edges.size(); i++) {
          if (dist[edges.get(i).from()] == inf) continue;
          if (dist[edges.get(i).to()] > dist[edges.get(i).from()] + edges.get(i).weight()) {
            relaxed = true;
            checked[edges.get(i).to()] = true;
            dist[edges.get(i).to()] = dist[edges.get(i).from()] + edges.get(i).weight();
            parent[edges.get(i).to()] = edges.get(i).from();
            if (k == graphMatrix.length - 1) {
              black_list.add(edges.get(i).to());
            }
          }
        }
        if (!relaxed) break;
      }
    }
    // finished, either all checked either cycle detected
    if (black_list.isEmpty()) {
      System.out.println("NO");
      return;
    }
    // cycle detected
    System.out.println("YES");
    List<Integer> cycleList = new ArrayList<>();
    int last = black_list.get(0);
    cycleList.add(last);
    while (!cycleList.contains(parent[last])) {
      cycleList.add(parent[last]);
      last = parent[last];
    }
    int indexOfCycleStart = cycleList.indexOf(parent[last]);
    System.out.println(cycleList.size() - indexOfCycleStart + 1);
    System.out.print(parent[last] + 1 + " ");
    for (int i = cycleList.size() - 1; i >= indexOfCycleStart; --i) {
      System.out.print(cycleList.get(i) + 1 + " ");
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int gSize = in.nextInt();
    int[][] graph = new int[gSize][gSize];
    List<Edge> edges = new ArrayList<>();
    for (int i = 0; i < gSize; i++) {
      for (int j = 0; j < gSize; j++) {
        graph[i][j] = in.nextInt();
        if (graph[i][j] != 100000) {
          edges.add(new Edge(i, j, graph[i][j]));
        }
      }
    }
    locateCycleOfNW(graph, edges);
  }
}
