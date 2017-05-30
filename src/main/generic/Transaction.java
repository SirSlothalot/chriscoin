package main.generic;

import java.io.Serializable;
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
	}

	public void addOut(Double amount) {
		outs.add(amount);
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

	}
}
