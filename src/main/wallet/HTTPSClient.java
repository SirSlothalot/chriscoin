package main.wallet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import main.generic.Message;

public class HTTPSClient {
	private String host = "127.0.0.1";
	private int port = 9999;

	private KeyStore keyStore;
	private Wallet wallet;

	HTTPSClient(Wallet wallet, KeyStore keyStore, String host, int port) {
		this.wallet = wallet;
		this.host = host;
		this.port = port;
		this.keyStore = keyStore;
	}

	// Create the and initialize the SSLContext
	private SSLContext createSSLContext() {
		try {
			// Create key manager
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keyStore, "pass1".toCharArray());
			KeyManager[] km = keyManagerFactory.getKeyManagers();

			// Create trust manager
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(keyStore);
			TrustManager[] tm = trustManagerFactory.getTrustManagers();

			// Initialize SSLContext
			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			sslContext.init(km, tm, null);

			return sslContext;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	
	// Start to run the server
		public boolean run(PublicKey publicKey) {
			SSLContext sslContext = this.createSSLContext();

			try {
				// Create socket factory
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

				// Create socket
				SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(this.host, this.port);

				System.out.println("SSL client started:");
				ClientThread thread = new ClientThread(sslSocket, wallet, publicKey);
				thread.start();
				thread.join();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	
	
	// Start to run the server
	public boolean run(Object message) {
		SSLContext sslContext = this.createSSLContext();

		try {
			// Create socket factory
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			// Create socket
			SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(this.host, this.port);

			System.out.println("SSL client started:");
			ClientThread thread = new ClientThread(sslSocket, wallet, message);
			thread.start();
			thread.join();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Thread handling the socket to server
	static class ClientThread extends Thread {
		private SSLSocket sslSocket;
		private Wallet wallet;
		private Object outgoingMessage;
		private PublicKey publicKey;

		ClientThread(SSLSocket sslSocket, Wallet wallet, PublicKey publicKey) {
			this.sslSocket = sslSocket;
			this.wallet = wallet;
			this.outgoingMessage = null;
			this.publicKey = publicKey;
		}
		
		ClientThread(SSLSocket sslSocket, Wallet wallet, Object message) {
			this.sslSocket = sslSocket;
			this.wallet = wallet;
			this.outgoingMessage = message;
			this.publicKey = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
			try {
				// Start handshake
				sslSocket.startHandshake();

				// Get session after the connection is established
				SSLSession sslSession = sslSocket.getSession();

				System.out.println("SSLSession :");
				System.out.println("\tProtocol : " + sslSession.getProtocol());
				System.out.println("\tCipher suite : " + sslSession.getCipherSuite());

				// Initialize streams
				ObjectOutputStream outputStream = new ObjectOutputStream(sslSocket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(sslSocket.getInputStream());
				PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				if(outgoingMessage == null) {
					// Request update
					printWriter.println("Request update");
					printWriter.flush();
					
					outputStream.writeObject(publicKey);
					outputStream.flush();

					// Receive update
					String line = null;
					ArrayList<Message> incomingMessages = null;

					while ((line = bufferedReader.readLine()) != null) {
						System.out.println("Inut : " + line);
						if (line.trim().equals("Imbound message")) {
							while ((incomingMessages = (ArrayList<Message>) inputStream.readObject()) != null) {
								wallet.receiveMessages(incomingMessages);
								incomingMessages = null;
								printWriter.println("Client received message");
								printWriter.flush();
								break;
							}
						} else if (line.trim().equals("No new messages for client")) {
							break;
						}
					}
				} else {
					printWriter.println("Imbound message");
					printWriter.flush();

					outputStream.writeObject(outgoingMessage);
					outputStream.flush();

					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						System.out.println("Inut : " + line);

						if (line.trim().equals("Miner received message")) {
							break;
						}
					}
				}

				sslSocket.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
