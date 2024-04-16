package Task09.Task09_B;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    String str1 = "abanabanaband";
    String str2 = "aaa";
    System.out.println(StringHandler.getPeriod(str1));
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

class StringHandler {
  public static int[] prefixFunction(StringBuilder suffixString, String prefixString, boolean endPriotity) {
    int[] start = new int[prefixString.length()];

    for (int i = 1; i < start.length; ++i) {
      int current = start[i - 1];
      while (prefixString.charAt(i) != prefixString.charAt(current) && current > 0)
        current = start[current - 1];
      if (prefixString.charAt(i) == prefixString.charAt(current))
        start[i] = current + 1;
    }
    if (suffixString == null) return start;

    int[] end = new int[endPriotity ? Math.min(suffixString.length(), prefixString.length()) : suffixString.length()];
    int from = suffixString.length() - end.length;
    end[0] = suffixString.charAt(from) == prefixString.charAt(0) ? 1 : 0;
    for (int i = from + 1; i < suffixString.length(); ++i) {
      int current = end[i - from - 1];
      while ((current == prefixString.length() || suffixString.charAt(i) != prefixString.charAt(current)) && current > 0)
        current = start[current - 1];
      if (suffixString.charAt(i) == prefixString.charAt(current))
        end[i - from] = current + 1;
    }

    return end;
  }

  public static int[] zFunction(String functionString, String prefixString) {
    int[] start = new int[prefixString.length()];

    int left = 0;
    int right = 0;
    for (int i = 1; i < start.length; ++i) {
      start[i] = Math.max(0, Math.min(right - i, start[i - left]));
      while (start[i] + i < prefixString.length() && prefixString.charAt(start[i]) == prefixString.charAt(start[i] + i))
        ++start[i];
      if (start[i] + i > right) {
        left = i;
        right = start[i] + i;
      }
    }
    if (functionString == null) return start;

    int[] end = new int[functionString.length()];
    left = 0;
    right = 0;
    for (int i = 0; i < functionString.length(); ++i) {
      end[i] = Math.max(0, Math.min(right - i, (i - left >= start.length) ? 0 : start[i - left]));
      while (end[i] + i < functionString.length() && end[i] < prefixString.length() && prefixString.charAt(end[i]) == functionString.charAt(end[i] + i))
        ++end[i];
      if (end[i] + i > right) {
        left = i;
        right = end[i] + i;
      }
    }
    return end;
  }

  public static int getPeriod(String str) {
    int[] z = new int[str.length()];

    int left = 0;
    int right = 0;
    for (int i = 1; i < z.length; ++i) {
      z[i] = Math.max(0, Math.min(right - i, z[i - left]));
      while (z[i] + i < str.length() && str.charAt(z[i]) == str.charAt(z[i] + i))
        ++z[i];
      if (z[i] + i > right) {
        left = i;
        right = z[i] + i;
        if (right == z.length) {
          return i;
        }
      }
    }
    return z.length;
  }
}