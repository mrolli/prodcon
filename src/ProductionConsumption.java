
/*
 * Selbstdeklaration Lösungsanteile
 * --------------------------------
 * Pflichtanteil 50%: ja/nein
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
public class ProductionConsumption {
    /**
     * Aktiviert die Applikation <i>ProductionConsumption</i>. Als Ausgangsbasis
     * werden die definierten Parameter ausgegeben.
     * <p>
     * Normalerweise wird die Methode erweitert, indem die weitere Funktionalität
     * (z.B. aktivieren von Threads) implementiert wird.
     * 
     * @param args
     *           Argumente die beim Starten der Applikation
     *           <i>ProductionConsumption</i> definiert wurden.
     */

    public static void main(String[] args) {
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

        // Priorität main-Thread festlegen
        Thread.currentThread().setPriority(data.getMainPriority());

        // Aktivitaet #1 Ausgabe Data (Parameter von Properties-File, Argumente)
        data.printData();

        // Generate the store according properites definitions.
        Store store = new Store(data.getMinimalStoreSize(), data.getMaximalStoreSize(),
                data.getMonitorStoreIsFair());


        String format = "Name: %s\t Id: %d\t Prioritaet: %d\t Zustand: %s\n";
        System.out.printf(format, Thread.currentThread().getName(), Thread.currentThread().getId(),
                Thread.currentThread().getPriority(), Thread.currentThread().getState());

        // generate producers; Aktivitaet #2
        for (int i = 1; i <= data.getNumberOfProducers(); i++) {
            Thread t = generateProducer(i, data, store);
            t.start();
        }

        // generate consumers; Aktivitaet #2
        for (int i = 1; i <= data.getNumberOfConsumers(); i++) {
            Thread t = generateConsumer(i, data, store);
            t.start();
        }
    }

    /**
     * Generates and returns a producer thread; Aktivitaet #2.
     * 
     * @param num
     *            Thread number
     * @param data
     *            Properties definitions to aquire producer properties from
     * @param store
     *            Target Store object to use in the producer
     * @return New generated and configured producer thread
     */
    private static Thread generateProducer(final int num, final Data data, final Store store) {
        Producer p = new Producer(store, data.getLotDefaultProduction(),
                data.getLotFactorProduction());

        Thread t = new Thread(p, data.getBaseNameOfProducers() + num);
        t.setPriority(5);
        return t;
    }

    /**
     * Generates and returns a consumer thread; Aktivitaet #2.
     * 
     * @param num
     *            Thread number
     * @param data
     *            Properties definitions to aquire consumer properties from
     * @param store
     *            Target Store object to use in the consumer
     * @return New generated and configured consumer thread
     */
    private static Thread generateConsumer(final int num, final Data data, final Store store) {
        Consumer c = new Consumer(store, data.getLotDefaultConsumption(),
                data.getLotFactorConsumption());

        Thread t = new Thread(c, data.getBaseNameOfConsumers() + num);
        t.setPriority(5);
        return t;
    }
}