package oai;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.Instant.ofEpochMilli;

public class RateLimiter {
    static class FixedWindowLimiter implements Limiter {
        // in seconds
        Clock clock;
        final int windowSize;

        // how many requests to block;
        final int limit;
        long lastWindow;
        int lastWindowCount;

        public FixedWindowLimiter(int windowSize, int limit, Clock clock) {
            this.clock = clock;
            this.lastWindow = 0;
            this.lastWindowCount = 0;
            this.windowSize = windowSize;
            this.limit = limit;
        }

        @Override
        public synchronized boolean enter() {
            long ts = this.clock.millis();
            long currentWindow = ts/1000/windowSize;
            if (currentWindow == lastWindow) {
                if (lastWindowCount < limit) {
                    System.out.printf("Thread %s, timestamp %s, %s/%s. Not throttling %n", Thread.currentThread().getName(),
                            ts, lastWindowCount, limit);
                    lastWindowCount += 1;
                    return true;
                } else {
                    System.out.printf("Thread %s, timestamp %s, %s/%s. Throttle! %n", Thread.currentThread().getName(),
                            ts, lastWindowCount, limit);
                    return false;
                }
            } else {
                lastWindow = currentWindow;
                lastWindowCount = 1;
                System.out.printf("Thread %s, timestamp %s, %s/%s. Not throttling! %n", Thread.currentThread().getName(),
                        ts, lastWindowCount, limit);
                return true;
            }
        }
    }

    static class Counter {
        long timestamp;
        AtomicInteger count;

        public Counter(long timestamp) {
            this.timestamp = timestamp;
            this.count = new AtomicInteger();
        }
    }
    static class SlidingWindowLimiter implements Limiter {
        final int windowSize;
        final int limit;

        Clock clock;
        Counter[] buffer;

        public SlidingWindowLimiter(int windowSize, int limit, Clock clock) {
            this.windowSize = windowSize;
            this.limit = limit;
            this.clock = clock;
            this.buffer = new Counter[windowSize];
            Arrays.fill(this.buffer, new Counter(0));
        }

        @Override
        public synchronized boolean enter() {
            long timestamp = this.clock.millis() / 1000;

            int hitsInWindow = 0;
            for (int i = 0; i < this.windowSize; i++) {
                Counter curr = buffer[i];
                if (timestamp - curr.timestamp < this.windowSize ) {
                    hitsInWindow += curr.count.get();
                }
            }

            if (hitsInWindow < this.limit) {
                int bucket = (int) timestamp % this.windowSize;
                if (buffer[bucket].timestamp == timestamp) {
                    // already has some records on the same timestamp
                    buffer[bucket].count.incrementAndGet();
                } else {
                    buffer[bucket] = new Counter(timestamp);
                    buffer[bucket].count.incrementAndGet();
                }
                System.out.printf("Thread %s, timestamp %s, %s/%s. Not throttling %n", Thread.currentThread().getName(),
                        timestamp, hitsInWindow, limit);
                return true;
            }
            System.out.printf("Thread %s, timestamp %s, %s/%s. Throttle! %n", Thread.currentThread().getName(),
                    timestamp, hitsInWindow, limit);
            return false;
        }
    }

    static class TokenBucketLimiter implements Limiter {
        final int maxTokens;
        // during refill, how many tokens can be generated
        final double tokenRate;

        double currTokens;
        int lastRefillTime;
        Clock clock;

        public TokenBucketLimiter(int maxTokens, double tokenRatePerSec, Clock clock) {
            this.maxTokens = maxTokens;
            this.tokenRate = tokenRatePerSec;
            // initialize to max tokens
            this.currTokens = maxTokens;
            this.lastRefillTime = 0;
            this.clock = clock;
        }
        @Override
        public synchronized boolean enter() {
            int timestamp = (int) clock.millis() / 1000;
            refill(timestamp);

            if (currTokens >= 1) {
                currTokens -= 1;
                return true;
            }
            return false;
        }

        private void refill(int timestamp) {
            double tokensToFill = (timestamp - lastRefillTime) * tokenRate;
            currTokens = Math.min(currTokens + tokensToFill, maxTokens);
            lastRefillTime = timestamp;
        }
    }

    static class LeakyBucketLimiter implements Limiter {
        Clock clock;
        ScheduledExecutorService executorService;
        Deque<String> taskQueue;
        public LeakyBucketLimiter(double leakRate, Clock clock) {
            executorService = Executors.newScheduledThreadPool(5);
            executorService.scheduleAtFixedRate(this::leak, 2, (long) (1.0 / leakRate), TimeUnit.SECONDS);
            taskQueue = new LinkedBlockingDeque<>();
            this.clock = clock;
        }
        @Override
        public boolean enter() {
            String task = Thread.currentThread().getName() + "-" + clock.millis();
            this.taskQueue.offer(task);
            System.out.printf("Enqueued task: %s %n", task);
            return true;
        }

