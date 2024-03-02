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

class GameState implements Comparable {
  public enum Move {
    U,
    D,
    L,
    R
  }

  public static int dimension;

  public int depth;
  public int value;
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
      return value - ((GameState) o).value;
    }
    return 0;
  }

  @Override
  public String toString() {
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

class AStar {
  private PriorityQueue<GameState> queue = new PriorityQueue<>();
  private Set<String> visited = new HashSet<>();
  private int visitCount = 0;
  private GameState target;
  private int size;
  private byte emptySign;

  public AStar(GameState source, GameState target, byte emptySign) {
    this.target = target;
    GameState.dimension = source.field.length;
    size = GameState.dimension;
    this.emptySign = emptySign;
    queue.add(source);
  }

  public GameState execute() {
    if (!isSolvable(queue.peek())) return null;
    visited.add(queue.peek().stringRepr);

    while (!queue.isEmpty()) {
      var current = queue.poll();
      ++visitCount;

      if (current.equals(target))
        return current;

      expand(current);
    }
    return null;
  }

  private void expand(GameState vertex) {
    byte x = vertex.emptyX;
    byte y = vertex.emptyY;

    if (x > 0) {
      tryGo(vertex, (byte) (x - 1), y, GameState.Move.U);
    }
    if (y > 0) {
      tryGo(vertex, x, (byte) (y - 1), GameState.Move.L);
    }
    if (x < size - 1) {
      tryGo(vertex, (byte) (x + 1), y, GameState.Move.D);
    }
    if (y < size - 1) {
      tryGo(vertex, x, (byte) (y + 1), GameState.Move.R);
    }
  }

  private void tryGo(GameState from, byte toX, byte toY, GameState.Move move) {
    byte[][] newField = Arrays.stream(from.field).map(byte[]::clone).toArray(byte[][]::new);
    swap(newField, from.emptyX, from.emptyY, toX, toY);
    GameState newGameState = new GameState(from, move, newField, toX, toY, from.depth + 1);
    if (!visited.contains(newGameState.stringRepr)) {
      newGameState.value = newGameState.depth + heuristic(newGameState);
      visited.add(newGameState.stringRepr);
      queue.add(newGameState);
    }
  }

  private void swap(byte[][] field, int x1, int y1, int x2, int y2) {
    byte tmp = field[x1][y1];
    field[x1][y1] = field[x2][y2];
    field[x2][y2] = tmp;
  }

  private int heuristic(GameState gameState) {
    return manhattanDist(gameState) + linearConflict(gameState);
  }

  // not used currently
  private int misplaceCount(GameState gameState) {
    int result = 0;

    for (var x = 0; x < size; x++) {
      for (var y = 0; y < size; y++)
        if (gameState.field[x][y] != target.field[x][y] && gameState.field[x][y] != emptySign) result++;
    }

    return result;
  }

  private int manhattanDist(GameState gameState) {
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
    int[][] rowTarget = new int[size][size];
    int[][] columnTarget = new int[size][size];
    for (int x = 0; x < size; ++x) {
      for (int y = 0; y < size; ++y) {
        rowTarget[x][y] = (gameState.field[x][y] - 1) / size;
        columnTarget[x][y] = (gameState.field[x][y] - 1) % size;
      }
    }
    rowTarget[gameState.emptyX][gameState.emptyY] = -1;

    int rowConflicts = 0;
    int columnConflicts = 0;
    for (int x = 0; x < size; ++x) {
      for (int y = 0; y < size; ++y) {
        //get row conflicts
        if (rowTarget[x][y] == x) {
          for (int k = y + 1; k < size; ++k) {
            if (gameState.field[x][y] > gameState.field[x][k] && rowTarget[x][k] == x) {
              rowConflicts += 2;
            }
          }
        }
        //get column conflicts
        if (columnTarget[x][y] == y) {
          for (int k = x + 1; k < size; ++k) {
            if (gameState.field[x][y] > gameState.field[k][y] && columnTarget[k][y] == y) {
              columnConflicts += 2;
            }
          }
        }
      }
    }

    return rowConflicts + columnConflicts;
  }

  private boolean isSolvable(GameState start) {
    byte[] linearForm = new byte[size * size];
    int counter = 0;
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++)
        linearForm[counter++] = start.field[i][j];

    int invCount = countInversions(linearForm);
    return size % 2 == 1 ? invCount % 2 == 0 : (start.emptyX % 2 == 0 ^ invCount % 2 == 0);
  }

  private int countInversions(byte[] arr) {
    // should be small so just N^2 count
    int counter = 0;
    for (int p1 = 0; p1 < size * size; p1++) {
      if (arr[p1] == emptySign) continue;
      for (int p2 = p1 + 1; p2 < size * size; p2++) {
        if (arr[p2] != emptySign && arr[p1] > arr[p2]) counter++;
      }
    }
    return counter;
  }

  public Stack<GameState.Move> reconstructOrder(GameState from) {
    Stack<GameState.Move> moves = new Stack<>();
    GameState current = from;
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
    AStar alg = new AStar(start, getTarget(), emptySign);
    GameState finish = alg.execute();
    if (finish == null) {
      System.out.println(-1);
      return;
    }
    var path = alg.reconstructOrder(finish);
    System.out.println(path.size());
    while (!path.isEmpty()) {
      System.out.print(path.pop() + " ");
    }
  }

  private static GameState getTarget() {
    byte[][] field = new byte[size][size];
    for (byte x = 0; x < size; ++x) {
      for (byte y = 0; y < size; ++y) {
        field[x][y] = (byte) (x * size + y + 1);
      }
    }
    field[size - 1][size - 1] = emptySign;
    return new GameState(null, null, field, (byte) (size - 1), (byte) (size - 1), 0);
  }
}
