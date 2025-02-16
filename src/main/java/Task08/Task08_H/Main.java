package Task08.Task08_H;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int xDimension = in.nextInt();
    int yDimension = in.nextInt();
    int price2_1 = in.nextInt();
    int price1_1 = in.nextInt();

    boolean[][] filledMap = new boolean[xDimension][yDimension];
    int emptyCounter = 0;
    for (int x = 0; x < xDimension; x++) {
      String line = in.nextString(yDimension + 2);
      for (int y = 0; y < line.length(); y++) {
        boolean isFilled = line.charAt(y) == '.';
        filledMap[x][y] = isFilled;
        emptyCounter += isFilled ? 0 : 1;
      }
    }

    // priority on 1x1 tiles
    if (price1_1 * 2 <= price2_1) {
      // if it's better to fill everything with them
      //if (price1_1 < 0) emptyCounter = xDimension * yDimension;
      System.out.println(emptyCounter * price1_1);
      return;
    }

    /*
    // if try full replacement on 2x1
    if (price2_1 < 0) {
      // full replace is allowed because 1x1 is also negative
      if (price1_1 <= 0) {
        emptyCounter = xDimension * yDimension;
        System.out.println(emptyCounter % 2 == 0 ? price2_1 * emptyCounter / 2 : price1_1 + price2_1 * (emptyCounter - 1) / 2);
        return;
      }
      // full replacement but avoid as many 1x1 as possible
      // full cover with 2x1 because field is even
      if (xDimension * yDimension % 2 == 0) {
        System.out.println(price2_1 * xDimension * yDimension / 2);
        return;
      }
      // try to leave one old empty and cover other tiles with 2x1
      boolean locatedIdeal = false;
      for (int x = 0; x < xDimension; x++) {
        for (int y = 0; y < yDimension; y++) {
          if (filledMap[x][y] && (x % 2 == y % 2)) {
            locatedIdeal = true;
            break;
          }
        }
        if (locatedIdeal) break;
      }
      if (locatedIdeal) {
        System.out.println(price2_1 * (xDimension * yDimension - 1) / 2);
        return;
      }
      // problem of positioning
      if (price2_1 + price1_1 < 0 || true) {
        // brute placement
        System.out.println(price1_1 + price2_1 * (xDimension * yDimension - 1) / 2);
        return;
      }
      // if possible so that 3 impossible covers are at old tiles
    }
     */

    ArrayList<SimpleEdge<Integer>> edges = new ArrayList<>();
    for (int x = 0; x < xDimension; x++) {
      for (int y = 0; y < yDimension; y++) {
        if (filledMap[x][y]) continue;
        int pos = x * yDimension + y;

        if (x < xDimension - 1 && !filledMap[x + 1][y]) {
          edges.add(new SimpleEdge<>(pos, pos + yDimension));
        }
        if (y < yDimension - 1 && !filledMap[x][y + 1]) {
          edges.add(new SimpleEdge<>(pos, pos + 1));
        }
      }
    }

    UnorderedGraph<Integer, SimpleEdge<Integer>> graph = new UnorderedGraph<>(IntStream.range(0, xDimension * yDimension).boxed().toList(), edges);
    var result = GraphHandler.kuhn(graph);
    System.out.println(result.size() * price2_1 + (emptyCounter - result.size() * 2) * price1_1);
  }
}

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

interface Edge<V> {
  V getFrom();

  V getTo();

  <E extends Edge<V>> E reversed();
}

class SimpleEdge<V> implements Edge<V> {
  private V from;
  private V to;

  public SimpleEdge(V from, V to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public V getFrom() {
    return from;
  }

  @Override
  public V getTo() {
    return to;
  }

  @Override
  public SimpleEdge<V> reversed() {
    return new SimpleEdge<>(getTo(), getFrom());
  }
}

class Graph<V, E extends Edge<V>> {
  protected int edgesNum = 0;
  protected List<V> vertices;
  protected HashMap<V, List<E>> edgesMap = new HashMap<>();

