package Task03.Task03_D;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
  static class DecTree {
    private static Random random = new Random();

    private Node root = null;
    private int size = 0;

    private static class Node {
      int value = 0;
      public Node left = null;
      public Node right = null;
      public int size = 0;
      private long priority;

      public Node(int value) {
        this.value = value;
        this.priority = random.nextLong();
      }
    }

    private static class Pair {
      Node first = null;
      Node second = null;

      public Pair(Node first, Node second) {
        this.first = first;
        this.second = second;
      }
    }

    private Pair split(Node node, int key) {
      if (node == null) {
        return new Pair(null, null);
      }
      // replace Node children so that all are in one side to the key
      // and another element in Pair is on the other side ALL
      // works by value (as a search tree)
      // left - <=
      // right - >
      if (node.value > key) {
        Pair pair = split(node.left, key);
        node.left = pair.second;
        updateSize(pair.first);
        updateSize(node);
        return new Pair(pair.first, node);
      } else {
        Pair pair = split(node.right, key);
        node.right = pair.first;
        updateSize(node);
        updateSize(pair.second);
        return new Pair(node, pair.second);
      }
    }

    // less and bigger in terms of value
    private Node merge(Node less, Node bigger) {
      if (less == null) {
        updateSize(bigger);
        return bigger;
      }
      if (bigger == null) {
        updateSize(less);
        return less;
      }
      // works by priority (as a binary heap)
      if (less.priority > bigger.priority) {
        less.right = merge(less.right, bigger);
        updateSize(less);
        return less;
      } else {
        bigger.left = merge(less, bigger.left);
        updateSize(bigger);
        return bigger;
      }
    }

    private void updateSize(Node node) {
      if (node != null) {
        node.size = 1 + getSize(node.left) + getSize(node.right);
      }
    }

    private int getSize(Node node) {
      return node == null ? 0 : node.size;
    }

    public void insert(int value) {
      if (find(value)) {
        return;
      }
      size++;
      Node node = new Node(value);
      Pair pair = split(root, value);
      root = merge(merge(pair.first, node), pair.second);
    }

    public boolean find(int value) {
      Node tmp = root;
      while (tmp != null) {
        if (tmp.value == value) {
          return true;
        }
        if (tmp.value > value) {
          tmp = tmp.left;
        } else {
          tmp = tmp.right;
        }
      }
      return false;
    }

    public void delete(int value) {
      if (!find(value)) {
        return;
      }
      size--;
      Pair pair = split(root, value);
      Pair leftPair = split(pair.first, value - 1);
      root = merge(leftPair.first, pair.second);
    }

    // excludes the value itself
    // depending on bigger will search next bigger or next smaller
    public int getNext(int value, boolean bigger) {
      return getNext(root, value, bigger);
    }

    private int getNext(Node node, int value, boolean bigger) {
      if (node == null) {
        return bigger ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      }
      if (bigger && node.value > value || !bigger && node.value < value) {
        return bigger
            ? Math.min(node.value, getNext(node.left, value, bigger))
            : Math.max(node.value, getNext(node.right, value, bigger));
      } else {
        return bigger ? getNext(node.right, value, bigger) : getNext(node.left, value, bigger);
      }
    }

    public int getKth(int k) {
      if (size <= k || k < 0) {
        return Integer.MIN_VALUE;
      }
      Node tmp = root;
      int toAdd = 0;
      while (tmp != null) {
        int index = toAdd + getSize(tmp.left);
        if (index == k) {
          return tmp.value;
        } else if (index < k) {
          toAdd = index + 1;
          tmp = tmp.right;
        } else {
          tmp = tmp.left;
        }
      }
      throw new RuntimeException("Error with sizes log");
    }
  }

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);
    DecTree tree = new DecTree();
    String command = scanner.next();
    int readVal = scanner.nextInt();
    while (true) {
      switch (command) {
        case "insert" -> {
          tree.insert(readVal);
        }
        case "delete" -> {
          tree.delete(readVal);
        }
        case "exists" -> {
          System.out.println(tree.find(readVal));
        }
        case "next" -> {
          int val = tree.getNext(readVal, true);
          System.out.println(val == Integer.MAX_VALUE ? "none" : val);
        }
        case "prev" -> {
          int val = tree.getNext(readVal, false);
          System.out.println(val == Integer.MIN_VALUE ? "none" : val);
        }
        case "kth" -> {
          int val = tree.getKth(readVal);
          System.out.println(val == Integer.MIN_VALUE ? "none" : val);
        }
      }
      if (scanner.hasNext()) {
        command = scanner.next();
        readVal = scanner.nextInt();
      } else {
        break;
      }
    }
  }
}
