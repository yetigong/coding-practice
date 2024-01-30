package rippling.kvstore;

import org.testng.Assert;
import org.testng.annotations.Test;

public class KVStoreImplTest {
    @Test
    public void testBasicCaseNoTrans() {
        KVStore kvStore = new KVStoreImpl();
        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        Assert.assertEquals(kvStore.read("key1"), "val1");
        Assert.assertEquals(kvStore.read("key2"), "val2");
        Assert.assertEquals(kvStore.read("key3"), "val3");
    }

    @Test
    public void testTransHappyPath() {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        kvStore.begin();
        kvStore.create("key1", "val101");
        kvStore.update("key2", "val102");
        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val102");
        kvStore.commit(2);
    }

    @Test
    public void testTransCreate() {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        kvStore.begin();
        kvStore.create("key1", "val101");
        kvStore.update("key2", "val102");
        kvStore.create("key4", "val104");
        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val102");
        Assert.assertEquals(kvStore.read("key4"), "val104");
        kvStore.commit(2);

        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val102");
        Assert.assertNotEquals(kvStore.read("key4"), "val104");
    }

    @Test
    public void testTransRollback() {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        kvStore.begin();
        kvStore.create("key1", "val101");
        kvStore.update("key2", "val102");
        kvStore.create("key4", "val104");
        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val102");
        Assert.assertEquals(kvStore.read("key4"), "val104");
        kvStore.rollback(2);

        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val2");
        Assert.assertNotEquals(kvStore.read("key4"), "val104");
    }

    @Test
    public void testTransDelete() {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        kvStore.begin();
        kvStore.create("key1", "val101");
        kvStore.update("key2", "val102");
        kvStore.delete("key3");
        kvStore.create("key4", "val104");
        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val102");
        Assert.assertEquals(kvStore.read("key3"), "tombstone");
        Assert.assertEquals(kvStore.read("key4"), "val104");
        kvStore.commit(2);

        Assert.assertEquals(kvStore.read("key1"), "val101");
        Assert.assertEquals(kvStore.read("key2"), "val102");
        Assert.assertEquals(kvStore.read("key3"), "val3");
        Assert.assertNotEquals(kvStore.read("key4"), "val104");
    }

    @Test
    public void testTransFromPostPart3_1() {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        kvStore.begin();
        kvStore.create("key3", "val8");
        Assert.assertEquals(kvStore.read("key3"), "val8");
        kvStore.create("key5", "val5");
        Assert.assertEquals(kvStore.read("key5"), "val5");
        kvStore.create("key5", "val7");
        Assert.assertEquals(kvStore.read("key5"), "val7");

        kvStore.create("key2", "val7");
        Assert.assertEquals(kvStore.read("key2"), "val7");

        kvStore.update("key2", "val8");
        Assert.assertEquals(kvStore.read("key2"), "val8");

        kvStore.delete("key1");
        Assert.assertEquals(kvStore.read("key1"), "tombstone");

        kvStore.commit(2);

        Assert.assertEquals(kvStore.read("key1"), "val1");
        Assert.assertEquals(kvStore.read("key2"), "val2");
        Assert.assertEquals(kvStore.read("key3"), "val8");
        Assert.assertEquals(kvStore.read("key5"), "val5");
    }

    @Test
    public void testTransFromPostPart3_2() {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");

        kvStore.begin();
        kvStore.create("key3", "val8");
        Assert.assertEquals(kvStore.read("key3"), "val8");
        kvStore.create("key5", "val5");
        Assert.assertEquals(kvStore.read("key5"), "val5");
        kvStore.create("key5", "val7");
        Assert.assertEquals(kvStore.read("key5"), "val7");

        kvStore.create("key2", "val7");
        Assert.assertEquals(kvStore.read("key2"), "val7");

        kvStore.update("key2", "val8");
        Assert.assertEquals(kvStore.read("key2"), "val8");

        kvStore.delete("key1");
        Assert.assertEquals(kvStore.read("key1"), "tombstone");

        kvStore.rollback(2);

        Assert.assertEquals(kvStore.read("key1"), "val1");
        Assert.assertEquals(kvStore.read("key2"), "val7");
        Assert.assertEquals(kvStore.read("key3"), "val8");
        Assert.assertEquals(kvStore.read("key5"), "val7");
    }

    @Test
    public void testTransMultiThread() throws InterruptedException {
        KVStore kvStore = new KVStoreImpl();

        kvStore.create("key1", "val1");
        kvStore.create("key2", "val2");
        kvStore.create("key3", "val3");
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread " + Thread.currentThread().getName() + " has started running!");
                kvStore.begin();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                kvStore.create("key1", "val101");
                kvStore.update("key2", "val102");
                Assert.assertEquals(kvStore.read("key1"), "val101");
                Assert.assertEquals(kvStore.read("key2"), "val102");
                kvStore.commit(1);
                Assert.assertEquals(kvStore.read("key1"), "val101");
                Assert.assertEquals(kvStore.read("key2"), "val2");
            }
        });

        // thread 2 is supposed to execute on create and delete commands earlier. But because thread 1 has
        // occupied the lock, thread 2 has to wait.
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread " + Thread.currentThread().getName() + " has started running!");
                kvStore.begin();
                kvStore.create("key1", "val201");
                kvStore.delete("key2");
                Assert.assertEquals(kvStore.read("key1"), "val201");
                Assert.assertEquals(kvStore.read("key2"), "tombstone");
                kvStore.commit(1);
                Assert.assertEquals(kvStore.read("key1"), "val201");
                Assert.assertEquals(kvStore.read("key2"), "val2");
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }
}
