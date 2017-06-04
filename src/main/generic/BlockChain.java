package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;

@SuppressWarnings("serial")
public class BlockChain implements Serializable {

	private HashMap<byte[], Block> blockChain;
	private byte[] topBlockHash;

	public BlockChain() {
		blockChain = new HashMap<byte[], Block>();
		topBlockHash = new byte[32];
	}

	public boolean put(Block block) {
		try {
			byte[] key = Hasher.hash(block.getBlockHeader());
			blockChain.put(key, block);
			topBlockHash = key;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Block peek() {
		return blockChain.get(topBlockHash);
	}

	public byte[] getTopHash() {
		return topBlockHash;
	}

	public Block findBlock(byte[] transHash) {

		Block curBlock = blockChain.get(topBlockHash);

		while (!curBlock.hasTransaction(transHash)) {
			if (blockChain.containsKey(curBlock.getBlockHeader().getPrevBlockHeadHash())) {
				curBlock = blockChain.get(curBlock.getBlockHeader().getPrevBlockHeadHash());
			} else {
				return null;
			}
		}
		
		return curBlock;

	}

	public PublicKey findTransactionReciever(byte[] transHash, int outIndex) {

		Block curBlock = blockChain.get(topBlockHash);
		while (!curBlock.hasTransaction(transHash)) {
			System.err.println(curBlock.toString());
			if (blockChain.containsKey(curBlock.getBlockHeader().getPrevBlockHeadHash())) {
				curBlock = blockChain.get(curBlock.getBlockHeader().getPrevBlockHeadHash());
			} else {
				return null;
			}
		}

		return curBlock.getTransaction(transHash).getRecieverKey(outIndex);

	}

	public int getBlockCount() {
		return blockChain.size();
	}

	public Block getBlock(byte[] blockHash) {
		return blockChain.get(blockHash);
	}

	public BlockHeaderChain genBlockHeaderChain() {
		BlockHeaderChain headChain = new BlockHeaderChain();
		byte[] currentHash = topBlockHash;
		while (blockChain.containsKey(currentHash)) {
			headChain.put(blockChain.get(currentHash).getBlockHeader());
			currentHash = blockChain.get(currentHash).getBlockHeader().getPrevBlockHeadHash();
		}
		return headChain;
	}

	@Override
	public String toString() {
		String temp = "-- BlockChain --\n";
		byte[] currentHash = topBlockHash;
		while (blockChain.containsKey(currentHash)) {
			temp += blockChain.get(currentHash).toString() + "\n";
			currentHash = blockChain.get(currentHash).getBlockHeader().getPrevBlockHeadHash();
		}
		return temp;
	}

}
