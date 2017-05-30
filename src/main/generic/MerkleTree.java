package main.generic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MerkleTree {	
	
	public static byte[] root(Object[] t) throws IOException{
		int len = t.length;
		byte[][] hashes = new byte[len][];
		for(int i = 0; i < len; i++){
			hashes[i] = serialize(t[i]);
			hashes[i] = hash(hashes[i]);
			hashes[i] = hash(hashes[i]);
		}
		
		double powerLen = Math.log(len)/Math.log(2);
		int newLen;
		if(powerLen % 1 != 0){
			newLen = 2;
			while(newLen < len){
				newLen = newLen*2;
			}
		}
		else{
			newLen = len;
		}
		byte[][] top = new byte[newLen][];
		
		for(int i = 0; i < len; i++){
			top[i] = hashes[i];
		}
		for(int i = len; i < newLen; i++){
			top[i] = hashes[len-1];
		}
		return combineHashes(top);
	}
	
	public static byte[] combineHashes(byte[][] hashes){
		if(hashes.length == 2){
			System.out.print("BEEP\n");
			byte[] left = hashes[0];
			byte[] right = hashes[1];
			byte[] combined = new byte[left.length + right.length];
			System.arraycopy(left,0,combined,0,left.length);
			System.arraycopy(right,0,combined,left.length,right.length);
			combined = hash(combined);
			combined = hash(combined);
			return combined;
		}
		else{
			byte[][] left = new byte[hashes.length/2][];
			byte[][] right = new byte[hashes.length/2][];
			for(int i = 0; i < hashes.length/2;i++){
				left[i] = hashes[i];
			}
			for(int i = hashes.length/2; i < hashes.length;i++){
				right[i-(hashes.length/2)] = hashes[i];
				
			}
			byte[] newLeft = combineHashes(left);
			byte[] newRight = combineHashes(right);
			byte[] combined = new byte[newLeft.length + newRight.length];
			System.arraycopy(newLeft,0,combined,0,newLeft.length);
			System.arraycopy(newRight,0,combined,newLeft.length,newRight.length);
			System.out.print("END\n");
			return combined;
		}
	}
	
	public static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }
	
	public static byte[] hash(byte[] obj){
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
