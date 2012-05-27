import java.io.*;
import java.util.*;

/**
 * BFH-TI, SWS CAS DS05, ParSynch (Frühling 2012)<br>
 * Java Concurrency Übung <i>ProductionConsumption</i>
 * <p>
 * Die Klasse <i>DataFundamentals</i> liest und validiert die Argumente sowie
 * die Properties-Definitionen.
 * <p>
 * Die Klasse sollte nicht verändert werden. Anpassungen/Erweiterungen können in
 * der Klasse <i>Data</i> vorgenommen werden.
 * 
 * @author hans.roethlisberger@bfh.ch
 * @version V15.04.2012
 * 
 */
public class DataFundamentals {
	// Default-Properties-File mit den Initialisierungsparametern, kann mit
	// Argument beim Start überschrieben werden
	private String nameOfPropFile = "Data.properties";
	// Für Definition der Keys für Properties-File
	private String[] propertiesKeys = new String[16];
	// Start der Verarbeitung der Argumente
	private int startOfArgs = 0;
	// Default-Anz. mögl. Eingabe-Arg. beim Start der Applikation (Kommandozeile)
	private int acceptedArgs = 3;
	// Max. Anz. von Prod.-Konsum.-Threads (additiv main- und Inspektor-Thread)
	private final int numberOfMaxProdCons = 2000;
	// Variablen die über Properties-Definitionen initialisiert werden
	// Gesamtproduktionszeit, kann mit Argument beim Start überschrieben werden
	private int timeToRun;
	// Anz. Produzenten-Threads, kann mit Arg. beim Start überschrieben werden
	private int numberOfProducers;
	// Anzahl Konsumenten-Threads, kann mit Arg. beim Start überschrieben werden
	private int numberOfConsumers;
	// Priorität für main-Thread, Default-Priorität: 5
	private int mainPriority;
	// Priorität für Inspektor-Thread
	private int inspectorPriority;
	// Intervall Inspektor, Wartezeit bis nächste Inspektion stattfindet
	private int cycleTimeInspector;
	// Minimale Grösse des Lagers
	private int minimalStoreSize;
	// Maximale Grösse des Lagers
	private int maximalStoreSize;
	// Basislosgrösse für Produzenten
	private int lotDefaultProduction;
	// Max. Multiplikator für Losgrösse Prod. (Parameter für random.nextInt()),
	// 1 ergibt fixe Losgrösse mit lotDefaultProduction
	private int lotFactorProduction;
	// Basislosgrösse für Konsumenten
	private int lotDefaultConsumption;
	// Max. Multiplikator für Losgrösse Kons. (Parameter für random.nextInt()),
	// 1 ergibt fixe Losgrösse mit lotDefaultConsumption
	private int lotFactorConsumption;
	// Name des Inspektors
	private String nameOfInspector;
	// Basisname der Produzenten, additiv: Laufnummer
	private String baseNameOfProducers;
	// Basisname der Konsumenten, additiv: Laufnummer
	private String baseNameOfConsumers;
	// Monitor für den Schutz des Lagers fair (true), resp. nicht fair (false)
	private boolean monitorStoreIsFair;

