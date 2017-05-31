package main.generic;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("serial")
public class BlockChain implements Serializable {

	HashMap<byte[], Block> blockChain;
	byte[] topBlockHash;

	public BlockChain() {
		blockChain = new HashMap<byte[], Block>();
		topBlockHash = new byte[32];
	}

	public boolean put(Block block) {
		try {
			Random rn = new Random();
			int nonce = rn.nextInt();
			block.genHeader(topBlockHash, nonce, 3);
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

	public byte[] findTransaction(byte[] transHash) {
		Block curBlock = blockChain.get(topBlockHash);

		while (!curBlock.hasTransaction(transHash)) {
			if (blockChain.containsKey(curBlock.getBlockHeader().getPrevBlockHeadHash())) {
				curBlock = blockChain.get(curBlock.getBlockHeader().getPrevBlockHeadHash());
			} else {
				return null;
			}
		}

		try {
			return Hasher.hash(curBlock.getBlockHeader());
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}
