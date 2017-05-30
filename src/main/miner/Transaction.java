package main.miner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;

@SuppressWarnings("serial")
class Transaction implements Serializable{
	
	private double amount;
	private PublicKey senderCert;
	private PublicKey recieverCert;
	
	public Transaction(double amount, PublicKey senderCert, PublicKey recieverCert) {
		this.amount = amount;
		this.senderCert = senderCert;
		this.recieverCert = recieverCert;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public PublicKey getSenderCert() {
		return senderCert;
	}
	
	public PublicKey getRecieverCert() {
		return recieverCert;
	}
	
	public byte[] getMessage() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(this);
		  out.flush();
		  bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
		    try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	public Transaction getTransaction() {
		return this;
	}

	@Override
	public String toString() {
		return "Transaction\n"
				+ "\tAmount:       " + amount + "\n"
				+ "\tsenderCert:   " + senderCert.toString() + "\n"
				+ "\trecieverCert: " + recieverCert.toString();
	}
}
