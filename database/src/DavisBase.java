import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DavisBase {

	/* This can be changed to whatever you like */
	static String prompt = "davisql> ";
	static String version = "v1.0b";
	static String copyright = "Chris Irwin Davis";
	static boolean isExit = false;
	/*
	 * Page size for all files is 512 bytes by default. You may choose to make it
	 * user modifiable
	 */
	static long pageSize = 512;

	/*
	 * The Scanner class is used to collect user commands from the prompt There are
	 * many ways to do this. This is just one.
	 *
	 * Each time the semicolon (;) delimiter is entered, the userCommand String is
	 * re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");

	public static void main(String[] args) throws FileNotFoundException {

		/* Display the welcome screen */
		splashScreen();

		/* Variable to collect user input from the prompt */
		String userCommand = "";
		// Builder.initializeDatabase();

		initialize();

		while (!isExit) {
			System.out.print(prompt);
			/* toLowerCase() renders command case insensitive */
			userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
			// userCommand = userCommand.replace("\n", "").replace("\r", "");
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");
	}

	public static void splashScreen() {
		System.out.println(line("-", 80));
		System.out.println("Welcome to DavisBaseLite"); // Display the string.
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-", 80));
	}

	public static String line(String s, int num) {
		String a = "";
		for (int i = 0; i < num; i++) {
			a += s;
		}
		return a;
	}

	/**
	 * Help: Display supported commands
	 */
	public static void help() {
		System.out.println(line("*", 80));
		System.out.println("SUPPORTED COMMANDS");
		System.out.println("All commands below are case insensitive");
		System.out.println();
		System.out.println("\tSELECT * FROM table_name;                        Display all records in the table.");
		System.out.println("\tSELECT * FROM table_name WHERE rowid = <value>;  Display records whose rowid is <id>.");
		System.out.println("\tDROP TABLE table_name;                           Remove table data and its schema.");
		System.out.println("\tVERSION;                                         Show the program version.");
		System.out.println("\tHELP;                                            Show this help information");
		System.out.println("\tEXIT;                                            Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*", 80));
	}

	/** return the DavisBase version */
	public static String getVersion() {
		return version;
	}

	public static String getCopyright() {
		return copyright;
	}

	public static void displayVersion() {
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
	}

	public static void parseUserCommand(String userCommand) throws FileNotFoundException {

		/*
		 * commandTokens is an array of Strings that contains one token per array
		 * element The first token can be used to determine the type of command The
		 * other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement
		 */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));

		switch (commandTokens.get(0)) {

		case "show":
			String[] condition = new String[0];
			String[] columnNames = { "*" };
			Builder.Query("davisbase_tables", columnNames, condition);
			break;
		case "select":
			System.out.println("CASE: SELECT");
			parseQueryString(userCommand);
			break;
		case "drop":
			System.out.println("CASE: DROP");
			dropTable(userCommand);
			break;
		case "create":
			System.out.println("CASE: CREATE");
			parseCreateString(userCommand);
			break;
		case "update":
			System.out.println("CASE: UPDATE");
			parseUpdateString(userCommand);
			break;
		case "help":
			help();
			break;
		case "version":
			displayVersion();
			break;
		case "exit":
			isExit = true;
			break;
		case "delete":
			parseDeleteString(userCommand);
			break;
		case "insert":
			parseInsertString(userCommand);
			break;
		case "quit":
			isExit = true;
		default:
			System.out.println("I didn't understand the command: \"" + userCommand + "\"");
			break;
		}
	}

	public static boolean findTable(String tableName) {

		String filename = tableName + ".tbl";

		File catalog = new File("data/catalog/");
		String[] tablenames = catalog.list();
		for (String table : tablenames) {
			if (filename.equals(table))
				return true;

		}
		File userdata = new File("data/userdata/");
		String[] tables = userdata.list();
		for (String table : tables) {

			if (filename.equals(table))
				return true;
		}
		return false;
	}

	private static void parseDeleteString(String userCommand) {
		String[] delete = userCommand.split("where");
		String[] table = delete[0].trim().split("from");
		String[] table1 = table[1].trim().split(" ");
		String tableName = table1[1].trim();
		String[] cond = Builder.check(delete[1]);

		if (!findTable(tableName)) {
			System.out.println("Table not present");
			return;
		}
		try {
			Builder.delete(tableName, cond);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stub method for dropping tables
	 * 
	 * @param dropTableString
	 *            is a String of the user input
	 * @throws FileNotFoundException 
	 */
	public static void dropTable(String dropTableString) throws FileNotFoundException {
		System.out.println("Parsing the string:\"" + dropTableString + "\"");
		String[] droptemp = dropTableString.split(" ");

		String tableName = droptemp[2].trim();

		if (!findTable(tableName)) {
			System.out.println("Table " + tableName + " is not present.");
			System.out.println();
		} else {
			Builder.dropTable(tableName);
			
		}

	}

	/**
	 * Stub method for executing queries
	 * 
	 * @param queryString
	 *            is a String of the user input
	 */
	public static void parseQueryString(String queryString) {
		System.out.println("Parsing the string:\"" + queryString + "\"");
		String tableName;
		String[] columnNames;
		String[] condition = new String[0];
		String temp[] = queryString.split("where");
		tableName = temp[0].split("from")[1].trim();
		columnNames = temp[0].split("from")[0].replaceAll("select", " ").split(",");

		if (!findTable(tableName)) {
			System.out.println("Table not present");
		}

		else {
			for (int i = 0; i < columnNames.length; i++)
				columnNames[i] = columnNames[i].trim();

			if (temp.length > 1)
				condition = Builder.check(temp[1]);

			Builder.Query(tableName, columnNames, condition);
		}
	}

	private static void parseUpdateString(String userCommand) {
		String[] updates = userCommand.toLowerCase().split("set");
		String[] table = updates[0].trim().split(" ");
		String tablename = table[1].trim();
		String set_value;
		String where = null;
		if (!findTable(tablename)) {
			System.out.println("Table not present");
			return;
		}
		if (updates[1].contains("where")) {
			String[] findupdate = updates[1].split("where");
			set_value = findupdate[0].trim();
			where = findupdate[1].trim();
			Builder.update(tablename, Builder.check(set_value), Builder.check((where)));
		} else {
			set_value = updates[1].trim();

			String[] no_where = new String[0];
			Builder.update(tablename, Builder.check(set_value), no_where);
		}
	}

	public static void parseCreateString(String createTableString) {
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));

		String tableName = createTableTokens.get(2);
		String[] temp = createTableString.replaceAll("\\(", "").replaceAll("\\)", "").split(tableName);
		String[] columnNames = temp[1].trim().split(",");

		for (int i = 0; i < columnNames.length; i++)
			columnNames[i] = columnNames[i].trim();

		if (findTable(tableName)) {
			System.out.println("Table " + tableName + " is already present.");
			System.out.println();
		} else {
			RandomAccessFile table;
			try {
				table = new RandomAccessFile("data/userdata/" + tableName + ".tbl", "rw");
				Builder.createTable(table, tableName, columnNames);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void parseInsertString(String insertString) {

		String[] insert = insertString.split(" ");
		String tableName = insert[2].trim();
		String values = insertString.split("values")[1].replaceAll("\\(", "").replaceAll("\\)", "").trim();

		String[] insertValues = values.split(",");
		for (int i = 0; i < insertValues.length; i++)
			insertValues[i] = insertValues[i].trim();

		if (!findTable(tableName)) {
			System.out.println("Table " + tableName + " does not exist.");
			System.out.println();
			return;
		} else
			Builder.insert(tableName, insertValues);

	}

	public static void initialize() {
		File file = new File("data/catalog");
		File userData = new File("data/userdata");
		file.mkdirs();
		userData.mkdirs();
		if (file.isDirectory()) {
			File davisBaseTables = new File("data/catalog/davisbase_tables.tbl");
			File davisBaseColumns = new File("data/catalog/davisbase_columns.tbl");

			if (!davisBaseTables.exists()) {
				Builder.initializeDatabase();
			}
			if (!davisBaseColumns.exists()) {
				Builder.initializeDatabase();
			}
		} else {
			Builder.initializeDatabase();
		}

	}
}
