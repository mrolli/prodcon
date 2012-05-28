
/**
 * @author mrolli
 *
 */
public class Consumer extends AbstractWorker implements Runnable {
    /**
     * Size of one lot.
     */
    private final int lotMinSize;

    /**
     * Maximal number of lots to consume at once.
     */
    private final int lotFactor;

    /**
     * Total number of lots produced.
     */
    private long sumLotsConsumed = 0;

    /**
     * Total number of products produced.
     */
    private long sumProductsConsumed = 0;

    /**
     * Initializes a new consumer runnable.
     * <p>
     * The consumer has a dependency to a store where it retrieves the products
     * it consumes.
     *
     * @param lotDefaultSize
     *            Default size of one lot
     * @param lotMaxNum
     *            Maximal number of lots to consume at once
     */
    public Consumer(final int lotDefaultSize,
            final int lotMaxNum) {
        lotMinSize = lotDefaultSize;
        lotFactor = lotMaxNum;
    }

    @Override
    public void run() {
        // Aktivitaet #2: Ausgabe der Thread-Daten
        printThreadInformation();

        // Aktivitaet #3: Synchronisation für Produktionsstart
        getStartBarrier().queueMe();

        while (!Thread.currentThread().isInterrupted()) {
            int lotSize = getRandomLotSize(lotMinSize, lotFactor);

            // Retrieve from store
            try {
                getStore().take(lotSize);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (Thread.currentThread().isInterrupted()) {
                break;
            }

            // Consume the lot
            sumLotsConsumed++;
            getBookkeeper().increaseLotsConsumed();
            for (int i = 0; i < lotSize; i++) {
                sumProductsConsumed++;
                getBookkeeper().increaseProductsConsumed();
            }

            // Aktivitaet #5: Ausgabe der aktuellen Konsumations-Daten
            printCurrentData(sumLotsConsumed, sumProductsConsumed);

        }

        // Aktivitaet #9: Synchronisation für Konsumationsende
        getStopBarrier().queueMe();

        // Aktivitaet #11: Ausgabe der Thread-Aktivitaeten
        printFinalSummary(sumLotsConsumed, sumProductsConsumed);
    }
}
