import java.util.Random;

/**
 * @author mrolli
 *
 */
public class Producer extends Thread {
    /**
     * Random integer generator for lot number.
     */
    private static Random randomLotGenerator;
    static {
        randomLotGenerator = new Random();
    }

    /**
     * The store the producer uses to deliver its produced products.
     */
    private final Store store;

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
     * @param targetStore
     *            The store to deliver to
     * @param lotDefaultSize
     *            Default size of one lot
     * @param lotMaxNum
     *            Maximal number of lots to produce at once
     */
    public Producer(final Store targetStore, final int lotDefaultSize, final int lotMaxNum) {
        store = targetStore;
        lotMinSize = lotDefaultSize;
        lotFactor = lotMaxNum;
    }

    @Override
    public void run() {
        printThread();

        while (!Thread.currentThread().isInterrupted()) {
            int currentLot = 0;
            int lotSize = getRandomLotSize();

            // Produce a lot
            for (int i = 0; i < lotSize; i++) {
                currentLot++;
                sumProductsProduced++;
                // @TODO increase global product production counter
            }
            sumLotsProduced++;
            // @TODO increase global lot production counter

            System.out.printf("%s: %d %d  ", Thread.currentThread().getName(), sumLotsProduced,
                    sumProductsProduced);

            // Put into store
            try {
                transfer = currentLot;
                store.put(transfer);
                transfer = 0;
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Prints out thread information.
     */
    public void printThread() {
        String format = "Name: %s\t Id: %d\t Prioritaet: %d\t Zustand: %s\n";
        System.out.printf(format, Thread.currentThread().getName(), Thread.currentThread().getId(),
                Thread.currentThread().getPriority(), Thread.currentThread().getState());
    }

    /**
     * Returns a random lot size within configured range.
     * 
     * @return Random lot size
     */
    private int getRandomLotSize() {
        return lotMinSize * (randomLotGenerator.nextInt(lotFactor) + 1);
    }
}
