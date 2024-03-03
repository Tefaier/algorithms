package Task06_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
  V from();

  V to();
}

class WeightedEdge<V> implements Edge<V> {
  private V from;
  private V to;
  private int weight;

  public WeightedEdge(V from, V to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  @Override
  public V from() {
    return from;
  }

  @Override
  public V to() {
    return to;
  }

  public int getWeight() {
    return weight;
  }
}

class Graph<V, E extends Edge<V>> {
  private int edgesNum = 0;
  private List<V> vertexes;
  private HashMap<V, List<E>> edgesMap = new HashMap<>();

  public Graph(List<V> vertexes, List<E> edges) {
    this.vertexes = vertexes;
    for (E edge : edges) {
      ++edgesNum;
      edgesMap.putIfAbsent(edge.from(), new ArrayList<>());
      edgesMap.get(edge.from()).add(edge);
    }
  }

  public List<E> getConnected(V vertex) {
    return edgesMap.getOrDefault(vertex, new ArrayList<>());
  }

  public int getVertexCount() {
    return vertexes.size();
  }

  public int getEdgeCount() {
    return edgesNum;
  }

  public List<V> getVertexes() {
    return vertexes;
  }
}

interface DijkstraVisitor<V, E extends Edge<V>> {
  public void instantiateVertex(V vertex);

  public void discoverVertex(V vertex, Integer distance);
}

class DijkstraVisitorDistances<V, E extends Edge<V>> implements DijkstraVisitor<V, E> {
  private static int infinity = 2009000999;
  private HashMap<V, Integer> distances = new HashMap<>();

  public DijkstraVisitorDistances(Graph<V, E> graph) {
    graph.getVertexes().forEach(this::instantiateVertex);
  }

  @Override
  public void instantiateVertex(V vertex) {
    distances.put(vertex, infinity);
  }

  @Override
  public void discoverVertex(V vertex, Integer distance) {
    distances.put(vertex, distance);
  }

  public HashMap<V, Integer> getDistances() {
    return distances;
  }
}

public class Main {
  private static Parser in = new Parser(System.in);

  private static <V, E extends WeightedEdge<V>, D extends DijkstraVisitor<V, E>> void dijkstra(Graph<V, E> graph, V start, D visitor) {
    Set<V> checked = new HashSet<>();
    class Unit implements Comparable {
      public V vertex;
      public int distance;

      public Unit(V vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
      }

      @Override
      public int compareTo(Object o) {
        if (o.getClass().isNestmateOf(Unit.class)) {
          return distance - ((Unit) o).distance;
        }
        return 0;
      }
    }
    PriorityQueue<Unit> queue = new PriorityQueue<>();
    queue.add(new Unit(start, 0));

    Unit unit;
    while ((unit = queue.poll()) != null) {
      if (checked.contains(unit.vertex)) continue;
      checked.add(unit.vertex);
      visitor.discoverVertex(unit.vertex, unit.distance);

      for (WeightedEdge<V> edge : graph.getConnected(unit.vertex)) {
        if (!checked.contains(edge.to())) {
          queue.add(new Unit(edge.to(), unit.distance + edge.getWeight()));
        }
      }
    }
  }

  public static void main(String[] args) {
    int tries = in.nextInt();
    for (int i = 0; i < tries; i++) {
      int roomNum = in.nextInt();
      int edgeNum = in.nextInt();
      List<WeightedEdge<Integer>> edges = new ArrayList<>();
      for (int j = 0; j < edgeNum; j++) {
        int v1 = in.nextInt();
        int v2 = in.nextInt();
        int weight = in.nextInt();
        edges.add(new WeightedEdge<>(v1, v2, weight));
        edges.add(new WeightedEdge<>(v2, v1, weight));
      }
      Graph<Integer, WeightedEdge<Integer>> graph = new Graph<>(IntStream.rangeClosed(0, roomNum - 1).boxed().toList(), edges);
      DijkstraVisitorDistances<Integer, WeightedEdge<Integer>> visitor = new DijkstraVisitorDistances<>(graph);
      dijkstra(graph, in.nextInt(), visitor);
      System.out.println(
          visitor.getDistances().entrySet().stream()
              .sorted(Comparator.comparingInt(Map.Entry::getKey))
              .map(entry -> entry.getValue().toString())
              .collect(Collectors.joining(" "))
      );
    }
  }
}
