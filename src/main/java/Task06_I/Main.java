package Task06_I;

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

class GameState {
  public enum Move {
    U,
    D,
    L,
    R
  }

  public int depth;
  public byte[][] field;
  public byte emptyX;
  public byte emptyY;

  public GameState(byte[][] field, byte emptyX, byte emptyY, int depth) {
    this.field = field;
    this.emptyX = emptyX;
    this.emptyY = emptyY;
    this.depth = depth;
  }
}

class IDA {
  private GameState source;
  private GameState target;
  private int size;
  private byte emptySign;
  private int threshold;
  private int addUpTo;
  private Stack<GameState.Move> path;
  private boolean isSolved;

  public IDA(GameState source, GameState target, byte emptySign) {
    this.source = source;
    this.target = target;
    size = source.field.length;
    this.emptySign = emptySign;
    this.isSolved = false;
  }

  public void execute() {
    if (!isSolvable(source)) return;
    path = new Stack<>();
    threshold = source.depth + heuristic(source);
    while (true) {
      threshold = idaIteration(source, null);
      if (isSolved) {
        break;
      }
    }
  }

  private int idaIteration(GameState state, GameState.Move lastMove) {
    int manh = manhattanDist(state);
    int val = state.depth + manh + linearConflict(state);
    if (val > threshold) {
      return val;
    }
    if (manh == 0) {
      isSolved = true;
      return val;
    }

    int min = Integer.MAX_VALUE;

    if (state.emptyX > 0 && !GameState.Move.U.equals(lastMove)) {
      min = Math.min(tryGo(state, (byte) (state.emptyX - 1), state.emptyY, GameState.Move.D), min);
      if (isSolved) {
        path.add(GameState.Move.D);
        return min;
      }
    }
    if (state.emptyY > 0 && !GameState.Move.L.equals(lastMove)) {
      min = Math.min(tryGo(state, state.emptyX, (byte) (state.emptyY - 1), GameState.Move.R), min);
      if (isSolved) {
        path.add(GameState.Move.R);
        return min;
      }
    }
    if (state.emptyX < size - 1 && !GameState.Move.D.equals(lastMove)) {
      min = Math.min(tryGo(state, (byte) (state.emptyX + 1), state.emptyY, GameState.Move.U), min);
      if (isSolved) {
        path.add(GameState.Move.U);
        return min;
      }
    }
    if (state.emptyY < size - 1 && !GameState.Move.R.equals(lastMove)) {
      min = Math.min(tryGo(state, state.emptyX, (byte) (state.emptyY + 1), GameState.Move.L), min);
      if (isSolved) {
        path.add(GameState.Move.L);
        return min;
      }
    }
    return min;
  }

  private int tryGo(GameState from, byte toX, byte toY, GameState.Move move) {
    byte[][] newField = Arrays.stream(from.field).map(byte[]::clone).toArray(byte[][]::new);
    swap(newField, from.emptyX, from.emptyY, toX, toY);
    GameState newGameState = new GameState(newField, toX, toY, from.depth + 1);
    return idaIteration(newGameState, move);
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

  public Stack<GameState.Move> getPath() {
    return path;
  }
}

public class Main {
  private static Parser in = new Parser(System.in);
  private static byte size = 4;
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
    GameState start = new GameState(field, emptyX, emptyY, 0);
    IDA alg = new IDA(start, getTarget(), emptySign);
    alg.execute();
    var path = alg.getPath();
    if (path == null) {
      System.out.println(-1);
      return;
    }
    System.out.println(path.size());
    while (!path.isEmpty()) {
      System.out.print(path.pop());
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
    return new GameState(field, (byte) (size - 1), (byte) (size - 1), 0);
  }
}
