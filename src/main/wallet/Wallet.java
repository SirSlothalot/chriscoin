package main.wallet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Wallet {

	private KeyStore keyStore;
	
	private String privateKey;
	private String publicKey; //a.k.a Certificate
	private Double balance;
	
	private ArrayList<Record> records;
	
	private HTTPSClient client;
	private String host;
	private int port;
	
	private static final String pubKeyFileName = "./src/data/clientPriv.pem";
	private static final String privKeyFileName = "./src/data/clientPub.pem";
	
	Wallet() {
		balance = 0d;
		records = new ArrayList<Record>();
		
		initKeys();
		initKeyStore("hello world");
		initClient(host, port); //change this to args[0], args[1]
		sendMessage("Jane", 60.0);
	}
	
	private void sendMessage(String receiver, Double amount) {
		try {
			Message m = new Message(publicKey, receiver, amount);
			System.out.println(m);
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
	
	
	//https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
	//https://tls.mbed.org/kb/cryptography/asn1-key-structures-in-der-and-pem
	private void initKeys() {	
		try {
			//initialize BouncyCastle
			Security.addProvider(new BouncyCastleProvider());
			
			//initialize key generator
		    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "BC");
		    gen.initialize(2048);
		    
		    //create keys
		    KeyPair keyPair = gen.genKeyPair();
		    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		    
		    //save private key
		    PemFile privPem = new PemFile(privateKey, "RSA Private Key");
		    privPem.write(privKeyFileName);
		    
		    //save public key
		    PemFile pubPem = new PemFile(publicKey, "RSA Public Key");
		    pubPem.write(pubKeyFileName);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initKeyStore(String password) {
		
		
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
			
			PrivateKey priv = generatePrivateKey(factory, privKeyFileName);			
			PublicKey pub = generatePublicKey(factory, pubKeyFileName);
			
			System.out.println(priv.toString());
			
//			keyStore = KeyStore.getInstance("PKCS8");
//	        keyStore.load(null);
//	        keyStore.setKeyEntry("alias", (Key) priv, password.toCharArray(), new java.security.cert.Certificate[]{pubPemToPKCS12()});
//	        keyStore.store(new FileOutputStream("clientKeyStore.jks"), password.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static PrivateKey generatePrivateKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
		return factory.generatePrivate(privKeySpec);
	}
	
	private static PublicKey generatePublicKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return factory.generatePublic(pubKeySpec);
	}
	
	private void initClient(String host, int port) {
		client = new HTTPSClient(host, port);
	}
	
	public static void main(String[] args) {
		Wallet w = new Wallet();
	}

}
