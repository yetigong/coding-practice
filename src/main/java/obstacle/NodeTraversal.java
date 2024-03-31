package obstacle;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class NodeTraversal {

    static class Node {
        String val;
        List<Node> children;

        public Node(String val, List<Node> children) {
            this.val = val;
            this.children = children;
        }

        public Node(String val) {
            this.val = val;
            this.children = new ArrayList<>();
        }

        public Node addChild(Node child) {
            this.children.add(child);
            return this;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val='" + val + '\'' +
                    ", children=" + children +
                    '}';
        }
    }
    static class Traversal {
        public List<Node> getLeaves(Node root) {
            List<Node> leaves = new ArrayList<>();
            getLeavesHelper(root, leaves);
            return leaves;
        }

        private void getLeavesHelper(Node root, List<Node> collector) {
            if (root == null) return;
            if (root.children.isEmpty()) {
                collector.add(root);
                return;
            }

            for (Node child: root.children) {
                getLeavesHelper(child, collector);
            }
        }

        public boolean isSimilar(Node root1, Node root2) {
            List<Node> root1Leaves = getLeaves(root1);
            List<Node> root2Leaves = getLeaves(root2);
            System.out.printf("Root1 leaves are : %s %n Root 2 leaves are: %s %n", root1Leaves, root2Leaves);
            if (root1Leaves.size() != root2Leaves.size()) {
                return false;
            }

            for (int i = 0; i < root1Leaves.size(); i++) {
                Node leave1 = root1Leaves.get(i);
                Node leave2 = root2Leaves.get(i);
                if (!leave1.val.equals(leave2.val)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Test
    public void testSimilar() {
        Node root1 = new Node(null);

        Node b = new Node("B");
        Node c = new Node(null);
        Node d = new Node("D");
        Node e = new Node("E");

        Node root2 = new Node(null);
        Node b2 = new Node("B");
        Node c2 = new Node(null);
        Node d2 = new Node("D");
        Node e2 = new Node("E");
        // A -> B and C. C -> D and E
        root1.addChild(b).addChild(c.addChild(d).addChild(e));

        // A -> C and E. C has B and D,
        root2.addChild(c2.addChild(b2).addChild(d2)).addChild(e2);

        Assert.assertEquals(new Traversal().isSimilar(root1, root2), true);
    }
    @Test
    public void testSimilar1() {
        Node root1 = new Node(null);
        Node b = new Node("B");
        Node c = new Node(null);
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");

        Node root2 = new Node(null);
        Node b2 = new Node("B");
        Node c2 = new Node(null);
        Node d2 = new Node("D");
        Node e2 = new Node("E");
        Node f2 = new Node("F");
        // A -> B and C. C -> D and E and F
        root1.addChild(b).addChild(c.addChild(d).addChild(e).addChild(f));

        // A -> C and E and F. C has B and D.
        root2.addChild(c2.addChild(b2).addChild(d2)).addChild(e2).addChild(f2);

        Assert.assertEquals(new Traversal().isSimilar(root1, root2), true);
    }

    @Test
    public void testSimilar2() {
        Node root1 = new Node(null);

        Node b = new Node("B");
        Node c = new Node(null);
        Node d = new Node("D");
        Node e = new Node("E");


        Node root2 = new Node(null);
        Node b2 = new Node("B");
        Node c2 = new Node(null);
        Node d2 = new Node("D");
        Node e2 = new Node("E2");
        // A -> B and C. C -> D and E
        root1.addChild(b).addChild(c.addChild(d).addChild(e));

        // A -> C and E1. C has B and D. This is not true
        root2.addChild(c2.addChild(b2).addChild(d2)).addChild(e2);

        Assert.assertEquals(new Traversal().isSimilar(root1, root2), false);
    }

    @Test
    public void testSimilar3() {
        Node root1 = new Node("root");
        Node root2 = new Node("root");

        Assert.assertEquals(new Traversal().isSimilar(root1, root2), true);
    }
}
