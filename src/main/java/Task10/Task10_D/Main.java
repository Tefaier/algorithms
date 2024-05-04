package Task10.Task10_D;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
  private static InputStreamReader in = new InputStreamReader(System.in);

  public static void main(String[] args) throws IOException {
    SuffixAutomate automate = new SuffixAutomate();
    int character;
    while ((character = in.read()) != '\n') {
      automate.addLetter(character);
    }
    automate.calc();
    System.out.println(automate.count());
  }
}

class SuffixAutomate {
  static class AutomateNode {
    public int length;
    public AutomateNode link;
    public short maxToEnd = (short) -20000;
    public short minToEnd = (short) 20000;
    public TreeMap<Integer, AutomateNode> next = new TreeMap<>();

    public AutomateNode(int length, AutomateNode link) {
      this.length = length;
      this.link = link;
    }

    public int diff() {
      return maxToEnd - minToEnd;
    }

    public void tryNewMin(short min) {
      if (min < minToEnd) minToEnd = min;
    }

    public void tryNewMax(short max) {
      if (max > maxToEnd) maxToEnd = max;
    }
  }

  public List<AutomateNode> nodes = new ArrayList<>();
  private int fullNodeIndex = 0;

  public SuffixAutomate() {
    nodes.add(new AutomateNode(0, null));
  }

  public void addString(String str) {
    for (int i = 0; i < str.length(); i++) {
      addLetter(str.charAt(i));
    }
  }

  public void addLetter(int character) {
    int newFullIndex = nodes.size();

    nodes.add(new AutomateNode(nodes.get(fullNodeIndex).length + 1, nodes.get(0)));
    AutomateNode newFullNode = nodes.get(newFullIndex);

    AutomateNode pointer = nodes.get(fullNodeIndex);
    while (pointer != null && !pointer.next.containsKey(character)) {
      pointer.next.put(character, newFullNode);
      pointer = pointer.link;
    }

    if (pointer == null) {
      fullNodeIndex = newFullIndex;
      return;
    }

    AutomateNode toSplit = pointer.next.get(character);
    if (pointer.length + 1 == toSplit.length) {
      newFullNode.link = toSplit;
    } else {
      nodes.add(new AutomateNode(pointer.length + 1, toSplit.link));
      AutomateNode cloneNode = nodes.get(nodes.size() - 1);
      cloneNode.next.putAll(toSplit.next);

      while (pointer != null && pointer.next.getOrDefault(character, null) == toSplit) {
        pointer.next.put(character, cloneNode);
        pointer = pointer.link;
      }

      newFullNode.link = cloneNode;
      toSplit.link = cloneNode;
    }

    fullNodeIndex = newFullIndex;
  }

  public void calc() {
    nodes.get(fullNodeIndex).maxToEnd = 0;
    nodes.get(fullNodeIndex).minToEnd = 0;
    nodes.stream().sorted((node1, node2) -> node2.length - node1.length).forEachOrdered(currentNode -> {
      for (AutomateNode node : currentNode.next.values()) {
        currentNode.tryNewMin((short) (node.minToEnd + 1));
        currentNode.tryNewMax((short) (node.maxToEnd + 1));
      }
      if (currentNode.link != null) {
        currentNode.link.tryNewMin(currentNode.minToEnd);
        currentNode.link.tryNewMax(currentNode.maxToEnd);
      }
    });
    nodes.get(0).maxToEnd = 0;
    nodes.get(0).minToEnd = 0;
  }

  public long count() {
    long counter = 0;
    Queue<Pair> queue = new ArrayDeque<>();
    queue.add(new Pair(nodes.get(0), 0));

    Pair pair = null;
    while ((pair = queue.poll()) != null) {
      if (pair.length() <= pair.node().diff()) ++counter;
      for (AutomateNode node : pair.node().next.values()) {
        queue.add(new Pair(node, pair.length() + 1));
      }
    }
    return --counter;
  }
}

record Pair(SuffixAutomate.AutomateNode node, int length) {
}
