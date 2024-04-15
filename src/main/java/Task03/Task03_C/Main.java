package Task03.Task03_C;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class AVLTree {
  private Node root = null;
  private int size = 0;

  public static class Node {
    int value = 0;
    int height = 0;
    Node left = null;
    Node right = null;
    String content = "";

    public Node(int value, String content) {
      this.value = value;
      this.content = content;
    }
  }

  private void updateHeight(Node node) {
    node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
  }

  private int getHeight(Node node) {
    return node == null ? 0 : node.height;
  }

  // can be negative
  private int getDisbalance(Node node) {
    return (node == null) ? 0 : getHeight(node.right) - getHeight(node.left);
  }

  private Node rightRotate(Node top) {
    Node newTop = top.left;
    Node bottom = newTop.right;
    newTop.right = top;
    top.left = bottom;
    updateHeight(top);
    updateHeight(newTop);
    return newTop;
  }

  private Node leftRotate(Node top) {
    Node newTop = top.right;
    Node bottom = newTop.left;
    newTop.left = top;
    top.right = bottom;
    updateHeight(top);
    updateHeight(newTop);
    return newTop;
  }

  private Node rebalance(Node target) {
    updateHeight(target);
    int balance = getDisbalance(target);
    if (balance > 1) {
      if (getHeight(target.right.right) > getHeight(target.right.left)) {
        // much more at right-right
        target = leftRotate(target);
      } else {
        // much more at right-left
        target.right = rightRotate(target.right);
        target = leftRotate(target);
      }
    } else if (balance < -1) {
      if (getHeight(target.left.left) > getHeight(target.left.right))
        // much more at left-left
        target = rightRotate(target);
      else {
        // much more at left-right
        target.left = leftRotate(target.left);
        target = rightRotate(target);
      }
    }
    return target;
  }

  // rebalances from right to left
  private void rebalanceChain(List<Node> chain) {
    for (int i = chain.size() - 1; i >= 0; i--) {
      rebalance(chain.get(i));
    }
  }

  // returns if overlap was located
  public boolean add(int value, String content) {
    if (root == null) {
      root = new Node(value, content);
    }
    Node tmp = root;
    List<Node> chain = new ArrayList<>();
    chain.add(tmp);
    while (tmp != null) {
      if (tmp.value == value) {
        return false;
      }
      if (tmp.value > value) {
        if (tmp.left == null) {
          tmp.left = new Node(value, content);
          size++;
        }
        tmp = tmp.left;
        chain.add(tmp);
      } else {
        if (tmp.right == null) {
          tmp.right = new Node(value, content);
          size++;
        }
        tmp = tmp.right;
        chain.add(tmp);
      }
    }
    rebalanceChain(chain);
    return true;
  }

  public Node find(int value) {
    Node tmp = root;
    while (tmp != null) {
      if (tmp.value == value) {
        return tmp;
      }
      if (tmp.value > value) {
        tmp = tmp.left;
      } else {
        tmp = tmp.right;
      }
    }
    return null;
  }
}

public class Main {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    AVLTree treeOnNicknames = new AVLTree();
    AVLTree treeOnCarNames = new AVLTree();
    int participants = Integer.parseInt(in.nextLine());
    for (int i = 0; i < participants; i++) {
      String[] line = in.nextLine().split(" ");
      String nickname = line[0];
      String carName = line[1];
      treeOnNicknames.add(nickname.hashCode(), carName);
      treeOnCarNames.add(carName.hashCode(), nickname);
    }

    int requests = Integer.parseInt(in.nextLine());
    for (int i = 0; i < requests; i++) {
      int request = in.nextLine().hashCode();
      AVLTree.Node node = treeOnNicknames.find(request);
      if (node != null) {
        System.out.println(node.content);
      } else {
        System.out.println(treeOnCarNames.find(request).content);
      }
    }
  }
}
