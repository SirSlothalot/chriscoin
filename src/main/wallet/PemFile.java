package main.wallet;


import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Key;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;


public class PemFile {
	private PemObject pem;

	public PemFile (Key key, String description) {
		this.pem = new PemObject(description, key.getEncoded());
	}

	public void write(String filename) throws FileNotFoundException, IOException {
		JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		try {
			pemWriter.writeObject(this.pem);
		} finally {
			pemWriter.close();
		}
	}
}
