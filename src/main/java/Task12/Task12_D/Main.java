package Task12.Task12_D;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
  private static final Parser in = new Parser(System.in);

  private static void test() {
    Random random = new Random();
    while (true) {
      HashSet<Integer> values = new HashSet<>();
      int n = random.nextInt(1, 10000);
      for (int i = 0; i < n; i++) {
        values.add(random.nextInt(-1000000000, 1000000000));
      }
      int r = random.nextInt(1000);
      FKS fks = new FKS();
      int[] values2 =
          Arrays.stream(values.toArray(new Integer[0])).mapToInt(Integer::intValue).toArray();
      fks.initialize(values2);
      for (int i = 0; i < r; i++) {
        int val = random.nextInt(-1000000000, 1000000000);
        if (fks.contains(val) != values.contains(val)) {
          System.out.println("ERROR");
        }
      }
    }
  }

  public static void main(String[] args) {
    // test();
    int valuesNum = in.nextInt();
    int[] values = new int[valuesNum];
    for (int i = 0; i < valuesNum; i++) {
      values[i] = in.nextInt();
    }
    FKS frozenSet = new FKS();
    frozenSet.initialize(values);

    int requestNum = in.nextInt();
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestNum; i++) {
      answer.append(frozenSet.contains(in.nextInt()) ? "Yes\n" : "No\n");
    }
    System.out.print(answer);
  }
}

class UniversalHashFunction {
  private static final int primeModulo = (int) (1e9 + 7);
  private static final Random random = new Random();

  private final long a;
  private final long b;

  public UniversalHashFunction(int a, int b) {
    this.a = a;
    this.b = b;
  }

  public int calc(int input) {
    return (int) (((a * input) % primeModulo + b) % primeModulo);
  }

  public static UniversalHashFunction generate() {
    return new UniversalHashFunction(
        random.nextInt(1, primeModulo - 1), random.nextInt(0, primeModulo - 1));
  }
}

class FKS {
  private class InnerFKS {
    private UniversalHashFunction selfHash;
    private List<Optional<Integer>> buckets;

    public InnerFKS(List<Integer> values) {
      boolean result;
      do {
        buckets = new ArrayList<>();
        for (int i = 0; i < values.size() * values.size(); i++) {
          buckets.add(Optional.empty());
        }

        result = tryGenerating(values);
      } while (!result);
    }

    private boolean tryGenerating(List<Integer> values) {
      boolean[] encountered = new boolean[buckets.size()];

      selfHash = UniversalHashFunction.generate();
      for (Integer value : values) {
        int index = selfHash.calc(value) % encountered.length;
        index += index < 0 ? encountered.length : 0;
        if (encountered[index]) {
          return false;
        }
        encountered[index] = true;
        buckets.set(index, Optional.of(value));
      }
      return true;
    }

    public boolean contains(int value) {
      if (buckets.isEmpty()) return false;
      int selfIndex = selfHash.calc(value) % buckets.size();
      selfIndex += selfIndex < 0 ? buckets.size() : 0;
      return buckets.get(selfIndex).isPresent() && buckets.get(selfIndex).get() == value;
    }
  }

  private int[] possibleValues;
  private UniversalHashFunction outerHash;
  private List<InnerFKS> bucketFKSes = new ArrayList<>();

  public void initialize(int[] possibleValues) {
    this.possibleValues = possibleValues;
    List<List<Integer>> result;
    do {
      result = tryGenerating();
    } while (result.stream().map(val -> val.size() * val.size()).reduce(0, Integer::sum)
        > 4 * possibleValues.length);
    for (int i = 0; i < possibleValues.length; i++) {
      bucketFKSes.add(new InnerFKS(result.get(i)));
    }
  }

  private List<List<Integer>> tryGenerating() {
    List<List<Integer>> buckets = new ArrayList<>();
    for (int i = 0; i < possibleValues.length; i++) {
      buckets.add(new ArrayList<>());
    }

    outerHash = UniversalHashFunction.generate();
    for (int possibleValue : possibleValues) {
      int index = outerHash.calc(possibleValue) % possibleValues.length;
      index += index < 0 ? possibleValues.length : 0;
      buckets.get(index).add(possibleValue);
    }
    return buckets;
  }

  public boolean contains(int value) {
    if (bucketFKSes.isEmpty()) return false;
    int outerIndex = outerHash.calc(value) % bucketFKSes.size();
    outerIndex += outerIndex < 0 ? bucketFKSes.size() : 0;
    return bucketFKSes.get(outerIndex).contains(value);
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
