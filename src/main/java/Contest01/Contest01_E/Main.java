package Contest01.Contest01_E;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
  private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) {
    var lines = in.lines().toList();
    int wordsNum = Integer.parseInt(Arrays.stream(lines.get(0).split(" ")).toList().get(0));
    int requestNum = Integer.parseInt(Arrays.stream(lines.get(0).split(" ")).toList().get(1));

    Tree tree = new Tree();
    for (int i = 1; i < 1 + wordsNum; i++) {
      var values = Arrays.stream(lines.get(i).split(" ")).toList();
      tree.addString(values.get(0), Integer.parseInt(values.get(1)));
    }

    StringBuilder answer = new StringBuilder();
    for (int i = 0; i < requestNum; i++) {
      String request = lines.get(i + wordsNum + 1);

      if (request.charAt(0) == '+') {
        tree.goBySymbol(request.charAt(2));
      } else {
        tree.deleteSymbol();
      }
      answer.append(tree.getCurrentMostPopularIndex()).append('\n');
    }

    System.out.println(answer);
  }
}

class Tree {
  private static int alphabetStart = 'a';
  private static int alphabetSize = 'z' - 'a' + 1;

  public class Node {
    public Node source;
    public Node[] next = new Node[alphabetSize];
    public Integer mostPopular;
    public Integer mostPopularity;

    public Node(Node source, Integer mostPopular, Integer mostPopularity) {
      this.source = source;
      this.mostPopular = mostPopular;
      this.mostPopularity = mostPopularity;
    }
  }

  private Node root = new Node(null, 0, 0);
  private Node currentNode = root;
  private int counter = 0;
  private int bufferedCounter = 0;

  public void addString(String string, int popularity) {
    ++counter;
    var traverseNode = root;
    for (int i = 0; i < string.length(); i++) {
      if (traverseNode.mostPopularity < popularity) {
        traverseNode.mostPopular = counter;
        traverseNode.mostPopularity = popularity;
      }
      if (traverseNode.next[string.charAt(i) - alphabetStart] == null) {
        traverseNode.next[string.charAt(i) - alphabetStart] = new Node(traverseNode, counter, popularity);
      }
      traverseNode = traverseNode.next[string.charAt(i) - alphabetStart];
    }

    if (traverseNode.mostPopularity < popularity) {
      traverseNode.mostPopular = counter;
      traverseNode.mostPopularity = popularity;
    }
  }

  public void goBySymbol(int ch) {
    if (bufferedCounter > 0 || currentNode.next[ch - alphabetStart] == null) {
      ++bufferedCounter;
    } else {
      currentNode = currentNode.next[ch - alphabetStart];
    }
  }

  public void deleteSymbol() {
    if (bufferedCounter > 0) {
      --bufferedCounter;
      return;
    }
    currentNode = currentNode.source;
  }

  public int getCurrentMostPopularIndex() {
    return bufferedCounter > 0 ? -1 : currentNode.mostPopular;
  }
}
