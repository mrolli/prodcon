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

        // Aktivitaet #3: Synchronisation für Produktionsstart
        getStartBarrier().queueMe();

        while (!Thread.currentThread().isInterrupted()) {
            int lotSize = getRandomLotSize(lotMinSize, lotFactor);

            // Produce a lot
            for (int i = 0; i < lotSize; i++) {
                sumProductsProduced++;
                getBookkeeper().increaseProductsProduced();
            }
            sumLotsProduced++;
            getBookkeeper().increaseLotsProduces();
            getBookkeeper().increaseTransfer(lotSize);

            // Aktivitaet #5: Ausgabe der aktuellen Konsumations-Daten
            printCurrentData(sumLotsProduced, sumProductsProduced);

            // Put into store
            try {
                getStore().put(lotSize);
                getBookkeeper().decreaseTransfer(lotSize);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Aktivitaet #9: Synchronisation für Produktionsende
        getStopBarrier().queueMe();

        // Aktivität #11:  Ausgabe der Thread-Aktivitaeten
        printFinalSummary("Produktion:", sumLotsProduced, sumProductsProduced);

    }
}
