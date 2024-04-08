package gpt;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * Description:
 * Design a thread-safe counter that can be incremented by multiple threads concurrently without leading to race conditions. Ensure that your implementation minimizes the performance impact often associated with locking mechanisms.
 *
 * Requirements:
 *
 * Implement a class ThreadSafeCounter with the following methods:
 * increment(): Increments the counter by 1 in a thread-safe manner.
 * getCount(): Returns the current count value.
 * Use locks to ensure that your counter is thread-safe.
 * Write a simple test case that creates multiple threads (e.g., 10 threads) where each thread increments the counter 100 times. After all threads complete their execution, the final count should be 1000.
 */
public class MultithreadCounter {
    public static class ThreadSafeCounter {
        AtomicInteger counter;

        public ThreadSafeCounter() {
            this.counter = new AtomicInteger(0);
        }
        public void increment() {
            // Implement this method
            this.counter.incrementAndGet();
        }

        public int getCount() {
            // Implement this method
            return this.counter.intValue();
        }
    }

    static class Worker implements Runnable {

        ThreadSafeCounter counter;
        final int interval;
        final int limit = 100;
        int count = 0;
        public Worker(ThreadSafeCounter counter, int interval) {
            this.counter = counter;
            this.interval = interval;
        }

        @Override
        public void run() {
            while (this.count < this.limit) {
                counter.increment();
                try {
                    Thread.sleep(new Random().nextInt(this.interval) + 1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.count++;
            }
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        ThreadSafeCounter counter = new ThreadSafeCounter();

        int numberOfThreads =  100;
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new Worker(counter, rand.nextInt(100)));
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.printf("Final result for counter is %s, expected to be %s. %n", counter.getCount(), numberOfThreads * 100);
    }
}
