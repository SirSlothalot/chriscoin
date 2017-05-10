package main.wallet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import sun.security.x509.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import sun.security.tools.keytool.CertAndKeyGen;

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
		
		Keys.initKeyStore(keyStore, "pass1");
		Keys.initKeys(keyStore, "pass1", "pass2");
//		readPemKeys();
		initClient(host, port); //change this to args[0], args[1]
		sendMessage("Jane", 60.0);
	}
	
	private void sendMessage(String receiver, Double amount) {
//		try {
//			Message m = new Message(publicKey, receiver, amount);
//			System.out.println(m);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
