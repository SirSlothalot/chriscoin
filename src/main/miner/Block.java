package main.miner;

import java.io.Serializable;

public class Block implements Serializable{
	
	int nonce;
	TestMessage msg;
	Block prev;
	byte[] prevHash;

	public Block(int n, TestMessage m, Block p,byte[] ph) {
		nonce = n;
		msg = m;
		prev = p;
		prevHash = ph;
	}
}
