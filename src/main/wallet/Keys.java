package main.wallet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

public class Keys {

	private static final String KEY_STORE_NAME = "./src/data/clientKeyStore.jks";
	private static final String PRIV_KEY_FILE_NAME = "./src/data/clientPriv.pem";
	private static final String CERT_FILE_NAME = "./src/data/clientCert.pem";
		
	//https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
	//https://tls.mbed.org/kb/cryptography/asn1-key-structures-in-der-and-pem
	static KeyStore initKeys(KeyStore keyStore, String keyStorePassword, String privKeyPassword) {	
		try {
			 //initialize KeyStore
		    keyStore = KeyStore.getInstance("PKCS12");
		    keyStore.load(null,null);
			
		    //initialize key generator
			CertAndKeyGen gen = new CertAndKeyGen("RSA","SHA256WithRSA");
		    gen.generate(2048);
		    
		    //create keys
		    PrivateKey privKey = gen.getPrivateKey();
		    X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ClientWallet"), (long)365*24*3600); 
		    X509Certificate[] chain = new X509Certificate[1];
		    chain[0]=cert;
		    
		    //store private into keyStore
		    keyStore.setKeyEntry("private", privKey, privKeyPassword.toCharArray(), chain);
	        keyStore.setCertificateEntry("cert", cert);
	        
	        //save keyStore
	        keyStore.store(new FileOutputStream(KEY_STORE_NAME), keyStorePassword.toCharArray());

	        //save private key as PEM file
		    PemFile privPem = new PemFile(privKey, "RSA Private Key");
		    privPem.write(PRIV_KEY_FILE_NAME);
		    
		    //save cert as PEM file
		    PemFile certPem = new PemFile(cert, "CERTIFICATE");
		    certPem.write(CERT_FILE_NAME);
		    
		    readPemKeys();
		    
		    return keyStore;
		    
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static void readPemKeys() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

			PrivateKey priv = pemToPrivateKey(keyFactory, PRIV_KEY_FILE_NAME);			
			X509Certificate cert = pemToCert(CERT_FILE_NAME);
			
			System.out.println(priv.toString());
			String str = cert.toString();
			System.out.println(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static PrivateKey pemToPrivateKey(KeyFactory factory, String filename) {
		try {
			PemFile pemFile = new PemFile(filename);
			byte[] content = pemFile.getPemObject().getContent();
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
			return factory.generatePrivate(privKeySpec);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static public X509Certificate pemToCert(String pem) {
		try {
	        CertificateFactory fact = CertificateFactory.getInstance("X.509");
	        FileInputStream is = new FileInputStream (pem);
	        X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
	        return cert;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
    }
}
