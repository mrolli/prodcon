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

    private int currentLot = 0;

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
        getStartBarrier().queue();

        while (!Thread.currentThread().isInterrupted()) {

            if (currentLot == 0) {
                int lotSize = getRandomLotSize(lotMinSize, lotFactor);

                // Produce a lot
                for (int i = 0; i < lotSize; i++) {
                    currentLot++;
                    sumProductsProduced++;
                    getBookkeeper().increaseProductsProduced();
                }
                sumLotsProduced++;
                getBookkeeper().increaseLotsProduces();
                getBookkeeper().increaseTransfer(currentLot);

                // Aktivitaet #5: Ausgabe der aktuellen Konsumations-Daten
                printCurrentThreadData(sumLotsProduced, sumProductsProduced);
            }

            try {
                visitInspector();

                // Put into store
                synchronized (this) {
                    if (!runningInspection()) {
                        getStore().put(currentLot);
                        getBookkeeper().decreaseTransfer(currentLot);
                        currentLot = 0;
                    }
                }
            } catch (InterruptedException e) {
                if (!runningInspection()) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Aktivitaet #9: Synchronisation für Produktionsende
        getStopBarrier().queue();

        // Aktivitaet #11: Ausgabe der Thread-Aktivitaeten
        String msg = String.format("\n %-8s\tLose: %-15d\tProduktion:  %-15d\t Transfer: %-15d", Thread
                .currentThread().getName(), sumLotsProduced, sumProductsProduced, currentLot);
        printFinalSummary(msg);

    }
}
