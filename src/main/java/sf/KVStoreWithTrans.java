package sf;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KVStoreWithTrans {
    static class KVStore {
        ReentrantReadWriteLock lock;
        Deque<Map<String, String>> stores;
        Map<String, String> store;

        boolean inTransaction;

        /**
         * What is a good representation of tombstone? maybe we need to store a status as part of the value?
         */
        static final String TOMB_STONE = "$tombstone";

        public KVStore() {
            this.lock = new ReentrantReadWriteLock();
            this.store = new HashMap<>();
            this.stores = new LinkedList<>();
            this.stores.push(store);
            this.inTransaction = false;
        }
        void put(String key, String value) {
            try {
                lock.writeLock().lock();
                if (value.equals(TOMB_STONE)) {
                    throw new IllegalArgumentException("Invalid value!");
                }
                this.stores.peek().put(key, value);
            }
            finally {
                lock.writeLock().unlock();
            }

        }

        /**
         * will this result in deadlock if we call get within a transaction?
         * @param key
         * @return
         */
        public String get(String key) {
            String result;
            try {
                lock.readLock().lock();
                if (inTransaction) {
                    result = getWithDelete(key, stores.peek());
                } else {
                    result = getWithDelete(key, store);
                }
            }
            finally {
                lock.readLock().unlock();
            }
            return result;
        }

        private String getWithDelete(String key, Map<String, String> store) {
            if (store.containsKey(key)) {
                if (store.get(key).equals(TOMB_STONE)) {
                    return null;
                } else {
                    return store.get(key);
                }
            }
            return null;
        }

        public void delete(String key) {
            try {
                lock.writeLock().lock();
                stores.peek().put(key, TOMB_STONE);
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public void begin() {
            lock.writeLock().lock();
            Map<String, String> temp = new HashMap<>();
            stores.push(temp);
        }

        public void commit() {
            try {
                if (stores.size() < 2) {
                    throw new IllegalStateException("Invalid stores stack state. Expecting at least 2 stacks");
                }
                Map<String, String> temp = stores.pop();
                stores.peek().putAll(temp);
            }
            finally {
                lock.writeLock().unlock();
            }
        }

        public void rollback() {
            try {
                if (stores.size() < 2) {
                    throw new IllegalStateException("Invalid stores stack state. Expecting at least 2 stacks");
                }
                stores.pop();
            }
            finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Test
    public void testCase1() {
        KVStore kvStore = new KVStore();
        kvStore.put("a", "a");
        kvStore.put("b", "b");
        kvStore.put("c", "c");
        kvStore.put("d", "d");

        kvStore.begin();
        kvStore.put("a", "a1");
        kvStore.put("b", "b1");
        kvStore.commit();

        kvStore.begin();
        kvStore.put("c", "c2");
        kvStore.delete("d");
        kvStore.rollback();

        Assert.assertEquals(kvStore.get("a"), "a1");
        Assert.assertEquals(kvStore.get("b"), "b1");
        Assert.assertEquals(kvStore.get("c"), "c");
        Assert.assertEquals(kvStore.get("d"), "d");
    }

    @Test
    public void testCaseNested() {
        KVStore kvStore = new KVStore();
        kvStore.put("a", "a");
        kvStore.put("b", "b");
        kvStore.put("c", "c");
        kvStore.put("d", "d");

        kvStore.begin(); // begin parent transaction
        kvStore.put("a", "a1");
        kvStore.put("b", "b1");

        kvStore.begin();
        kvStore.put("e", "e1");
        kvStore.delete("d");
        kvStore.commit();

        kvStore.begin();
        kvStore.put("c", "c2");
        kvStore.put("d", "d2");
        kvStore.rollback();

        kvStore.commit(); // commit parent transaction

        Assert.assertEquals(kvStore.get("a"), "a1");
        Assert.assertEquals(kvStore.get("b"), "b1");
        Assert.assertEquals(kvStore.get("c"), "c");
        Assert.assertEquals(kvStore.get("d"), null);
        Assert.assertEquals(kvStore.get("e"), "e1");
    }

    @Test
    public void testForDeadlock() throws InterruptedException {
        KVStore kvStore = new KVStore();
        kvStore.put("a", "a");
        kvStore.put("b", "b");
        kvStore.put("c", "c");
        kvStore.put("d", "d");

        kvStore.begin(); // begin parent transaction
        kvStore.put("a", "a1");
        kvStore.put("b", "b1");
        System.out.println(Thread.currentThread().getName() + " within parent transction, can get c (should be blocked): " + kvStore.get("c"));
        Thread anotherThread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " within parent transction, can get c (should be blocked): " + kvStore.get("c"));
        });
        anotherThread.start();

        kvStore.begin();
        kvStore.put("c", "c2");
        kvStore.put("d", "d2");
        kvStore.rollback();

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " completed parent transaction! " + kvStore.get("c"));
        kvStore.commit(); // commit parent transaction

        Assert.assertEquals(kvStore.get("a"), "a1");
        Assert.assertEquals(kvStore.get("b"), "b1");
        Assert.assertEquals(kvStore.get("c"), "c");
        Assert.assertEquals(kvStore.get("d"), "d");
    }
}
