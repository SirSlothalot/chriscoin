package main.wallet;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class Wallet {

	private KeyStore keyStore;
	
	private String privateKey;
	private String publicKey; //a.k.a Certificate
	private Double balance;
	
	private ArrayList<Record> records;
	
	private HTTPSClient client;
	private String host;
	private int port;
	
	private static final String pubKeyFileName = "clientPriv.pem";
	private static final String privKeyFileName = "clientPub.pem";
	
	Wallet() throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, IOException, CertificateException {
		balance = 0d;
		records = new ArrayList<Record>();
		
		initKeys();
		initKeyStore("hello world");
		initClient(host, port); //change this to args[0], args[1]
		sendMessage("Jane", 60.0);
	}
	
	private void sendMessage(String receiver, Double amount) throws NoSuchAlgorithmException {
		Message m = new Message(publicKey, receiver, amount);
		System.out.println(m);
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
	    privPem.write(privKeyFileName);
	    
	    //save public key
	    PemFile pubPem = new PemFile(publicKey, "RSA Public Key");
	    pubPem.write(pubKeyFileName);
	}
	
	private void initKeyStore(String password) throws IOException, CertificateException {
		PrivateKey key = privPemToPKCS12();
		Certificate X509Certificate = pubPemToPKCS12();
		
		
	}
	
	private PrivateKey privPemToPKCS12() throws IOException {
		//retrieve private key
		FileReader reader = new FileReader(privKeyFileName);
		
		PEMParser pem = new PEMParser(reader);
        PEMKeyPair pemKeyPair = ((PEMKeyPair)pem.readObject());
        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter().setProvider("SC");
        KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);

        PrivateKey key = keyPair.getPrivate();

        pem.close();
        reader.close();
        
        return key;
	}
	
	private Certificate pubPemToPKCS12() throws IOException, CertificateException {
		
		FileReader reader = new FileReader(pubKeyFileName);
		PEMParser pem = new PEMParser(reader);

        X509CertificateHolder certHolder = (X509CertificateHolder) pem.readObject();
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        
        Certificate X509Certificate = new JcaX509CertificateConverter().setProvider("SC").getCertificate(certHolder);

        pem.close();
        reader.close();
        
        return X509Certificate;
	}
	
	
	
	private void pemToPKCS12(String privKeyFile, String pubKeyFile, String password) throws Exception {
		
		FileReader reader = new FileReader(privKeyFile);
		
		PEMParser pem = new PEMParser(reader);
        PEMKeyPair pemKeyPair = ((PEMKeyPair)pem.readObject());
        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter().setProvider("SC");
        KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);

        PrivateKey key = keyPair.getPrivate();

        pem.close();
        reader.close();
		
		//retrieve public key
		
        
        
        //put keys into keystore
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null);
        keyStore.setKeyEntry("alias", (Key) key, password.toCharArray(),
            new java.security.cert.Certificate[]{pubPemToPKCS12()});
        keyStore.store(bos, password.toCharArray());
        bos.close();
        //return bos.toByteArray();
        
        
		
	}
	
	private void initClient(String host, int port) {
		client = new HTTPSClient(host, port);
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, IOException, CertificateException {
		Wallet w = new Wallet();
	}

}
