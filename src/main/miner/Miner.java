package main.miner;

import java.security.KeyStore;

import main.miner.HTTPSServer;
import main.miner.Keys;
import main.wallet.HTTPSClient;
import main.wallet.Message;
import main.wallet.Wallet;

public class Miner {

	private KeyStore keyStore;

	private HTTPSServer server;
	private static final int PORT = 9999;
	
	Miner() {
		keyStore = Keys.initKeyStore(keyStore, "pass1");
		Keys.initKeys(keyStore, "pass1", "pass1");
	    Keys.loadTrustedCertificates(keyStore);
	    initServer();
	}
	
	private void initServer() {
		server = new HTTPSServer(this, keyStore, PORT);
		server.run();
	}
	
	public static void main(String[] args) {
		Miner w = new Miner();
	}
}
