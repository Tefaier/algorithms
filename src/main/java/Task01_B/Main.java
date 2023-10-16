package Task01_B;

import java.util.ArrayList;
import java.util.Scanner;

class Heap<T extends Comparable> {
  ArrayList<T> arr;

  public Heap(ArrayList<T> arr) {
    this.arr = arr;
    heapify();
  }

  public Heap() {
    this.arr = new ArrayList<>();
  }

  private void siftUp(int index) {
    if (index == 0) return;
    int parent = (index - 1) / 2;
    if (arr.get(parent).compareTo(arr.get(index)) > 0) {
      T tmp = arr.get(parent);
      arr.set(parent, arr.get(index));
      arr.set(index, tmp);
      siftUp(parent);
    }
  }

  private void siftDown(int index) {
    int left = 2 * index + 1;
    int right = 2 * index + 2;
    if (right > arr.size()) return;

    if (right == arr.size()) {
      right = left;
    }

    int imin = arr.get(left).compareTo(arr.get(right)) < 0 ? left : right;
    if (arr.get(index).compareTo(arr.get(imin)) > 0) {
      T tmp = arr.get(imin);
      arr.set(imin, arr.get(index));
      arr.set(index, tmp);
      siftDown(imin);
    }
  }

  public void add(T value) {
    arr.add(value);
    siftUp(arr.size() - 1);
  }

  public T top() {
    return arr.get(0);
  }

  public T getMin() {
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

class SegmentBorder implements Comparable {
  public final int value;
  public final int borderState;

  public SegmentBorder(int value, boolean isLeft) {
    this.value = value;
    this.borderState = isLeft ? -1 : 1;
  }

  @Override
  public int compareTo(Object o) {
    if (o instanceof SegmentBorder) {
      return (value == ((SegmentBorder) o).value) ? borderState : value - ((SegmentBorder) o).value;
    } else {
      return 0;
    }
  }
}

public class Main {

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    ArrayList<SegmentBorder> values = new ArrayList<>();
    ArrayList<SegmentBorder> sorted = new ArrayList<>();
    ArrayList<Integer> answerL = new ArrayList<>();
    ArrayList<Integer> answerR = new ArrayList<>();

    int segmentsNumber = input.nextInt();

    for (int i = 0; i < segmentsNumber; i++) {
      values.add(new SegmentBorder(input.nextInt(), true));
      values.add(new SegmentBorder(input.nextInt(), false));
    }
    Heap<SegmentBorder> heap = new Heap(values);
    for (int i = 0; i < segmentsNumber * 2; i++) {
      sorted.add(heap.getMin());
    }

    int counter = 0;
    Integer startPos = null;
    for (SegmentBorder border : sorted) {
      if (startPos == null) {
        startPos = border.value;
      }
      counter += border.borderState;
      if (counter == 0) {
        answerL.add(startPos);
        answerR.add(border.value);
        startPos = null;
      }
    }

    System.out.println(answerL.size());
    for (int i = 0; i < answerL.size(); i++) {
      System.out.println(answerL.get(i) + " " + answerR.get(i));
    }
  }
}
