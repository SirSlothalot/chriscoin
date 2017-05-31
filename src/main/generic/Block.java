package main.generic;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Block {

	private int blockSize;
	private BlockHeader blockHeader;
	private int transactionCount;
	private HashMap<byte[], Transaction> transactions;

	public Block(Transaction trans) throws NoSuchAlgorithmException, IOException {
		transactions = new HashMap<byte[], Transaction>();
		transactionCount = 0;
		addTransaction(trans);

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

	public int getBlockSize() {
		return blockSize;
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

}
