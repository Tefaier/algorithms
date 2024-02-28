package Task06_B;

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

record Edge(int from, int to, int weight) {
}

class Graph {
  private int vertexCount;
  private ArrayList<Edge> edges;

  public Graph(int vertexCount) {
    this.vertexCount = vertexCount;
    edges = new ArrayList<>();
  }

  public int getVertexCount() {
    return vertexCount;
  }

  public void addEdge(Edge edge) {
    edges.add(edge);
  }

  public List<Edge> getEdges() {
    return edges;
  }
}

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int roomNum = in.nextInt();
    int edgeNum = in.nextInt();
    int virusesNum = in.nextInt();
    int[] viruses = new int[virusesNum];
    for (int i = 0; i < virusesNum; i++) {
      viruses[i] = in.nextInt();
    }
    Graph graph = new Graph(roomNum);
    for (int j = 0; j < edgeNum; j++) {
      graph.addEdge(new Edge(in.nextInt(), in.nextInt(), in.nextInt()));
    }
    int start = in.nextInt();
    int target = in.nextInt();
  }
}
