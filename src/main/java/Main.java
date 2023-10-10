import java.util.*;

class Heap {
  ArrayList<Integer> arr;
  ArrayList<Integer> operations;

  public Heap(ArrayList<Integer> arr) {
    this.arr = arr;
    this.operations = new ArrayList<>();
    heapify();
  }

  public Heap() {
    this.arr = new ArrayList<>();
    this.operations = new ArrayList<>();
  }

  private void siftUp(int index) {
    if (index == 0) {
      return;
    }
    int parent = (index - 1) / 2;
    if (arr.get(parent) > arr.get(index)) {
      swapOperated(index, parent);
      Integer tmp = arr.get(parent);
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

    int imin = arr.get(left) < arr.get(right) ? left : right;
    if (arr.get(index) > arr.get(imin)) {
      swapOperated(index, imin);
      Integer tmp = arr.get(imin);
      arr.set(imin, arr.get(index));
      arr.set(index, tmp);
      siftDown(imin);
    }
  }

  public void add(Integer value) {
    recordOperation(arr.size());
    arr.add(value);
    siftUp(arr.size() - 1);
  }

  public Integer getMin() {
    recordOperation(-1);
    return arr.get(0);
  }

  public Integer extractMin() {
    recordOperation(-1);
    updateOperated(0, -1);
    if (arr.size() > 1) {
      updateOperated(arr.size() - 1, 0);
    }
    Integer tmp = arr.get(0);
    arr.set(0, arr.get(arr.size() - 1));
    arr.remove(arr.size() - 1);
    siftDown(0);
    return tmp;
  }

  public boolean isEmpty() {
    return arr.size() == 0;
  }

  public void adjustValue(int operation, int change) {
    recordOperation(-1);
    int index = getOperated(operation);
    arr.set(index, arr.get(index) - change);
    siftUp(index);
  }

  private void heapify() {
    for (int i = arr.size() / 2; i >= 0; i--) {
      siftDown(i);
    }
  }

  private void recordOperation(int index) {
    // index of the new element and -1 if it's not relevant
    operations.add(index);
  }

  private int getOperated(int operation) {
    // returns index of element in the arr
    return operations.get(operation - 1);
  }

  private void updateOperated(int index, int indexNew) {
    int location = operations.indexOf(index);
    operations.set(location, indexNew);
  }

  private void swapOperated(int swap1, int swap2) {
    int location1 = operations.indexOf(swap1);
    int location2 = operations.indexOf(swap2);
    int tmp = operations.get(location1);
    operations.set(location1, operations.get(location2));
    operations.set(location2, tmp);
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
