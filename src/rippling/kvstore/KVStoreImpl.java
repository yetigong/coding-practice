package rippling.kvstore;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableMap;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KVStoreImpl implements KVStore {
    private Map<String, String> store;
    private Lock lock;
    private boolean inTrans;
    private List<Map<String, String>> staging;
    private Map<String, String> latest;

    /**
     * a special character defined to mark the element as deleted in a transaction, so we don't end up
     * read it again from the store.
     */
    private static String TOMB_STONE = "tombstone";
    public KVStoreImpl() {
        store = new HashMap<>();
        lock = new ReentrantLock();
        inTrans = false;
        staging = new ArrayList<Map<String, String>>();
    }

    /**
     * TODO:
     * there is a lot of redundant code.
     * refactor this to make sure we remove the redundancy.
     * @param key
     * @return
     */
    @Override
    public String read(String key) {
        if (!inTrans) {
            if (!store.containsKey(key)) {
                return "[read] no key found for " + key;
            }

            return store.get(key);
        } else {
            if (latest.containsKey(key)) {
                return latest.get(key);
            } else {
                if (!store.containsKey(key)) {
                    return "[read] no key found for " + key;
                }

                return store.get(key);
            }
        }
    }

    @Override
    public String create(String key, String value) {
        if (!inTrans) {
            store.put(key, value);
            return value;
        } else {
            this.latest.put(key, value);
            this.staging.add(ImmutableMap.copyOf(latest));
            return value;
        }
    }

    @Override
    public String update(String key, String value) {
        if (!inTrans) {
            if (!store.containsKey(key)) {
                return "[update] no key found for " + key;
            }

            store.put(key, value);
        } else {
            if (latest.containsKey(key) || store.containsKey(key)) {
                latest.put(key, value);
                this.staging.add(ImmutableMap.copyOf(latest));
            } else {
                return "[update] no key found for " + key;
            }
        }
        return value;
    }

    @Override
    public String delete(String key) {
        if (!inTrans) {
            if (!store.containsKey(key)) {
                return "[delete] no key found for " + key;
            }
            store.remove(key);
            return null;
        } else {
            if (latest.containsKey(key) || store.containsKey(key)) {
                latest.put(key, TOMB_STONE);
                this.staging.add(ImmutableMap.copyOf(latest));
                return null;
            } else {
                return "[delete] no key found for " + key;
            }
        }
    }

    @Override
    public void begin() {
        System.out.println("thread " + Thread.currentThread().getName() + " is trying to acquire lock");
        this.lock.lock();
        this.inTrans = true;
        this.latest = new HashMap<>();
    }

    @Override
    public void commit(int num) {
        if (num <= 0 || staging.isEmpty()) {
            return;
        }
        // commit num-th staging state into store
        Map<String, String> snapshot;
        if (num >= staging.size()) {
            snapshot = staging.get(staging.size() - 1);
        } else {
            snapshot = staging.get(num - 1);
        }

        snapshot.entrySet().stream().forEach(entry -> {
            if (entry.getValue().equals(TOMB_STONE)) {
                this.store.remove(entry.getKey());
            } else {
                this.store.put(entry.getKey(), entry.getValue());
            }
        });

        // clear all staging areas
        this.inTrans = false;
        this.staging = new ArrayList<Map<String, String>>();
        this.latest = new HashMap<>();

        // release lock at last
        this.lock.unlock();
    }

    @Override
    public void rollback(int num) {
        int totalOps = staging.size();
        commit(totalOps - num);
    }
}
