package main.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Keys {

	/*
	 * Checks if keyStore exists - Yes - load existing keyStore - No - create
	 * new keyStore
	 */
	public static KeyStore initKeyStore(KeyStore keyStore, String keyStorePassword, String path) {
		try {
			keyStore = KeyStore.getInstance("PKCS12");
			InputStream file = Keys.class.getResourceAsStream(path + Constants.KEY_STORE_NAME);
			if (file != null) {
				keyStore.load(Keys.class.getResourceAsStream(path + Constants.KEY_STORE_NAME), keyStorePassword.toCharArray());
				return keyStore;
			} else {
				keyStore.load(null, null);
				keyStore.store(new FileOutputStream(path + Constants.KEY_STORE_NAME), keyStorePassword.toCharArray());
				return keyStore;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Checks if keyStore contains private key and certificate

	// https://www.txedo.com/blog/java-generate-rsa-keys-write-pem-file/
	// https://tls.mbed.org/kb/cryptography/asn1-key-structures-in-der-and-pem
	public static void initKeys(KeyStore keyStore, String keyStorePassword, String privKeyPassword, String path) {

		try {
			if (!keyStore.containsAlias(Constants.PRIVATE_KEY_NAME) || !keyStore.containsAlias(Constants.CERT_NAME)) {
				// initialize key generator
				// CertAndKeyGen gen = new CertAndKeyGen("RSA","SHA256WithRSA");
				// gen.generate(2048);

				// //create keys
				// PrivateKey privKey = gen.getPrivateKey();
				// X509Certificate cert = gen.getSelfCertificate(new
				// X500Name("CN=ClientWallet"), (long)365*24*3600);
				// X509Certificate[] chain = new X509Certificate[1];
				// chain[0]=cert;

				// String[] cmd = new String[]{"/bin/sh", CERT_GENERATOR};
				// Runtime rt = Runtime.getRuntime();
				// Process proc = rt.exec(cmd);
				// proc.waitFor();

				PrivateKey privKey = pemToPrivateKey(path + Constants.PRIV_KEY_FILE);
				X509Certificate cert = pemToCert(path + Constants.CERT_FILE);
				X509Certificate[] chain = new X509Certificate[1];
				chain[0] = cert;

				// store private key and certificate into keyStore
				keyStore.setKeyEntry(Constants.PRIVATE_KEY_NAME, privKey, privKeyPassword.toCharArray(), chain);
				keyStore.setCertificateEntry(Constants.CERT_NAME, cert);

				// save private key as PEM file
				PemFile privPem = new PemFile(privKey, "RSA Private Key");
				privPem.write(path + Constants.PRIV_KEY_FILE);

				// save cert as PEM file
				PemFile certPem = new PemFile(cert, "CERTIFICATE");
				certPem.write(path + Constants.CERT_FILE);

				// save keyStore with new keys
				keyStore.store(new FileOutputStream(path + Constants.KEY_STORE_NAME), keyStorePassword.toCharArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadTrustedCertificates(KeyStore keyStore, String path) {
		File dir = new File(path + Constants.TRUSTED_CERTS_DIR);
		File[] files = dir.listFiles();

		X509Certificate cert;

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				cert = pemToCert(files[i].getPath());
				if (cert != null) {
					try {
						X500Principal p = cert.getSubjectX500Principal();
						String name = p.getName();
						String[] distinguishedNames = name.split(",");
						name = distinguishedNames[0].substring(3);
						keyStore.setCertificateEntry(name, cert);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// private static void printPemPrivateKey(String file) {
	// try {
	// PrivateKey priv = pemToPrivateKey(Constants.PRIV_KEY_FILE);
	// System.out.println(priv.toString());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private static void printPemCert(String file) {
	// try {
	// X509Certificate cert = pemToCert(Constants.CERT_FILE);
	// System.out.println(cert.toString());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static PrivateKey pemToPrivateKey(String filename) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
			PemFile pemFile = new PemFile(filename);
			byte[] content = pemFile.getPemObject().getContent();
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
			return factory.generatePrivate(privKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// http://stackoverflow.com/questions/24137463/how-to-load-public-certificate-from-pem-file
	public static X509Certificate pemToCert(String pem) {
		String extension = pem.substring(pem.lastIndexOf(".") + 1, pem.length());
		if (extension.equals("pem")) {
			try {
				CertificateFactory fact = CertificateFactory.getInstance("X.509");
				FileInputStream is = new FileInputStream(pem);
				X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
				return cert;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
}
