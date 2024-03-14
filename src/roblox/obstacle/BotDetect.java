package roblox.obstacle;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.IntStream;

public class BotDetect {
    public static void main(String[] args) {
        List<String> inputs = List.of(
                "1000, 1.1.1.1",
                "1000, 1.1.1.0",
                "1000, 1.1.1.2",
                "1001, 1.1.1.2",
                "1001, 1.1.1.0",
                "1002, 1.1.1.0",
                "1003, 1.1.1.0",
                "1004, 1.1.1.0"
        );

        BotService sol = new BotService(inputs);
        System.out.println("IP is bot: " + sol.isBot(1005, "1.1.1.0", 2, 5));
    }

    static class BotService {
        Map<String, Map<Integer, Integer>> hitMap;
        public BotService(List<String> inputs) {
            hitMap = new HashMap<>();
            for (String input: inputs) {
                String[] parts = input.split(",");
                int timestamp = Integer.parseInt(parts[0].trim());
                String ip = parts[1].trim();
                hitMap.putIfAbsent(ip, new HashMap<>());
                int hits = hitMap.get(ip).getOrDefault(timestamp, 0);
                hitMap.get(ip).put(timestamp, hits+1);
            }
        }

        public Set<String> greaterThan(List<String> inputs, int count) {
            Map<String, Integer> ipCounter = new HashMap<>();
            Set<String> result = new HashSet<>();
            for (String input: inputs) {
                String[] parts = input.split(",");
                String ip = parts[1].trim();
                int freq = ipCounter.getOrDefault(ip, 0) + 1;
                if (freq >= count)
                    result.add(ip);
                ipCounter.put(ip, freq);
            }
            return result;
        }

        /**
         * Requirement:
         * return true if the ip has been queried more than n times in last w timewindow
         *
         * Comment:
         * Needs some clarification on what is the relationship between w and ts. Assuming w is last w seconds,
         * and ts is always going to be larger than timestamps provided in the input - we can use a buffer ring.
         *
         * If ts can be any timestamp, and so we are querying (timestamp - w, timestamp], then we need to keep track of
         * all timestamps for every IP.
         *
         * assuming timestamp and counter don't overflow..
         *
         * @param ts
         * @param ip
         * @return
         */
        public boolean isBot(int ts, String ip, int n, int w) {
            Map<Integer, Integer> hits = hitMap.get(ip);
            return IntStream.range(ts - w + 1, ts + 1).boxed()
                    .map(t -> hits.getOrDefault(t, 0)).mapToInt(Integer::valueOf).sum() >= n;
        }
    }


    @Test
    public void testGreaterThan() {
        List<String> inputs = List.of(
                "1000, 1.1.1.1",
                "1000, 1.1.1.0",
                "1000, 1.1.1.2",
                "1001, 1.1.1.2",
                "1001, 1.1.1.0",
                "1002, 1.1.1.0",
                "1003, 1.1.1.0",
                "1004, 1.1.1.0"
        );

        BotService sol = new BotService(inputs);
        Assert.assertEquals(sol.greaterThan(inputs, 2),
                Set.of("1.1.1.2", "1.1.1.0"));

        Assert.assertEquals(sol.greaterThan(inputs, 1),
                Set.of("1.1.1.2", "1.1.1.0", "1.1.1.1"));

        Assert.assertEquals(sol.greaterThan(inputs, 5),
                Set.of("1.1.1.0"));
    }

    @Test
    public void testIsBot() {
        List<String> inputs = List.of(
                "1000, 1.1.1.1",
                "1000, 1.1.1.0",
                "1000, 1.1.1.2",
                "1001, 1.1.1.2",
                "1001, 1.1.1.0",
                "1002, 1.1.1.0",
                "1003, 1.1.1.0",
                "1004, 1.1.1.0"
        );
        BotService sol = new BotService(inputs);

        Assert.assertEquals(sol.isBot(1005, "1.1.1.0", 3, 5), true);
        Assert.assertEquals(sol.isBot(1005, "1.1.1.0", 3, 3), false);
        Assert.assertEquals(sol.isBot(1005, "1.1.1.0", 3, 4), true);
        Assert.assertEquals(sol.isBot(1005, "1.1.1.1", 3, 5), false);
        Assert.assertEquals(sol.isBot(1005, "1.1.1.2", 3, 5), false);
    }

}
