package main.server;


import org.json.simple.JSONObject;

public class Record {

	private JSONObject transaction;
	
	Record(String senderCert, String receiverCert, double amount) {
		transaction = new JSONObject();
		
		transaction.put("senderCert", senderCert);
		transaction.put("recieverCert", receiverCert);
		transaction.put("amount", new Double(amount));	
	}
	
	public String toString() {
		return transaction.toString();
	}
	
	public static void main(String[] args){
		Record t = new Record(args[0], args[1], Double.parseDouble(args[2]));
	}
}
