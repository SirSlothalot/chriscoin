package main.wallet;

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

	@Override
	public String toString() {
		return "Transaction\n"
				+ "\tAmount:       " + amount + "\n"
				+ "\tsenderCert:   " + senderCert.toString() + "\n"
				+ "\trecieverCert: " + recieverCert.toString();
	}
}
