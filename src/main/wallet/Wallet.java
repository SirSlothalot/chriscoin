package main.wallet;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Scanner;

import main.generic.*;

public class Wallet {

	private KeyStore keyStore;

	private ArrayList<Transaction> records;

	private HTTPSClient client;
	private static String host = "127.0.0.1";

	private static final int PORT = 9999;

	
	private static final String RECORDS_DIR = "/MyRecords";

	static boolean running;

	Wallet() {
		System.out.println("-- Wallet Initialising --");
		records = loadWallet();
		System.out.println("-- Wallet Initialised --");
	}

	private boolean refresh() {
		
		client = new HTTPSClient(this, null, keyStore, host, PORT);
		if (client.run()) {
			return true;
		} else {
			System.err.println("Can't connect to server");
			return false;
		}
		
	}

	// PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key",
	// "pass1".toCharArray());
	// PublicKey pubKey = (PublicKey)
	// keyStore.getCertificate("my-certificate").getPublicKey();
	// PublicKey receiverKey = (PublicKey)
	// keyStore.getCertificate("peer-certificate-0").getPublicKey();

	public synchronized void receiveMessage(Transaction message) {
		addRecord(message);
	}
	
	public synchronized void receiveMessage(BlockHeaderChain message) {

		// TODO
		
	}
	
	public synchronized void receiveMessage(BlockHeader message) {

		// TODO
		
	}

	private void newTransaction(String amount, String reciever) {
		Double dAmount;
		try {
			dAmount = Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			System.err.println("Transaction failed. Amount must be a number");
			return;
		}
		PublicKey recieverKey, myKey;
		try {
			recieverKey = (PublicKey) keyStore.getCertificate(reciever).getPublicKey();
			myKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
		} catch (Exception e) {
			System.out.println("Could not find payee '" + reciever + "'");
			return;
		}

		Transaction t = new Transaction();

		ArrayList<Integer> parTransIndexs = new ArrayList<Integer>();
		ArrayList<byte[]> parTransHashes = new ArrayList<byte[]>();

		double total = findTransactions(parTransIndexs, parTransHashes, dAmount);

		if (total == -1) {
			double balTemp = calcBalance();
			System.out.println("Could not send ChrisCoins!");
			System.out.println("\tYour Balance: " + balTemp + " CC");
			System.out.println("\tSend Amount: " + dAmount + " CC");
			return;
		}

		for (int i = 0; i < parTransIndexs.size(); i++) {
			t.addInput(parTransHashes.get(i), parTransIndexs.get(i));
		}
		t.addOut(dAmount, recieverKey);
		t.addOut(total - dAmount, myKey);
	}

