package main.miner;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;


public class PemFile {
	private PemObject pem;

	//Creates PEMObject from key
	public PemFile (Key key, String description) {
		this.pem = new PemObject(description, key.getEncoded());
	}
	
	//Creates PEMObject from cert
	public PemFile (X509Certificate cert, String description) throws CertificateEncodingException {
		this.pem = new PemObject(description, cert.getEncoded());
	}
	
	//Creates PEMObject from file
	public PemFile(String filename) throws FileNotFoundException, IOException {
		PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(filename)));
		try {
			this.pem = pemReader.readPemObject();
		} finally {
			pemReader.close();
		}
	}

	
	
	
	//Writes PEMObject to file
	public void write(String filename) throws FileNotFoundException, IOException {
		JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		try {
			pemWriter.writeObject(this.pem);
		} finally {
			pemWriter.close();
		}
	}
	
	public PemObject getPemObject() {
		return pem;
	}
}
