package Task06_H;

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

class Vertex {
  public int depth;
  public int value;
}

class GameState extends Vertex implements Comparable {
  public enum Move {
    U,
    D,
    L,
    R
  }

  public byte[][] field;
  public byte emptyX;
  public byte emptyY;
  public String stringRepr; // precalculated toString
  public GameState parent;
  public Move moveFromParent;

  public GameState(GameState parent, Move move, byte[][] field, byte emptyX, byte emptyY, int depth) {
    this.parent = parent;
    this.moveFromParent = move;
    this.field = field;
    this.emptyX = emptyX;
    this.emptyY = emptyY;
    this.depth = depth;
    this.stringRepr = this.toString();
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof GameState) {
      int diff = value - ((GameState) o).value;
      return diff != 0 ? diff : depth - ((GameState) o).depth;
    }
    return 0;
  }

  @Override
  public String toString() {
    if (stringRepr != null) return stringRepr;
    StringBuilder builder = new StringBuilder();
    for (var arr : field) {
      for (byte state : arr) {
        builder.append(state).append(',');
      }
    }
    return builder.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GameState) {
      return stringRepr.equals(((GameState) obj).stringRepr);
    }
    return false;
  }
}

interface Heuristic<V extends Vertex> {
  public int calculate(V vertex);
}

class ManhattanAndLinearConflict<V extends GameState> implements Heuristic<V> {
  private int size;
  private byte emptySign;

  public ManhattanAndLinearConflict(int size, byte emptySign) {
    this.size = size;
    this.emptySign = emptySign;
  }

  @Override
  public int calculate(V vertex) {
    return manhattanDist(vertex) + linearConflict(vertex);
  }

  private int manhattanDist(V gameState) {
    int result = 0;
    byte val;
    int xTarget;
    int yTarget;

    for (var x = 0; x < size; x++) {
      for (var y = 0; y < size; y++) {
        val = gameState.field[x][y];
        if (val == emptySign) continue;
        xTarget = (val - 1) / size;
        yTarget = (val - 1) % size;
        result += Math.abs(x - xTarget) + Math.abs(y - yTarget);
      }
    }

    return result;
  }

  private int linearConflict(GameState gameState) {
    byte[][] rowTarget = new byte[size][size];
    byte[][] columnTarget = new byte[size][size];
    createTargets(rowTarget, columnTarget, gameState);

    byte[][] rowInConflict = new byte[size][size];
    byte[][] columnInConflict = new byte[size][size];
    calculateConflicts(rowTarget, columnTarget, rowInConflict, columnInConflict, gameState);

    return solveConflicts(rowInConflict, columnInConflict, gameState);
  }

  private void createTargets(byte[][] rowTarget, byte[][] columnTarget, GameState gameState) {
    for (int x = 0; x < size; ++x) {
      for (int y = 0; y < size; ++y) {
        rowTarget[x][y] = (byte) ((gameState.field[x][y] - 1) / size);
        columnTarget[x][y] = (byte) ((gameState.field[x][y] - 1) % size);
      }
    }
    rowTarget[gameState.emptyX][gameState.emptyY] = -1;
    columnTarget[gameState.emptyX][gameState.emptyY] = -1;
  }

  private void calculateConflicts(byte[][] rowTarget, byte[][] columnTarget, byte[][] rowInConflict, byte[][] columnInConflict, GameState gameState) {
    for (int x = 0; x < size; ++x) {
      for (int y = 0; y < size; ++y) {
        //get row conflicts
        if (rowTarget[x][y] == x) {
          for (int k = y + 1; k < size; ++k) {
            if (gameState.field[x][y] > gameState.field[x][k] && rowTarget[x][k] == x) {
              rowInConflict[x][y]++;
              rowInConflict[x][k]++;
            }
          }
        }
        //get column conflicts
        if (columnTarget[x][y] == y) {
          for (int k = x + 1; k < size; ++k) {
            if (gameState.field[x][y] > gameState.field[k][y] && columnTarget[k][y] == y) {
              columnInConflict[x][y]++;
              columnInConflict[k][y]++;
            }
          }
        }
      }
    }
  }

  private int solveConflicts(byte[][] rowInConflict, byte[][] columnInConflict, GameState gameState) {
    int counter = 0;
    byte max;
    byte target = -1;
    // by row
    for (byte x = 0; x < size; ++x) {
      while (true) {
        // if continue
        max = 0;
        for (byte y = 0; y < size; ++y) {
          if (max < rowInConflict[x][y]) {
            max = rowInConflict[x][y];
            target = y;
          }
        }
        if (max == 0) break;
        // move tile logic
        rowInConflict[x][target] = 0;
        for (byte y = 0; y < size; ++y) {
          if (gameState.field[x][y] > gameState.field[x][target] ^ y > target) {
            --rowInConflict[x][y];
          }
        }
        ++counter;
      }
    }
    // by column
    for (byte y = 0; y < size; ++y) {
      while (true) {
        // if continue
        max = 0;
        for (byte x = 0; x < size; ++x) {
          if (max < columnInConflict[x][y]) {
            max = columnInConflict[x][y];
            target = x;
          }
        }
        if (max == 0) break;
        // move tile logic
        columnInConflict[target][y] = 0;
        for (byte x = 0; x < size; ++x) {
          if (gameState.field[x][y] > gameState.field[target][y] ^ x > target) {
            --columnInConflict[x][y];
          }
        }
        ++counter;
      }
    }
    return counter * 2;
  }
}