        private void leak() {
            if (!taskQueue.isEmpty()) {
                String task = taskQueue.poll();
                System.out.printf("Task Queue is [%s]. Processed task: %s %n", taskQueue, task);
            }
        }

        public void shutdown() {
            this.executorService.shutdown();
        }
    }

    static class FakeClock extends Clock {
        long millis = 0L;
        @Override
        public ZoneId getZone() {
            return null;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ofEpochMilli(this.millis);
        }

        public void setMillis(long millis) {
            this.millis = millis;
        }
    }
    public static void main(String[] args) {
        testLeakyBucket();
    }

    /**
     * This test case can be used to test both fixed window and sliding window
     * The expected behavior is different for them.
     *
     * 1. For fixed window, until 60_000ms, it can allow 5 hits. In 61_000ms, it can allow another 5 hits.
     * 2. For sliding window, it can allow 2 hits in 1_000ms, 3 hits in 60_000ms. In 61_000ms, it can allow another 2 hits.
     */
    public static void testSlidingWindow() {
        FakeClock settable = new FakeClock();
        settable.setMillis(1_000L); // belong to first time window
        Limiter limiter = new FixedWindowLimiter(60 , 5, settable);
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                boolean result = limiter.enter();
                System.out.printf("Thread %s, timestamp %s, result is : %s %n", Thread.currentThread().getName(),
                        settable.millis, result);
            }
        };

        for (int i = 0; i < 2; i++) {
            threadPool.execute(task);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // enter the last part of the first window, still should throttle 3 of them
        settable.setMillis(59_000);
        for (int i = 0; i < 5; i++) {
            threadPool.execute(task);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // the window should have rolled, 2 more requests can be allowed
        settable.setMillis(61_000);
        for (int i = 0; i < 5; i++) {
            threadPool.execute(task);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A test case for token bucket.
     *
     * The expected behavior is:
     * 1. initially the tokens are full. So we can handle 5 requests at 10_000ms;
     * 2. anytime between 10_000ms to 13_000ms, we can't take new requests.
     * 3. at 13_000ms and after, we can take 1 more requests.
     * 4. at 25_000ms and after, we can take 1 more requests.
     * 5. at 50_000ms and after, we can take 2 more requests.
     */
    public static void testTokenBucket() {
        FakeClock settable = new FakeClock();
        settable.setMillis(1_000L); // belong to first time window
        Limiter limiter = new TokenBucketLimiter(5, 5.0/60, settable);
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                boolean result = limiter.enter();
                System.out.printf("Thread %s, timestamp %s, result is : %s %n", Thread.currentThread().getName(),
                        settable.millis, result);
            }
        };

        // 5/6 requests should go through
        for (int i = 0; i < 6; i++) {
            threadPool.execute(task);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        settable.setMillis(10_000);
        // no requests will go through
        for (int i = 0; i < 5; i++) {
            threadPool.execute(task);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // the window should have rolled, 1 more requests can be allowed
        settable.setMillis(13_000);
        for (int i = 0; i < 5; i++) {
            threadPool.execute(task);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // the window should have rolled, 3 more requests can be allowed
        settable.setMillis(50_000);
        for (int i = 0; i < 5; i++) {
            threadPool.execute(task);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void testLeakyBucket() {
        Clock clock = Clock.systemDefaultZone();

        LeakyBucketLimiter limiter = new LeakyBucketLimiter(0.5, clock);
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                boolean result = limiter.enter();
                System.out.printf("Thread %s, timestamp %s, result is : %s %n", Thread.currentThread().getName(),
                        clock.millis(), result);
            }
        };

        // all requests will be enqued
        for (int i = 0; i < 5; i++) {
            threadPool.execute(task);
        }

        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        threadPool.shutdown();
        limiter.shutdown();
        try {
            threadPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Rate limiter helps in limiting the rate of execution of a piece of code. The rate is defined in terms of
     * TPS(Transactions per second). Rate of 5 would suggest, 5 transactions/second. Transaction could be a DB call, API call,
     * or a simple function call.
     * <p>
     * Every {@link RateLimiter} implementation should implement either {@link RateLimiter#throttle(Code)} or, {@link RateLimiter#enter()}.
     * They can also choose to implement all.
     * <p>
     * {@link Code} represents a piece of code that needs to be rate limited. It could be a function call, if the code to be rate limited
     * spreads across multiple functions, we need to use entry() and exit() contract.
     */
    public interface Limiter {
        /**
         * When the piece of code that needs to be rate limited cannot be represented as a contiguous
         * code, then entry() should be used before we start executing the code. This brings the code inside the rate
         * limiting boundaries.
         *
         * @return true if the code will execute and false if rate limited.
         * <p
         */
        boolean enter();
    }
}
