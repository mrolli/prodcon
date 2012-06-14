
public class Inspector extends AbstractWorker implements Runnable {
    private int cycleTime;
    private int inspections = 0;

    public Inspector(int cycleTime) {
        this.cycleTime = cycleTime;

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
                Bookkeeper bk = getBookkeeper();
                Store st = getStore();
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
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Aktivitaet #9: Synchronisation für Produktionsende
        getStopBarrier().queue();

        // Aktivitaet #11: Ausgabe der Thread-Aktivitaeten
        String msg = String.format("\n %-8s\tInspektionen: %d", getName(), getInspections());
        printFinalSummary(msg);
    }

    public String getName() {
        return Thread.currentThread().getName();
    }

    public int getInspections() {
        return inspections;
    }
}
