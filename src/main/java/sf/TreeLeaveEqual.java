package sf;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TreeLeaveEqual {
    static class TreeNode {
        TreeNode left;
        TreeNode right;
        String val;

        public TreeNode(String val) {
            this.val = val;
        }
    }

    /**
     * The method should return true if the leaves forms the same string
     * @param node1
     * @param node2
     * @return
     */
    static boolean treeLeavesEqual(TreeNode node1, TreeNode node2) {
        return printLeaves(node1).equals(printLeaves(node2));
    }

    private static String printLeaves(TreeNode node) {
        if (node == null) return "";

        if (node.left == null && node.right == null) {
            return node.val;
        }

        String left = printLeaves(node.left);
        String right = printLeaves(node.right);

        return left + right;
    }

    @Test
    public void testHappyPath() {
        TreeNode a = new TreeNode("a");
        TreeNode b = new TreeNode("b");
        TreeNode c = new TreeNode("c");
        TreeNode d = new TreeNode("d");
        TreeNode e = new TreeNode ("e");
        a.left = b;
        a.right = c;
        c.left = d;
        c.right = e; // expect bde to be printed

        TreeNode a1 = new TreeNode("a");
        TreeNode b1 = new TreeNode("b");
        TreeNode b2 = new TreeNode("b");
        TreeNode c1 = new TreeNode("c");
        TreeNode d1 = new TreeNode("d");
        TreeNode e1 = new TreeNode ("e");
        a1.left = b1;
        a1.right = c1;
        b1.left = b2;
        b1.right = d1;
        c1.right = e1; // expect bde to be printed

        Assert.assertEquals(printLeaves(a), "bde");
        Assert.assertEquals(treeLeavesEqual(a, a1), true);
    }

    @Test
    public void testSadPath() {
        TreeNode a = new TreeNode("a");
        TreeNode b = new TreeNode("b");
        TreeNode c = new TreeNode("c");
        TreeNode d = new TreeNode("d");
        TreeNode e = new TreeNode ("e");
        a.left = b;
        a.right = c;
        c.left = d;
        c.right = e; // expect bde to be printed

        TreeNode a1 = new TreeNode("a");
        TreeNode b1 = new TreeNode("b");
        TreeNode b2 = new TreeNode("b");
        TreeNode c1 = new TreeNode("c");
        TreeNode d1 = new TreeNode("d");
        TreeNode e1 = new TreeNode ("e");
        a1.left = b1;
        b1.left = d1;
        a1.right = c1;
        c1.left = b2;
        c1.right = e1; // expect dbe to be printed

        Assert.assertEquals(printLeaves(a1), "dbe");
        Assert.assertEquals(treeLeavesEqual(a, a1), false);
    }
}