	/**
	 * Konstruiert ein <i>DataFundamentals</i> Objekt.
	 * 
	 * @param nameOfPropertiesFile
	 *           Filename zum Lesen der Properties-Daten.
	 * @param args
	 *           Argumente die beim Starten der Applikation
	 *           <i>ProductionConsumption</i> definiert wurden.
	 */
	DataFundamentals(String nameOfPropertiesFile, String[] args) {
		// Def. der Keys für Prop.-File, Namen der Keys entspr. den Variablennamen
		propertiesKeys[0] = "timeToRun";
		propertiesKeys[1] = "numberOfProducers";
		propertiesKeys[2] = "numberOfConsumers";
		propertiesKeys[3] = "mainPriority";
		propertiesKeys[4] = "inspectorPriority";
		propertiesKeys[5] = "cycleTimeInspector";
		propertiesKeys[6] = "minimalStoreSize";
		propertiesKeys[7] = "maximalStoreSize";
		propertiesKeys[8] = "lotDefaultProduction";
		propertiesKeys[9] = "lotFactorProduction";
		propertiesKeys[10] = "lotDefaultConsumption";
		propertiesKeys[11] = "lotFactorConsumption";
		propertiesKeys[12] = "nameOfInspector";
		propertiesKeys[13] = "baseNameOfProducers";
		propertiesKeys[14] = "baseNameOfConsumers";
		propertiesKeys[15] = "monitorStoreIsFair";

		// Properties-File laden
		if (!nameOfPropertiesFile.equals("")) { // Prop.-File nicht als Arg. def.?
			// Properties-File gemaess Default definieren
			this.nameOfPropFile = nameOfPropertiesFile;
			// Korrektur Index der Argumente (Prop.-File als Argument definiert)
			startOfArgs = 1;
			acceptedArgs++; // Korrektur Anzahl zulässiger Argumente
		}

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(this.nameOfPropFile));
		} catch (IOException ex) {
			if (startOfArgs == 0) {
				printErrorMessages(1, 2); // Default Properties-File nicht gefunden
				System.exit(1);
			} else { // Properties File gemäss args[0] nicht gefunden
				printErrorMessages(1, 3);
				System.exit(1);
			}
		}
		// Einlesen und validieren der Properties-Definitionen
		getAndCheckProperties(properties);
		// Logische Prüfung der Definitionen und Konsolenargumente
		verifyingParameters(args);
	}

	/**
	 * Einlesen und validieren der Properties-Definitionen. Prüfen, ob alle Keys
	 * und die dazugehörigen Argumente definiert sind.
	 * 
	 * @param properties
	 *           Definierte Properties.
	 */
	private void getAndCheckProperties(Properties properties) {
		String key = null; // Für Abarb. Properties-Keys, zu bearbeitender Key
		// Einl. und prüfen der Prop. int-Definitionen (Datentyp, Properties-Keys)
		try {
			timeToRun = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[0]));
			numberOfProducers = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[1]));
			numberOfConsumers = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[2]));
			mainPriority = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[3]));
			inspectorPriority = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[4]));
			cycleTimeInspector = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[5]));
			minimalStoreSize = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[6]));
			maximalStoreSize = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[7]));
			lotDefaultProduction = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[8]));
			lotFactorProduction = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[9]));
			lotDefaultConsumption = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[10]));
			lotFactorConsumption = Integer.parseInt(properties
					.getProperty(key = propertiesKeys[11]));
		} catch (NumberFormatException ex) {
			printErrorMessages(1, 4); // Unzulässige Definition in Properties-File
			printErrorMessages(key, 5); // Key kann nicht definiert werden
			printErrorMessages(6);
			System.exit(1);
		}

		// Einlesen und prüfen der Prop. String-Definitionen (Properties-Keys)
		nameOfInspector = properties.getProperty(propertiesKeys[12]);
		baseNameOfProducers = properties.getProperty(propertiesKeys[13]);
		baseNameOfConsumers = properties.getProperty(propertiesKeys[14]);
		key = null; // Default für Prüfung der String-Definitionen zurücksetzen

		if (nameOfInspector == null || nameOfInspector.equals(""))
			key = propertiesKeys[12];
		else if (baseNameOfProducers == null || baseNameOfProducers.equals(""))
			key = propertiesKeys[13];
		else if (baseNameOfConsumers == null || baseNameOfConsumers.equals(""))
			key = propertiesKeys[14];

		if (key != null) {
			printErrorMessages(1, 4); // Unzulässige Definition in Properties-File
			printErrorMessages(key, 5); // Key kann nicht definiert werden
			printErrorMessages(7);
			System.exit(1);
		}

		// Einlesen und prüfen der Properties Boolean-Definition (Properties-Key,
		// Argumente false/true) als Lower-Case String
		String queueOrderMonitorStore = properties
				.getProperty(propertiesKeys[15]).toLowerCase();

		if (queueOrderMonitorStore == null
				|| (!queueOrderMonitorStore.equals("false") && !queueOrderMonitorStore
						.equals("true"))) {
			printErrorMessages(1, 4); // Unzulässige Definition in Properties-File
			// Key kann nicht definiert werden
			printErrorMessages(propertiesKeys[15], 5);
			printErrorMessages(8);
			System.exit(1);
		} else
			monitorStoreIsFair = Boolean.parseBoolean(queueOrderMonitorStore);
	}

	/**
	 * Minimale logische Prüfung der Definitionen (Properties-File und Argumente
	 * der Kommandozeile).
	 * 
	 * @param args
	 *           Argumente die beim Starten der Applikation
	 *           <i>ProductionConsumption</i> definiert wurden.
	 */
	private void verifyingParameters(String[] args) {
		Scanner scanner = new Scanner(System.in); // Tastatureingabe
		// Validierung der Eingabeargumente (Anzahl, int), ablegen in Variablen
		if (args.length > acceptedArgs) {
			printErrorMessages(1, 9); // Zu viele Argumente eingegeben
			System.exit(1);
		}

		int argument = 0; // Zwischenablage der Argumente
		if (args.length > startOfArgs) {
			// Akzeptierte Anzahl Argumente eingegeben
			for (int i = startOfArgs; i < args.length; i++) {
				try {
					argument = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					printErrorMessages(1, 9, 10); // Argumente sind nicht vom Typ int
					System.exit(1);
				}
				switch (i - startOfArgs) { // Argumente ablegen
				case 0:
					timeToRun = argument;
					break;
				case 1:
					numberOfProducers = argument;
					break;
				case 2:
					numberOfConsumers = argument;
					break;
				}
			}
		}

		// Minimale Prüfung der Parameter
		if (timeToRun <= 0) {
			printErrorMessages(1, 11); // Arbeitszeit muss > 0 sein
			System.exit(1);
		}
		if (numberOfProducers <= 0) {
			printErrorMessages(1, 12); // Anzahl Produzenten muss > 0 sein
			System.exit(1);
		}
		if (numberOfConsumers <= 0) {
			printErrorMessages(1, 13); // Anzahl Konsumenten muss > 0 sein
			System.exit(1);
		}
		if ((numberOfMaxProdCons - numberOfConsumers - numberOfProducers) < 0) {
			printErrorMessages(1, 14); // Summme Produzenten/Konsumenten zu gross
			System.exit(1);
		}
		if (mainPriority < 1 || mainPriority > 10 || inspectorPriority < 1
				|| inspectorPriority > 10) {
			printErrorMessages(1, 15); // Thread-Prior. nicht zwischen 1 und 10
			System.exit(1);
		}
		if (cycleTimeInspector <= 0) {
			printErrorMessages(1, 16); // Wartezeit Inspektor muss > 0 sein
			System.exit(1);
		}
		if (minimalStoreSize < 0) {
			printErrorMessages(1, 17); // Minimale Lagergrösse muss >= 0 sein
			System.exit(1);
		}
		if (maximalStoreSize < 1) {
			printErrorMessages(1, 18); // Maximale Lagergrösse muss > 0 sein
			System.exit(1);
		}
		if (lotDefaultProduction < 1 || lotDefaultConsumption < 1) {
			// Losgroessen fuer Produzenten und Konsumenten müssen > 0 sein
			printErrorMessages(1, 19);
			System.exit(1);
		}
		if (lotFactorProduction < 1 || lotFactorConsumption < 1) {
			// Faktoren fuer Produzenten und Konsumenten müssen > 0 sein
			printErrorMessages(1, 20);
			System.exit(1);
		}

		// Bestimmung der minimal notwendigen Lagergrösse (maximalStoreSize) in
		// Abhängigkeit der übrigen Lagerparameter

		// GGT der beiden Lose
		int gcdLotSizes = gcdEuklid(lotDefaultProduction, lotDefaultConsumption);
		// Notwendige Lagergrösse wenn minimalStoreSize = 0
		int requiredStoreSize = (lotDefaultProduction * lotFactorProduction + lotDefaultConsumption
				* lotFactorConsumption)
				- gcdLotSizes;
		// Additive Korrektur wenn minimalStoreSize > 0
		if (minimalStoreSize > 0) {
			requiredStoreSize = requiredStoreSize
					+ (((minimalStoreSize - 1) / gcdLotSizes) + 1) * gcdLotSizes;
		}
		if (maximalStoreSize < requiredStoreSize) { // Lager zu klein?
			printErrorMessages(1); // Lagerparameter ungünstig definiert
			printErrorMessages(Integer.toString(requiredStoreSize), 21);
			// Wenn keine Tastatur-Eingabe: Programm weiter abarbeiten
			if (scanner.nextLine().length() != 0) {
				printErrorMessages(22); // Das Programm wurde abgebrochen
				System.exit(1);
			}
		}
	}

	/**
	 * Berechnet das GGT für die Bestimmung der minimal notwendigen Lagergrösse
	 * (Euklidscher Algorithmus).
	 * 
	 * @param figure1
	 *           Erster Parameter für die Bestimmung des GGTs.
	 * @param figure2
	 *           Zweiter Parameter für die Bestimmung des GGTs.
	 * @return
	 */
	private int gcdEuklid(int figure1, int figure2) {
		int r;
		do {
			r = figure1 % figure2;
			figure1 = figure2;
			figure2 = r;
		} while (r != 0);
		return figure1; // GGT
	}

	/**
	 * Gibt die entsprechende Fehlermeldung aus bei falsch definierten Parametern
	 * (Fehlermeldungen ohne explizit definierten Fehlermeldungs-Parameter).
	 * 
	 * @param errorMessageNr
	 *           Nummer der Fehlermeldung.
	 */
	private void printErrorMessages(int... errorMessageNr) {
		for (int i : errorMessageNr) {
			switch (i) {
			case 1:
				System.err.println("\n\n  Unzulaessige Eingabe !!");
				break;
			case 2:
				System.err
						.println("\n  --> Default Properties-File nicht gefunden."
								+ "\n      - Definierter Filename: "
								+ this.nameOfPropFile + "\n\n");
				break;
			case 3:
				System.err
						.println("\n  --> Als Argument definiertes Properties-File nicht gefunden.\n\n");
				break;
			case 4:
				System.err
						.println("\n  --> Unzulaessige Definition im Properties-File: "
								+ nameOfPropFile);
				break;
			case 6:
				System.err.println("      - Kein Key-Argument definiert.\n"
						+ "      - Argument kannn nicht als Integer geparst werden.");
				break;
			case 7:
				System.err
						.println("      - Kein Key-Argument definiert (sinnvollerweise keine Zahlen waehlen).");
				break;
			case 8:
				System.err
						.println("      - Moegliches Key-Argument: true oder false.\n\n");
				break;
			case 9:
				System.err.println("\n  --> Moegliche Argumente:"
						+ "\n      - Produktionszeit (s)"
						+ "\n      - Anzahl Produzenten"
						+ "\n      - Anzahl Konsumenten\n\n");
				break;
			case 10:
				System.err.println("        .... muessen Integer sein.\n\n");
				break;
			case 11:
				System.err.println("\n  --> Arbeitszeit (s) muss > 0 sein.\n\n");
				break;
			case 12:
				System.err.println("\n  --> Anzahl Produzenten muss > 0 sein.\n\n");
				break;
			case 13:
				System.err.println("\n  --> Anzahl Konsumenten muss > 0 sein.\n\n");
				break;
			case 14:
				System.err
						.println("\n  --> Anzahl Produzenten/Konsumenten zu gross!!"
								+ "\n\n      Maximale Summe Produzenten/Konsumenten: "
								+ numberOfMaxProdCons
								+ "\n\n      - Definierte Anzahl Produzenten: "
								+ numberOfProducers
								+ "\n      - Definierte Anzahl Konsumenten: "
								+ numberOfConsumers + "\n\n");
				break;
			case 15:
				System.err.println("\n  --> Prioritaeten fuer main- oder Inspek-"
						+ "\n      tor-Thread muessen >= 1 UND <= 10 sein.\n\n");
				break;
			case 16:
				System.err
						.println("\n  --> Wartezeit (ms) fuer Inspektor muss > 0 sein.\n\n");
				break;
			case 17:
				System.err
						.println("\n  --> Minimale Lagergroesse muss >= 0 sein.\n\n");
				break;
			case 18:
				System.err
						.println("\n  --> Maximale Lagergroesse muss > 0 sein.\n\n");
				break;
			case 19:
				System.err.println("\n  --> Losgroessen fuer Produzenten und"
						+ "\n      Konsumenten muessen > 0 sein.\n\n");
				break;
			case 20:
				System.err.println("\n  --> Faktoren fuer Losgroesse der Produ-"
						+ "\n      zenten und Konsumenten muessen > 0 sein.\n\n");
				break;
			case 22:
				System.out
						.println("          Das Programm wurde abgebrochen!!\n\n");
				break;
			}
		}
	}

	/**
	 * Gibt die entsprechende Fehlermeldung aus bei falsch definierten Parametern
	 * (Fehlermeldungen mit explizit definiertem Fehlermeldungs-Parameter).
	 * 
	 * @param arg
	 *           Argument, Paramter für die Fehlermeldung.
	 * @param errorMessageNr
	 *           Nummer der Fehlermeldung.
	 */
	private void printErrorMessages(String arg, int... errorMessageNr) {
		for (int i : errorMessageNr) {
			switch (i) {
			case 5:
				System.err
						.println("\n      Fehler bei Key: "
								+ arg
								+ "\n      --> Key konnte nicht eingelesen werden.\n"
								+ "\n      Fehlermoeglichkeiten:"
								+ "\n      - Key fehlt oder ist falsch definiert."
								+ "\n      - Zwischen Key und Key-Definition muss als Delimi-"
								+ "\n        ter ein <White-Space>, <:> oder <=> definiert sein.");
				break;
			case 21:
				System.err
						.println("\n\n  --> Lagerparameter unguenstig dimensioniert."
								+ "\n\n      --> die Lagerkapazitaet ist zu klein."
								+ "\n\n          Vor allem bei wenigen Threads koennen die "
								+ "\n          Produzenten und Konsumenten blockiert werden."
								+ "\n\n      Moegliche Korrektur ueber maximale Lagergroesse (maximalStoreSize)"
								+ "\n\n      - Aktuelle maximale Lagergroesse: "
								+ maximalStoreSize
								+ "\n      - Minimal notwendige maximale Lagergroesse: "
								+ arg
								+ "\n\n      --> Gleichwohl mit Programmablauf weiterfahren: --> Eingabe <CR>"
								+ "\n          Eingabe <beliebige Taste> + <CR> bricht das Programm ab.\n");
				break;
			}
		}
	}

	/**
	 * 
	 * Ausgabe der definierten Paramter. (Aktivität #1, Aufruf durch
	 * <i>main()</i> beim Programmstart).
	 */
	public void printData() {
		System.out.print("\n [Argument] Eingelesenes Properties-File: "
				+ nameOfPropFile
				+ "\n [Argument] Produktions- Konsumations-Dauer (s): " + timeToRun
				+ "\n [Argument] Anzahl Produzenten-Threads: " + numberOfProducers
				+ "\n [Argument] Anzahl Konsumenten-Threads: " + numberOfConsumers
				+ "\n Maximal moegliche Summe Produzenten/Konsumenten: "
				+ numberOfMaxProdCons + "\n Prioritaet main-Thread: "
				+ mainPriority);
		if (!nameOfInspector.equals("none")) { // Kein Inspektor implementiert
			System.out.print("\n Prioritaet Inspektor-Thread: "
					+ inspectorPriority + "\n Intervall Inspektor (ms): "
					+ cycleTimeInspector);
		}
		System.out.println("\n Monitor Lager ist fair: " + monitorStoreIsFair
				+ "\n Minimale Lagergroesse: " + minimalStoreSize
				+ "\n Maximale Lagergroesse: " + maximalStoreSize
				+ "\n Basisgroesse Los Produzenten: " + lotDefaultProduction
				+ "\n Maximaler Multiplikationsfaktor Losgroesse Produzenten: "
				+ lotFactorProduction + "\n Basisgroesse Lot Konsumenten: "
				+ lotDefaultConsumption
				+ "\n Maximaler Multiplikationsfaktor Losgroesse Konsumenten: "
				+ lotFactorConsumption);
	}

	/**
	 * Returniert die Produktionszeit (definierte Laufzeit der Applikation).
	 * 
	 * @return Laufzeit, Produktionszeit der Applikation (Properties-Key:
	 *         <i>timeToRun</i>).
	 */
	public int getTimeToRun() {
		return timeToRun;
	}

	/**
	 * Returniert die Anzahl der definierten Produzenten.
	 * 
	 * @return Anzahl der Produzenten (Properties-Key:
	 *         <i>numberOfProducers</i>).
	 */
	public int getNumberOfProducers() {
		return numberOfProducers;
	}

	/**
	 * Returniert die Anzahl der definierten Konsumenten.
	 * 
	 * @return Anzahl der Konsumenten(Properties-Key: <i>numberOfConsumers</i>).
	 */
	public int getNumberOfConsumers() {
		return numberOfConsumers;
	}

	/**
	 * Returniert die Anzahl der maximal definierbaren Produzenten und
	 * Konsumenten (Summe).
	 * 
	 * @return Anzahl maximal definerbaren Konsumenten (Properties-Key:
	 *         <i>numberOfMaxProdCons</i>).
	 */
	public int getNumberOfMaxProducersConsumers() {
		return numberOfMaxProdCons;
	}

	/**
	 * Returniert die Inaktivitätszeit des Inspektors zwischen zwei
	 * Inspektions-Intervallen.
	 * 
	 * @return Inaktivitätszeit des Inspektors (Properties-Key:
	 *         <i>cycleTimeInspector</i>).
	 */
	public int getCycleTimeInspector() {
		return cycleTimeInspector;
	}

	/**
	 * Returniert die Priorität des main-Threads.
	 * 
	 * @return Priorität des main-Threads (Properties-Key: <i>mainPriority</i>).
	 */
	public int getMainPriority() {
		return mainPriority;
	}

	/**
	 * Returniert die Priorität des Inspektor-Threads.
	 * 
	 * @return Priorität des Inspektor-Threads (Properties-Key:
	 *         <i>inspectorPriority</i>).
	 */
	public int getInspectorPriority() {
		return inspectorPriority;
	}

	/**
	 * Returniert die minimale Grösse des Lagers.
	 * 
	 * @return Minimale Grösse des Lagers (Properties-Key:
	 *         <i>minimalStoreSize</i>).
	 */
	public int getMinimalStoreSize() {
		return minimalStoreSize;
	}

	/**
	 * Returniert die maximale Grösse des Lagers.
	 * 
	 * @return Maximale Grösse des Lagers (Properties-Key:
	 *         <i>maximalStoreSize</i>).
	 */
	public int getMaximalStoreSize() {
		return maximalStoreSize;
	}

	/**
	 * Returniert die Basislosgrösse der Produzenten.
	 * 
	 * @return Basislosgrösse der Produzenten (Properties-Key:
	 *         <i>lotDefaultProduction</i>).
	 */
	public int getLotDefaultProduction() {
		return lotDefaultProduction;
	}

	/**
	 * Returniert den maximalen Multiplikator für die Losgrösse der Produzenten.
	 * 
	 * @return Maximaler Multiplikator für die Losgrösse der Produzenten
	 *         (Properties-Key: <i>lotFactorProduction</i>).
	 */
	public int getLotFactorProduction() {
		return lotFactorProduction;
	}

	/**
	 * Returniert die Basislosgrösse der Konsumenten.
	 * 
	 * @return Basislosgrösse der Produzenten (Properties-Key:
	 *         <i>lotDefaultConsumption</i>).
	 */
	public int getLotDefaultConsumption() {
		return lotDefaultConsumption;
	}

	/**
	 * Returniert den maximalen Multiplikator für die Losgrösse der Konsumenten.
	 * 
	 * @return Maximaler Multiplikator für die Losgrösse der Konsumenten
	 *         (Properties-Key: <i>lotFactorConsumption</i>).
	 */
	public int getLotFactorConsumption() {
		return lotFactorConsumption;
	}

	/**
	 * Returniert den Thread-Basisnamen der Produzenten.
	 * 
	 * @return Basisname der Produzenten (Properties-Key:
	 *         <i>baseNameOfProducers</i>).
	 */
	public String getBaseNameOfProducers() {
		return baseNameOfProducers;
	}

	/**
	 * Returniert den Thread-Basisnamen der Konsumenten.
	 * 
	 * @return Basisname der Produzenten (Properties-Key:
	 *         <i>baseNameOfConsumers</i>).
	 */
	public String getBaseNameOfConsumers() {
		return baseNameOfConsumers;
	}

	/**
	 * Returniert den Thread-Namen des Inspektors.
	 * 
	 * @return Thread-Namen des Inspektors (Properties-Key:
	 *         <i>nameOfInspector</i>).
	 */
	public String getNameOfInspector() {
		return nameOfInspector;
	}

	/**
	 * Returniert die Definition des Monitors zum Schützen des Lagers (fair
	 * (true), nicht fair (false)).
	 * 
	 * @return Monitor (Lager) ist fair (true), oder nicht fair (false)
	 *         (Properties-Key: <i>monitorStoreIsFair</i>).
	 */
	public boolean getMonitorStoreIsFair() {
		return monitorStoreIsFair;
	}
}