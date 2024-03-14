package sigma.sheet;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Spreadsheet {

    public static void main(String[] args) {
        Sheet sheet = new Sheet(new String[] {"a", "b", "c"});
        sheet.set(0, "a", 1);
        sheet.set(0, "b", 1);
        sheet.set(0, "c", 1);
        sheet.set(1, "a", 4);
        sheet.set(2, "b", 5);
        sheet.set(3, "c", 6);
        sheet.set(5, "a", 7);

        sheet.set(1, "a", 2);
        Assert.assertEquals(sheet.get(0, "a"), 1);
        Assert.assertEquals(sheet.get(1, "a"), 2);

        List<String> topN = sheet.getTopN(10);
        for (String row : topN) {
            System.out.println(row);
        }

        sheet.add(0, "c", 0, "a", 0, "b");
        Assert.assertEquals(sheet.get(0, "c"), 2);
        sheet.set(0, "a", 3);
        Assert.assertEquals(sheet.get(0, "c"), 4);

        sheet.add(1, "c", 0, "c", 0, "b");
        Assert.assertEquals(sheet.get(1, "c"), 5);

        sheet.set(0, "b", 2);
        Assert.assertEquals(sheet.get(0, "c"), 5);
        Assert.assertEquals(sheet.get(1, "c"), 7);
    }

    @Test
    public void testAddition() {
        Sheet sheet = new Sheet(new String[] {"a", "b", "c"});
        sheet.set(0, "a", 1);
        sheet.set(0, "b", 1);
        sheet.set(0, "c", 1);
        sheet.set(1, "a", 4);
        sheet.set(2, "b", 5);
        sheet.set(3, "c", 6);
        sheet.set(5, "a", 7);

        sheet.add(0, "c", 0, "a", 0, "b");
        sheet.add(1, "c", 1, "a", 1, "b");
        sheet.add(2, "c", 1, "c", 0, "c");

        Assert.assertEquals(sheet.get(2, "c"), 6);
    }

    @Test
    public void testAddition2() {
        Sheet sheet = new Sheet(new String[] {"a", "b", "c"});
        sheet.set(0, "a", 1);
        sheet.set(0, "b", 1);
        sheet.set(0, "c", 1);
        sheet.set(1, "a", 4);
        sheet.set(2, "b", 5);
        sheet.set(3, "c", 6);
        sheet.set(5, "a", 7);

        sheet.add(1, "c", 1, "a", 0, "c");
        sheet.add(0, "c", 0, "a", 0, "b");

        Assert.assertEquals(sheet.get(1, "c"), 6);

        sheet.set(0, "a", 5);

        Assert.assertEquals(sheet.get(1, "c"), 10);
    }

    @Test
    public void testLoop() {
        Sheet sheet = new Sheet(new String[] {"a", "b", "c"});
        sheet.set(0, "a", 1);
        sheet.set(0, "b", 1);
        sheet.set(0, "c", 1);
        sheet.set(1, "a", 4);
        sheet.set(2, "b", 5);
        sheet.set(3, "c", 6);
        sheet.set(5, "a", 7);

        sheet.set(1, "a", 2);
        // test cases for loop
        sheet.set(3, "a", 1);
        sheet.set(3, "b", 2);

        sheet.add(3, "b", 3, "a", 3, "b");
    }

    static class Point {
        int row;
        String col;

        public Point (int row, String col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return String.format("Point[%s, %s] ", this.row, this.col);
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(row) + col.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != Point.class) {
                return false;
            }

            Point that = (Point) obj;
            return this.row == that.row && this.col.equals(that.col);
        }
    }

    // should extend some ops super class
    static class Add {
        Point val1;
        Point val2;
        Point target;

        public Add (Point val1, Point val2, Point target) {
            this.val1 = val1;
            this.val2 = val2;
            this.target = target;
        }

        @Override
        public String toString() {
            return String.format("Add %s, %s to %s", val1, val2, target);
        }
    }
    static class Sheet {
        Map<Integer, int[]> sheet;
        Map<String, Integer> columnIndex;

        Map<Point, List<Add>> opsMap;
        int numCols = 0;
        public Sheet(String[] columns) {
            // validate columns length always > 1
            columnIndex = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                columnIndex.put(columns[i], i);
            }
            numCols = columns.length;
            sheet = new HashMap<>();

            this.opsMap = new HashMap<>();
        }

        public void set(int row, String column, int val) {
            // assuming column is always valid. add validation later
            sheet.computeIfAbsent(row, k -> new int[numCols]);
            sheet.get(row)[this.columnIndex.get(column)] = val;
            eval(new Point(row, column));
        }

        public int get(int row, String column) {
            // assuming column is always valid
            if (sheet.get(row) == null) {
                return 0;
            }
            return sheet.get(row)[columnIndex.get(column)];
        }

        public List<String> getTopN(int n) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (sheet.get(i) == null) {
                    result.add(printRow(new int[numCols]));
                } else {
                    result.add(printRow(Arrays.copyOf(sheet.get(i), numCols)));
                }
            }
            return result;
        }

        public void add(int targetRow, String targetCol, int row1, String col1, int row2, String col2) {
            Point val1 = new Point(row1, col1);
            Point val2 = new Point(row2, col2);
            Point target = new Point(targetRow, targetCol);
            this.opsMap.computeIfAbsent(val1, k -> new ArrayList<>());
            this.opsMap.computeIfAbsent(val2, k -> new ArrayList<>());
            this.opsMap.get(val1).add(new Add(val1, val2, target));
            this.opsMap.get(val2).add(new Add(val1, val2, target));
            if (hasLoop(val1) || hasLoop(val2)) {
                int lastIndex1 = this.opsMap.get(val1).size() - 1;
                this.opsMap.get(val1).remove(lastIndex1);
                int lastIndex2 = this.opsMap.get(val2).size() - 1;
                this.opsMap.get(val2).remove(lastIndex2);
            }
            eval(val1);
        }

        /**
         * need to trigger a chained evaluation of all cells, that has connections to the source cell.
         *
         * This will be used when any cell value changes, or a new operation related to a cell is created.
         * @param source
         */
        public void eval(Point source) {
            System.out.printf("Evaluating %s %n", source);
            List<Point> currSource = new ArrayList<>();
            List<Point> nextSource = new ArrayList<>();
            currSource.add(source);
            while (!currSource.isEmpty()) {
                for (Point point: currSource) {
                    List<Add> ops = opsMap.getOrDefault(point, new ArrayList<>());
                    for (Add addition: ops) {
                        int int1 = this.get( addition.val1.row,  addition.val1.col);
                        int int2 = this.get( addition.val2.row,  addition.val2.col);
                        this.set(addition.target.row, addition.target.col, int1 + int2);
                        System.out.printf("handling addition %s %n", addition);
                        nextSource.add(addition.target);
                    }
                }
                currSource = nextSource;
                nextSource = new ArrayList<>();
            }
        }

        // the following methods detects whether this new addition will result in loops
        public boolean hasLoop(Point source) {
            Set<Point> visited = new HashSet<>();
            Set<Point> recursionSet = new HashSet<>();
            boolean hasLoop = dfs(source, visited, recursionSet);
            if (hasLoop) {
                System.err.println("Loop detected, originating from " + source);
            }
            return hasLoop;
        }

        private boolean dfs(Point start, Set<Point> visited, Set<Point> recursionSet) {

            if (recursionSet.contains(start)) {
                return true;
            }

            if (visited.contains(start)) {
                return false;
            }

            visited.add(start);
            recursionSet.add(start);
            List<Point> neighbors = this.opsMap.getOrDefault(start, new ArrayList<>())
                    .stream().map(operation -> operation.target)
                    .toList();

            for (Point neighbor: neighbors) {
                if (dfs(neighbor, visited, recursionSet)) {
                    return true;
                }
            }
            // all neighbors don't have loops
            recursionSet.remove(start);
            return false;
        }

        private String printRow(int[] row) {
            return Arrays.stream(row).boxed().map(i->i.toString())
                    .collect(Collectors.joining(" "));
        }
    }
}
