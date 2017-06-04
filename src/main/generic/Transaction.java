package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Transaction implements Serializable {

	int inputCounter;
	ArrayList<Input> ins;
	
	int outputCounter;
	ArrayList<Output> outs;
	
	Timestamp timeStamp;

	public Transaction() {
		timeStamp = new Timestamp(new Date().getTime());
		this.inputCounter = 0;
		this.ins = new ArrayList<Input>();
		this.outputCounter = 0;
		this.outs = new ArrayList<Output>();
	}

	public void addInput(byte[] parentTransactionHash, int parentOutputIndex) {
		ins.add(new Input(parentTransactionHash, parentOutputIndex));
		inputCounter++;
	}

	public void addOut(Double amount, PublicKey reciever) {
		outs.add(new Output(amount, reciever));
		outputCounter++;
	}
	
	public int getInputCount() {
		return inputCounter;
	}
	
	public int getOutputCount() {
		return outputCounter;
	}
	
	public byte[] getParentHash(int inIndex) {
		return ins.get(inIndex).getParentTransactionHash();
	}
	
	public int getParentOutputIndex(int inIndex) {
		return ins.get(inIndex).getParentOutputIndex();
	}
	
	public double getOutputAmount(int outIndex) {
		return outs.get(outIndex).getAmount();
	}
	
	public PublicKey getRecieverKey(int outIndex) {
		return outs.get(outIndex).getPubKey();
	}
	
	public int getOutputIndex(PublicKey pubKey) {
		for (int i = 0; i < outputCounter; i++) {
			if (getRecieverKey(i).equals(pubKey)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public String toString() {
		String temp;
		try {
			temp = "TransactionHash: " + Hasher.bytesToHex(Hasher.hash(this)) + "\n";
			temp += "Inputs ... Count: " + inputCounter + "\n";
			for (int i = 0; i < inputCounter; i++) {
				temp += ins.get(i).toString() + "\n";
			}
			temp += "Outputs ... Count: " + outputCounter + "\n";
			for (int i = 0; i < outputCounter; i++) {
				temp += outs.get(i).toString() + "\n";
			}
			temp += "-- End Transaction --";
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return temp;
		
	}

	private class Input implements Serializable {
		private byte[] parentTransactionHash;
		private int parentOutputIndex;

		public Input(byte[] parentTransactionHash, int parentOutputIndex) {
			this.parentTransactionHash = parentTransactionHash;
			this.parentOutputIndex = parentOutputIndex;
		}

		public byte[] getParentTransactionHash() {
			return parentTransactionHash;
		}

		public int getParentOutputIndex() {
			return parentOutputIndex;
		}
		
		@Override
		public String toString() {
			String temp;
			
			temp = "\tParent Transaction Hash: " + Hasher.bytesToHex(parentTransactionHash) + "\n"
					+ "\tParent Output Index: " + parentOutputIndex;
			
			return temp;
		}

	}
	
	private class Output implements Serializable {
		private double amount;
		PublicKey pubKey;
		
		public Output(double amount, PublicKey pubKey) {
			this.amount = amount;
			this.pubKey = pubKey;
		}

		public double getAmount() {
			return amount;
		}

		public PublicKey getPubKey() {
			return pubKey;
		}
		
		@Override
		public String toString() {
			String temp;
			
			temp = "\tAmount: " + amount + " CC\n"
					+ "\tReciever PubKey: " + pubKey.toString();
			
			return temp;
		}

	}
}
