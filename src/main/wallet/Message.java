package main.wallet;

import java.security.*;

import org.bouncycastle.util.encoders.Hex;

@SuppressWarnings("serial")
public class Message extends Transaction{
	
	private byte[] signature;
	private String nonce;
	private String minerHash;
	

//	public Message(byte inTransactionHashes[], int inTransactionIndices[], PublicKey outAddresses[], double outAmounts[], PrivateKey senderPrivKey) {
	public Message(PublicKey outAddresses[], double outAmounts[]) {	
		
		
		
		super(inTransactionHashes, inTransactionIndices, outAddresses, outAmounts);
		
		signature = sign(super.getMessage(), senderPrivKey);
		nonce = null;
		minerHash = null;
		
	}

	private byte[] sign(byte[] message, PrivateKey senderPrivKey) {
		try {
			Signature sig = Signature.getInstance("SHA256withRSA");
		    sig.initSign(senderPrivKey);
		    sig.update(message);
		    byte[] signatureBytes = sig.sign();
		    
			return signatureBytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public String getNonce() {
		return nonce;
	}
	
	public String getMinerHash() {
		return minerHash;
	}
	
	public Transaction getTransaction() {
		return super.getTransaction();
	}
	
//	@Override
//	public String toString() {
//		return "Message\n"
//				+ "\tAmount:       " + super.getAmount() + "\n"
//				+ "\tsenderCert:   " + super.getSenderCert() + "\n"
//				+ "\trecieverCert: " + super.getRecieverCert() + "\n"
//				+ "\tsignature:    " + new String(Hex.encode(signature));
//	}

//	private String sign() throws NoSuchAlgorithmException {
//		MessageDigest digest = MessageDigest.getInstance("SHA-256");
//		byte[] hash = digest.digest(toString().getBytes(StandardCharsets.UTF_8));
//		String sha256hex = new String(Hex.encode(hash));
//		return sha256hex;
//	}
}
