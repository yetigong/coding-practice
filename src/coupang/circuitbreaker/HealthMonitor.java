package coupang.circuitbreaker;

/**
 * This class is keeping track of the health of the system
 *
 * Implementing a Time Window for Error Rate Detection
 *
 * One common approach is to use a sliding window algorithm. This involves tracking request outcomes (successes/failures) along with their timestamps. Here's a simplified strategy:
 *
 *     Divide the time window into smaller buckets (e.g., if your window is 1 minute, you might have 6 buckets of 10 seconds each).
 *     Record the outcome of each request in the current bucket.
 *     Periodically shift the window, either by moving to a new bucket on a schedule (every 10 seconds in this example) or by evaluating the window dynamically based on the timestamps of incoming requests.
 *     To calculate the failure rate, sum the failures in all buckets within the window and divide by the total number of requests in the same period.
 */
public class HealthMonitor {

    // assuming this class returns the error rate of the downstream service in a time window
    public int getErrorRate() {
        return 100;
    }

    // assuming this class returns the num of successful calls within past time window
    public int getSuccessfulRequest() {
        return 100;
    }

    public synchronized void recordSuccess() {

    }

    public synchronized void recordFailure() {

    }
}
