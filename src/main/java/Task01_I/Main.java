package Task01_I;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {

  static class Parser {

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

  public static void main(String[] args) {
    Parser input = new Parser(System.in);

    int number = input.nextInt();

    Long[] values = new Long[number];
    for (int i = 0; i < values.length; i++) {
      values[i] = input.nextLong();
    }

    ArrayDeque[] bitValues = new ArrayDeque[256];
    for (int i = 0; i < bitValues.length; i++) {
      bitValues[i] = new ArrayDeque<Long>();
    }

    for (int k = 0; k < 8; k++) {
      for (int i = 0; i < values.length; i++) {
        int bucket = (int) (values[i] >> (k * 8)) & 255;
        bitValues[bucket].addLast(values[i]);
      }
      int j = 0;
      for (int i = 0; i < bitValues.length; i++) {
        while (!bitValues[i].isEmpty()) {
          values[j] = (long) bitValues[i].removeFirst();
          j++;
        }
      }
    }

    for (int i = 0; i < values.length; i++) {
      System.out.println(values[i]);
    }
  }
}
