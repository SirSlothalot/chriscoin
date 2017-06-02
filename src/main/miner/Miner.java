package main.miner;

import main.generic.Block;
import main.generic.BlockChain;
import main.generic.BlockHeaderChain;
import main.generic.Constants;
import main.generic.Hasher;
import main.generic.Keys;
import main.generic.Transaction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

import java.security.cert.Certificate;

public class Miner {

	private static boolean running;

	BlockChain blockChain;
	Block currentBlock;

	private boolean hasUpdates;

	private static int PORT = 9999;
	private HTTPSServer server;

	private UpdatesRepository updatesRepo;

	private KeyStore keyStore;

	Miner() {
		System.out.println("-- Miner Initialising --");

		keyStore = Keys.initKeyStore(keyStore, "pass1", Constants.DESKTOP_DIR + Constants.MINER_DIR);
		Keys.initKeys(keyStore, "pass1", "pass1", Constants.DESKTOP_DIR + Constants.MINER_DIR);
		Keys.loadTrustedCertificates(keyStore, Constants.DESKTOP_DIR);

		updatesRepo = loadUpdatesRepo();
		blockChain = loadBlockChain();
		currentBlock = loadCurrentBlock();

		server = new HTTPSServer(this, keyStore, PORT);
		server.run();
		System.out.println("-- Miner Initialised --");
	}

	private BlockChain loadBlockChain() {
		intialiseDirs();
		ObjectInput input = null;
		try {
			InputStream file = new FileInputStream(
					Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR + "blockchain.ser");
			InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream(buffer);
			BlockChain temp = (BlockChain) input.readObject();
			input.close();
			System.out.println("BlockChain loaded..." + "\nBlock count: " + temp.getBlockCount());
			return temp;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			return createBlockChain();
		}
	}

