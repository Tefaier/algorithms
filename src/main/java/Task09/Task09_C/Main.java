package Task09.Task09_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int wordNum = in.nextInt();
    HashString.calcBase(12);
    HashString[] words = new HashString[wordNum];
    HashString[] wordsReversed = new HashString[wordNum];
    StringBuilder str;
    for (int i = 0; i < words.length; ++i) {
      str = new StringBuilder(in.nextString(11));
      words[i] = new HashString(str);
      wordsReversed[i] = new HashString(str.reverse());
      // wordsReversed[i] = new HashString(new StringBuilder(str).reverse().toString());
    }
    List<Integer> values = new ArrayList<>();
    for (int w1 = 0; w1 < wordNum; ++w1) {
      for (int w2 = 0; w2 < wordNum; ++w2) {
        if (w1 == w2) continue;
        if (StringHandler.palindromCheckByHash(words[w1], wordsReversed[w1], words[w2], wordsReversed[w2])) {
          values.add(w1 + 1);
          values.add(w2 + 1);
        }
      }
    }
    System.out.println(values.size() / 2);
    for (int i = 0; i < values.size() / 2; ++i) {
      System.out.println(values.get(i * 2) + " " + values.get(i * 2 + 1));
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

// backward hash
class HashString {
  private static long aplhabet = 257;
  private static int module1 = 1000000007;
  private static int module2 = 1000000009;
  private static int[] base1;
  private static int[] base2;

  private StringBuilder str;
  private int[] pref1;
  private int[] pref2;

  public HashString(StringBuilder str) {
    this.str = str;
    pref1 = new int[this.str.length() + 1];
    pref2 = new int[this.str.length() + 1];
    for (int i = 1; i < this.str.length() + 1; ++i) {
      int ch = this.str.charAt(i - 1);
      pref1[i] = (int) ((pref1[i - 1] * aplhabet + ch) % module1);
      pref2[i] = (int) ((pref2[i - 1] * aplhabet + ch) % module2);
    }
  }

  public static int concatHash1(HashString left, HashString right) {
    int hLeft = (int) ((long) (left.pref1[left.str.length()]) * base1[right.str.length()] % module1);
    return (hLeft + right.pref1[right.str.length()]) % module1;
  }

  public static int concatHash2(HashString left, HashString right) {
    int hLeft = (int) ((long) (left.pref2[left.str.length()]) * base2[right.str.length()] % module2);
    return (hLeft + right.pref2[right.str.length()]) % module2;
  }

  public static void calcBase(int upTo) {
    base1 = new int[upTo];
    base1[0] = 1;
    base2 = new int[upTo];
    base2[0] = 1;
    for (int i = 1; i < upTo; ++i) {
      base1[i] = (int) (base1[i - 1] * aplhabet % module1);
      base2[i] = (int) (base2[i - 1] * aplhabet % module2);
    }
  }
}

class StringHandler {
  public static boolean palindromCheckByHash(HashString forw1, HashString backw1, HashString forw2, HashString backw2) {
    return HashString.concatHash1(forw1, forw2) == HashString.concatHash1(backw2, backw1)
        && HashString.concatHash2(forw1, forw2) == HashString.concatHash2(backw2, backw1);
  }
}
