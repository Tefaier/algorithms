package Task06_A;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  private static Parser in = new Parser(System.in);

  private static <V, E extends WeightedEdge<V>, D extends DijkstraVisitor<V, E>> void dijkstra(Graph<V, E> graph, V start, D visitor) {
    Set<V> checked = new HashSet<>();
    HashMap<V, Integer> currentDistances = new HashMap<>();
    graph.getVertexes().forEach(vert -> currentDistances.put(vert, Integer.MAX_VALUE));
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
        if (edge.getWeight() + currentDistances.get(unit.vertex) < currentDistances.get(edge.getTo())) {
          currentDistances.put(edge.getTo(), edge.getWeight() + currentDistances.get(unit.vertex));
          queue.add(new Unit(edge.getTo(), unit.distance + edge.getWeight()));
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
      }
      UnorderedGraph<Integer, WeightedEdge<Integer>> graph = new UnorderedGraph<>(IntStream.rangeClosed(0, roomNum - 1).boxed().toList(), edges);
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
  public V getFrom() {
    return from;
  }

  @Override
  public V getTo() {
    return to;
  }

  public int getWeight() {
    return weight;
  }

  @Override
  public WeightedEdge<V> reversed() {
    return new WeightedEdge<>(getTo(), getFrom(), weight);
  }
}

class Graph<V, E extends Edge<V>> {
  protected int edgesNum = 0;
  protected List<V> vertexes;
  protected HashMap<V, List<E>> edgesMap = new HashMap<>();

  public Graph(List<V> vertexes, List<E> edges) {
    this.vertexes = vertexes;
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
    return vertexes.size();
  }

  public int getEdgeCount() {
    return edgesNum;
  }

  public List<V> getVertexes() {
    return vertexes;
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
