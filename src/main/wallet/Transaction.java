package main.wallet;

import java.io.Serializable;

@SuppressWarnings("serial")
class Transaction implements Serializable{
	
	private double amount;
	private String senderCert;
	private String recieverCert;
	
	public Transaction(double amount, String senderCert, String recieverCert) {
		this.amount = amount;
		this.senderCert = senderCert;
		this.recieverCert = recieverCert;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getSenderCert() {
		return senderCert;
	}
	
	public String getRecieverCert() {
		return recieverCert;
	}

	@Override
	public String toString() {
		return "Transaction\n"
				+ "Amount:       " + amount + "\n"
				+ "senderCert:   " + senderCert + "\n"
				+ "recieverCert: " + recieverCert;
	}
}
