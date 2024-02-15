package coupang.circuitbreaker;

import java.util.Random;

/**
 * this class mocks the behavior of a client that calls downstream services
 *
 * It can produce either success or failure result. It invokes the health monitor to record the success and failure calls.
 *
 */
public class WrappedClient {
    private HealthMonitor monitor;
    public WrappedClient(HealthMonitor monitor) {
        this.monitor = monitor;
    }

    public boolean callDownStreams() {
        // add some randomization to produce error calls
        int random = new Random().nextInt(100);
        if (random > 80) {
            System.out.println("Client callilng downstream failed!");
            this.monitor.recordFailure();
            return false;
        } else {
            System.out.println("Client callilng downstream success!");
            this.monitor.recordSuccess();
            return true;
        }
    }
}
