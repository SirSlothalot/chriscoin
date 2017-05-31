package main.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Keys {

	/*
	 * Checks if keyStore exists - Yes - load existing keyStore - No - create
	 * new keyStore
	 */
	static KeyStore initKeyStore(KeyStore keyStore, String keyStorePassword) {
		try {
			keyStore = KeyStore.getInstance("PKCS12");
			InputStream file = Keys.class.getResourceAsStream(Constants.KEY_STORE_NAME);
			if (file != null) {
				keyStore.load(Keys.class.getResourceAsStream(Constants.KEY_STORE_NAME), keyStorePassword.toCharArray());
				return keyStore;
			} else {
				keyStore.load(null, null);
				System.out.println("hey");
				keyStore.store(new FileOutputStream(Constants.KEY_STORE_NAME), keyStorePassword.toCharArray());
				System.out.println("hey 2");
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
	static void initKeys(KeyStore keyStore, String keyStorePassword, String privKeyPassword) {

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

				PrivateKey privKey = pemToPrivateKey(Constants.PRIV_KEY_FILE);
				X509Certificate cert = pemToCert(Constants.CERT_FILE);
				X509Certificate[] chain = new X509Certificate[1];
				chain[0] = cert;

				// store private key and certificate into keyStore
				keyStore.setKeyEntry(Constants.PRIVATE_KEY_NAME, privKey, privKeyPassword.toCharArray(), chain);
				keyStore.setCertificateEntry(Constants.CERT_NAME, cert);

				// save private key as PEM file
				PemFile privPem = new PemFile(privKey, "RSA Private Key");
				privPem.write(Constants.PRIV_KEY_FILE);

				// save cert as PEM file
				PemFile certPem = new PemFile(cert, "CERTIFICATE");
				certPem.write(Constants.CERT_FILE);

				// save keyStore with new keys
				keyStore.store(new FileOutputStream(Constants.KEY_STORE_NAME), keyStorePassword.toCharArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void loadTrustedCertificates(KeyStore keyStore) {
		File dir = new File(Constants.TRUSTED_CERTS_DIR);
		File[] files = dir.listFiles();

		X509Certificate cert;

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				cert = pemToCert(files[i].getPath());
				if (cert != null) {
					try {
						keyStore.setCertificateEntry("peer-certificate-" + i, cert);
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

	private static PrivateKey pemToPrivateKey(String filename) {
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
	private static X509Certificate pemToCert(String pem) {
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
