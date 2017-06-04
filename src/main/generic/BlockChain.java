package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;

@SuppressWarnings("serial")
public class BlockChain implements Serializable {

	private HashMap<String, Block> blockChain;
	private byte[] topBlockHash;

	public BlockChain() {
		blockChain = new HashMap<String, Block>();
		topBlockHash = new byte[32];
	}

	public boolean put(Block block) {
		try {
			byte[] key = Hasher.hash(block.getBlockHeader());
			String str = new String(key);
			blockChain.put(str, block);
			topBlockHash = key;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Block peek() {
		return blockChain.get(new String(topBlockHash));
	}

	public byte[] getTopHash() {
		return topBlockHash;
	}

	public Block findBlock(byte[] transHash) {
		String str = new String(transHash);
		Block curBlock = blockChain.get(new String(topBlockHash));

		while (!curBlock.hasTransaction(transHash)) {
			if (blockChain.containsKey(new String(curBlock.getBlockHeader().getPrevBlockHeadHash()))) {
				curBlock = blockChain.get(new String(curBlock.getBlockHeader().getPrevBlockHeadHash()));
			} else {
				return null;
			}
		}
		
		return curBlock;

	}

	public PublicKey findTransactionReciever(byte[] transHash, int outIndex) {

		Block curBlock = blockChain.get(new String(topBlockHash));
		while (!curBlock.hasTransaction(transHash)) {
			System.err.println(curBlock.toString());
			if (blockChain.containsKey(new String(curBlock.getBlockHeader().getPrevBlockHeadHash()))) {
				curBlock = blockChain.get(new String(curBlock.getBlockHeader().getPrevBlockHeadHash()));
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
		return blockChain.get(new String(blockHash));
	}

	public BlockHeaderChain genBlockHeaderChain() {
		BlockHeaderChain headChain = new BlockHeaderChain();
		byte[] currentHash = topBlockHash;
		String str = new String(currentHash);
		while (blockChain.containsKey(str)) {
			headChain.put(blockChain.get(str).getBlockHeader());
			currentHash = blockChain.get(str).getBlockHeader().getPrevBlockHeadHash();
			if(currentHash != null) {
				str = new String(currentHash);
			} else {
				break;
			}
			
		}
		return headChain;
	}

	@Override
	public String toString() {
		String temp = "-- BlockChain --\n";
		byte[] currentHash = topBlockHash;
		String str = new String(currentHash);
		while (blockChain.containsKey(str)) {
			temp += blockChain.get(str).toString() + "\n";
			currentHash = blockChain.get(str).getBlockHeader().getPrevBlockHeadHash();
			if(currentHash != null) {
				str = new String(currentHash);
			} else {
				break;
			}
		}
		return temp;
	}

}
