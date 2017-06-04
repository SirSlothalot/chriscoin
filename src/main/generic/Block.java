package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Block implements Serializable {

	private BlockHeader blockHeader;
	private int transactionCount;
	private HashMap<String, Transaction> transactions;

	public Block() {
		transactions = new HashMap<String, Transaction>();
		transactionCount = 0;
	}

	public void genHeader(byte[] prevBlockHash, int nonce, int diffTime) {
		Transaction[] trans = new Transaction[transactions.size()];
		transactions.values().toArray(trans);
		try {
			blockHeader = new BlockHeader(prevBlockHash, MerkleTree.root(trans), diffTime, nonce);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public HashMap<String, Transaction> addTransaction(Transaction trans) {
		byte[] transHash = null;
		Transaction tra = trans;
		try {
			transHash = Hasher.hash(trans);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String str = new String(transHash);
		transactions.put(str, tra);
		transactionCount++;
		return transactions;
	}
	
	public boolean isFull() {
		return (transactionCount >= 10);
	}

	public BlockHeader getBlockHeader() {
		return blockHeader;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	public HashMap<String, Transaction> getTransactions() {
		return transactions;
	}

	public boolean hasTransaction(byte[] transHash) {
		String str = new String(transHash);
		return transactions.containsKey(str);
	}
	
	public Transaction getTransaction(byte[] transHash) {
		String str = new String(transHash);
		return transactions.get(str);
	}
	
	@Override
	public String toString() {
		String temp = blockHeader.toString() + "\n";
		temp += "Transaction Count: " + transactionCount;
		return temp;
	}

}
