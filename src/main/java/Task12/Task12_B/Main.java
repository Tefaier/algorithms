package Task12.Task12_B;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Objects;

public class Main {
  private static final Parser in = new Parser(System.in);

  static class LinkContainer {
    public LinkContainer prev;
    public LinkContainer next;
    public final int key;
    public final Integer value;

    public LinkContainer(int key, Integer value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      LinkContainer that = (LinkContainer) o;
      return key == that.key;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(key);
    }
  }

  public static void main(String[] args) {
    int requestNum = in.nextInt();
    int limit = in.nextInt();

    LinkContainer first = null;
    LinkContainer last = null;
    int counter = 0;

    Hashtable<Integer, LinkContainer> activeLinks = new Hashtable<>();
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestNum; i++) {
      int reqType = in.nextInt();
      if (reqType == 1) {
        int key = in.nextInt();
        LinkContainer val = activeLinks.get(key);
        // System.out.println(val == null ? "-1\n" : (val.value.toString() + "\n"));
        answer.append(val == null ? "-1\n" : (val.value.toString() + "\n"));
        if (val != null) {
          if (val.next != null) {
            val.next.prev = val.prev;
          } else {
            first = val.prev;
          }
          if (val.prev != null) {
            val.prev.next = val.next;
          } else {
            last = val.next;
          }
          if (first == null) {
            val.prev = null;
            val.next = null;
            first = val;
            last = val;
          } else {
            first.next = val;
            val.prev = first;
            val.next = null;
            first = val;
          }
        }
      } else {
        int key = in.nextInt();
        int value = in.nextInt();
        LinkContainer val = activeLinks.get(key);
        if (val != null) {
          if (val.next != null) {
            val.next.prev = val.prev;
          } else {
            first = val.prev;
          }
          if (val.prev != null) {
            val.prev.next = val.next;
          } else {
            last = val.next;
          }
        } else {
          counter++;
        }
        LinkContainer newVal = new LinkContainer(key, value);
        activeLinks.put(key, newVal);
        if (first == null) {
          first = newVal;
          last = newVal;
        } else {
          first.next = newVal;
          newVal.prev = first;
          first = newVal;
        }
        if (counter > limit) {
          activeLinks.remove(last.key);
          last.next.prev = null;
          last = last.next;
          counter--;
        }
      }
    }
    System.out.println(answer);
  }
}

// https://habr.com/ru/articles/91283/
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
