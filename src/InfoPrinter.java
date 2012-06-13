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
    protected synchronized void printFinalSummary(final String message) {
        // collect all producer/consumer threads
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
        System.out.print(message);
        notifyAll();
    }
}
