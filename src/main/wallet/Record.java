package main.wallet;

import java.util.Date;

@SuppressWarnings("serial")
public class Record extends Transaction {
	
	private Date timeStamp;
	
	public Record(double amount, String senderCert, String recieverCert) {
		super(amount, senderCert, recieverCert);
		timeStamp = new Date();
	}
	
	public long getTime() {
		return timeStamp.getTime();
	}
	
	@Override
	public String toString() {
		return "Record\n"
				+ "Amount:       " + super.getAmount() + "\n"
				+ "senderCert:   " + super.getSenderCert() + "\n"
				+ "recieverCert: " + super.getRecieverCert() + "\n"
				+ "timeStamp     " + new Date().toString();
	}
	
}
