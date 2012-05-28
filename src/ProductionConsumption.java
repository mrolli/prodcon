import java.util.ArrayList;

/*
 * Selbstdeklaration Lösungsanteile
 * --------------------------------
 * Pflichtanteil 50%: ja
 *
 * Fehler:
 *
 *
 * Erweiterungen:
 *
 * Prioritäten 5%: ja/nein
 * Synchr. Ausgabe Thread-Daten #2, 11 20%: ja/nein
 * Threads Start/Ende synchr. #3, 9 20%: ja/nein
 *
 * Fehler:
 *
 *
 * Inspektor-Thread: ja/nein
 * Aktivität #2,6,11 10%: ja/nein
 * Aktiv wenn Lose abgeschlossen #6 10%: ja/nein
 *
 * Fehler:
 *
 *
 * Erwarteter Lösungsanteil: xx%
 */

/**
 * BFH-TI, SWS CAS DS05, ParSynch (Frühling 2012)<br>
 * Java Concurrency Übung <i>ProductionConsumption</i>
 * <p>
 * Die Klasse <i>ProductionConsumption</i> startet mit der <i>main()</i>-Methode
 * die Applikation <i>ProductionConsumption</i>.
 * <p>
 * Die Klasse ist durch die Studierenden entsprechend der Aufgabenstellung zu
 * erweitern.
 *
 * @author hans.roethlisberger@bfh.ch
 * @version V15.04.2012
 */
public final class ProductionConsumption {
    /**
     * Aktiviert die Applikation <i>ProductionConsumption</i>. Als Ausgangsbasis
     * werden die definierten Parameter ausgegeben.
     * <p>
     * Normalerweise wird die Methode erweitert, indem die weitere
     * Funktionalität (z.B. aktivieren von Threads) implementiert wird.
     *
     * @param args
     *            Argumente die beim Starten der Applikation
     *            <i>ProductionConsumption</i> definiert wurden.
     * @throws InterruptedException
     *             In case main thrad was interrupted
     */
    public static void main(final String[] args) throws InterruptedException {
        // Einlesen Properties-Daten
        final Data data;
        // Name von Properties-File als Argument
        String nameOfPropertiesFile = "";

        // Argument könnte Name für Properties-File sein
        if (args.length > 0) {
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // Wenn Arg. ein String ist, als Filename interpretieren
                nameOfPropertiesFile = args[0];
            }
        }

        // Objekt erzeugen, Properties-Definitionen sowie Argumente prüfen
        data = new Data(nameOfPropertiesFile, args);

        // Prioritaet main-Thread festlegen
        Thread.currentThread().setPriority(data.getMainPriority());

        // Aktivitaet #1: Ausgabe Data (Parameter von Properties-File,
        // Argumente)
        data.printData();

        // Generate the store according properites definitions.
        Store store = new Store(data.getMinimalStoreSize(), data.getMaximalStoreSize(),
                data.getMonitorStoreIsFair());
        AbstractWorker.setStore(store);

        // Generate the global bookkeeper object
        AbstractWorker.setBookkeeper(new Bookkeeper());
        // Generate the global information printer
        AbstractWorker.setPrinter(new InfoPrinter());

        // Thread handling starten und Produktions/Konsumationsstart und -Ende
        // synchronisieren
        int totalNumOfThreads = data.getNumberOfProducers() + data.getNumberOfConsumers();
        if (!data.getNameOfInspector().equals("none")) {
            totalNumOfThreads++;
        }
        AbstractWorker.setStartBarrier(new SynchronizationBarrier(totalNumOfThreads + 1,
                new Runnable() {
                    @Override
                    public void run() {
                        // Aktivitaet #4: Ausgabe von PRODUKTION/KONSUMATION
                        // WIRD GESTARTET
                        System.out.print("\n\n\n PRODUKTION/KONSUMATION WIRD GESTARTET !!!!\n\n");
                    }
                }));

