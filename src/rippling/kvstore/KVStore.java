package rippling.kvstore;

/***
 * This is a question based on:
 * https://leetcode.com/discuss/interview-question/3928848/Rippling-Phone-Screen-or-Sr-Software-Engineer-US-or-Rejected
 *
 * My approach would be to start with single connection with no nested transactions.
 *
 * Concurrent transactions can be very tricky. It is even tricky to think about the expected behavior:
 * imagine:
 * client 1 trans:
 * get A
 * get B
 * create C where C = A + B
 *
 * client 2 trans:
 * delete A
 * update B -> new val
 *
 * what's the behavior when both happens concurrently?
 *
 * The only way I could think of to define a definite behavior for the above scenario, is to serialize the transactions.
 * However, it requires a global lock, which is inefficient.
 *
 * If we create a row based lock, then we are volurable to deadlocks. We probably also need to make "Getting all locks" for rows
 * needed to a transactional operation. Then it is still a serial operation.
 *
 */
public interface KVStore {
    String read(String key);
    String create(String key, String value);

    String update(String key, String value);
    String delete(String key);

    void begin();
    void commit(int num);
    void rollback(int num);
}
