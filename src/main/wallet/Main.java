package main.wallet;

import main.miner.HTTPSServer;

public class Main {
	public static void main(String args[]) {
		HTTPSServer server = new HTTPSServer();
		server.run();
		Wallet w = new Wallet();
	}
}