	private double findTransactions(ArrayList<Integer> parTransIndexs, ArrayList<byte[]> parTransHashes,
			Double amount) {

		Double amountFound = 0.0;
		PublicKey pubKey;

		try {
			pubKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return -1;
		}
		for (int i = 0; i < records.size(); i++) {
			int outIndex = records.get(i).getOutputIndex(pubKey);
			if (outIndex != -1) {
				parTransIndexs.add(outIndex);
				try {
					parTransHashes.add(Hasher.hash(records.get(i)));
				} catch (NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
				amountFound += records.get(i).getOutputAmount(outIndex);
			}
			if (amountFound > amount) {
				// TODO transaction fee
				return amountFound;
			}
		}
		return -1;

	}

	private void addRecord(Transaction trans) {
		records.add(trans);
	}

	private void printBalance() {
		System.out.println("Balance: " + calcBalance() + " CC");
	}

	private void saveWallet() {
		try {
			OutputStream file = new FileOutputStream(Constants.DESKTOP_DIR + RECORDS_DIR + "records.ser");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(records);
			output.close();
			System.out.println("Records saved..." + "\tRecord size: " + records.size());
			printBalance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Transaction> loadWallet() {
		intialiseDirs();
		ObjectInput input = null;
		try {
			InputStream file = new FileInputStream(Constants.DESKTOP_DIR + RECORDS_DIR + "records.ser");
			InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream(buffer);
			ArrayList<Transaction> temp = (ArrayList<Transaction>) input.readObject();
			input.close();
			System.out.println("Records loaded..." + "\tRecord size: " + temp.size());
			return temp;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			return new ArrayList<Transaction>();
		}
	}

	private static void intialiseDirs() {
		File dir = new File(Constants.DESKTOP_DIR + RECORDS_DIR);
		dir.mkdirs();
		dir = new File(Constants.DESKTOP_DIR + Constants.TRUSTED_CERTS_DIR);
		dir.mkdirs();
		
		
	}

	public double calcBalance() {
		double bal = 0;
		if (refresh()) {

			PublicKey pubKey;
			try {
				pubKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
			} catch (KeyStoreException e) {
				e.printStackTrace();
				return -1;
			}
			for (int i = 0; i < records.size(); i++) {
				int outIndex = records.get(i).getOutputIndex(pubKey);
				if (outIndex != -1) {

					bal += records.get(i).getOutputAmount(outIndex);
				}
			}
			return bal;
		} else {
			System.out.println("Cannot access balance if not online");
			return 0;
		}

	}

	private void parseCommand(String command) {
		String[] commands = command.split(" ");

		if (commands.length < 1) {
			return;
		}
		commands[0].toLowerCase();

		if (commands[0].equals("transaction")) {
			if (commands.length > 1) {
				if (commands[1].equals("new")) {
					if (commands.length > 3) {
						newTransaction(commands[2], commands[3]);
					} else {
						System.out.println("Must have input 'amount' and 'reciever'");
					}
				} else if (commands[1].equals("view")) {
					viewRecords();
				}
			} else {
				System.out.println("Must have input 'new' or 'view'");
			}
		} else if (commands[0].equals("refresh")) {
			refresh();
		} else if (commands[0].equals("balance")) {
			System.out.println("Your balance: " + calcBalance());
		} else if (commands[0].equals("host")) {
			if (commands.length > 1) {
				setHost(commands[1]);
			} else {
				System.out.println("Must have input 'ip'");
			}
		} else if (commands[0].equals("exit")) {
			running = false;
		} else if (commands[0].equals("help")) {
			printHelp();
		}
	}

	private void viewRecords() {
		if (records.size() == 0) {
			System.out.println("No records to show");
		}
		for (int i = 0; i < records.size(); i++) {
			System.out.println(records.get(i).toString());
		}

	}

	private void setHost(String newHost) {
		// TODO check if ip string

		String oldHost = host;
		host = newHost;
		if (!refresh()) {
			System.out.println("Could not connect to '" + newHost + "'. Reverting to '" + oldHost + "'");
			host = oldHost;
		}
	}

	private static void printHelp() {
		System.out.println("-- List of commands and purposes --");
		System.out.println();
		System.out.println("transaction view");
		System.out.println("- View all transactions where you are the recipient");
		System.out.println("transaction new 'amount' 'reciever'");
		System.out.println("- sends an amount of ChrisCoins to a chosen sender");
		System.out.println("- 'amount' = the number of ChrisCoins that you want to send");
		System.out.println("- 'reciever' = name of the payee");
		System.out.println("balance");
		System.out.println("- displays your current balance");
		System.out.println("refresh");
		System.out.println("- polls server for updates");
		System.out.println("host 'ip'");
		System.out.println("- sets ip of host to connect to");
		System.out.println("- 'ip' = ip of host");
		System.out.println("exit");
		System.out.println("- exits the program");

	}

	public static void main(String[] args) {

		Wallet wallet = new Wallet();
		Scanner scanner = new Scanner(System.in);
		running = true;
		while (running) {
			String command = scanner.nextLine();
			wallet.parseCommand(command);
		}
		scanner.close();
		wallet.saveWallet();
		System.exit(0);
	}

}
