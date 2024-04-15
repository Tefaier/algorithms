package Task08.Task08_G;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  private static final Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    int lSize = in.nextInt();
    int rSize = in.nextInt();

    List<SimpleEdge<Integer>> edges = new ArrayList<>();
    for (int i = 1; i <= lSize; i++) {
      int input;
      while ((input = in.nextInt()) != 0) {
        edges.add(new SimpleEdge<>(i, input + lSize));
      }
    }

    UnorderedGraph<Integer, SimpleEdge<Integer>> graph = new UnorderedGraph<>(IntStream.range(1, lSize + rSize + 1).boxed().toList(), edges);
    var result = GraphHandler.kuhn(graph, IntStream.range(1, lSize + 1).boxed().toList(), IntStream.range(lSize + 1, lSize + rSize + 1).boxed().toList());
    System.out.println(result.size());
    System.out.println(result.stream().map(edge -> edge.getTo() > lSize ? edge.getFrom() + " " + (edge.getTo() - lSize) : edge.getTo() + " " + (edge.getFrom() - lSize)).collect(Collectors.joining("\n")));
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
