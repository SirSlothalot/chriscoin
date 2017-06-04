package main.wallet;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import main.generic.*;

public class Wallet {

	private KeyStore keyStore;

	private ArrayList<Transaction> records;

	private HTTPSClient client;
	private static String HOST = "127.0.0.1";
	private static int PORT = 9999;

	static boolean running;

	Wallet() {
		System.out.println("-- Wallet Initialising --");
		records = loadWallet();
		System.out.println("-- Wallet Initialised --");

		keyStore = Keys.initKeyStore(keyStore, "pass1", Constants.DESKTOP_DIR + Constants.WALLET_DIR);
		Keys.initKeys(keyStore, "pass1", "pass1", Constants.DESKTOP_DIR + Constants.WALLET_DIR);
		Keys.loadTrustedCertificates(keyStore, Constants.DESKTOP_DIR);

		client = new HTTPSClient(this, keyStore, HOST, PORT);
		refresh();
	}

	private boolean refresh() {
		try {
			PublicKey pub = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
			System.out.println("Wallet HashCode: " + pub.hashCode());
			if (client.run(pub)) {
				return true;
			} else {
				System.err.println("Coud not connect to " + HOST + ".\nConsider changing host by using the 'host <ip-address>' command.");
				return false;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return false;
		}
	}

	// PublicKey pubKey = (PublicKey)
	// keyStore.getCertificate("my-certificate").getPublicKey();
	// PublicKey receiverKey = (PublicKey)
	// keyStore.getCertificate("peer-certificate-0").getPublicKey();

	public synchronized void receiveMessages(ArrayList<Message> messages) {
		for (Message message : messages) {
			if(validMessage(message)) {
				addRecord(message.getTransaction());
			}
		}
	}
	
	private boolean validMessage(Message message) {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
		    sig.initVerify(message.getPublicKey());
		    sig.update(message.getByteTransaction());
		    return sig.verify(message.getSignedTransaction());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void receiveMessage(BlockHeaderChain message) {

		// TODO

	}

	public synchronized void receiveMessage(BlockHeader message) {

		// TODO

	}

	private void newTransaction(String amount, String receiver) {
		Double dAmount;
		try {
			dAmount = Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			System.err.println("Transaction failed. Amount must be a number");
			return;
		}
		PublicKey receiverKey, myKey;
		try {
			receiverKey = (PublicKey) keyStore.getCertificate(receiver).getPublicKey();
			myKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
		} catch (Exception e) {
			System.out.println("Could not find payee '" + receiver + "'");
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
		t.addOut(dAmount, receiverKey);
		
		PrivateKey privateKey = null;
		try {
			privateKey = (PrivateKey) keyStore.getKey("my-private-key", "pass1".toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		Message message = new Message(t, privateKey, myKey);
		
		if(client.run(message)) {
			
			System.out.println(amount + " ChrisCoins were sent to the miners.");
		} else {
			System.out.println("Could not send ChrisCoins!");
			System.err.println("Can't connect to server");
		}
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

	private void addRecord(Transaction t) {
		int numInputs = t.getInputCount();
		for (int i = 0; i < numInputs; i++) {
			removeRecordIfExists(t.getParentHash(i));
		}
		records.add(t);
	}

	private void removeRecordIfExists(byte[] transHash) {
		for (int i = 0; i < records.size(); i++) {
			byte[] recordTransHash = null;
			try {
				recordTransHash = Hasher.hash(records.get(i));
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}
			if (Arrays.equals(recordTransHash, transHash)) {
				records.remove(i);
				break;
			}
		}
	}

	private void printBalance() {
		System.out.println("Balance: " + calcBalance() + " CC");
	}

	private void saveWallet() {
		try {
			OutputStream file = new FileOutputStream(
					Constants.DESKTOP_DIR + Constants.WALLET_DIR + Constants.RECORDS_DIR + "records.ser");
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
			InputStream file = new FileInputStream(
					Constants.DESKTOP_DIR + Constants.WALLET_DIR + Constants.RECORDS_DIR + "records.ser");
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
		File dir = new File(Constants.DESKTOP_DIR + Constants.WALLET_DIR + Constants.RECORDS_DIR);
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

		String oldHost = HOST;
		HOST = newHost;
		client = new HTTPSClient(this, keyStore, HOST, PORT);
		if (!refresh()) {
			System.out.println("Could not connect to '" + newHost + "'. Reverting to '" + oldHost + "'");
			HOST = oldHost;
			client = new HTTPSClient(this, keyStore, HOST, PORT);
		}
	}

	public void shutdown() {
		System.out.println("-- Wallet Saving --");
		saveWallet();
		System.out.println("-- Wallet Shutdown --");
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
						System.out.println("Must have input 'amount' and 'receiver'");
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
		} else {
			System.err.println("'" + command + "' is not a valid command");
		}
	}

	private static void printHelp() {
		System.out.println("-- List of commands and purposes --");
		System.out.println();
		System.out.println("transaction view");
		System.out.println("- View all transactions where you are the recipient");
		System.out.println("transaction new 'amount' 'receiver'");
		System.out.println("- sends an amount of ChrisCoins to a chosen sender");
		System.out.println("- 'amount' = the number of ChrisCoins that you want to send");
		System.out.println("- 'receiver' = name of the payee");
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
		wallet.shutdown();
		System.exit(0);
	}

}
