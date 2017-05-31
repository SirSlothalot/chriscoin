package main.generic;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class BlockHeaderChain implements Serializable {

	HashMap<byte[], BlockHeader> blockHeaderChain;
	byte[] topBlockHash;

	public BlockHeaderChain() {
		blockHeaderChain = new HashMap<byte[], BlockHeader>();
		topBlockHash = new byte[32];
	}

	public boolean put(BlockHeader blockHeader) {
		try {
			byte[] key = Hasher.hash(blockHeader);
			blockHeaderChain.put(key, blockHeader);
			topBlockHash = key;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public BlockHeader peek() {
		return blockHeaderChain.get(topBlockHash);
	}

	public byte[] getTopHash() {
		return topBlockHash;
	}

}
