package main.wallet;

import java.security.PublicKey;
import java.util.Date;

@SuppressWarnings("serial")
public class Record extends Transaction {
	
	private Date timeStamp;
	
	public Record(double amount, PublicKey senderCert, PublicKey recieverCert) {
		super(amount, senderCert, recieverCert);
		timeStamp = new Date();
	}
	
	public long getTime() {
		return timeStamp.getTime();
	}
	
	@Override
	public String toString() {
		return "Record\n"
				+ "\tAmount:       " + super.getAmount() + "\n"
				+ "\tsenderCert:   " + super.getSenderCert().toString() + "\n"
				+ "\trecieverCert: " + super.getRecieverCert().toString() + "\n"
				+ "\ttimeStamp     " + new Date().toString();
	}
	
}
