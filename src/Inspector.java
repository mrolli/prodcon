import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Inspector extends AbstractWorker implements Runnable {
    private final int cycleTime;
    private final ArrayList<Thread> workers;
    private volatile int expectedWorkers = 0;
    private int inspections = 0;
    private volatile boolean runningInspection = false;

    private final ReentrantLock lock;
    private final Condition workerBarrier;
    private final Condition inspectorLock;

    public Inspector(final int cycleTime, ArrayList<Thread> parties) {
        this.cycleTime = cycleTime;
        this.workers = parties;
        lock = new ReentrantLock();
        workerBarrier = lock.newCondition();
        inspectorLock = lock.newCondition();
    }

    @Override
    public void run() {
        // Aktivitaet #2: Ausgabe der Thread-Daten
        printThreadInformation();

        // Aktivitaet #3: Synchronisation für Produktionsstart
        getStartBarrier().queue();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(cycleTime);
                lock.lock();
                try {
                    init();
                    runningInspection = true;

                    // Wartende Threads (z.B. im Store) interrupten
                    for (Thread t : workers) {
                        if (t.getId() != Thread.currentThread().getId()) {
                            t.interrupt();
                        }
                    }

                    while (expectedWorkers > 1) {
                        inspectorLock.await();
                    }

                    Bookkeeper bk = getBookkeeper();
                    Store st = getStore();
                    // Aktivitaet #6: Ausgabe der Inspektion
                    String pattern = "\n\n **** %s: Lose produziert: %d Lose konsumiert: %d "
                        + "Produktion: %d Konsumation: %d Transfer: %d Lager: %d ****  \n\n";
                    System.out.printf(pattern, getName(),
                        bk.getLotsProduced(),
                        bk.getLotsConsumed(),
                        bk.getProductsProduced(),
                        bk.getProductsConsumed(),
                        bk.getTransfer(),
                        st.getCurrentStock());
                    inspections++;

                    runningInspection = false;
                    workerBarrier.signalAll();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                runningInspection = false;
                Thread.currentThread().interrupt();
            }
        }

        // Aktivitaet #9: Synchronisation für Produktionsende
        getStopBarrier().queue();

        // Aktivitaet #11: Ausgabe der Thread-Aktivitaeten
        String msg = String.format("\n %-8s\tInspektionen: %d", getName(), getInspections());
        printFinalSummary(msg);
    }

    public void visit() throws InterruptedException {
        if (!runningInspection) {
            return;
        }
        lock.lock();
        try {
            // ein weiterer Worker ist eingetroffen
            expectedWorkers--;
            // Inspector informieren
            inspectorLock.signal();
            while (runningInspection) {
                try {
                    workerBarrier.await();
                } catch (InterruptedException ie) {
                    if (isShuttingDown()) {
                        throw ie;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isInspecting() {
        return runningInspection;
    }

    public String getName() {
        return Thread.currentThread().getName();
    }

    public int getInspections() {
        return inspections;
    }

    private void init() {
        // -1 wegen Inspektor selbst
        expectedWorkers = workers.size() - 1;
    }
}
