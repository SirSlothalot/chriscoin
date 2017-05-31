package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Transaction implements Serializable {

	int inputCounter;
	ArrayList<Input> ins;
	
	int outputCounter;
	ArrayList<Double> outs;

	public Transaction() {
		this.inputCounter = 0;
		this.ins = new ArrayList<Input>();
		this.outputCounter = 0;
		this.outs = new ArrayList<Double>();
	}

	public void addInput(byte[] parentTransactionHash, int parentOutputIndex) {
		ins.add(new Input(parentTransactionHash, parentOutputIndex));
		inputCounter++;
	}

	public void addOut(Double amount) {
		outs.add(amount);
		outputCounter++;
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
				temp += "\tIndex: " + i + " ... Amount: " + outs.get(i) + " CC\n";
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
}
