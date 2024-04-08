package gpt;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
