package Task06_J;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

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
  public int vertexCount;
  public int teleportCount;
  private ArrayList<Edge> edges;

  public Graph() {
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

class Teleport {
  public static int inCost;
  public static int outCost;

  // sorted
  public List<Integer> points = new ArrayList<>();
  public int tpNumber;

  public Teleport(int point, int tpNumber) {
    points.add(point);
    this.tpNumber = tpNumber;
  }

  public void addPoint(Integer point) {
    points.add(point);
  }

  public Integer getMin() {
    return points.get(0);
  }

  public Integer getMax() {
    return points.get(points.size() - 1);
  }

  // if its existence can make any way faster
  public static boolean checkMeaning(Teleport teleport, Integer upCost, Integer downCost) {
    return (teleport.getMax() - teleport.getMin()) * Math.max(upCost, downCost) > inCost + outCost;
  }

  public static boolean coversPoint(Teleport teleport, Integer point) {
    return teleport.getMin() < point && point < teleport.getMax();
  }
}

class WorldAxisPoint {
  public List<Integer> tpNumbers = new ArrayList<>();
  public boolean withSuperPoint = false;

  public WorldAxisPoint(boolean withSuperPoint) {
    this.withSuperPoint = withSuperPoint;
  }

  public WorldAxisPoint(Integer tpNumber) {
    tpNumbers.add(tpNumber);
  }

  public void addTp(Integer tpNumber) {
    tpNumbers.add(tpNumber);
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static int infinity = 2009000999;

  private static int[] fordBelman(int start, Graph graph) {
    int offset = graph.teleportCount;
    int[] distances = new int[graph.getVertexCount() + 1];
    Arrays.fill(distances, infinity);
    distances[start + offset] = 0;

    for (int k = 0; k < graph.getVertexCount(); k++) {
      boolean relaxed = false;
      for (Edge edge : graph.getEdges()) {
        if (distances[edge.from() + offset] != infinity) {
          if (distances[edge.to() + offset] > distances[edge.from() + offset] + edge.weight()) {
            distances[edge.to() + offset] = distances[edge.from() + offset] + edge.weight();
            relaxed = true;
          }
        }
      }
      if (!relaxed) break;
    }

    return distances;
  }

  public static void main(String[] args) {
    int deckTarget = in.nextInt();
    int upCost = in.nextInt();
    int downCost = in.nextInt();
    int inTeleport = in.nextInt();
    int outTeleport = in.nextInt();
    int teleportCount = in.nextInt();

    if (deckTarget == 1) {
      System.out.println(0);
      return;
    }

    // creates teleports
    Teleport.inCost = inTeleport;
    Teleport.outCost = outTeleport;
    List<Teleport> usefulTp = new ArrayList<>();
    List<Teleport> superNeg = new ArrayList<>();
    List<Teleport> superPos = new ArrayList<>();
    boolean startIsCovered = false;
    boolean endIsCovered = false;
    for (int i = 1; i <= teleportCount; i++) {
      int teleportPoints = in.nextInt();
      Teleport teleport = new Teleport(in.nextInt(), -1);
      for (int j = 1; j < teleportPoints; j++) {
        teleport.addPoint(in.nextInt());
      }
      if (!Teleport.checkMeaning(teleport, upCost, downCost)) continue;
      if (!startIsCovered) startIsCovered = Teleport.coversPoint(teleport, 1);
      if (!endIsCovered) endIsCovered = Teleport.coversPoint(teleport, deckTarget);
      if (teleport.getMax() <= 1) {
        superNeg.add(teleport);
        continue;
      }
      if (teleport.getMin() >= deckTarget) {
        superPos.add(teleport);
        continue;
      }
      usefulTp.add(teleport);
    }
    if (startIsCovered) usefulTp.addAll(superNeg);
    if (endIsCovered) usefulTp.addAll(superPos);
    for (int i = 0; i < usefulTp.size(); i++) {
      usefulTp.get(i).tpNumber = i;
    }
    // usefulTp contains teleports that are to be used, tpNumber corresponds to index in list

    TreeMap<Integer, WorldAxisPoint> worldAxis = new TreeMap<>();
    worldAxis.put(1, new WorldAxisPoint(true));
    worldAxis.put(deckTarget, new WorldAxisPoint(true));
    for (Teleport tp : usefulTp) {
      for (Integer point : tp.points) {
        if (worldAxis.containsKey(point)) {
          worldAxis.get(point).addTp(tp.tpNumber);
        } else {
          worldAxis.put(point, new WorldAxisPoint(tp.tpNumber));
        }
      }
    }
    // tpNumbers are sorted inside keys of worldAxis

    // remove sequences of the same tpsConfiguration
    List<Integer> toRemove = new ArrayList<>();
    List<Integer> tpsCompare = null;
    Integer prevVert = null;
    int strike = 0;
    for (var vert : worldAxis.entrySet()) {
      if (!vert.getValue().tpNumbers.equals(tpsCompare)) {
        tpsCompare = vert.getValue().tpNumbers;
        prevVert = null;
        strike = 1;
        continue;
      }
      strike++;
      if (strike > 2) {
        toRemove.add(prevVert);
      }
      prevVert = vert.getKey();
      if (vert.getValue().withSuperPoint) {
        strike = 1;
      }
    }
    for (var point : toRemove) {
      worldAxis.remove(point);
    }

    // construct graph based on worldAxis information
    // world axis takes numbers 0 --> worldAxis.size() - 1
    // teleports take numbers worldAxis.size() --> worldAxis.size() + usefulTp.size() - 1
    Graph graph = new Graph();
    graph.vertexCount = worldAxis.size() + usefulTp.size();
    int index = 0;
    Integer from = null;
    Integer target = null;
    Integer prevPoint = null;
    WorldAxisPoint prevData = null;
    for (var vert : worldAxis.entrySet()) {
      // recover indexes of source and target for future work
      if (vert.getValue().withSuperPoint) {
        if (from == null) {
          from = index;
        } else {
          target = index;
        }
      }

      // world axis connection to previous
      if (prevPoint != null) {
        graph.addEdge(new Edge(index - 1, index, upCost * (vert.getKey() - prevPoint)));
        graph.addEdge(new Edge(index, index - 1, downCost * (vert.getKey() - prevPoint)));
      }

      // creation of teleport connections
      for (Integer tpNumber : vert.getValue().tpNumbers) {
        graph.addEdge(new Edge(index, worldAxis.size() + tpNumber, inTeleport));
        graph.addEdge(new Edge(worldAxis.size() + tpNumber, index, outTeleport));
      }

      // prepare for further iteration
      prevPoint = vert.getKey();
      prevData = vert.getValue();
      index++;
    }

    // launch path logic
    System.out.println(fordBelman(from, graph)[target]);
  }
}
