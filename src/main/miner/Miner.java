package main.miner;

import main.generic.Hasher;
import main.generic.TestMessage;
import main.generic.Transaction;
import main.wallet.Wallet;
import main.generic.MerkleTree;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class Miner {

	private static boolean running;
	// private KeyStore keyStore;
	// private UpdatesRepository updates;

	// private HTTPSServer server;
	// private static final int PORT = 9999;

	/*
	 * Miner() { keyStore = Keys.initKeyStore(keyStore, "pass1");
	 * Keys.initKeys(keyStore, "pass1", "pass1");
	 * Keys.loadTrustedCertificates(keyStore); updates = new
	 * UpdatesRepository(); //load this from file!!! initServer();
	 * 
	 * }
	 * 
	 * private void initServer() { server = new HTTPSServer(this, keyStore,
	 * PORT); server.run(); }
	 */

	/*
	 * Finds nonce that when hashed with msg, fufils the test given the
	 * difficulty. Returns the int of the nonce that fufils the test.
	 */
	public static int proof(TestMessage msg, int difficulty) throws IOException {
		System.out.print("Finding proof of work...\n");
		// Random integer
		Random rn = new Random();
		int nonce = rn.nextInt();
		// Infinite Loop
		while (true) {
			// Converting data and int to byte[]
			byte[] data1 = serialize(msg);
			byte[] data2 = ByteBuffer.allocate(4).putInt(nonce).array();
			// Combining data into one byte[]
			byte[] combined = new byte[data1.length + data2.length];
			System.arraycopy(data1, 0, combined, 0, data1.length);
			System.arraycopy(data2, 0, combined, data1.length, data2.length);
			// Hashing (SHA256)
			byte[] hash = hash(combined);
			// Hash Test (More 0's = more difficulty, 3 ~ 2 minutes)
			Boolean test = true;
			for (int i = 0; i < difficulty; i++) {
				if (hash[i] != 0) {
					test = false;
				}
			}
			if (test) {
				System.out.print(nonce);
				System.out.print(" - ");
				System.out.print(Hasher.bytesToHex(hash));
				System.out.print("\n");
				;
				System.out.print("Proof of work found.\n");
				return nonce;
			} else {
				if (nonce == 2147483647) {
					nonce = -2147483648;
				} else {
					nonce++;
				}
			}
		}
	}

	/*
	 * Takes a single byte[] variable and performs SHA-256 hash and returns the
	 * hash in a byte[] format.
	 */
	public static byte[] hash(byte[] obj) {
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
	 * Takes a serializable object (...implements serializable) and converts it
	 * into a byte[0] format which is required for the hash function.
	 */
	public static byte[] serialize(Object obj) throws IOException {
		try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
			try (ObjectOutputStream o = new ObjectOutputStream(b)) {
				o.writeObject(obj);
			}
			return b.toByteArray();
		}
	}

	/*
	 * public ArrayList<Message> getUpdatesForClient(PublicKey pub) { return
	 * updates.getUpdate(pub); }
	 * 
	 * 
	 * public void addUpdateForClient(PublicKey client, Message message) {
	 * updates.addUpdate(client, message); }
	 */
	
	private void parseCommand(String command) {
		
	}
	
	public static void main(String[] args) {

		Miner miner = new Miner();
		Scanner scanner = new Scanner(System.in);
		running = true;
		while (running) {
			String command = scanner.nextLine();
			miner.parseCommand(command);
		}
		scanner.close();
		System.exit(0);
	}
}
