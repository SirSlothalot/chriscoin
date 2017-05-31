package main.miner;

import main.generic.Hasher;
import main.generic.TestMessage;
import main.generic.MerkleTree;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Miner {

	//private KeyStore keyStore;
	//private UpdatesRepository updates;

	//private HTTPSServer server;
	//private static final int PORT = 9999;
	
	/*Miner() {
		keyStore = Keys.initKeyStore(keyStore, "pass1");
		Keys.initKeys(keyStore, "pass1", "pass1");
	    Keys.loadTrustedCertificates(keyStore);
	    updates = new UpdatesRepository(); //load this from file!!! 
	    initServer();
	    
	}
	
	private void initServer() {
		server = new HTTPSServer(this, keyStore, PORT);
		server.run();
	}*/
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException{
		// XX: Miner w = new Miner();
		
		// BlockChain blockchain = new BlockChain();

		
		TestMessage msg = new TestMessage("Alice",20);
		TestMessage msg2 = new TestMessage("Bob",20);
		TestMessage[] msgs = {msg,msg,msg,msg};
		TestMessage[] msgs2 = {msg2, msg2, msg2, msg2, msg2};
		int size = 1000;
		TestMessage[] msgs3 = new TestMessage[size];
		for (int i = 0; i < size; i++) {
			if (i % 3 == 0) {
				msgs3[i] = msg;
			} else {
				msgs3[i] = msg2;
			}
			
		}
		System.out.print("Root of msgs\n");
		System.out.print(Hasher.bytesToHex(MerkleTree.root(msgs)) + "\n");
		System.out.print("Root of msgs again\n");
		System.out.print(Hasher.bytesToHex(MerkleTree.root(msgs)) + "\n");
		System.out.print("Root of msgs2\n");
		System.out.print(Hasher.bytesToHex(MerkleTree.root(msgs2)) + "\n");
		System.out.print("Root of msgs2 again\n");
		System.out.print(Hasher.bytesToHex(MerkleTree.root(msgs2)) + "\n");
		System.out.print("Root of msgs3\n");
		System.out.print(Hasher.bytesToHex(MerkleTree.root(msgs3)) + "\n");
		System.out.print("Root of msgs3 again\n");
		System.out.print(Hasher.bytesToHex(MerkleTree.root(msgs3)) + "\n");
		
		
		
		long start = System.nanoTime();
		int nonce = proof(msg,1);
		long end = System.nanoTime();
		long elapsed = end - start;
		double seconds = (double)elapsed / 1000000000;
		System.out.print("Found nonce: " + nonce + " in " + seconds + " seconds."+ "\n");
		
		/*
		blockchain.add(nonce, msg);
		blockchain.add(nonce, msg);
		blockchain.add(nonce, msg);
		
		System.out.print(blockchain.top.prevHash + "\n");
		System.out.print(blockchain.top.prev.prevHash + "\n");
		System.out.print(blockchain.top.prev.prev.prevHash + "\n");
		*/
		
	}
	/*
	 * Finds nonce that when hashed with msg, fufils the test given the difficulty.
	 * Returns the int of the nonce that fufils the test.
	 */
	public static int proof(TestMessage msg, int difficulty) throws IOException{
		System.out.print("Finding proof of work...\n");
		// Random integer
		Random rn = new Random();
		int nonce = rn.nextInt();
		// Infinite Loop
		while(true){
			// Converting data and int to byte[]
			byte[] data1 = serialize(msg);
			byte[] data2 = ByteBuffer.allocate(4).putInt(nonce).array();
			// Combining data into one byte[]
			byte[] combined = new byte[data1.length + data2.length];
			System.arraycopy(data1,0,combined,0,data1.length);
			System.arraycopy(data2,0,combined,data1.length,data2.length);
			// Hashing (SHA256)
			byte[] hash = hash(combined);
			// Hash Test (More 0's = more difficulty, 3 ~ 2 minutes)
			Boolean test = true;
			for(int i = 0; i < difficulty; i++){
				if(hash[i] != 0){test = false;}
			}
			if(test){
				System.out.print(nonce);
				System.out.print(" - ");
				System.out.print(Hasher.bytesToHex(hash));
				System.out.print("\n");;
				System.out.print("Proof of work found.\n");
				return nonce;
			}
			else{
				if(nonce == 2147483647){nonce = -2147483648;}
				else{nonce++;}
			}
		}
	}
	
	/*
	 * Takes a single byte[] variable and performs SHA-256 hash and returns the hash
	 * in a byte[] format.
	 */
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
	

	/*
	 * Takes a serializable object (...implements serializable) and converts it into
	 * a byte[0] format which is required for the hash function.
	 */
	public static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }
	
	/*public ArrayList<Message> getUpdatesForClient(PublicKey pub) {
		return updates.getUpdate(pub);
	}
	
	
	public void addUpdateForClient(PublicKey client, Message message) {
		updates.addUpdate(client, message);
	}*/
}
