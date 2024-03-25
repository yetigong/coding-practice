package oai;

import java.util.*;

/***
 *
 */
public class SQL {
    class Node {
        Node prev;
        Node next;
        int value;
        List<String> ids;
        public Node (int value) {
            this.value = value;
            this.ids = new ArrayList<>();
            this.prev = null;
            this.next = null;
        }
    }

    Map<String, Integer> table;
    TreeMap<Integer, Node> index;
    Node head;
    Node tail;
    public SQL() {
        this.table = new HashMap<>();
        this.index = new TreeMap<>();
        this.head = new Node(Integer.MIN_VALUE);
        this.tail = new Node(Integer.MAX_VALUE);
        this.head.next = this.tail;
        this.tail.prev = this.head;
        this.index.put(Integer.MIN_VALUE, this.head);
        this.index.put(Integer.MAX_VALUE, this.tail);
    }
    public void insert(String id, int value) {
        // Implementation
        if (this.table.containsKey(id)) {
            return;
        }
        this.table.put(id, value);
        if (this.index.containsKey(value)) {
            this.index.get(value).ids.add(id);
        } else {
            Node node = new Node(value);
            node.ids.add(id);
            Node prev = this.index.floorEntry(value).getValue();
            Node next = this.index.ceilingEntry(value).getValue();
            prev.next = node;
            node.prev = prev;
            next.prev = node;
            node.next = next;
            this.index.put(value, node);
        }
    }

    public List<String> selectEquals(int value) {
        // Implementation
        return this.index.get(value).ids;
    }

    public List<String> selectRange(int lowerBound, int upperBound) {
        // Implementation
        Node lb = this.index.ceilingEntry(lowerBound).getValue();
        Node ub = this.index.floorEntry(upperBound).getValue();
        // System.out.printf("Current index structure is %s %n, lb is %s, ub is %s %n", this.index, lb.next.value, ub.prev.value);
        List<String> result = new ArrayList<>();
        if (lb == this.tail || ub == this.head)
            return List.of();
        while (lb.value != ub.value) {
            result.addAll(lb.ids);
            lb = lb.next;
        }
        result.addAll(ub.ids);
        return result;
    }

    public static void main(String[] args) {
        // Demonstration of functionality
        SQL sql = new SQL();
        sql.insert("1", 5);
        sql.insert("2", 5);
        sql.insert("3", 4);

        sql.insert("4", 1);
        sql.insert("5", 10);
        sql.insert("6", 2);
        sql.insert("7", 2);
        // expect 1
        System.out.printf("result for selecting [5] is %s %n", sql.selectEquals(5));

        // expect 3
        System.out.printf("result for selecting [4] is %s %n", sql.selectEquals(4));

        System.out.printf("result for selecting [4-5] is %s %n", sql.selectRange(4, 5));
        System.out.printf("result for selecting [11-12] is %s %n", sql.selectRange(11, 12));
        System.out.printf("result for selecting [0, 1] is %s %n", sql.selectRange(0, 1));
        System.out.printf("result for selecting [1, 4] is %s %n", sql.selectRange(1, 4));
    }
}
