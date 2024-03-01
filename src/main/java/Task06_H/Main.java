package Task06_H;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

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
  public static int xDimension;
  public static int yDimension;

  public int depth;
  public int value;
  public byte[][] field;
  public byte emptyX;
  public byte emptyY;
  public String stringRepr;

  public GameState(byte[][] field, byte emptyX, byte emptyY, int depth) {
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
      return this.toString().equals(obj.toString());
    }
    return false;
  }
}

class AStar {
  private PriorityQueue<GameState> queue = new PriorityQueue<>();
  private Set<String> visited = new HashSet<>();
  private int visitCount = 0;
  private GameState target;
  private int sizeX;
  private int sizeY;
  private byte emptySign;

  public AStar(GameState source, byte emptySign) {
    GameState.xDimension = source.field.length;
    GameState.yDimension = source.field[0].length;
    this.emptySign = emptySign;
    queue.add(source);
    sizeX = source.field.length;
    sizeY = source.field[0].length;
  }

  public GameState execute() {
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
      tryGo(vertex, (byte) (x - 1), y);
    }
    if (y > 0) {
      tryGo(vertex, x, (byte) (y - 1));
    }
    if (x < sizeX - 1) {
      tryGo(vertex, (byte) (x + 1), y);
    }
    if (y < sizeY - 1) {
      tryGo(vertex, x, (byte) (y + 1));
    }
  }

  private void tryGo(GameState from, byte toX, byte toY) {
    byte[][] newField = Arrays.stream(from.field).map(byte[]::clone).toArray(byte[][]::new);
    swap(newField, from.emptyX, from.emptyY, toX, toY);
    GameState newGameState = new GameState(newField, toX, toY, from.depth + 1);
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

  private int misplaceCount(GameState gameState) {
    int result = 0;

    for (var x = 0; x < GameState.xDimension; x++) {
      for (var y = 0; y < GameState.yDimension; y++)
        if (gameState.field[x][y] != target.field[x][y] && gameState.field[x][y] != emptySign) result++;
    }

    return result;
  }

  private int manhattanDist(GameState gameState) {
    int result = 0;
    byte val;
    int xTarget;
    int yTarget;

    for (var x = 0; x < GameState.xDimension; x++) {
      for (var y = 0; y < GameState.yDimension; y++) {
        val = gameState.field[x][y];
        if (val == emptySign) continue;
        xTarget = (val - 1) / GameState.yDimension;
        yTarget = (val - 1) % GameState.yDimension;
        result += Math.abs(x - xTarget) + Math.abs(y - yTarget);
      }
    }

    return result;
  }

  private int linearConflict(GameState gameState) {
    int[][] rowTarget = new int[GameState.xDimension][GameState.yDimension];
    int[][] columnTarget = new int[GameState.xDimension][GameState.yDimension];
    for (int x = 0; x < GameState.xDimension; ++x) {
      for (int y = 0; y < GameState.yDimension; ++y) {
        if (gameState.field[x][y] != emptySign) {
          rowTarget[x][y] = (gameState.field[x][y] - 1) / GameState.yDimension;
          columnTarget[x][y] = (gameState.field[x][y] - 1) % GameState.yDimension;
        } else {
          rowTarget[x][y] = -1;
          columnTarget[x][y] = -1;
        }
      }
    }

    int rowConflicts = 0;
    int columnConflicts = 0;
    for (int x = 0; x < GameState.xDimension; ++x) { // -1?
      for (int y = 0; y < GameState.yDimension - 1; ++y) {
        //get row conflicts
        if (rowTarget[x][y] == x) {
          for (int k = y + 1; k < GameState.yDimension; ++k) {
            if (gameState.field[x][y] > gameState.field[x][k] && rowTarget[x][k] == x) {
              rowConflicts += 2;
            }
          }
        }
        //get column conflicts
        if (columnTarget[x][y] == y) {
          for (int k = x + 1; k < GameState.xDimension; ++k) {
            if (gameState.field[x][y] > gameState.field[k][y] && columnTarget[k][y] == y) {
              columnConflicts += 2;
            }
          }
        }
      }
    }
    return rowConflicts + columnConflicts;
  }
}

public class Main {
  public static void main(String[] args) {
    Parser in = new Parser(System.in);
  }
}
