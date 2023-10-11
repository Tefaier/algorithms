import java.util.*;

class IntWithOperation {
  public long value;
  public final long operation;

  public IntWithOperation(long value, long operation) {
    this.value = value;
    this.operation = operation;
  }
}

class Heap {
  ArrayList<IntWithOperation> arr;
  int operations;

  public Heap() {
    this.arr = new ArrayList<>();
    this.operations = 0;
  }

  private void siftUp(int index) {
    if (index == 0) {
      return;
    }
    int parent = (index - 1) / 2;
    if (arr.get(parent).value - arr.get(index).value > 0) {
      IntWithOperation tmp = arr.get(parent);
      arr.set(parent, arr.get(index));
      arr.set(index, tmp);
      siftUp(parent);
    }
  }

  private void siftDown(int index) {
    int left = 2 * index + 1;
    int right = 2 * index + 2;
    if (left >= arr.size()) {
      return;
    }

    if (right == arr.size()) {
      right = left;
    }

    int imin = arr.get(left).value - arr.get(right).value > 0 ? right : left;
    if (arr.get(index).value - arr.get(imin).value > 0) {
      IntWithOperation tmp = arr.get(imin);
      arr.set(imin, arr.get(index));
      arr.set(index, tmp);
      siftDown(imin);
    }
  }

  public void add(Long value) {
    arr.add(new IntWithOperation(value, operations));
    siftUp(arr.size() - 1);
  }

  public Long getMin() {
    return arr.get(0).value;
  }

  public void extractMin() {
    arr.set(0, arr.get(arr.size() - 1));
    arr.remove(arr.size() - 1);
    siftDown(0);
  }

  public void adjustValue(long operation, long change) {
    int index = getOperated(operation);
    arr.get(index).value -= change;
    siftUp(index);
  }

  public void recordOperation() {
    operations++;
  }

  private int getOperated(long operation) {
    // returns index of element in the arr
    for (int i = 0; i < arr.size(); i++) {
      if (arr.get(i).operation == operation) {
        return i;
      }
    }
    throw new RuntimeException("Lol");
  }
}

public class Main {
  public static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    Task01_3();
  }

  public static void Task01_3() {
    Heap heap = new Heap();
    long operations = in.nextLong();
    in.nextLine();
    for (long i = 0; i < operations; i++) {
      String command = in.nextLine().replace("\n", "");
      String[] details = command.split(" ");
      heap.recordOperation();
      switch (details[0]) {
        case "insert":
          heap.add(Long.parseLong(details[1]));
          break;
        case "decreaseKey":
          heap.adjustValue(Long.parseLong(details[1]), Long.parseLong(details[2]));
          break;
        case "getMin":
          System.out.println(heap.getMin());
          break;
        case "extractMin":
          heap.extractMin();
          break;
        default:
          break;
      }
    }
  }
}
