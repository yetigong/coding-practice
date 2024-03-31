package oai;

import java.time.Clock;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class TimeKV {
    public static class Store {
        Clock clock;
        Map<String, ConcurrentSkipListMap<Long, String>> store;
        public Store(Clock clock) {
            this.clock = clock;
            this.store = new ConcurrentHashMap<>();
        }

        public void set(String key, String value) {
            long ts = clock.instant().getNano() + new Random().nextLong();
            this.store.putIfAbsent(key, new ConcurrentSkipListMap<>());
            this.store.get(key).put(ts, value);
        }

        public String get(String key, long timestamp) {
            if (!this.store.containsKey(key)) {
                return "";
            }

            if (this.store.get(key).floorEntry(timestamp) == null) {
                return "";
            }
            return this.store.get(key).floorEntry(timestamp).getValue();
        }
    }

    public static void main(String[] args) {
        Clock clock = Clock.systemDefaultZone();
        Store store = new Store(clock);

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Runnable task = () -> {
            store.set("key", "value");
            store.get("key", clock.millis());
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        };
        for (int i = 0; i < 1000; i++) {
            System.out.printf("Execution %s %n", i);
            executor.execute(task);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            // something wrong
        }
        System.out.printf("After execution, the store has %s elements %n", store.store.get("key").size());
    }
}
