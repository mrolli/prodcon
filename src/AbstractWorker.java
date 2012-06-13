import java.util.Random;

/**
 * Abstract base class for all worker threads.
 *
 * @author mrolli
 */
abstract class AbstractWorker {
    /**
     * Random integer generator for lot number.
     */
    private static Random randomLotGenerator;
    static {
        randomLotGenerator = new Random();
    }

    /**
     * The store used by all worker threads.
     */
    private static Store store;

    /**
     * The bookkeeper used by all worker threads.
     */
    private static Bookkeeper bookkeeper;

    /**
     * Barrier to synchronize start production/consumption.
     */
    private static SynchronizationBarrier startBarrier;

    /**
     * Barrier to synchronize end of production/consumption.
     */
    private static SynchronizationBarrier stopBarrier;

    /**
     * Information print to print statistics to system output.
     */
    private static InfoPrinter printer;

    /**
     * @return the store
     */
    public static Store getStore() {
        if (store == null) {
            throw new RuntimeException("No store has been setup!");
        }
        return store;
    }

    /**
     * Set the store used by all worker threads.
     *
     * @param staticStore
     *            Static store
     */
    public static void setStore(final Store staticStore) {
        if (store != null) {
            throw new RuntimeException("A store has already been setup!");
        }
        AbstractWorker.store = staticStore;
    }

    /**
     * @return the bookkeeper
     */
    public static Bookkeeper getBookkeeper() {
        if (bookkeeper == null) {
            throw new RuntimeException("No bookkeeper has been setup!");
        }
        return bookkeeper;
    }

    /**
     * Set the bookkeeper used by all worker threads.
     *
     * @param staticBookkeeper
     *            Static bookkeeper
     */
    public static void setBookkeeper(final Bookkeeper staticBookkeeper) {
        if (bookkeeper != null) {
            throw new RuntimeException("A bookkeeper has already been setup!");
        }
        AbstractWorker.bookkeeper = staticBookkeeper;
    }

    /**
     * Get the start barrier.
     *
     * @return startBarrier
     */
    public static SynchronizationBarrier getStartBarrier() {
        if (startBarrier == null) {
            throw new RuntimeException("No start barrier has been setup!");
        }
        return startBarrier;
    }

    /**
     * Set the start barrier.
     *
     * @param barrier
     *            the startStopBarrier to set
     */
    public static void setStartBarrier(final SynchronizationBarrier barrier) {
        if (AbstractWorker.startBarrier != null) {
            throw new RuntimeException("A start barrier has already been setup!");
        }
        AbstractWorker.startBarrier = barrier;
    }

    /**
     * Get the stop barrier.
     *
     * @return stopBarrier
     */
    public static SynchronizationBarrier getStopBarrier() {
        if (stopBarrier == null) {
            throw new RuntimeException("No stop barrier has been setup!");
        }
        return stopBarrier;
    }

    /**
     * Set the stop barrier.
     *
     * @param barrier
     *            the stopBarrier to set
     */
    public static void setStopBarrier(final SynchronizationBarrier barrier) {
        if (AbstractWorker.stopBarrier != null) {
            throw new RuntimeException("A stop barrier has already been setup!");
        }
        AbstractWorker.stopBarrier = barrier;
    }

    /**
     * Set the information print to use.
     *
     * @param printer Information printer
     */
    public static void setPrinter(final InfoPrinter printer) {
        if (AbstractWorker.printer != null) {
            throw new RuntimeException("An information printer has already been setup!");
        }
        AbstractWorker.printer = printer;
    }

    /**
     * Returns a random lot size within configured range.
     *
     * @param lotMinSize
     *            Minimal lot size
     * @param lotFactor
     *            Maximal number of lots
     * @return Random lot size
     */
    protected int getRandomLotSize(final int lotMinSize, final int lotFactor) {
        return lotMinSize * (randomLotGenerator.nextInt(lotFactor) + 1);
    }

    /**
     * Aktivitaet #5: Proxy method to InfoPrinter instance.
     *
     * @see InfoPrinter.printThradInformation()
     */
    protected void printThreadInformation() {
        printer.printThreadInformation();
    }

    /**
     * Aktivitaet #5: Prints out production/consumption data of current thread.
     *
     * @param sumLots
     *            Sum of lots produced/consumed by current thread
     * @param sumProducts
     *            Sum of products produced/consumed by current thread
     */
    protected synchronized void printCurrentThreadData(final long sumLots, final long sumProducts) {
        System.out.printf("%s: %d %d  ", Thread.currentThread().getName(), sumLots, sumProducts);
    }

    /**
     * Aktivitaet #11: Proxy method to InfoPrinter instance.
     *
     * @see InfoPrinter.printFinalSummary()
     * @param msg The message to print out
     */
    protected void printFinalSummary(final String msg) {
        printer.printFinalSummary(msg);
    }
}

