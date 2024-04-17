package Task09.Task09_D;

import java.io.DataInputStream;
import java.io.InputStream;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int fieldSizeX = in.nextInt();
    int fieldSizeY = in.nextInt();
    String[] fieldFull = new String[fieldSizeX];
    for (int i = 0; i < fieldSizeX; i++) {
      fieldFull[i] = in.nextString(fieldSizeY);
    }

    int partSizeX = in.nextInt();
    int partSizeY = in.nextInt();
    String[] fieldPart = new String[partSizeX];
    for (int i = 0; i < partSizeX; i++) {
      fieldPart[i] = in.nextString(partSizeY);
    }

    HashTable.calcBase(fieldSizeX + 1, fieldSizeY + 1);
    HashTable fullHash = new HashTable(fieldFull);
    HashTable partHash = new HashTable(fieldPart);

    --partSizeX;
    --partSizeY;
    long counter = 0;
    for (int x = 0; x < fieldSizeX - partSizeX; ++x) {
      for (int y = 0; y < fieldSizeY - partSizeY; ++y) {
        counter += StringHandler.notMultipleErrors(fullHash, partHash, x, y, x + partSizeX, y + partSizeY) ? 1 : 0;
      }
    }

    System.out.println(counter);
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

// backward hash
class HashTable {
  private static long shiftX = 257;
  private static long shiftY = 251;
  private static long module1 = 1000000007;
  private static long module2 = 1000000009;
  private static long[] baseX1;
  private static long[] baseY1;
  private static long[] baseX2;
  private static long[] baseY2;

  private String[] str;
  public long[][] prefTable1;
  public long[][] prefTable2;
  public int sizeX;
  public int sizeY;

  public HashTable(String[] strList) {
    this.str = strList;
    sizeX = strList.length;
    sizeY = strList[0].length();
    prefTable1 = new long[sizeX + 1][sizeY + 1];
    prefTable2 = new long[sizeX + 1][sizeY + 1];
    long normaliser1 = module1 * shiftX * shiftY;
    long normaliser2 = module2 * shiftX * shiftY;
    for (int x = 1; x <= sizeX; ++x) {
      for (int y = 1; y <= sizeY; ++y) {
        int ch = this.str[x - 1].charAt(y - 1);
        prefTable1[x][y] = (
            prefTable1[x - 1][y] * shiftX +
                prefTable1[x][y - 1] * shiftY -
                prefTable1[x - 1][y - 1] * shiftX * shiftY +
                ch + normaliser1
        ) % module1;
        prefTable2[x][y] = (
            prefTable2[x - 1][y] * shiftX +
                prefTable2[x][y - 1] * shiftY -
                prefTable2[x - 1][y - 1] * shiftX * shiftY +
                ch + normaliser2
        ) % module2;
      }
    }
  }

  // works by indexes in initial table
  // x2 >= x1 y2 >= y1
  public long hashSubTable1(int x1, int y1, int x2, int y2) {
    ++x2;
    ++y2;
    long hFull = prefTable1[x2][y2];
    long hPx = prefTable1[x1][y2] * baseX1[x2 - x1] % module1;
    long hPy = prefTable1[x2][y1] * baseY1[y2 - y1] % module1;
    long hMin = (prefTable1[x1][y1] * baseX1[x2 - x1] % module1) * baseY1[y2 - y1] % module1;
    return (hFull + hMin - hPx - hPy + 2 * module1) % module1;
  }

  // works by indexes in initial table
  // x2 >= x1 y2 >= y1
  public long hashSubTable2(int x1, int y1, int x2, int y2) {
    ++x2;
    ++y2;
    long hFull = prefTable2[x2][y2];
    long hPx = prefTable2[x1][y2] * baseX2[x2 - x1] % module2;
    long hPy = prefTable2[x2][y1] * baseY2[y2 - y1] % module2;
    long hMin = (prefTable2[x1][y1] * baseX2[x2 - x1] % module2) * baseY2[y2 - y1] % module2;
    return (hFull + hMin - hPx - hPy + 2 * module2) % module2;
  }

  public static void calcBase(int upToX, int upToY) {
    baseX1 = new long[upToX];
    baseY1 = new long[upToY];
    baseX2 = new long[upToX];
    baseY2 = new long[upToY];
    baseX1[0] = 1;
    baseY1[0] = 1;
    baseX2[0] = 1;
    baseY2[0] = 1;
    for (int i = 1; i < upToX; ++i) {
      baseX1[i] = baseX1[i - 1] * shiftX % module1;
      baseX2[i] = baseX2[i - 1] * shiftX % module2;
    }
    for (int i = 1; i < upToY; ++i) {
      baseY1[i] = baseY1[i - 1] * shiftY % module1;
      baseY2[i] = baseY2[i - 1] * shiftY % module2;
    }
  }
}

class StringHandler {
  // both sides included
  public static boolean notMultipleErrors(HashTable full, HashTable part, int x1, int y1, int x2, int y2) {
    if (!hasError(full, part, x1, y1, x2, y2, x1, y1)) return true;

    boolean error1;
    boolean error2;
    int m;
    int offsetX = x1;
    int offsetY = y1;
    while (x2 != x1) {
      m = (x1 + x2) / 2; // included in "left"
      error1 = hasError(full, part, x1, y1, m, y2, offsetX, offsetY);
      error2 = hasError(full, part, m + 1, y1, x2, y2, offsetX, offsetY);
      if (error1 && error2) return false;
      if (error1) {
        x2 = m;
      } else {
        x1 = m + 1;
      }
    }
    while (y2 != y1) {
      m = (y1 + y2) / 2; // included in "upper"
      error1 = hasError(full, part, x1, y1, x1, m, offsetX, offsetY);
      error2 = hasError(full, part, x1, m + 1, x1, y2, offsetX, offsetY);
      if (error1 && error2) return false;
      if (error1) {
        y2 = m;
      } else {
        y1 = m + 1;
      }
    }
    return true;
  }

  private static boolean hasError(HashTable full, HashTable part, int x1, int y1, int x2, int y2, int partOffsetX, int partOffsetY) {
    return full.hashSubTable1(x1, y1, x2, y2) != part.hashSubTable1(x1 - partOffsetX, y1 - partOffsetY, x2 - partOffsetX, y2 - partOffsetY) ||
        full.hashSubTable2(x1, y1, x2, y2) != part.hashSubTable2(x1 - partOffsetX, y1 - partOffsetY, x2 - partOffsetX, y2 - partOffsetY);
  }
}
