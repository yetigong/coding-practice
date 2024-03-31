package sigma.pivottable;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sigma.pivottable.PivotTableSolution.PivotTable.COL_SUM;
import static sigma.pivottable.PivotTableSolution.PivotTable.ROW_SUM;

public class PivotTableSolution {
    public static void main(String[] args) {
        List<Map<String, String>> table = List.of(
                Map.of("storeid", "1", "color", "red", "date", "1-2-2022", "sold-count", "20"),
                Map.of("storeid", "2", "color", "blue", "date", "2-2-2022", "sold-count", "100"),
                Map.of("storeid", "3", "color", "black", "date", "3-2-2022", "sold-count", "30"),
                Map.of("storeid", "1", "color", "blue", "date", "1-2-2022", "sold-count", "40")
        );

        PivotTable pvt = new PivotTable("color", "date", table);

        System.out.print(pvt);
    }

    @Test
    public void testPivotTable() {
        List<Map<String, String>> table = List.of(
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "red", "size", "XS", "sold-count", "1"),
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "blue", "size", "L", "sold-count", "20"),
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "green", "size", "M", "sold-count", "30"),
                Map.of("storeid", "abcd1234", "date", "2021-05-26", "color", "red", "size", "XS", "sold-count", "40"),
                Map.of("storeid", "abcd1234", "date", "2021-05-26", "color", "black", "size", "S", "sold-count", "50"),

                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "blue", "size", "XL", "sold-count", "100"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "green", "size", "M", "sold-count", "110"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "black", "size", "L", "sold-count", "120"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-26", "color", "blue", "size", "S", "sold-count", "130"),

                Map.of("storeid", "e1f9g2h8", "date", "2021-05-26", "color", "red", "size", "M", "sold-count", "200"),
                Map.of("storeid", "e1f9g2h8", "date", "2021-05-27", "color", "black", "size", "XS", "sold-count", "210")
        );

        PivotTable pivot1 = new PivotTable("color", "date", table);

        Map<String, Map<String, Integer>> expected = Map.of(
                "red", Map.of("2021-05-25", 1, "2021-05-26", 240, ROW_SUM, 241),
                "blue", Map.of("2021-05-25", 120, "2021-05-26", 130, ROW_SUM, 250),
                "green", Map.of("2021-05-25", 140, ROW_SUM, 140),
                "black", Map.of("2021-05-25", 120, "2021-05-26", 50, "2021-05-27", 210, ROW_SUM, 380),
                COL_SUM, Map.of("2021-05-25", 381, "2021-05-26", 420, "2021-05-27", 210, ROW_SUM, 1011)
        );

        Assert.assertEquals(pivot1.pivotTable, expected);
    }

    @Test
    public void testPivotTable1() {
        List<Map<String, String>> table = List.of(
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "red", "size", "XS", "sold-count", "1"),
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "blue", "size", "L", "sold-count", "20"),
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "green", "size", "M", "sold-count", "30"),
                Map.of("storeid", "abcd1234", "date", "2021-05-26", "color", "red", "size", "XS", "sold-count", "40"),
                Map.of("storeid", "abcd1234", "date", "2021-05-26", "color", "black", "size", "S", "sold-count", "50"),

                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "blue", "size", "XL", "sold-count", "100"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "green", "size", "M", "sold-count", "110"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "black", "size", "L", "sold-count", "120"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-26", "color", "blue", "size", "S", "sold-count", "130"),

                Map.of("storeid", "e1f9g2h8", "date", "2021-05-26", "color", "red", "size", "M", "sold-count", "200"),
                Map.of("storeid", "e1f9g2h8", "date", "2021-05-27", "color", "black", "size", "XS", "sold-count", "210")
        );

        PivotTable pivot1 = new PivotTable("color", "size", table);

        Map<String, Map<String, Integer>> expected = Map.of(
                "red", Map.of("XS", 41, "M", 200, ROW_SUM, 241),
                "blue", Map.of("S", 130, "L", 20, "XL", 100, ROW_SUM, 250),
                "green", Map.of("M", 140, ROW_SUM, 140),
                "black", Map.of("XS", 210, "S", 50, "L", 120, ROW_SUM, 380),
                COL_SUM, Map.of("XS", 251, "S", 180, "M", 340, "L", 140, "XL", 100, ROW_SUM, 1011)
        );

        Assert.assertEquals(pivot1.pivotTable, expected);
    }

    @Test
    public void testSubDim() {
        List<Map<String, String>> table = List.of(
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "red", "size", "XS", "sold-count", "1"),
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "blue", "size", "L", "sold-count", "20"),
                Map.of("storeid", "abcd1234", "date", "2021-05-25", "color", "green", "size", "M", "sold-count", "30"),
                Map.of("storeid", "abcd1234", "date", "2021-05-26", "color", "red", "size", "XS", "sold-count", "40"),
                Map.of("storeid", "abcd1234", "date", "2021-05-26", "color", "black", "size", "S", "sold-count", "50"),

                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "blue", "size", "XL", "sold-count", "100"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "green", "size", "M", "sold-count", "110"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-25", "color", "black", "size", "L", "sold-count", "120"),
                Map.of("storeid", "5678wxyz", "date", "2021-05-26", "color", "blue", "size", "S", "sold-count", "130"),

                Map.of("storeid", "e1f9g2h8", "date", "2021-05-26", "color", "red", "size", "M", "sold-count", "200"),
                Map.of("storeid", "e1f9g2h8", "date", "2021-05-27", "color", "black", "size", "XS", "sold-count", "210")
        );

        PivotTable pivot1 = new PivotTable("color", "size", "date", table);

        Map<String, Map<String, Integer>> expected = Map.of(
                "red/XS", Map.of("2021-05-25", 1, "2021-05-26", 40, ROW_SUM, 41),
                "red/M", Map.of("2021-05-26", 200, ROW_SUM, 200),
                "blue/S", Map.of("2021-05-26", 130, ROW_SUM, 130),
                "blue/L", Map.of("2021-05-25", 20, ROW_SUM, 20),
                "blue/XL", Map.of("2021-05-25", 100, ROW_SUM, 100),
                "green/M", Map.of("2021-05-25", 140, ROW_SUM, 140),
                "black/S", Map.of("2021-05-26", 50, ROW_SUM, 50),
                "black/XS", Map.of("2021-05-27", 210, ROW_SUM, 210),
                "black/L", Map.of("2021-05-25", 120, ROW_SUM, 120),
                COL_SUM, Map.of("2021-05-25", 381, "2021-05-26", 420, "2021-05-27", 210, ROW_SUM, 1011)
        );


        Assert.assertEquals(pivot1.pivotTable, expected);
    }

    static class PivotTable {
        Map<String, Map<String, Integer>> pivotTable;
        String dim1;

        String subDim1;
        String dim2;

        static final String SOLD_COL = "sold-count";
        static final String ROW_SUM = "row-sum";

        static final String COL_SUM = "col-sum";
        List<Map<String, String>> table;

        public PivotTable(String dim1, String dim2, List<Map<String, String>> table) {
            this.table = table;
            this.dim1 = dim1;
            this.dim2 = dim2;
            this.pivotTable = new HashMap<>();
            // assuming dims provided are always valid
            populate();
        }

        public PivotTable(String dim1, String subDim1, String dim2, List<Map<String, String>> table) {
            this.table = table;
            this.dim1 = dim1;
            this.subDim1 = subDim1;
            this.dim2 = dim2;
            this.pivotTable = new HashMap<>();
            // assuming dims provided are always valid
            populate();
        }

        public void populate() {
            for (Map<String, String> row: table) {
                // assuming dim1 always has value in original table;
                String dim1Val = row.get(this.dim1);
                String dim2Val = row.get(this.dim2);
                String subDim1Val = null;
                if (subDim1 != null) {
                    subDim1Val = row.get(this.subDim1);
                    dim1Val = dim1Val + "/" + subDim1Val;
                }
                int sold = Integer.parseInt(row.getOrDefault(SOLD_COL, "0"));
                if (dim1Val != null && dim2Val != null) {
                    pivotTable.computeIfAbsent(dim1Val, key -> new HashMap<>());
                    pivotTable.get(dim1Val).computeIfAbsent(dim2Val, key -> 0);
                    // initialize row sum
                    pivotTable.get(dim1Val).computeIfAbsent(ROW_SUM, key -> 0);

                    // initialize col sum
                    pivotTable.computeIfAbsent(COL_SUM, key -> new HashMap<>());
                    pivotTable.get(COL_SUM).putIfAbsent(dim2Val, 0);
                    pivotTable.get(COL_SUM).putIfAbsent(ROW_SUM, 0);

                    int original = pivotTable.get(dim1Val).get(dim2Val);
                    pivotTable.get(dim1Val).put(dim2Val, original + sold);

                    int originalRowSum = pivotTable.get(dim1Val).entrySet().stream().filter(entry -> !entry.getKey().equals(ROW_SUM))
                            .mapToInt(Map.Entry::getValue).sum();
                    pivotTable.get(dim1Val).put(ROW_SUM, originalRowSum);

                    int originalColSum = pivotTable.get(COL_SUM).get(dim2Val);
                    int originalTotalSum = pivotTable.get(COL_SUM).get(ROW_SUM);
                    pivotTable.get(COL_SUM).put(dim2Val, originalColSum + sold);
                    pivotTable.get(COL_SUM).put(ROW_SUM, originalTotalSum + sold);
                }
            }
        }

        /**
         * print the contents of the pivot table
         * @return
         */
        @Override

        public String toString() {
            return String.format("[pivot table][dim1=%s][dim2=%s] %s", this.dim1, this.dim2, this.pivotTable);
        }
    }
}