        AbstractWorker.setStopBarrier(new SynchronizationBarrier(totalNumOfThreads + 1,
                new Runnable() {
                    @Override
                    public void run() {
                        // Aktivitaet #10: Ausgabe von PRODUKTION/KONSUMATION WURDE
                        // GESTOPPT
                        System.out.print("\n\n\n PRODUKTION/KONSUMATION WURDE GESTOPPT!!!!\n");
                    }
                }));
        //AbstractWorker.setStartSortBarrier(new SortingBarrier(totalNumOfThreads + 1));
        //AbstractWorker.setEndSortBarrier(new SortingBarrier(totalNumOfThreads));
        AbstractWorker.getPrinter().resetQueue(totalNumOfThreads + 1);

        /* Threads erzeugen */
        ArrayList<Thread> myThreads = new ArrayList<Thread>(totalNumOfThreads);
        // Produzenten werden für Transfersummen unten weider benötigt
        ArrayList<Producer> myProducers = new ArrayList<Producer>(data.getNumberOfProducers());
        // Produzenten-Threads erzeugen und speichern
        for (int i = 1; i <= data.getNumberOfProducers(); i++) {
            Producer p = new Producer(data.getLotDefaultProduction(),
                    data.getLotFactorProduction());
            myProducers.add(p);

            Thread t = new Thread(p, data.getBaseNameOfProducers() + i);
            myThreads.add(t);
            t.setPriority(5);
            t.start();
        }

        // Konsumenten-Threads erzeugen und speichern
        for (int i = 1; i <= data.getNumberOfConsumers(); i++) {
            Consumer c = new Consumer(data.getLotDefaultConsumption(),
                    data.getLotFactorConsumption());

            Thread t = new Thread(c, data.getBaseNameOfConsumers() + i);
            myThreads.add(t);
            t.setPriority(5);
            t.start();
        }


        // Aktivitaet #2: Ausgabe der Thread-Daten des main-Thread
        System.out.println("\n Threads:");
        AbstractWorker.getPrinter().printThreadInformation();
        AbstractWorker.getStartBarrier().queueMe();

        // queue zurücksetzen
        AbstractWorker.getPrinter().resetQueue(totalNumOfThreads);

        Thread.sleep(data.getTimeToRun() * 1000);

        // Aktivitaet #7: Alle Thread werden interrupted
        System.out.printf("\n\n %s: Alle Threads werden nun interrupted!!\n", Thread
                .currentThread().getName());

        // Threads terminieren und synchronisieren
        for (Thread t : myThreads) {
            if (t.isAlive()) {
                t.interrupt();
            }
        }

        // Aktivitaet #8: Alle threads sind interrupted
        System.out.printf("\n\n %s: Alle Threads interrupted!!\n\n", Thread.currentThread()
                .getName());

        // Aktivitaet #10: Ausgabe von PRODUKTION/KONSUMATION WURDE GESTOPPT
        // main-Methode löst als letzter nun aus.
        AbstractWorker.getStopBarrier().queueMe();

        // Threads terminieren und synchronisieren
        for (Thread t : myThreads) {
            if (t.isAlive()) {
                t.join();
            }
        }

        // Aktivitaet #12: Ausgabe der abschliessenden Statistik
        Bookkeeper bookkeeper = AbstractWorker.getBookkeeper();
        long transfer = 0;
        for (Producer p : myProducers) {
            transfer += p.getCurrentTransfer();
        }
        System.out.printf("\n\n %s (Summen):", Thread.currentThread().getName());
        System.out.printf("\n Lose produziert: %d", bookkeeper.getLotsProduced());
        System.out.printf("\n Lose konsumiert: %d", bookkeeper.getLotsConsumed());
        System.out.printf("\n Produktion: %d", bookkeeper.getProductsProduced());
        System.out.printf("\n Konsumation: %d", bookkeeper.getProductsConsumed());
        System.out.printf("\n In Auslieferung an Lager (Transfer): %d", transfer);
        System.out.printf("\n Lagerbestand: %d", store.getCurrentStock());
        System.out.printf("\n Anzahl Inspektionen: %d", 0);
        System.out.println();
        System.out.println();
    }

    /**
     * Private constructor; forbid class instantiation.
     */
    private ProductionConsumption() {
    }
}
