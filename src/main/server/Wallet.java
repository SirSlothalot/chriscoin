import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import sun.security.tools.keytool.CertAndKeyGen;

public class Wallet {

	private String privateKey;
	private String publicKey; //a.k.a Certificate
	private Double balance;
	
	ArrayList<Record> records;
	
	HTTPSClient client;
	String host;
	int port;
	
	Wallet() {
		balance = 0d;
		records = new ArrayList<Record>();
		
		initKeys();
		initClient(host, port); //change this to args[0], args[1]
	}
	
	private void sendMessage(String receiver, Double amount) {
		Message m = new Message(publicKey, receiver, amount);
		//send JSON to miner over socket
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
	
	private void initKeys(char[] password) {
		//setup private/public keys
		

		
		try{
			//create empty KeyStore
		    KeyStore keyStore = KeyStore.getInstance("JKS");
		    keyStore.load(null,null);
		    
		    //create keys
		    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		    gen.initialize(2048);
		    KeyPair keyPair = gen.genKeyPair();
		    
		    PrivateKey privateKey = keyPair.getPrivate();
		    PublicKey publicKey = keyPair.getPublic();
		    
		    //save the KeyStore to a file
		    keyStore.store(new FileOutputStream("walletKeys.jks"), password);

		    
		}catch(Exception e){
		    e.printStackTrace();
		}
		
		
		//Create and store private/public keys
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
//			gen.initialize(2048);
//			
//			KeyPair 
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
		//http://stackoverflow.com/questions/9890313/how-to-use-keystore-in-java-to-store-private-key
	}
	
	private void initClient(String host, int port) {
		client = new HTTPSClient(host, port);
	}
	
	public static void main(String[] args) {
		Wallet w = new Wallet();
	}

}






//transactions.add(new Transaction("Alice", "Bob", 400.50));
//transactions.add(new Transaction("Sandy", "Mitch", 345.50));