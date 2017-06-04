package main.generic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

@SuppressWarnings("serial")
public class Message implements Serializable {
	private byte[] signedTransaction;
	private PublicKey publicKey;
	private Transaction transaction;
	
	public Message(Transaction transaction, PrivateKey privateKey, PublicKey publicKey) {
		this.publicKey = publicKey;
		this.transaction = transaction;
		signedTransaction = sign(transaction, privateKey);
		
	}
	
	private byte[] sign(Transaction transaction, PrivateKey privateKey) {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
		    sig.initSign(privateKey);
		    sig.update(getByteTransaction());
		    byte[] signatureBytes = sig.sign();
			return signatureBytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public byte[] getByteTransaction() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(transaction);
			out.flush();
			byte[] bytes = bos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}   
	}
	
	
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public byte[] getSignedTransaction() {
		return signedTransaction;
	}
}