interface AStarVisitor<V extends Vertex> {

  public List<V> expandVertex(V vertex);

  public void setAnswer(V vertex);
}

class AStarVisitorNPuzzle implements AStarVisitor<GameState> {
  private int size;
  private byte emptySign;
  private boolean isSolvable;

  private GameState result;

  public AStarVisitorNPuzzle(int size, byte emptySign, GameState from) {
    this.size = size;
    this.emptySign = emptySign;
    this.isSolvable = isSolvable(from);
  }

  private boolean isSolvable(GameState start) {
    byte[] linearForm = new byte[size * size];
    int counter = 0;
    for (int x = 0; x < size; ++x)
      for (int y = 0; y < size; ++y)
        linearForm[counter++] = start.field[x][y];

    int invCount = countInversions(linearForm);
    return (size % 2 == 1) ? (invCount % 2 == 0) : (start.emptyX % 2 == 0 ^ invCount % 2 == 0);
  }

  private int countInversions(byte[] arr) {
    // should be small so just N^2 count
    int counter = 0;
    for (int p1 = 0; p1 < size * size; ++p1) {
      if (arr[p1] == emptySign) continue;
      for (int p2 = p1 + 1; p2 < size * size; ++p2) {
        if (arr[p2] != emptySign && arr[p1] > arr[p2]) ++counter;
      }
    }
    return counter;
  }

  @Override
  public List<GameState> expandVertex(GameState vertex) {
    if (!isSolvable) return new ArrayList<>();

    byte x = vertex.emptyX;
    byte y = vertex.emptyY;

    List<GameState> answer = new ArrayList<>();

    if (x > 0) {
      answer.add(tryGo(vertex, (byte) (x - 1), y, GameState.Move.U));
    }
    if (y > 0) {
      answer.add(tryGo(vertex, x, (byte) (y - 1), GameState.Move.L));
    }
    if (x < size - 1) {
      answer.add(tryGo(vertex, (byte) (x + 1), y, GameState.Move.D));
    }
    if (y < size - 1) {
      answer.add(tryGo(vertex, x, (byte) (y + 1), GameState.Move.R));
    }

    return answer;
  }

  private GameState tryGo(GameState from, byte toX, byte toY, GameState.Move move) {
    byte[][] newField = Arrays.stream(from.field).map(byte[]::clone).toArray(byte[][]::new);
    swap(newField, from.emptyX, from.emptyY, toX, toY);
    return new GameState(from, move, newField, toX, toY, from.depth + 1);
  }

  private void swap(byte[][] field, int x1, int y1, int x2, int y2) {
    byte tmp = field[x1][y1];
    field[x1][y1] = field[x2][y2];
    field[x2][y2] = tmp;
  }

  @Override
  public void setAnswer(GameState vertex) {
    result = vertex;
  }

  public Stack<GameState.Move> reconstructOrder() {
    if (result == null) return null;
    Stack<GameState.Move> moves = new Stack<>();
    GameState current = result;
    while (current.parent != null) {
      moves.add(current.moveFromParent);
      current = current.parent;
    }
    return moves;
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static byte size = 3;
  private static byte emptySign = 0;

  private static <V extends Vertex, A extends AStarVisitor<V>, H extends Heuristic<V>> void AStar(V from, A visitor, H heuristic) {
    PriorityQueue<V> queue = new PriorityQueue<>();
    Set<String> visited = new HashSet<>();
    from.value = from.depth + heuristic.calculate(from);
    queue.add(from);
    visited.add(from.toString());

    V current;
    while ((current = queue.poll()) != null) {
      if (current.value - current.depth == 0) {
        visitor.setAnswer(current);
        break;
      }

      for (V vert : visitor.expandVertex(current)) {
        if (!visited.contains(vert.toString())) {
          vert.value = vert.depth + heuristic.calculate(vert);
          visited.add(vert.toString());
          queue.add(vert);
        }
      }
    }
  }

  public static void main(String[] args) {
    byte emptyX = -1;
    byte emptyY = -1;
    byte[][] field = new byte[size][size];
    for (byte x = 0; x < size; ++x) {
      for (byte y = 0; y < size; ++y) {
        field[x][y] = (byte) in.nextInt();
        if (field[x][y] == emptySign) {
          emptyX = x;
          emptyY = y;
        }
      }
    }
    GameState start = new GameState(null, null, field, emptyX, emptyY, 0);
    AStarVisitorNPuzzle visitor = new AStarVisitorNPuzzle(size, emptySign, start);
    Heuristic<GameState> heuristic = new ManhattanAndLinearConflict<>(size, emptySign);
    AStar(start, visitor, heuristic);
    var path = visitor.reconstructOrder();
    if (path == null) {
      System.out.println(-1);
      return;
    }
    System.out.println(path.size());
    while (!path.isEmpty()) {
      System.out.print(path.pop());
    }
  }
}
