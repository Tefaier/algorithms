package Task09.Task09_C;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
текущее слово слева, поиск совпадений начала в обратных с остатком палиндромом (или пустым), убрать самосовпадения
текущее слово справа, поиск совпадений себя обратного в прямых с остатком палиндромом (not empty)
 */

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int wordNum = in.nextInt();
    HashString.calcBase(12);
    HashString[] words = new HashString[wordNum];
    HashString[] wordsReversed = new HashString[wordNum];
    StringBuilder str;
    List<HashMap<Long, HashMap<Long, List<Integer>>>> prefixDataForward = new ArrayList<>();
    List<HashMap<Long, HashMap<Long, List<Integer>>>> prefixDataBackward = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      prefixDataForward.add(new HashMap<>());
      prefixDataBackward.add(new HashMap<>());
    }

    for (int i = 0; i < words.length; ++i) {
      str = new StringBuilder(in.nextString(11));
      words[i] = new HashString(str);
      wordsReversed[i] = new HashString(str.reverse());

      for (int r = 0; r < str.length(); ++r) {
        if (StringHandler.palindromPartCheck(words[i], wordsReversed[i], r + 1, str.length() - 1)) {
          long hashForw1 = words[i].hashSubString1(0, r);
          long hashForw2 = words[i].hashSubString2(0, r);
          prefixDataForward.get(r).putIfAbsent(hashForw1, new HashMap<>());
          prefixDataForward.get(r).get(hashForw1).putIfAbsent(hashForw2, new ArrayList<>());
          prefixDataForward.get(r).get(hashForw1).get(hashForw2).add(i);
        }
      }
      for (int r = 0; r < str.length() - 1; ++r) {
        if (StringHandler.palindromPartCheck(wordsReversed[i], words[i], r + 1, str.length() - 1)) {
          long hashBack1 = wordsReversed[i].hashSubString1(0, r);
          long hashBack2 = wordsReversed[i].hashSubString2(0, r);
          prefixDataBackward.get(r).putIfAbsent(hashBack1, new HashMap<>());
          prefixDataBackward.get(r).get(hashBack1).putIfAbsent(hashBack2, new ArrayList<>());
          prefixDataBackward.get(r).get(hashBack1).get(hashBack2).add(i);
        }
      }
    }

    List<Integer> values = new ArrayList<>();
    HashMap<Long, List<Integer>> default1 = new HashMap<>();
    List<Integer> default2 = new ArrayList<>();
    for (int word = 0; word < wordNum; ++word) {
      HashString self = words[word];
      HashString selfReversed = wordsReversed[word];

      for (Integer other : prefixDataBackward
          .get(self.length - 1)
          .getOrDefault(
              self.hashSubString1(0, self.length - 1),
              default1).
          getOrDefault(
              self.hashSubString2(0, self.length - 1),
              default2)) {
        if (other == word) continue;
        values.add(word + 1);
        values.add(other + 1);
      }

      for (Integer other : prefixDataForward
          .get(self.length - 1)
          .getOrDefault(
              selfReversed.hashSubString1(0, selfReversed.length - 1),
              default1).
          getOrDefault(
              selfReversed.hashSubString2(0, selfReversed.length - 1),
              default2)) {
        if (other == word) continue;
        values.add(other + 1);
        values.add(word + 1);
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
  private static long module1 = 1000000007;
  private static long module2 = 1000000009;
  private static long[] base1;
  private static long[] base2;

  public StringBuilder str;
  public int length;
  private long[] pref1;
  private long[] pref2;

  public HashString(StringBuilder str) {
    this.str = str;
    this.length = str.length();
    pref1 = new long[this.str.length() + 1];
    pref2 = new long[this.str.length() + 1];
    for (int i = 1; i < this.str.length() + 1; ++i) {
      int ch = this.str.charAt(i - 1);
      pref1[i] = (pref1[i - 1] * aplhabet + ch) % module1;
      pref2[i] = (pref2[i - 1] * aplhabet + ch) % module2;
    }
  }

  public long hashSubString1(int left, int right) {
    ++right;
    return (pref1[right] - pref1[left] * base1[right - left] % module1 + module1) % module1;
  }

  public long hashSubString2(int left, int right) {
    ++right;
    return (pref2[right] - pref2[left] * base2[right - left] % module2 + module2) % module2;
  }

  public static long concatHash1(HashString left, HashString right) {
    long hLeft = (left.pref1[left.str.length()]) * base1[right.str.length()] % module1;
    return (hLeft + right.pref1[right.str.length()]) % module1;
  }

  public static long concatHash2(HashString left, HashString right) {
    long hLeft = (left.pref2[left.str.length()]) * base2[right.str.length()] % module2;
    return (hLeft + right.pref2[right.str.length()]) % module2;
  }

  public static void calcBase(int upTo) {
    base1 = new long[upTo];
    base1[0] = 1;
    base2 = new long[upTo];
    base2[0] = 1;
    for (int i = 1; i < upTo; ++i) {
      base1[i] = base1[i - 1] * aplhabet % module1;
      base2[i] = base2[i - 1] * aplhabet % module2;
    }
  }
}

class StringHandler {
  public static boolean palindromCheckByHash(HashString forw1, HashString backw1, HashString forw2, HashString backw2) {
    return HashString.concatHash1(forw1, forw2) == HashString.concatHash1(backw2, backw1)
        && HashString.concatHash2(forw1, forw2) == HashString.concatHash2(backw2, backw1);
  }

  public static boolean palindromPartCheck(HashString str, HashString strReversed, Integer from, Integer to) {
    return from > to || (
        str.hashSubString1(from, to) == strReversed.hashSubString1(str.length - to - 1, str.length - from - 1) &&
            str.hashSubString2(from, to) == strReversed.hashSubString2(str.length - to - 1, str.length - from - 1)
    );
  }
}
