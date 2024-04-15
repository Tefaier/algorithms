package Task06.Task06_F;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
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

  public Edge getEdge(int index) {
    return edges.get(index);
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static int infinity = 100000000;
  private static int negInf = infinity * -1;

  private static List<Integer> flightPathByFloid(Graph graph, int[] townSeq) {
    int size = graph.getVertexCount();
    int[][] matrix = new int[size][size];
    int[][] nextMatrix = new int[size][size]; // shows which edge to use
    for (int[] row : matrix) {
      Arrays.fill(row, negInf);
    }
    for (int[] row : nextMatrix) {
      Arrays.fill(row, -1);
    }
    for (int i = 0; i < size; i++) {
      matrix[i][i] = 0;
    }
    int counter = 0;
    for (Edge edge : graph.getEdges()) {
      if (matrix[edge.from()][edge.to()] < edge.weight()) {
        matrix[edge.from()][edge.to()] = edge.weight();
        nextMatrix[edge.from()][edge.to()] = counter;
      }
      ++counter;
    }

    // create APSP
    int newValue;
    for (int add = 0; add < size; ++add) {
      for (int from = 0; from < size; ++from) {
        for (int to = 0; to < size; ++to) {
          if (matrix[from][add] == negInf || matrix[add][to] == negInf) continue;
          newValue = Math.min(matrix[from][add] + matrix[add][to], infinity);
          if (newValue > matrix[from][to]) {
            matrix[from][to] = newValue;
            nextMatrix[from][to] = nextMatrix[from][add];
          }
        }
      }
    }

    for (int from = 0; from < size; ++from) {
      for (int to = 0; to < size; ++to) {
        for (int mid = 0; mid < size; ++mid) {
          if (matrix[from][mid] > negInf && matrix[mid][mid] > 0 && matrix[mid][to] > negInf) {
            nextMatrix[from][to] = -2;
            break;
          }
        }
      }
    }

    List<Integer> path = new ArrayList<>();
    int index = 0;
    int currentTown = townSeq[0];
    Edge edge;
    while (index < townSeq.length - 1) {
      if (nextMatrix[currentTown][townSeq[index + 1]] == -2) return null;
      while (currentTown != townSeq[index + 1]) {
        path.add(nextMatrix[currentTown][townSeq[index + 1]] + 1);
        currentTown = graph.getEdge(nextMatrix[currentTown][townSeq[index + 1]]).to();
      }
      ++index;
    }
    return path;
  }

  public static void main(String[] args) {
    int townsNum = in.nextInt();
    int edgeNum = in.nextInt();
    int lecturesNum = in.nextInt();
    Graph graph = new Graph(townsNum);
    for (int j = 0; j < edgeNum; j++) {
      graph.addEdge(new Edge(in.nextInt() - 1, in.nextInt() - 1, in.nextInt()));
    }
    int[] lectureTowns = new int[lecturesNum];
    for (int i = 0; i < lecturesNum; i++) {
      lectureTowns[i] = in.nextInt() - 1;
    }
    var result = flightPathByFloid(graph, lectureTowns);
    if (result == null) {
      System.out.println("infinitely kind");
      return;
    }
    System.out.println(result.size());
    System.out.println(result.stream().map(edge -> Integer.toString(edge)).collect(Collectors.joining(" ")));
  }
}
