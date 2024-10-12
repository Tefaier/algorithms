package Task11.Task11_G;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int heapsNum = in.nextInt();
    int commandsNum = in.nextInt();
    var cluster = new BinomialHeapCluster<>(heapsNum, Integer.MIN_VALUE);
    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < commandsNum; i++) {
      int commandType = in.nextInt();
      switch (commandType) {
        case (0):
          cluster.addValue(in.nextInt() - 1, in.nextInt());
          break;
        case 1:
          cluster.moveElements(in.nextInt() - 1, in.nextInt() - 1);
          break;
        case 2:
          cluster.deleteElement(in.nextInt() - 1);
          break;
        case 3:
          cluster.changeValue(in.nextInt() - 1, in.nextInt());
          break;
        case 4:
          answer.append(cluster.getMinimumValue(in.nextInt() - 1)).append('\n');
          break;
        case 5:
          cluster.deleteMinimum(in.nextInt() - 1);
          break;
      }
    }
    System.out.print(answer);
  }
}

class BinomialHeapCluster<T extends Comparable<T>> {
  private BinomialHeap<T>[] heaps;
  private List<BinomialHeap<T>.Node> references = new ArrayList<>();
  private final T minimumValue;

  public BinomialHeapCluster(int heapsNumber, T minimumValue) {
    this.minimumValue = minimumValue;
    this.heaps = new BinomialHeap[heapsNumber];
    for (int i = 0; i < heapsNumber; i++) {
      heaps[i] = new BinomialHeap<>();
    }
  }

  public void addValue(int heapIndex, T value) {
    BinomialHeap<T>.Node newNode = heaps[heapIndex].createNode(value);
    newNode.id = references.size();
    references.add(newNode);
    heaps[heapIndex].insert(newNode);
  }

  public void moveElements(int fromHeapIndex, int toHeapIndex) {
    heaps[toHeapIndex].merge(heaps[fromHeapIndex]);
  }

  public void deleteElement(int index) {
    references.get(index).deleteSelf(minimumValue, references);
  }

  public void changeValue(int elementIndex, T newValue) {
    references.get(elementIndex).setNewValue(newValue, references);
  }

  public T getMinimumValue(int heapIndex) {
    return heaps[heapIndex].getMin();
  }

  public void deleteMinimum(int heapIndex) {
    heaps[heapIndex].extractMin();
  }
}

class BinomialHeap<T extends Comparable<T>> {
  class Node implements Comparable<Node> {
    T value;
    List<Node> children;
    Node parent;
    // ownerHeap is set for root only
    BinomialHeap<T> ownerHeap;
    int id;

    public Node(T value, int id) {
      this.value = value;
      this.id = id;
      children = new ArrayList<>();
    }

    public static <T extends Comparable<T>> void swapIdentity(
        BinomialHeap<T>.Node node1,
        BinomialHeap<T>.Node node2,
        List<BinomialHeap<T>.Node> references) {
      T temp = node1.value;
      node1.value = node2.value;
      node2.value = temp;

      int tempId = node1.id;
      node1.id = node2.id;
      node2.id = tempId;

      if (references != null) {
        references.set(node1.id, node1);
        references.set(node2.id, node2);
      }
    }

    public void deleteSelf(T smallestValue, List<Node> references) {
      alterValue(smallestValue, references).ownerHeap.extractMin();
    }

    public void setNewValue(T newValue, List<Node> references) {
      alterValue(newValue, references);
    }

    private Node alterValue(T newValue, List<Node> references) {
      if (value.compareTo(newValue) < 0) {
        value = newValue;
        return siftDown(references);
      } else {
        value = newValue;
        return siftUp(references);
      }
    }

    private Node siftUp(List<Node> references) {
      Node activeNode = this;
      while (activeNode.parent != null) {
        if (activeNode.parent.compareTo(activeNode) > 0) {
          swapIdentity(activeNode, activeNode.parent, references);
          activeNode = activeNode.parent;
        } else {
          break;
        }
      }
      return activeNode;
    }

    private Node siftDown(List<Node> references) {
      Node activeNode = this;
      while (!activeNode.children.isEmpty()) {
        Node minChild = activeNode.children.stream().min(Node::compareTo).get();
        ;
        if (minChild.compareTo(activeNode) < 0) {
          swapIdentity(activeNode, minChild, references);
          activeNode = minChild;
        } else {
          break;
        }
      }
      return activeNode;
    }

