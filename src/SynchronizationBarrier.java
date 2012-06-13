
/**
 * Barrier for synchronisation purposes (heavily influenced by class CyclicBarrier).
 * <p>
 * Threads are queued unless defined thread count has been reached. When this happens
 * the barrier is tripped and all threads are notified. The barrier command gets run
 * only once.
 *
 * @author mrolli
 */
public class SynchronizationBarrier {
    /**
     * Barrier command to run once after the barrier is tripped.
     */
    private final Runnable barrierCommand;

    /**
     * Has the barrier command been run.
     */
    private boolean barrierCommandRun = false;

    /**
     * Is the barrier tripped?
     */
    private boolean tripped = false;

    /**
     * Number of threads to wait for.
     */
    private final int parties;

    /**
     * Number of threads that have reached the barrier.
     */
    private int seen = 0;

    /**
     * Creates a barrier with given number of threads to wait for.
     *
     * @param parties Number of threads to await
     */
    public SynchronizationBarrier(final int parties) {
        this(parties, null);
    }

    /**
     * Creates a barrier with given number of threds to wait for and an
     * additional barrier action to run once the barrier has been tripped.
     *
     * @param parties Number of threads to await
     * @param barrierAction Barrier action after barrier is tripped
     */
    public SynchronizationBarrier(final int parties, final Runnable barrierAction) {
        if (parties <= 0) {
            throw new IllegalArgumentException("Parties must be > 0");
        }
        this.parties = parties;
        this.barrierCommand = barrierAction;
    }

    /**
     * Method to call to enlist a thread at the barrier.
     */
    public synchronized void queue() {
        seen++;
        while (parties > seen && !tripped) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignored
            }
        }
        tripped = true;

        // run barrier command if available
        if (barrierCommand != null && !barrierCommandRun) {
            barrierCommand.run();
            barrierCommandRun = true;
        }
        notify();
    }
}
