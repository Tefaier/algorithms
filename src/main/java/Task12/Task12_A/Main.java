package Task12.Task12_A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    int requestNum = Integer.parseInt(in.readLine());
    HashMap likes = new HashMap(requestNum * 3);
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestNum; i++) {
      switch (in.readLine().charAt(0)) {
        case '?':
          answer.append(likes.get(in.readLine())).append('\n');
          break;
        case '+':
          likes.insert(in.readLine(), in.readLine());
          break;
        case '-':
          likes.remove(in.readLine());
      }
    }
    System.out.print(answer);
  }
}

class HashMap {
  class HashNode {
    String key;
    String value;

    public HashNode(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }

  int capacity;
  int size;
  HashNode[] nodesArray;
  HashNode tombStone;

  public HashMap(int initialCapacity) {
    this.capacity = initialCapacity;
    this.size = 0;
    this.nodesArray = new HashNode[this.capacity];
    this.tombStone = new HashNode("", "-1");
  }

  private int hashCode(String hash) {
    return Math.abs(hash.hashCode()) % this.capacity;
  }

  private int jumpFunction(int index, int iteration) {
    index += iteration * iteration + iteration;
    return index % this.capacity;
  }

  public void insert(String key, String value) {
    HashNode temp = new HashNode(key, value);
    int jumpIndex = hashCode(key);
    int iteration = 1;
    while (this.nodesArray[jumpIndex] != null
        && !key.equals(this.nodesArray[jumpIndex].key)
        && this.nodesArray[jumpIndex] != tombStone) {
      jumpIndex = jumpFunction(jumpIndex, iteration);
      iteration++;
    }

    if (this.nodesArray[jumpIndex] == null || this.nodesArray[jumpIndex] == tombStone) {
      this.size++;
    }
    this.nodesArray[jumpIndex] = temp;
  }

  public void remove(String key) {
    int jumpIndex = hashCode(key);
    int iteration = 1;
    while (this.nodesArray[jumpIndex] != null) {
      if (key.equals(this.nodesArray[jumpIndex].key)) {
        this.nodesArray[jumpIndex] = this.tombStone;
        this.size--;
      }
      jumpIndex = jumpFunction(jumpIndex, iteration);
      iteration++;
    }
    // not found!!!
  }

  public String get(String key) {
    int jumpIndex = hashCode(key);
    int iteration = 1;
    while (this.nodesArray[jumpIndex] != null) {
      if (key.equals(this.nodesArray[jumpIndex].key)) {
        return this.nodesArray[jumpIndex].value;
      }
      jumpIndex = jumpFunction(jumpIndex, iteration);
      iteration++;
    }

    return tombStone.value;
  }
}
