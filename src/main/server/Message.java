package main.server;


import org.json.simple.JSONObject;

public class Message {

	private JSONObject message;
	
	Message(String senderCert, String receiverCert, double amount) {
		message = new JSONObject();
		
		message.put("senderCert", senderCert);
		message.put("recieverCert", receiverCert);
		message.put("amount", new Double(amount));
		message.put("signature", sign(senderCert, receiverCert, amount));
	}
	
	private String sign(String senderCert, String recieverCert, double amount) {
		//do a SHA256 hash
		
		return "something";
	}
	
	public JSONObject getJSON() {
		return message;
	}
	
	public String toString() {
		return message.toString();
	}
}
