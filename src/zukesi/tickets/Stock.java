package zukesi.tickets;

import java.util.List;

public class Stock {
    static class Segmnent {
        int low;
        int high;
        int profilt;

        public Segmnent(int low, int high, int profit) {
            this.low = low;
            this.high = high;
            this.profilt = profit;
        }

        @Override
        public String toString() {
            return "Segmnent{" +
                    "low=" + low +
                    ", high=" + high +
                    ", profilt=" + profilt +
                    '}';
        }
    }

    static int globalProfit(List<Segmnent> segmnentList) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        int profit = 0;
        for (int i = 0; i < segmnentList.size(); i++) {
            if (i == 0) {
                min = segmnentList.get(0).low;
                max = segmnentList.get(0).high;
                profit = segmnentList.get(0).profilt;
            } else {
                profit = Math.max(profit, segmnentList.get(i).high - min);
                // update min after the computing the profit
                min = Math.min(min, segmnentList.get(i).low);
                profit = Math.max(profit, segmnentList.get(i).profilt);
            }
        }

        return profit;
    }

    public static void main(String[] args) {
        List<Segmnent> segments = List.of(
                new Segmnent(1, 7, 4),
                new Segmnent(3, 6, 3),
                new Segmnent(2, 5, 3),
                new Segmnent(3, 9, 6)
        );

        System.out.println(globalProfit(segments));
    }
}
