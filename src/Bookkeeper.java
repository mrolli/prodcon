
/**
 * Simple storage class to account any number of lots and products produced and
 * consumed.
 * <p>
 * Any number of threads may concurrently access the methods of this class
 * because access is synchornized.
 *
 * @author mrolli
 */
public class Bookkeeper {
    /**
     * Total number of lots produced.
     */
    private long lotsProduced = 0;
    /**
     * Total number of lots consumed.
     */
    private long lotsConsumed = 0;
    /**
     * Total number of products produced.
     */
    private long productsProduced = 0;
    /**
     * Total number of products consumed.
     */
    private long productsConsumed = 0;

    /**
     * Total number of products in transfer.
     */
    private long transfer = 0;

    private Object producerLock = new Object();

    private Object consumerLock = new Object();

    private Object transferLock = new Object();

    /**
     * Increases the number of lots produced.
     */
    public void increaseLotsProduces() {
        synchronized (producerLock) {
            lotsProduced++;
        }
    }

    /**
     * Increases the number of lots consumed.
     */
    public void increaseLotsConsumed() {
        synchronized (consumerLock) {
            lotsConsumed++;
        }
    }

    /**
     * Increases the number of products produced.
     */
    public void increaseProductsProduced() {
        synchronized (producerLock) {
            productsProduced++;
        }
    }

    /**
     * Increases the number of products consumed.
     */
    public void increaseProductsConsumed() {
        synchronized (consumerLock) {
            productsConsumed++;
        }
    }

    /**
     * Increases the number of products in transfer.
     *
     * @param deltaTransfer Delta transfer
     */
    public void increaseTransfer(final long deltaTransfer) {
        synchronized (transferLock) {
            transfer += deltaTransfer;
        }
    }

    /**
     * Decrease the number of products in transfer.
     *
     * @param deltaTransfer Delta transfer
     */
    public void decreaseTransfer(final long deltaTransfer) {
        synchronized (transferLock) {
            transfer -= deltaTransfer;
        }
    }

    /**
     * Returns the total amount of lots produced.
     *
     * @return Number of lots produced
     */
    public long getLotsProduced() {
        synchronized (producerLock) {
            synchronized (consumerLock) {
                synchronized (transferLock) {
                    return lotsProduced;
                }
            }
        }
    }

    /**
     * Returns the total amount of lots consumed.
     *
     * @return Number of lots consumed
     */
    public long getLotsConsumed() {
        synchronized (producerLock) {
            synchronized (consumerLock) {
                synchronized (transferLock) {
                    return lotsConsumed;
                }
            }
        }
    }

    /**
     * Returns the total amount of products produced.
     *
     * @return Number of products produced
     */
    public long getProductsProduced() {
        synchronized (producerLock) {
            synchronized (consumerLock) {
                synchronized (transferLock) {
                    return productsProduced;
                }
            }
        }
    }

    /**
     * Returns the total amount of products consumed.
     *
     * @return Number of products consumed
     */
    public long getProductsConsumed() {
        synchronized (producerLock) {
            synchronized (consumerLock) {
                synchronized (transferLock) {
                    return productsConsumed;
                }
            }
        }
    }

    /**
     * Returns the total amount of products in transfer.
     *
     * @return Number of products in transfer
     */
    public synchronized long getTransfer() {
        synchronized (producerLock) {
            synchronized (consumerLock) {
                synchronized (transferLock) {
                    return transfer;
                }
            }
        }
    }
}
