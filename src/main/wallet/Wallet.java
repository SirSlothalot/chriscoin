package main.wallet;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Wallet {
	
	private KeyStore keyStore;

	private Double balance;
	
	private ArrayList<Record> records;
	
	private HTTPSClient client;
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 9999;
	
	private static final String RECORDS_DIR	= 	"./src/data/wallet/records/";
	
	Wallet() {
		loadWallet();
		balance = calcBalance();
		
		keyStore = Keys.initKeyStore(keyStore, "pass1");
		Keys.initKeys(keyStore, "pass1", "pass1");
	    Keys.loadTrustedCertificates(keyStore);
	    
		initClient(HOST, PORT); //change this to args[0], args[1]
		
		sendMessage("Jane", -60.0);
		sendMessage("Bob", -20.0);
		
		balance = calcBalance();
		saveWallet();
		printWallet();
	}
	
	private void sendMessage(String receiver, Double amount) {
		if (canSendAmount(amount)) { 
			try {
				PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key", "pass1".toCharArray());
				PublicKey pubKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
				PublicKey receiverKey = (PublicKey) keyStore.getCertificate("peer-certificate-0").getPublicKey();
				Message message = new Message(amount, pubKey, receiverKey, privKey);
				addRecord(message.getTransaction());
				System.out.println(message.toString());
				processAmount(amount);
				printBalance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Not enough money to send");
		}
	}
	
	private void receiveMessage(Message mesaage) {
//		check who is receiver/sender
//		records.add(new Record(sender, publicKey, amount));
//		updateBalance(amount);
		
		
		
	}
	
	private void addRecord(Transaction trans) {
		records.add(new Record(trans.getAmount(), trans.getSenderCert(), trans.getRecieverCert()));
	}
	
	private void updateBalance(Double amount) {
		//check if sender or receiver
		//deduct or add amount
	}
	
	private void printBalance() {
		System.out.println("Balance: " + balance);
	}
	
	private void printWallet() {
		printBalance();
		for(Record r : records) {
			System.out.println(r.toString());
		}
	}
	
	private void saveWallet() {
		try {
			OutputStream file = new FileOutputStream(RECORDS_DIR + "records.ser");
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
	private void loadWallet() {
		ObjectInput input = null;
		try {
			InputStream file = new FileInputStream(RECORDS_DIR + "records.ser");
		    InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream (buffer);
		    records = (ArrayList<Record>) input.readObject();
		    input.close();
		    System.out.println("Records loaded..." + "\tRecord size: " + records.size());
		    printBalance();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			records = new ArrayList<Record>();
		}
	}
	
	private double calcBalance() {
		double bal = 0;
		for (int i = 0; i < records.size(); i++) {
			bal += records.get(i).getAmount();
		}
		return bal;
	}
	
	private boolean canSendAmount(Double amount) {
		return (balance + amount) >= 0;
	}
	
	private void processAmount(double amount) {
		balance += amount;
	}
	
	private void initClient(String host, int port) {
		client = new HTTPSClient(keyStore, host, port);
		client.run();
	}
	
	public static void main(String[] args) {
		Wallet w = new Wallet();
	}

}
