import java.util.*;

class IntWithOperation implements Comparable {
  public int value;
  public final int operation;

  public IntWithOperation(int value, int operation) {
    this.value = value;
    this.operation = operation;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof IntWithOperation) {
      return value - ((IntWithOperation) o).value;
    } else {
      return 0;
    }
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
    if (arr.get(parent).compareTo(arr.get(index)) > 0) {
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

    int imin = arr.get(left).compareTo(arr.get(right)) < 0 ? left : right;
    if (arr.get(index).compareTo(arr.get(imin)) > 0) {
      IntWithOperation tmp = arr.get(imin);
      arr.set(imin, arr.get(index));
      arr.set(index, tmp);
      siftDown(imin);
    }
  }

  public void add(Integer value) {
    operations++;
    arr.add(new IntWithOperation(value, operations));
    siftUp(arr.size() - 1);
  }

  public Integer getMin() {
    operations++;
    return arr.get(0).value;
  }

  public void extractMin() {
    operations++;
    arr.set(0, arr.get(arr.size() - 1));
    arr.remove(arr.size() - 1);
    siftDown(0);
  }

  public void adjustValue(int operation, int change) {
    operations++;
    int index = getOperated(operation);
    arr.get(index).value -= change;
    siftUp(index);
  }

  private int getOperated(int operation) {
    // returns index of element in the arr
    for (int i = 0; i < arr.size(); i++) {
      if (arr.get(i).operation == operation) {
        return i;
      }
    }
    return -1;
  }
}

public class Main {
  public static Scanner in = new Scanner(System.in);

  public static void main(String[] args) {
    Task01_3();
  }

  public static void Task01_3() {
    Heap heap = new Heap();
    int operations = in.nextInt();
    in.nextLine();
    for (int i = 0; i < operations; i++) {
      String command = in.nextLine().replace("\n", "");
      String[] details = command.split(" ");
      switch (details[0]) {
        case "insert":
          heap.add(Integer.parseInt(details[1]));
          break;
        case "decreaseKey":
          heap.adjustValue(Integer.parseInt(details[1]), Integer.parseInt(details[2]));
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
