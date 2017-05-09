package main.wallet;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;
import org.json.simple.JSONObject;

public class Message {

	private JSONObject message;

	Message(String senderCert, String receiverCert, double amount) throws NoSuchAlgorithmException {
		message = new JSONObject();

		message.put("senderCert", senderCert);
		message.put("recieverCert", receiverCert);
		message.put("amount", new Double(amount));
		message.put("signature", sign());
	}

	private String sign() throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(message.toJSONString().getBytes(StandardCharsets.UTF_8));
		String sha256hex = new String(Hex.encode(hash));
		return sha256hex;
	}

	public JSONObject getJSON() {
		return message;
	}

	@Override
	public String toString() {
		return message.toString();
	}
}
