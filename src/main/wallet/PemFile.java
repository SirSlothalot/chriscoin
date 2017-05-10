package main.wallet;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;


public class PemFile {
	private PemObject pem;

	public PemFile (Key key, String description) {
		this.pem = new PemObject(description, key.getEncoded());
	}
	
	public PemFile (X509Certificate cert, String description) throws CertificateEncodingException {
		this.pem = new PemObject(description, cert.getEncoded());
	}
	
	public PemFile(String filename) throws FileNotFoundException, IOException {
		PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(filename)));
		try {
			this.pem = pemReader.readPemObject();
		} finally {
			pemReader.close();
		}
	}

	public void write(String filename) throws FileNotFoundException, IOException {
		JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		try {
			pemWriter.writeObject(this.pem);
		} finally {
			pemWriter.close();
		}
	}
	
//	public void write(X509Certificate cert) {
//		 try {
//			 Base64 encoder = new Base64();
//			 String cert_begin = "-----BEGIN CERTIFICATE-----\n";
//			 String end_cert = "-----END CERTIFICATE-----";
//	
//			 byte[] derCert = cert.getEncoded();
//			 String pemCertPre = new String(encoder.encode(derCert));
//			 String pemCert = cert_begin + pemCertPre + end_cert;
//			 
//			 
//			 
//		 } catch(Exception e) {
//			 e.printStackTrace();
//		 }
//	}
	
	public PemObject getPemObject() {
		return pem;
	}
}
