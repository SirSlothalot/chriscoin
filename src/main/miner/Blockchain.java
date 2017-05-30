package main.miner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Blockchain {

	byte[] topHash;
	Block top;
	
	public Blockchain() {
		top = null;
		topHash = null;
	}
	
	public void add(int n,TestMessage m) throws IOException{
		top = new Block(n,m,top,topHash);
		byte[] topSrl = serialize(top);
		topHash = hash(topSrl);
	}
	
	private static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }
	
	private static byte[] hash(byte[] obj){
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(obj);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
