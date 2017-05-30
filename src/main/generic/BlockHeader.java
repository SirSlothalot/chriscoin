package main.generic;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class BlockHeader implements Serializable {
	private byte[] prevBlockHeadHash;
	private byte[] merkleRoot;
	private Date timeStamp;
	private int difficultyTime;
	private byte[] nonce;

	public BlockHeader(byte[] prevBlockHash, byte[] merkleRoot, int diffTime, byte[] nonce) {
		this.prevBlockHeadHash = prevBlockHash;
		this.merkleRoot = merkleRoot;
		timeStamp = new Date();
		difficultyTime = diffTime;
		this.nonce = nonce;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public byte[] getNonce() {
		return nonce;
	}

	public byte[] getMerkleRoot() {
		return merkleRoot;
	}

	public byte[] getPrevBlockHeadHash() {
		return prevBlockHeadHash;
	}

	public int getDifficultyTime() {
		return difficultyTime;
	}
}