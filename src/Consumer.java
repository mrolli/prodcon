import java.util.Random;

/**
 * @author mrolli
 *
 */
public class Consumer implements Runnable {
    /**
     * Random integer generator for lot number.
     */
    private static Random randomLotGenerator;
    static {
        randomLotGenerator = new Random();
    }

    /**
     * The store the consumer uses to retrieve the products to consume.
     */
    private final Store store;

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
     * @param targetStore
     *            The store to deliver to
     * @param lotDefaultSize
     *            Default size of one lot
     * @param lotMaxNum
     *            Maximal number of lots to consume at once
     */
    public Consumer(final Store targetStore, final int lotDefaultSize, final int lotMaxNum) {
        store = targetStore;
        lotMinSize = lotDefaultSize;
        lotFactor = lotMaxNum;
    }

    @Override
    public void run() {
        printThread();

        while (!Thread.currentThread().isInterrupted()) {
            int lotSize = getRandomLotSize();
            int currentLot = 0;
            boolean lotRetrieved = false;

            // Retrieve from store
            try {
                currentLot = store.take(lotSize);
                lotRetrieved = true;
            } catch (InterruptedException e) {
                Thread.interrupted();
            }

            if (lotRetrieved) {
                // Consume a lot
                for (int i = 0; i < lotSize; i++) {
                    currentLot--;
                    sumProductsConsumed++;
                    // @TODO increase global product consumption counter
                }
                sumLotsConsumed++;
                // @TODO increase global lot consumption counter
            }

            System.out.printf("%s: %d %d  ", Thread.currentThread().getName(), sumLotsConsumed,
                    sumProductsConsumed);
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
