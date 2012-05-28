import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of the product store.
 * 
 * This store uses a ReentrantLock to synchronize the store requests of
 * producers and consumers and to ensure that the store's current stock is
 * always within its limits.
 * 
 * @author mrolli
 */
public class Store {
    /**
     * Minimal size of store.
     */
    private final int minStoreSize;

    /**
     * Maximal size of store.
     */
    private final int maxStoreSize;

    /**
     * Current number of products in store.
     */
    private int currentStock = 0;

    /**
     * Lock to synchronize access to store.
     */
    private final ReentrantLock storeLock;

    /**
     * Lock condition for store state empty.
     */
    private final Condition notEmpty;

    /**
     * Lock condition for store state full.
     */
    private final Condition notFull;

    /**
     * Initializes a store with given dimensions.
     * 
     * @param minSize
     *            Max. size of store
     * @param maxSize
     *            Min. size of store
     * @param fair
     *            Denotes if producers/consumers should be fairly treated
     */
    public Store(final int minSize, final int maxSize, final boolean fair) {
        minStoreSize = minSize;
        maxStoreSize = maxSize;

        // Setup locking infrastructure within the store.
        storeLock = new ReentrantLock(fair);
        notEmpty = storeLock.newCondition();
        notFull = storeLock.newCondition();
    }

    /**
     * Stores an amount of products in the store if store capacity allows it.
     * 
     * @param num
     *            Amount of products to store
     * @throws InterruptedException
     *             In case a thread in the waiting queue was interrupted
     */
    public void put(final int num) throws InterruptedException {
        storeLock.lock();
        try {
            while (currentStock + num > maxStoreSize) {
                notFull.await();
            }
            currentStock += num;

            // Notify all consumers as we do not know how much the next consumer
            // would like consume.
            notEmpty.signalAll();
        } finally {
            storeLock.unlock();
        }
    }

    /**
     * Returns the requested amount of products from the store if stock.
     * 
     * @param num
     *            Amount of products to request
     * @throws InterruptedException
     *             In case a thread in the waiting queue was interrupted
     */
    public void take(final int num) throws InterruptedException {
        storeLock.lock();
        try {
            while (currentStock - num < minStoreSize) {
                notEmpty.await();
            }
            currentStock -= num;

            // Notify all producers as we do not know how much the next consumer
            // would like consume.
            notFull.signalAll();
        } finally {
            storeLock.unlock();
        }
    }

    /**
     * Returns the current number of products in stock.
     * 
     * @return The number of products in stock
     */
    public int getCurrentStock() {
        storeLock.lock();
        try {
            return currentStock;
        } finally {
            storeLock.unlock();
        }
    }
}
