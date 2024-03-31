package circuitbreaker;

/***
 * improvements
 * 1. test cases to add
 * 2. health monitor implementation
 * 3. half_open state - current based on success rate on top of thresholded data. This is slightly different from the
 * requirement to based on # of requests. But recording # of requests is a bit tricky.
 * 4. client method has hard coded configurations that should be refactored
 *
 */
public class CircuitBreaker {
    private StateManager stateManager;
    private WrappedClient client;
    private HealthMonitor healthMonitor;

    private final int testingRequestRatio;
    public CircuitBreaker(int threshold, int testingRequestRatio, int openTimeout) {
        this.healthMonitor = new HealthMonitor();
        this.stateManager = new StateManager(this.healthMonitor, threshold, openTimeout);
        this.client = new WrappedClient(healthMonitor);
        this.testingRequestRatio = testingRequestRatio;
    }

    /**
     * This method mocks the behavior that when the upstream callers are calling, it processes the request,
     * and handles the circuit breaking main logic based on the state.
     */
    public void process(int requestId) {
        System.out.println("Received request " + requestId);
        if (this.stateManager.getState().equals(State.CLOSED)) {
            this.client.callDownStreams();
            this.stateManager.transitionState();
        } else if (this.stateManager.getState().equals(State.OPEN)) {
            // this should be properly converted to 503 error code
            throw new RuntimeException("service unavailable!");
        } else {
            if (requestId % 100 < this.testingRequestRatio) {
                this.client.callDownStreams();
                this.stateManager.transitionState();
            } else {
                // this should be properly converted to 503 error code
                throw new RuntimeException("service unavailable!");
            }
        }
    }
}
