package main.wallet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

public class Keys {

	private static final String KEY_STORE_NAME = "./src/data/clientKeyStore.jks";
	private static final String PUB_KEY_FILE_NAME = "./src/data/clientPub.pem";
	private static final String PRIV_KEY_FILE_NAME = "./src/data/clientPriv.pem";
	private static final String CERT_FILE_NAME = "./src/data/clientCert.pem";
	
//	static void initKeyStore(KeyStore keyStore, String keyStorePassword) {
//		try{
//		    keyStore = KeyStore.getInstance("PKCS12");
//		    keyStore.load(null, null); 
//		    keyStore.store(new FileOutputStream(KEY_STORE_NAME), keyStorePassword.toCharArray());
//		} catch (Exception ex){
//		    ex.printStackTrace();
//		}
//	}
	
	//https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
	//https://tls.mbed.org/kb/cryptography/asn1-key-structures-in-der-and-pem
	static void initKeys(KeyStore keyStore, String keyStorePassword, String privKeyPassword) {	
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
		    
		    //store keys into keyStore
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
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void readPemKeys() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

			
			PrivateKey priv = generatePrivateKey(keyFactory, PRIV_KEY_FILE_NAME);			
//			Certificate cert = generateCert(certFactory, CERT_FILE_NAME);
			X509Certificate cert = convertToX509Certificate(CERT_FILE_NAME);
			
			System.out.println(priv.toString());
//			System.out.println(cert.toString());
			
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
	
	static PublicKey generatePublicKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemFile pemFile = new PemFile(filename);
		byte[] content = pemFile.getPemObject().getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return factory.generatePublic(pubKeySpec);
	}
		
	static X509Certificate generateCert(CertificateFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		
		InputStream inputStream;
	    X509Certificate cert = null;
	    try {
	        inputStream = filename.getClass().getResourceAsStream(filename);
	        cert = (X509Certificate)factory.generateCertificate(inputStream);
	        inputStream.close();
	    } catch(Exception e){
	        e.printStackTrace();
	    }
	    return cert;
	}
	
	static public X509Certificate convertToX509Certificate(String pem) {
        try {
        	X509Certificate cert = null;
        	StringReader reader = new StringReader(pem);
            PEMParser pr = new PEMParser(reader);
            cert = (X509Certificate)pr.readObject();
            return cert;
        } catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
        
    }
}
