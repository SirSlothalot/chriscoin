package main.wallet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;



public class Keys {
	

	private static final String KEY_STORE_NAME = "./src/data/clientKeyStore.jks";
	private static final String PUB_KEY_FILE_NAME = "./src/data/clientPriv.pem";
	private static final String PRIV_KEY_FILE_NAME = "./src/data/clientPub.pem";
	
	static void initKeyStore(KeyStore keyStore, String keyStorePassword) {
		try{
		    keyStore = KeyStore.getInstance("PKCS12");
		    keyStore.load(null, null); 
		    keyStore.store(new FileOutputStream(KEY_STORE_NAME), keyStorePassword.toCharArray());
		} catch (Exception ex){
		    ex.printStackTrace();
		}
	}
	
	//https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
	//https://tls.mbed.org/kb/cryptography/asn1-key-structures-in-der-and-pem
	static void initKeys(KeyStore keyStore, String keyStorePassword, String privKeyPassword) {	
		try {
			
			CertAndKeyGen gen = new CertAndKeyGen("RSA","SHA256WithRSA");
		    gen.generate(2048);
		    
		    PrivateKey privKey = gen.getPrivateKey();
		    X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ClientWallet"), (long)365*24*3600); 
		    X509Certificate[] chain = new X509Certificate[1];
		    chain[0]=cert;
		    
		    keyStore = KeyStore.getInstance("PKCS12");
		    keyStore.load(new FileInputStream(KEY_STORE_NAME), keyStorePassword.toCharArray());
		    keyStore.setKeyEntry("private", privKey, privKeyPassword.toCharArray(), chain);
	        keyStore.setCertificateEntry("cert", cert);
	        keyStore.store(new FileOutputStream(KEY_STORE_NAME), keyStorePassword.toCharArray());

		    
		    
		    
		    
//			//initialize BouncyCastle
//			Security.addProvider(new BouncyCastleProvider());
//			
//			//initialize key generator
//		    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "BC");
//		    gen.initialize(2048);
//		    
//		    //create keys
//		    KeyPair keyPair = gen.genKeyPair();
//		    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//		    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	        
	        RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey("private", privKeyPassword.toCharArray());
		    
		    //save private key
		    PemFile privPem = new PemFile(privateKey, "RSA Private Key");
		    privPem.write(PRIV_KEY_FILE_NAME);
		    
		    //save public key
		    PemFile pubPem = new PemFile(cert, "RSA Public Key");
		    pubPem.write(PUB_KEY_FILE_NAME);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void readPemKeys() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
			
			PrivateKey priv = generatePrivateKey(factory, PRIV_KEY_FILE_NAME);			
			Certificate pub = generatePublicKey(factory, PUB_KEY_FILE_NAME);
			
			System.out.println(priv.toString());
			System.out.println(pub.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static PrivateKey generatePrivateKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
		return factory.generatePrivate(privKeySpec);
	}
	
	static Certificate generatePublicKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return (Certificate) factory.generatePublic(pubKeySpec);
	}
}
