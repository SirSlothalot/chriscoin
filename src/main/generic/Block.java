package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Block implements Serializable {

	private BlockHeader blockHeader;
	private int transactionCount;
	private HashMap<byte[], Transaction> transactions;

	public Block() {
		transactions = new HashMap<byte[], Transaction>();
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

	public boolean addTransaction(Transaction trans) {
		try {
			transactions.put(Hasher.hash(trans), trans);
			transactionCount++;
			return true;
		} catch (Exception e) {
			return false;
		}
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

	public HashMap<byte[], Transaction> getTransactions() {
		return transactions;
	}

	public boolean hasTransaction(byte[] transHash) {
		return transactions.containsKey(transHash);
	}
	
	public Transaction getTransaction(byte[] transHash) {
		return transactions.get(transHash);
	}
	
	@Override
	public String toString() {
		String temp = blockHeader.toString() + "\n";
		temp += "Transaction Count: " + transactionCount;
		return temp;
	}

}
