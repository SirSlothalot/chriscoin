import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.ArrayList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.simple.JSONObject;
import org.bouncycastle.util.io.pem.*;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X509CertImpl;

public class Wallet {

	private String privateKey;
	private String publicKey; //a.k.a Certificate
	private Double balance;
	
	ArrayList<Record> records;
	
	HTTPSClient client;
	String host;
	int port;
	
	Wallet() throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
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
	
//	private void initKeys(char[] password) {
//		//setup private/public keys
//		
//
//		
//		try{
//			//create empty KeyStore
//		    KeyStore keyStore = KeyStore.getInstance("JKS");
//		    keyStore.load(null,null);
//		    
//		    //initialize key generator
//		    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
//		    gen.initialize(2048);
//		    
//		    //create keys
//		    KeyPair keyPair = gen.genKeyPair();
//		    PrivateKey privateKey = keyPair.getPrivate();
//		    PublicKey publicKey = keyPair.getPublic();
//		    
//		    if (!"X.509".equalsIgnoreCase(publicKey.getFormat())) {
//		    	throw new IllegalArgumentException("publicKey's is not X.509, but "
//		    			+ publicKey.getFormat());
//	    	}
//		    
//		    X509Certificate cert = generateCertificate(keyPair);
//		    Certificate[] certChain = new Certificate[1];
//		    certChain[0] = cert;
//		    
//		    keyStore.setKeyEntry("key1", privateKey, password, certChain);
//		    
//		}catch(Exception e){
//		    e.printStackTrace();
//		}
//		
//		
//		
//	    
//	    //save the KeyStore to a file
//	    keyStore.store(new FileOutputStream("walletKeys.jks"), password);
//
//	    //https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/ 
//	    
//	    //http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/sun/security/tools/keytool/CertAndKeyGen.java#CertAndKeyGen.0privateKey
//	    //http://www.programcreek.com/java-api-examples/java.security.KeyPairGenerator
//
//		//http://stackoverflow.com/questions/9890313/how-to-use-keystore-in-java-to-store-private-key
//	}
//	
//	private X509Certificate generateCertificate(KeyPair keyPair) {
//		X509v3CertificateBuilder cert = new X509v3CertificateGenerator();
//		
//	}
	
	
	
	//https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
	private void initKeys() throws FileNotFoundException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException {	
		
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
	    privPem.write("clientPriv.pem");
	    
	    //save public key
	    PemFile pubPem = new PemFile(publicKey, "RSA Public Key");
	    pubPem.write("clientPub.pem");
	}
	
	
	private void initClient(String host, int port) {
		client = new HTTPSClient(host, port);
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		Wallet w = new Wallet();
	}

}






//transactions.add(new Transaction("Alice", "Bob", 400.50));
//transactions.add(new Transaction("Sandy", "Mitch", 345.50));