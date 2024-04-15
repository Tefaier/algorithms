package Task01.Task01_D;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  // Heap version with maximum
  static class Heap<T extends Comparable> {
    ArrayList<T> arr;

    public Heap(ArrayList<T> arr) {
      this.arr = arr;
      heapify();
    }

    public int size() {
      return arr.size();
    }

    public Heap() {
      this.arr = new ArrayList<>();
    }

    private void siftUp(int index) {
      if (index == 0) return;
      int parent = (index - 1) / 2;
      if (arr.get(parent).compareTo(arr.get(index)) < 0) {
        T tmp = arr.get(parent);
        arr.set(parent, arr.get(index));
        arr.set(index, tmp);
        siftUp(parent);
      }
    }

    private void siftDown(int index) {
      int left = 2 * index + 1;
      int right = 2 * index + 2;
      if (right > arr.size()) {
        return;
      }

      if (right == arr.size()) {
        right = left;
      }

      int imax = arr.get(left).compareTo(arr.get(right)) > 0 ? left : right;
      if (arr.get(index).compareTo(arr.get(imax)) < 0) {
        T tmp = arr.get(imax);
        arr.set(imax, arr.get(index));
        arr.set(index, tmp);
        siftDown(imax);
      }
    }

    public void add(T value) {
      arr.add(value);
      siftUp(arr.size() - 1);
    }

    public T top() {
      return arr.get(0);
    }

    public T removeMax() {
      T tmp = arr.get(0);
      arr.set(0, arr.get(arr.size() - 1));
      arr.remove(arr.size() - 1);
      siftDown(0);
      return tmp;
    }

    public boolean isEmpty() {
      return arr.size() == 0;
    }

    private void heapify() {
      for (int i = arr.size() / 2; i >= 0; i--) {
        siftDown(i);
      }
    }
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int mod = (1 << 30) - 1;
    int valuesToCount = input.nextInt();
    int smallestToGet = input.nextInt();
    int a0 = input.nextInt();
    int mult = input.nextInt();
    int incr = input.nextInt();

    ArrayList<Long> sortedValues = new ArrayList<>();

    long lastValue = (a0 * mult + incr) & mod;
    Heap<Long> heap = new Heap();
    heap.add(lastValue);

    for (int i = 2; i <= valuesToCount; i++) {
      lastValue = (lastValue * mult + incr) & mod;
      if (heap.size() < smallestToGet) {
        heap.add(lastValue);
      } else if (lastValue < heap.top()) {
        heap.removeMax();
        heap.add(lastValue);
      }
    }

    while (!heap.isEmpty()) {
      sortedValues.add(heap.removeMax());
    }
    for (int i = sortedValues.size() - 1; i >= 0; i--) {
      System.out.print(sortedValues.get(i) + " ");
    }
  }
}
