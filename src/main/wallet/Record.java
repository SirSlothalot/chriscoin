package main.wallet;


import org.json.simple.JSONObject;

public class Record {

	private JSONObject transaction;

	@SuppressWarnings("unchecked")
	Record(String senderCert, String receiverCert, double amount) {
		transaction = new JSONObject();
		transaction.put("senderCert", senderCert);
		transaction.put("recieverCert", receiverCert);
		transaction.put("amount", new Double(amount));
	}

	@Override
	public String toString() {
		return transaction.toString();
	}
}
