package sigma.permission;

import java.util.*;

public class Permissions {

    public static void main(String[] args) {
        Node root = new Node("root");
        Node level1A = new Node("level1_A");
        Node level1B = new Node("level1_B");
        root.addChild(level1A);
        root.addChild(level1B);

        Node level2A = new Node("level2_A");
        Node level2B = new Node("level2_B");
        Node level2C = new Node("level2_C");
        Node level2D = new Node("level2_D");

        level1A.addChild(level2A);
        level1A.addChild(level2B);
        level1B.addChild(level2C);
        level1B.addChild(level2D);

        User alice = new User(1, "Alice");
        User bob = new User(2, "Bob");

        PermissionSystem system = new PermissionSystem();
        system.grantPermission(root, alice);
        system.grantPermission(level2C, bob);
        system.grantPermission(root, alice);
        System.out.println("The current permission structure is : " + system.permissionMap);
    }

    static class Node {
        String val;
        List<Node> children;

        public Node (String val, List<Node> children) {
            this.val = val;
            this.children = children;
        }

        public Node(String val) {
            this.val = val;
            this.children = new ArrayList<>();
        }

        public void addChild(Node child) {
            this.children.add(child);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val='" + val + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(val, node.val) && Objects.equals(children, node.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, children);
        }
    }

    static class User {
        int id;
        String name;
        public User (int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return id == user.id && Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    static class PermissionSystem {
        Map<Node, Map<User, Boolean>> permissionMap;

        public PermissionSystem() {
            this.permissionMap = new HashMap<>();
        }
        void grantPermission(Node node, User user) {
            permissionMap.computeIfAbsent(node, key -> new HashMap<>());
            permissionMap.get(node).computeIfAbsent(user, key -> Boolean.TRUE);

            if (node.children != null && !node.children.isEmpty()) {
                for (Node child: node.children) {
                    grantPermission(child, user);
                }
            }
        }
        boolean hasPermission(Node node, User user) {
            return permissionMap.get(node).getOrDefault(user, false);
        }
    }

}
