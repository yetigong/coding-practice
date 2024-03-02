package verkd.shortestpath;

import java.util.*;

public class ShortestPath {
    /**
     * 刚来地里，求大米第一题，给一个matrix找到Graph最短的path
     * Matrix = {[false, false, true],
     * [false, false, true]
     * [false, true, true]}
     *
     * node就是graph里面的点，从matrix可以看出有这个graph有3个node，他们的之间的关系存在matrix里面.
     * 比如： matrix 0，0 是false，就是从node 0 to node 0 there’s no path. 下面一个0,1 是 false 意思就是从node 0 to node 1 there’s no path.
     * 再下面 0,2 true 意思是node 0 to node 2 there’s a path. 以此类推，问题是要你通过matrix的信息找出node 0 to node 1’s shortest path.
     *
     * @param matrix
     * @param start
     * @param end
     * @return
     */
    public int bfs(int[][] matrix, int start, int end) {
        // validating matrix is valid
        if (matrix == null || matrix.length < 1 || matrix[0].length < 1) {
            return -1;
        }

        Set<Integer> visited = new HashSet<>();
        Set<Integer> curr = new HashSet<>();
        curr.add(start);
        int result = 0;

        while (!curr.isEmpty()) {
            Set<Integer> next = new HashSet<>();
            for (Integer source: curr) {
                if (source == end) {
                    return result;
                }
                int[] dests = matrix[source];
                for (int i = 0; i < dests.length; i++) {
                    if (dests[i] == 1 && !visited.contains(i)) {
                        next.add(i);
                    }
                }
                visited.add(source);
            }
            curr = next;
            result += 1;
        }

        return -1;
    }

    public int dijikstraVersion(int[][] matrix, int start, int end) {
        // validating matrix is valid
        if (matrix == null || matrix.length < 1 || matrix[0].length < 1) {
            return -1;
        }

        int n = matrix.length;
        Map<Integer, Integer> nodeDist = new HashMap<>();
        PriorityQueue<Edge> edges = new PriorityQueue<>((a, b) -> Integer.compare(a.weight, b.weight));
        for (int i = 0; i < n; i++) {
            nodeDist.put(i, Integer.MAX_VALUE);
        }
        nodeDist.put(start, 0);
        edges.offer(new Edge(start, 0));

        while (!edges.isEmpty()) {
            Edge edge = edges.poll();
            // iterating all neighbors of this edge
            for (int j = 0; j < n; j++) {
                if (matrix[edge.target][j] > 0) {
                    Edge toNeighbor = new Edge(j, edge.weight + matrix[edge.target][j]);
                    if (toNeighbor.target == end) {
                        return toNeighbor.weight;
                    }
                    if (toNeighbor.weight < nodeDist.get(toNeighbor.target)) {
                        // we have found a shorter path
                        nodeDist.put(toNeighbor.target, toNeighbor.weight);
                        edges.offer(toNeighbor);
                    }
                }
            }
        }
        return (nodeDist.get(end) == Integer.MAX_VALUE) ? -1 : nodeDist.get(end);
    }

    /**
     * This is an implemention of the dijkstra's algorithm for finding the shortest path.
     *
     * The main idea is:
     * 1. to start from a node, find all the neighbors. also keep a map of visited nodes and their distance from starting node.
     * 2.   for each neighbor, compute current node min + neighbors distance. Update the map, also insert into the Queue
     * 3.  after visiting all neighbors, mark the curr node as visited.
     * @param matrix
     * @param start
     * @param end
     * @return
     */
    public int dijkstra(int[][] matrix, int start, int end) {
        // keeps track of distance from start, to any node
        Map<Integer, Integer> distanceMap = new HashMap<>();

        for (int i = 0; i < matrix.length; i++) {
            distanceMap.put(i, Integer.MAX_VALUE);
        }
        distanceMap.put(start, 0);

        // neighbors to visit, it is ordered by the distance to source. We always process the closest node from source
        PriorityQueue<Edge> edges = new PriorityQueue<>((a, b) -> Integer.compare(a.weight, b.weight));
        edges.add(new Edge(start, 0));

        while (!edges.isEmpty()) {
            Edge edge = edges.poll();

            for (int i = 0; i < matrix[edge.target].length; i++) {
                if (matrix[edge.target][i] != 0) {
                    int distanceToI = edge.weight + matrix[edge.target][i];
                    if (distanceToI < distanceMap.get(i)) {
                        // find a closer path to i, update map and insert into queue
                        distanceMap.put(i, distanceToI);
                        edges.add(new Edge(i, distanceToI));
                        System.out.printf("A better path is found to %s, distance map is updated to %s, %n", i, distanceMap);
                    }
                }
            }
        }
        return distanceMap.get(end);
    }

    class Edge {
        int target;
        int weight;
        public Edge(int target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }
}
