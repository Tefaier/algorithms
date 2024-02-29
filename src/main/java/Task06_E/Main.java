package Task06_E;

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

public class Main {
  private static Parser in = new Parser(System.in);
  private static int inf = Integer.MAX_VALUE;

  private static List<Integer> locateCycleOfNW(int[][] graphMatrix) {
    int[] dist = new int[graphMatrix.length];
    Arrays.fill(dist, inf);

    // shows vertexes that were suspicious in iteration
    ArrayList<Integer> black_list = new ArrayList<>();
    // pointer shows first not analyzed vertex
    int pointer = 0;
    while (black_list.isEmpty() && pointer < graphMatrix.length) {
      while (pointer < graphMatrix.length && dist[pointer] != inf) pointer++;
      if (pointer == graphMatrix.length) break;

      dist[pointer] = 0;
      for (int k = 0; k < graphMatrix.length; k++) {
        for (int i = 0; i < m; i++) {
          int f = edges[i].from;
          int t = edges[i].to;
          long w = edges[i].w;
          if (dist[f] == inf) continue;
          if (dist[t] > dist[f] + w) {
            dist[t] = dist[f] + w;
            if (k == graphMatrix.length - 1) {
              black_list.add(t);
            }
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int gSize = in.nextInt();
    int[][] graph = new int[gSize][gSize];
    for (int i = 0; i < gSize; i++) {
      for (int j = 0; j < gSize; j++) {
        graph[i][j] = in.nextInt();
      }
    }
    var result = locateCycleOfNW(graph);
  }
}