  public Graph(List<V> vertices, List<E> edges) {
    this.vertices = vertices;
    for (E edge : edges) {
      ++edgesNum;
      edgesMap.putIfAbsent(edge.getFrom(), new ArrayList<>());
      edgesMap.get(edge.getFrom()).add(edge);
    }
  }

  public List<E> getConnected(V vertex) {
    return edgesMap.getOrDefault(vertex, new ArrayList<>());
  }

  public int getVertexCount() {
    return vertices.size();
  }

  public int getEdgeCount() {
    return edgesNum;
  }

  public List<V> getVertices() {
    return vertices;
  }
}

class UnorderedGraph<V, E extends Edge<V>> extends Graph<V, E> {
  public UnorderedGraph(List<V> vertices, List<E> edges) {
    super(vertices, edges);
    for (E edge : edges) {
      edgesMap.putIfAbsent(edge.getTo(), new ArrayList<>());
      edgesMap.get(edge.getTo()).add(edge.reversed());
    }
  }
}

class GraphHandler {
  private record twoParts<V>(List<V> L, List<V> R) {
  }

  public static <V, E extends Edge<V>> List<E> kuhn(Graph<V, E> graph) {
    var parts = constructBiparts(graph);
    return kuhn(graph, parts.L(), parts.R());
  }

  public static <V, E extends Edge<V>> List<E> kuhn(Graph<V, E> graph, List<V> L, List<V> R) {
    if (L.size() > R.size()) return kuhn(graph, R, L);

    // now L is <= R
    HashMap<V, E> connectionFromR = new HashMap<>();
    for (V v : L) {
      tryKuhnConnection(graph, v, new HashSet<>(), connectionFromR);
    }
    return connectionFromR.values().stream().toList();
  }

  private static <V, E extends Edge<V>> boolean tryKuhnConnection(Graph<V, E> graph, V from, Set<V> visitedL, HashMap<V, E> connectionFromR) {
    if (visitedL.contains(from)) return false;
    visitedL.add(from);
    for (E edge : graph.getConnected(from)) {
      V to = edge.getTo();
      E problemEdge = connectionFromR.get(to);
      if (problemEdge == null || tryKuhnConnection(graph, problemEdge.getFrom() == to ? problemEdge.getTo() : problemEdge.getFrom(), visitedL, connectionFromR)) {
        connectionFromR.put(to, edge);
        return true;
      }
    }
    return false;
  }

  private static <V> twoParts<V> constructBiparts(Graph<V, ?> graph) {
    List<V> L = new ArrayList<>();
    List<V> R = new ArrayList<>();

    Set<V> visitedVertices = new HashSet<>();
    Queue<V> queue = new ArrayDeque<>();
    HashMap<V, Integer> heights = new HashMap<>();

    while (visitedVertices.size() != graph.vertices.size()) {
      for (int i = 0; i < graph.vertices.size(); i++) {
        if (!visitedVertices.contains(graph.vertices.get(i))) {
          queue.add(graph.vertices.get(i));
          visitedVertices.add(graph.vertices.get(i));
          heights.put(graph.vertices.get(i), 0);
          break;
        }
      }

      V vertex;
      int evenHeightCounter = 1;
      int oddHeightCounter = 0;
      while ((vertex = queue.poll()) != null) {
        for (var edge : graph.getConnected(vertex)) {
          if (!visitedVertices.contains(edge.getTo())) {
            int toHeight = heights.get(edge.getFrom()) + 1;
            queue.add(edge.getTo());
            visitedVertices.add(edge.getTo());
            heights.put(edge.getTo(), toHeight);
            if (toHeight % 2 == 0) {
              evenHeightCounter++;
            } else {
              oddHeightCounter++;
            }
          }
        }
      }

      boolean evenToL = evenHeightCounter < oddHeightCounter;
      heights.forEach((key, value) -> {
        if (evenToL ^ value % 2 != 0) {
          L.add(key);
        } else {
          R.add(key);
        }
      });
      heights.clear();
    }

    return new twoParts<>(L, R);
  }
}