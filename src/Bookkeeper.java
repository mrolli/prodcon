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

    /**
     * Increases the number of lots produced.
     */
    public synchronized void increaseLotsProduces() {
        lotsProduced++;
    }

    /**
     * Increases the number of lots consumed.
     */
    public synchronized void increaseLotsConsumed() {
        lotsConsumed++;
    }

    /**
     * Increases the number of products produced.
     */
    public synchronized void increaseProductsProduced() {
        productsProduced++;
    }

    /**
     * Increases the number of products consumed.
     */
    public synchronized void increaseProductsConsumed() {
        productsConsumed++;
    }

    /**
     * Increases the number of products in transfer.
     *
     * @param deltaTransfer Delta transfer
     */
    public synchronized void increaseTransfer(final long deltaTransfer) {
        transfer += deltaTransfer;
    }

    /**
     * Decrease the number of products in transfer.
     *
     * @param deltaTransfer Delta transfer
     */
    public synchronized void decreaseTransfer(final long deltaTransfer) {
        transfer -= deltaTransfer;
    }

    /**
     * Returns the total amount of lots produced.
     *
     * @return Number of lots produced
     */
    public synchronized long getLotsProduced() {
        return lotsProduced;
    }

    /**
     * Returns the total amount of lots consumed.
     *
     * @return Number of lots consumed
     */
    public synchronized long getLotsConsumed() {
        return lotsConsumed;
    }

    /**
     * Returns the total amount of products produced.
     *
     * @return Number of products produced
     */
    public synchronized long getProductsProduced() {
        return productsProduced;
    }

    /**
     * Returns the total amount of products consumed.
     *
     * @return Number of products consumed
     */
    public synchronized long getProductsConsumed() {
        return productsConsumed;
    }

    /**
     * Returns the total amount of products in transfer.
     *
     * @return Number of products in transfer
     */
    public synchronized long getTransfer() {
        return transfer;
    }
}
