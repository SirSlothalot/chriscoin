package main.wallet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;

public class Wallet {
	
	private KeyStore keyStore;

	private Double balance;
	
	private ArrayList<Record> records;
	
	private HTTPSClient client;
	private String host;
	private int port;
	
	Wallet() {
		balance = 0d;
		records = new ArrayList<Record>();
		
		//Keys.initKeyStore(keyStore, "pass1");
		keyStore = Keys.initKeys(keyStore, "pass1", "pass1");
//		readPemKeys();
		initClient(host, port); //change this to args[0], args[1]
		try {
			sendMessage("Jane", 60.0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String receiver, Double amount) {
		try {
			PrivateKey privKey = (PrivateKey) keyStore.getKey("private", "pass1".toCharArray());
			Message message = new Message(amount, "pub", receiver, privKey);
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
		client = new HTTPSClient(host, port);
	}
	
	public static void main(String[] args) {
		Wallet w = new Wallet();
	}

}
