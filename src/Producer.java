/**
 * @author mrolli
 *
 */
public class Producer extends AbstractWorker implements Runnable {
    /**
     * Size of one lot.
     */
    private final int lotMinSize;

    /**
     * Maximal number of lots to produce at once.
     */
    private final int lotFactor;

    /**
     * Number of products in transfer.
     */
    private int transfer = 0;

    /**
     * Total number of lots produced.
     */
    private long sumLotsProduced = 0;

    /**
     * Total number of products produced.
     */
    private long sumProductsProduced = 0;

    /**
     * Initializes a new producer runnable.
     * <p>
     * The producer has a dependency to a store where it delivers the products
     * it produces.
     * 
     * @param lotDefaultSize
     *            Default size of one lot
     * @param lotMaxNum
     *            Maximal number of lots to produce at once
     */
    public Producer(final int lotDefaultSize, final int lotMaxNum) {
        lotMinSize = lotDefaultSize;
        lotFactor = lotMaxNum;
    }

    @Override
    public void run() {
        // Aktivitaet #2: Ausgabe der Thread-Daten
        printThreadInformation();

        while (!Thread.currentThread().isInterrupted()) {
            int lotSize = getRandomLotSize(lotMinSize, lotFactor);

            // Produce a lot
            for (int i = 0; i < lotSize; i++) {
                sumProductsProduced++;
                getBookkeeper().increaseProductsProduced();
            }
            transfer = lotSize;
            sumLotsProduced++;
            getBookkeeper().increaseLotsProduces();

            // Aktivitaet #5: Ausgabe der aktuellen Konsumations-Daten
            printCurrentData(sumLotsProduced, sumProductsProduced);

            // Put into store
            try {
                getStore().put(transfer);
                transfer = 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Aktivitaet #11: Ausgabe der Thread-Aktivitaeten
        printFinalSummary(sumLotsProduced, sumProductsProduced);
    }

    /**
     * Returns the number of products in transfer.
     * 
     * @return The number of transfer products
     */
    public int getCurrentTransfer() {
        return transfer;
    }
}
