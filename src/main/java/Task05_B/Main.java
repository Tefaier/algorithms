package Task05_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

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

  // code base taken from https://tutorialhorizon.com/algorithms/count-number-of-subgraphs-in-a-given-graph/
  static class Graph {
    int vertices;
    List<Integer>[] adjList;
    public StringBuilder answer = new StringBuilder();
    private StringBuilder currentCluster;
    private int sizeCounter;
    private int checkedPoint = 0;


    public Graph(int vertices) {
      this.vertices = vertices + 1;
      adjList = new List[vertices + 1];
      for (int i = 0; i < vertices + 1; i++) {
        adjList[i] = new ArrayList<>();
      }
    }

    public void addEdge(int source, int destination) {
      adjList[source].add(destination);
      adjList[destination].add(source);
    }


    public int countConnectedComponents() {
      boolean[] visited = new boolean[vertices];
      int count = 0;
      int index;
      while ((index = checkGraphIsVisited(visited)) != -1) {
        currentCluster = new StringBuilder();
        sizeCounter = 0;
        dfs(index, visited);
        count++;
        answer.append(sizeCounter).append('\n').append(currentCluster).append('\n');
        checkedPoint = index;
      }
      return count;
    }

    public int checkGraphIsVisited(boolean[] visited) {
      for (int i = checkedPoint + 1; i < visited.length; i++) {
        if (!visited[i])
          return i;
      }
      return -1;
    }

    public void dfs(int start, boolean[] visited) {
      sizeCounter++;
      currentCluster.append(start).append(' ');
      visited[start] = true;
      for (var next : adjList[start]) {
        if (!visited[next]) dfs(next, visited);
      }
    }
  }

  public static void main(String[] args) {
    Parser in = new Parser(System.in);
    int vertexes = in.nextInt();
    int edges = in.nextInt();
    Graph graph = new Graph(vertexes);
    for (int i = 0; i < edges; i++) {
      graph.addEdge(in.nextInt(), in.nextInt());
    }
    System.out.println(graph.countConnectedComponents());
    System.out.print(graph.answer);
  }
}
