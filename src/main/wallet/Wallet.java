package main.wallet;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
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

	private static final String DESKTOP_DIR	= System.getProperty("user.home") + "/Desktop/ChrisCoin";
	private static final String RECORDS_DIR = "/MyRecords";

	static boolean running;


	Wallet() {
		System.out.println("-- Wallet Initialising --");
		records = loadWallet();
		System.out.println("-- Wallet Initialised --");
	}

	private boolean refresh() {
		return false;
		// TODO Auto-generated method stub

	}

//	private void sendMessage(String[] receivers, Double[] amounts) {
//		if (canSendAmount(amounts)) {
//			try {
//				PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key", "pass1".toCharArray());
//				PublicKey pubKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
//				PublicKey receiverKey = (PublicKey) keyStore.getCertificate("peer-certificate-0").getPublicKey();
//
//				Message message = new Message(receivers, amounts);
//				//Message message = new Message(amount, pubKey, receiverKey, privKey);
//
//				initClient(this, message, HOST, PORT); //change this to message, args[0], args[1]
//
//				addRecord(message.getTransaction());
//				System.out.println(message.toString());
//				processAmount(amount);
//				printBalance();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			System.err.println("Not enough money to send");
//		}
//	}

	public synchronized void receiveMessage(Object message) {
//		check who is receiver/sender
//		records.add(new Record(sender, publicKey, amount));
//		updateBalance(amount);
		// TODO
	}

	private void newTransaction(String amount, String reciever) {
		Double dAmount = Double.ParseDouble(amount);
		if(dAmount == null) {
			System.out.println("Transaction failed. Amount must be a number.");
			return;
		}
		Transaction t = new Transaction();


		ArrayList<Integer> parTransIndex = new ArrayList<Integer>();
		ArrayList<byte[]> parTransHash = new ArrayList<byte[]>();
		findTransactions(parTransHash, parTransIndex, dAmount);

		if(parentTransactions != null) {
			for(int i = 0; i < parTransHash.size(); i++) {
				t.addInput(parTransHash.get(i), parTransIndex.get(i));
			}
		} else {
			System.out.println("Insufficient funds to make payment of " + amount + "chriscoins.");
		}

	}

	private void findTransactions(ArrayList<Integer> parTransIndex, ArrayList<byte[]> parTransHash, Double amount) {
		Double amountFound = 0.0;
		int index = -1;


		for(int i = 0; i < records.size(); i++) {
			index = records.get(i).indexOfReceiver();

			if(amountFound >= amount) {break;}
		}
	}

	private void addRecord(Transaction trans) {
		records.add(trans);
	}

	private void printBalance() {
		System.out.println("Balance: " + calcBalance() + " CC");
	}

//	private void printWallet() {
//		printBalance();
//		for(Record r : records) {
//			System.out.println(r.toString());
//		}
//	}

	private void saveWallet() {
		try {
			OutputStream file = new FileOutputStream(DESKTOP_DIR + RECORDS_DIR + "records.ser");
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
			InputStream file = new FileInputStream(DESKTOP_DIR + RECORDS_DIR + "records.ser");
		    InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream (buffer);
			ArrayList<Transaction> temp = (ArrayList<Transaction>) input.readObject();
		    input.close();
		    System.out.println("Records loaded..." + "\tRecord size: " + records.size());
		    return temp;
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} catch(IOException e) {
			return new ArrayList<Transaction>();
		}
	}

	private static void intialiseDirs() {
		File dir = new File(DESKTOP_DIR + RECORDS_DIR);
		dir.mkdirs();
	}


	public double calcBalance() {
		double bal = 0;
		refresh();
		for (int i = 0; i < records.size(); i++) {
			
		}
		return bal;
	}
	
	private boolean canSendAmount(Double amounts) {
		return false;
	}

	private void initClient(Wallet wallet, Object message, String host, int port) {
		client = new HTTPSClient(wallet, message, keyStore, host, port);
		client.run();
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
					}
				} else if (commands[1].equals("view")) {
					viewRecords();
				}
			}
		} else if (commands[0].equals("refresh")) {
			refresh();
		} else if (commands[0].equals("balance")) {
			System.out.println("Your balance: " + calcBalance());
		} else if (commands[0].equals("host")) {
			if (commands.length > 1) {
				setHost(commands[1]);
			}
		} else if (commands[0].equals("exit")) {
			running = false;
		} else if (commands[0].equals("help")) {
			printHelp();
		}
	}

	private void viewRecords() {
		// TODO Auto-generated method stub

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
