package main.wallet;

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
	
	Wallet() {
		balance = 0d;
		records = new ArrayList<Record>();
		
		keyStore = Keys.initKeyStore(keyStore, "pass1");
		Keys.initKeys(keyStore, "pass1", "pass1");
	    Keys.loadTrustedCertificates(keyStore);
	    
		initClient(HOST, PORT); //change this to args[0], args[1]
		
		sendMessage("Jane", 60.0);
		sendMessage("Bob", 20.0);
	}
	
	private void sendMessage(String receiver, Double amount) {
		try {
			PrivateKey privKey = (PrivateKey) keyStore.getKey("my-private-key", "pass1".toCharArray());
			PublicKey pubKey = (PublicKey) keyStore.getCertificate("my-certificate").getPublicKey();
			PublicKey receiverKey = (PublicKey) keyStore.getCertificate("peer-certificate-0").getPublicKey();
			Message message = new Message(amount, pubKey, receiverKey, privKey);
			System.out.println(message.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void receiveMessage(Message mesaage) {
//		check who is receiver/sender
//		records.add(new Record(sender, publicKey, amount));
//		updateBalance(amount);
	}
	
	private void updateBalance(Double amount) {
		//check if sender or receiver
		//deduct or add amount
	}
	
	private void printWallet() {
		for(Record r : records) {
			System.out.println(r.toString());
		}
	}
	
	private void initClient(String host, int port) {
		client = new HTTPSClient(keyStore, host, port);
		client.run();
	}
	
	public static void main(String[] args) {
		Wallet w = new Wallet();
	}

}