	private void saveBlockChain() {
		try {
			OutputStream file = new FileOutputStream(
					Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR + "blockchain.ser");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(blockChain);
			output.close();
			System.out.println("BlockChain saved..." + "\nBlock count: " + blockChain.getBlockCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BlockChain createBlockChain() {
		BlockChain blockChain = new BlockChain();
		Transaction genesisTrans = new Transaction();
		Enumeration<String> peers;
		try {
			peers = keyStore.aliases();
			while (peers.hasMoreElements()) {
				Certificate cert = keyStore.getCertificate(peers.nextElement());
				if (cert != null) {
					PublicKey pub = (PublicKey) cert.getPublicKey();
					genesisTrans.addOut(Constants.GENESIS_AMOUNT, pub);
				}
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		Block genesisBlock = new Block();
		genesisBlock.addTransaction(genesisTrans);
		try {
			genesisBlock.genHeader(null, proof(genesisBlock, 3), 3);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		blockChain.put(genesisBlock);

		addToRepositry(genesisBlock);

		return blockChain;
	}

	private Block loadCurrentBlock() {
		intialiseDirs();
		ObjectInput input = null;
		try {
			InputStream file = new FileInputStream(
					Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR + "currentblock.ser");
			InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream(buffer);
			Block temp = (Block) input.readObject();
			input.close();
			System.out.println("Current block loaded");
			return temp;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	private void saveCurrentBlock() {
		try {
			OutputStream file = new FileOutputStream(
					Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR + "currentblock.ser");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(currentBlock);
			output.close();
			System.out.println("Current block saved");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private UpdatesRepository loadUpdatesRepo() {
		intialiseDirs();
		ObjectInput input = null;
		try {
			InputStream file = new FileInputStream(
					Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR + "updatesRepo.ser");
			InputStream buffer = new BufferedInputStream(file);
			input = new ObjectInputStream(buffer);
			UpdatesRepository temp = (UpdatesRepository) input.readObject();
			input.close();
			System.out.println("Updates repository loaded");
			return temp;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} catch (IOException e) {
			return new UpdatesRepository();
		}
	}

	private void saveUpdatesRepo() {
		try {
			OutputStream file = new FileOutputStream(
					Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR + "updatesRepo.ser");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(updatesRepo);
			output.close();
			System.out.println("Updates repository saved");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Transaction> getUpdatesForClient(PublicKey pub) {
		return updatesRepo.getUpdate(pub);
	}

	private void intialiseDirs() {
		File dir = new File(Constants.DESKTOP_DIR + Constants.MINER_DIR + Constants.BLOCKCHAIN_DIR);
		dir.mkdirs();
		dir = new File((Constants.DESKTOP_DIR + Constants.TRUSTED_CERTS_DIR));
	}

	public synchronized void receiveTransaction(Transaction transaction) {
		System.out.println(transaction.toString());
		appendTransaction(transaction);
	}

	private void sendBlockHeaders() {
		BlockHeaderChain headChain = blockChain.genBlockHeaderChain();

		// TODO Send shit
	}

	private Block findTransactionBlock(Transaction trans) {
		try {
			return blockChain.getBlock(blockChain.findTransaction(Hasher.hash(trans)));
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void appendTransaction(Transaction trans) {
		if (!currentBlock.isFull()) {
			currentBlock.addTransaction(trans);
		} else {
			addBlockToChain(currentBlock);
			createNewBlock();
			currentBlock.addTransaction(trans);
		}
	}

	private void createNewBlock() {
		currentBlock = new Block();
	}

	private void addBlockToChain(Block block) {
		try {
			block.genHeader(blockChain.getTopHash(), proof(block, 3), 3);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		blockChain.put(block);
		addToRepositry(block);
		hasUpdates = true;
	}

	private void addToRepositry(Block block) {
		Object[] transArr = block.getTransactions().values().toArray();
		for (int k = 0; k < transArr.length; k++) {
			Transaction trans = (Transaction) transArr[k];
			for (int i = 0; i < trans.getInputCount(); i++) {
				updatesRepo.addUpdate(
						blockChain.getBlock(blockChain.findTransaction(trans.getParentHash(i)))
								.getTransaction(trans.getParentHash(i)).getRecieverKey(trans.getParentOutputIndex(i)),
						trans);
			}
			for (int o = 0; o < trans.getOutputCount(); o++) {
				updatesRepo.addUpdate(trans.getRecieverKey(o), trans);
			}
		}
	}

	/*
	 * Finds nonce that when hashed with msg, fulfills the test given the
	 * difficulty. Returns the int of the nonce that fufils the test.
	 */
	public static int proof(Block block, int difficulty) throws IOException, NoSuchAlgorithmException {
		System.out.print("Finding proof of work...\n");
		// Random integer
		Random rn = new Random();
		int nonce = rn.nextInt();
		// Infinite Loop
		while (true) {
			// Converting data and int to byte[]
			byte[] data1 = Hasher.serialize(block);
			byte[] data2 = ByteBuffer.allocate(4).putInt(nonce).array();
			// Combining data into one byte[]
			byte[] combined = new byte[data1.length + data2.length];
			System.arraycopy(data1, 0, combined, 0, data1.length);
			System.arraycopy(data2, 0, combined, data1.length, data2.length);
			// Hashing (SHA256)
			byte[] hash = Hasher.hash(combined);
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
	 * public ArrayList<Message> getUpdatesForClient(PublicKey pub) { return
	 * updates.getUpdate(pub); }
	 * 
	 * 
	 * public void addUpdateForClient(PublicKey client, Message message) {
	 * updates.addUpdate(client, message); }
	 */

	private void printBlockChain() {
		System.out.println(blockChain.toString());
	}

	public void shutdown() {
		System.out.println("-- Miner Saving --");
		saveBlockChain();
		saveCurrentBlock();
		saveUpdatesRepo();
		System.out.println("-- Miner Shutdown --");
	}

	private void parseCommand(String command) {
		String[] commands = command.split(" ");

		if (commands.length < 1) {
			return;
		}
		commands[0].toLowerCase();

		if (commands[0].equals("blockchain")) {
			if (commands.length > 1) {
				if (commands[1].equals("view")) {
					printBlockChain();
				}
			} else {
				System.err.println("blockchain requires an argument");
			}

		} else if (commands[0].equals("help")) {
			printHelp();
		} else if (commands[0].equals("exit")) {
			running = false;
		} else {
			System.err.println("'" + command + "' is not a valid command");
		}

	}

	private static void printHelp() {
		System.out.println("-- List of commands and purposes --");
		System.out.println();
		System.out.println("blockchain view");
		System.out.println("- Prints out the BlockChain");
		System.out.println("exit");
		System.out.println("- exits the program");

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
		miner.shutdown();
		System.exit(0);
	}
}