    @Override
    public int compareTo(Node o) {
      return !o.value.equals(value) ? value.compareTo(o.value) : id - o.id;
    }
  }

  class BinomialTree implements Comparable<BinomialTree> {

    private final Node root;

    private BinomialTree(T value, int nodeId) {
      root = new Node(value, nodeId);
    }

    private BinomialTree(Node root) {
      this.root = root;
    }

    @Override
    public int compareTo(BinomialTree o) {
      return root.compareTo(o.root);
    }

    public BinomialTree merge(BinomialTree otherTree) {
      if (otherTree == null) {
        return this;
      }

      if (root.compareTo(otherTree.root) < 0) {
        root.children.add(otherTree.root);
        otherTree.root.parent = root;
        return this;
      } else {
        otherTree.root.children.add(root);
        root.parent = otherTree.root;
        return otherTree;
      }
    }
  }

  private List<BinomialTree> trees;

  public BinomialHeap() {
    trees = new ArrayList<>();
  }

  private BinomialHeap(Node node) {
    trees = new ArrayList<>();
    trees.add(new BinomialTree(node));
    trees.get(0).root.ownerHeap = this;
  }

  private BinomialHeap(List<Node> nodes) {
    trees = new ArrayList<>();
    for (Node node : nodes) {
      // node.ownerHeap = this;
      trees.add(new BinomialTree(node));
    }
  }

  public T extractMin() {
    var minInfo = locateMinTree();
    T returnValue = minInfo.minTree.root.value;
    BinomialHeap<T> childrenHeap = new BinomialHeap<T>(minInfo.minTree.root.children);

    trees.set(minInfo.minTreeIndex, null);
    merge(childrenHeap);

    return returnValue;
  }

  class minInfo {
    public int minTreeIndex;
    public BinomialTree minTree;

    public minInfo(int minTreeIndex, BinomialTree minTree) {
      this.minTreeIndex = minTreeIndex;
      this.minTree = minTree;
    }
  }

  private minInfo locateMinTree() {
    int minIndex = -1;
    BinomialTree minTree = null;

    for (int i = 0; i < trees.size(); i++) {
      if (trees.get(i) == null) continue;
      if (minIndex == -1 || trees.get(i).compareTo(minTree) < 0) {
        minIndex = i;
        minTree = trees.get(i);
      }
    }

    return new minInfo(minIndex, minTree);
  }

  // heap that calls this method empties heap in the argument
  public void merge(BinomialHeap<T> otherHeap) {
    int selfSize = trees.size();
    int otherSize = otherHeap.trees.size();
    int maxSize = Math.max(otherSize, selfSize);
    BinomialHeap<T>.BinomialTree transferTree = null;
    var resultTrees = new ArrayList<BinomialHeap<T>.BinomialTree>();

    for (int i = 0; i < maxSize; i++) {
      var otherTree = i >= otherSize ? null : otherHeap.trees.get(i);
      var selfTree = i >= selfSize ? null : trees.get(i);

      if (transferTree == null) {
        if (selfTree != null && otherTree != null) {
          transferTree = selfTree.merge(otherTree);
          resultTrees.add(null);
        } else if (selfTree == null && otherTree != null) {
          resultTrees.add(otherTree);
        } else {
          resultTrees.add(selfTree);
        }
      } else {
        if (selfTree != null && otherTree != null) {
          resultTrees.add(transferTree);
          transferTree = selfTree.merge(otherTree);
        } else if (selfTree == null && otherTree != null) {
          transferTree = otherTree.merge(transferTree);
          resultTrees.add(null);
        } else if (selfTree != null) {
          transferTree = selfTree.merge(transferTree);
          resultTrees.add(null);
        } else {
          resultTrees.add(transferTree);
          transferTree = null;
        }
      }
    }

    if (transferTree != null) {
      resultTrees.add(transferTree);
    }

    for (var resultTree : resultTrees) {
      if (resultTree != null) {
        resultTree.root.parent = null;
        resultTree.root.ownerHeap = this;
      }
    }

    trees = resultTrees;
    otherHeap.trees = new ArrayList<>();
  }

  public T getMin() {
    return locateMinTree().minTree.root.value;
  }

  public void insert(Node node) {
    BinomialHeap<T> newHeap = new BinomialHeap<>(node);
    merge(newHeap);
  }

  public Node createNode(T value) {
    return new Node(value, 0);
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
