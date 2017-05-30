package main.wallet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;

@SuppressWarnings("serial")
class Transaction implements Serializable{
	
//	private double amount;
//	private PublicKey senderCert;
//	private PublicKey recieverCert;
	
	private int numInputs;
	private int numOutputs;
	
	private byte[] prevTransactionHashes; 	//references to previous transactions - where the money comes from (32 bytes - merkle tree?)
	private int[]	 prevTransactionIndices; 	//the index within the previous transaction
	
	private PublicKey[] outAddresses;		//the addresses of receivers
	private double[] outAmounts;			//the amounts each address is receiving
	
	private byte[] signature;
	private byte[] nonce;
	private byte[] minerHash;
	
	public Transaction(byte[] prevTransactionHashes, int[] prevTransactionIndices, PublicKey[] outAddresses, double[] outAmounts) {
		this.prevTransactionHashes = prevTransactionHashes;
		this.prevTransactionIndices = prevTransactionIndices;
		this.outAddresses = outAddresses;
		this.outAmounts = outAmounts;
		this.numInputs = this.prevTransactionHashes.length;
		this.numOutputs = this.outAddresses.length;
	}
	
	public double[] getAmounts() {
		return outAmounts;
	}
	
	public PublicKey[] getRecieverCerts() {
		return outAddresses;
	}
	
//	public PublicKey[] getSenderCerts() {
//		return senderCert;
//	}
	
	public byte[] getMessage() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(this);
		  out.flush();
		  bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
		    try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	public Transaction getTransaction() {
		return this;
	}

//	@Override
//	public String toString() {
//		return "Transaction\n"
//				+ "\tAmount:       " + amount + "\n"
//				+ "\tsenderCert:   " + senderCert.toString() + "\n"
//				+ "\trecieverCert: " + recieverCert.toString();
//	}
}
