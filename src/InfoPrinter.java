import java.util.PriorityQueue;


public class InfoPrinter {

    private static PriorityQueue<Long> queue;

    private static int queueTripLimit;

    private static boolean queueTripped;

    public synchronized void resetQueue(int capacity) throws InterruptedException {
        while (queue != null && queue.size() > 0) {
            wait();
        }

        queue = new PriorityQueue<Long>(capacity);
        queueTripLimit = capacity;
        queueTripped = false;
        notifyAll();
    }

    /**
     * Aktivitaet #5: Prints out production/consumption data of current thread.
     *
     * @param sumLots
     *            Sum of lots produced/consumed by current thread
     * @param sumProducts
     *            Sum of products produced/consumed by current thread
     */
    protected synchronized void printCurrentData(final long sumLots, final long sumProducts) {
        System.out.printf("%s: %d %d  ", Thread.currentThread().getName(), sumLots, sumProducts);
    }

    /**
     * Aktivitaet #2: Prints out the thread information of current thread.
     */
    protected synchronized void printThreadInformation() {
        // collect all threads
        queue.add(new Long(Thread.currentThread().getId()));
        while (queueTripLimit > queue.size() && !queueTripped) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignored
            }

        }
        queueTripped = true;

        while (Thread.currentThread().getId() != queue.element()) {
            notifyAll();
            try {
                wait();
            } catch (InterruptedException e) {
                // ignored
            }
        }
        queue.remove();
        String format = "\n Name: %-8s    Id: %-4d    Prioritaet: %d    Zustand: %s";
        System.out.printf(format, Thread.currentThread().getName(), Thread.currentThread().getId(),
                Thread.currentThread().getPriority(), Thread.currentThread().getState());
        notifyAll();
    }

    /**
     * Aktivitaet #11: Ausgabe der Thread-Aktivitaeten.
     *
     * @param type The type of action done (Production/Consumption)
     * @param sumLots
     *            Sum of lots produced/consumed by current thread
     * @param sumProducts
     *            Sum of products produced/consumed by current thread
     */
    protected synchronized void printFinalSummary(final String type, final long sumLots,
            final long sumProducts) {
        // collect all threads
        queue.add(new Long(Thread.currentThread().getId()));
        while (queueTripLimit > queue.size() && !queueTripped) {
            try {
                wait();
            } catch(InterruptedException e) {
                // ignored
            }
        }
        queueTripped = true;

        while (Thread.currentThread().getId() != queue.element()) {
            notifyAll();
            try {
                wait();
            } catch(InterruptedException e) {
                // ignored
            }
        }
        queue.remove();
        System.out.printf("\n %-8s    Lose: %-12d    %-12s %d", Thread.currentThread().getName(),
                sumLots, type, sumProducts);
        notifyAll();
    }
}
