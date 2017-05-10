package main.wallet;

import java.security.*;
import org.bouncycastle.util.encoders.Hex;

@SuppressWarnings("serial")
public class Message extends Transaction{
	
	private String signature;
	private String nonce;
	private String minerHash;
	

	public Message(double amount, String senderCert, String recieverCert, PrivateKey senderPrivKey) {
		super(amount, senderCert, recieverCert);
		signature = sign(super.toString(), senderPrivKey);
		nonce = null;
		minerHash = null;
		
	}

	private String sign(String message, PrivateKey senderPrivKey) {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
		    sig.initSign(senderPrivKey);
		    sig.update(message.getBytes());
		    byte[] signatureBytes = sig.sign();
		    System.out.println("Singature:" + new String(Hex.encode(signatureBytes)));

		    
			return "lol";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getNonce() {
		return nonce;
	}
	
	public String getMinerHash() {
		return minerHash;
	}

//	private String sign() throws NoSuchAlgorithmException {
//		MessageDigest digest = MessageDigest.getInstance("SHA-256");
//		byte[] hash = digest.digest(toString().getBytes(StandardCharsets.UTF_8));
//		String sha256hex = new String(Hex.encode(hash));
//		return sha256hex;
//	}
}
