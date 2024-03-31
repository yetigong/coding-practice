package circuitbreaker;

import java.util.Timer;
import java.util.TimerTask;

public class StateManager {
    private State state;
    private HealthMonitor monitor;

    private Timer timer;
    private final int threshold;
    private final int openTimeout;
    public StateManager(HealthMonitor monitor, int threshold, int openTimeout) {
        this.state = State.CLOSED;
        this.monitor = monitor;
        this.threshold = threshold;
        this.timer = new Timer();
        this.openTimeout = openTimeout;
    }

    public State getState() {
        return this.state;
    }

    /***
     * make this method synchronized so it guarantees thread safety
     */
    public synchronized void transitionState() {
        // Cancel any existing timer task to prevent duplicate scheduling. this may not be necessary though as this method is synchronized, which prevents multiple threads tries to schedule task.
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = new Timer(); // Create a new timer for the next task. This is needed because after a timer is canceled, it can't be used again
        }


        if (this.state.equals(State.CLOSED) || state.equals(State.HALF_OPEN)) {
            // cut off
            if (monitor.getErrorRate() > this.threshold) {
                System.out.println("exceeded error threshold! Change to OPEN state!");
                this.state = State.OPEN;
                this.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (StateManager.this) {
                            System.out.println("timeout reached, switching back to HALF_OPEN state!");
                            state = State.HALF_OPEN;
                        }
                    }
                }, this.openTimeout);
            } else {
                if (state.equals(State.CLOSED)) {
                    System.out.println("System healthy, stay at CLOSED state!");
                } else {
                    System.out.println("System resumes to healthy state, changing from HALF_CLOSE to CLOSED state!");
                    state = State.CLOSED;
                }

            }
        } else {
            System.out.println("Stay at OPEN state!");
        }
    }
}
