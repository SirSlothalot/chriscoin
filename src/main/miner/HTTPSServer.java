package main.miner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import main.generic.Transaction;

public class HTTPSServer {
    private int port = 9999;
    private boolean isServerDone = false;
    
    private KeyStore keyStore;
    private Miner miner;

    public static void main(String[] args){
        HTTPSServer server = new HTTPSServer();
        server.run();
    }

    public HTTPSServer(){
    }

    HTTPSServer(int port){
        this.port = port;
    }
    
    HTTPSServer(Miner miner, KeyStore keyStore, int port){
    	this.miner = miner;
    	this.keyStore = keyStore;
    	this.port = port;
    }

    // Create the and initialize the SSLContext
    private SSLContext createSSLContext(){
        try{            
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
            
//          SecureRandom random = new SecureRandom();
//          byte bytes[] = new byte[20];
//          random.nextBytes(bytes);            
//          sslContext.init(km,  tm, random);
            
            sslContext.init(km, tm, null);
            
            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    // Start to run the server
    public void run(){
        SSLContext sslContext = this.createSSLContext();

        try{
            // Create server socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);

            ServerController serverController = new ServerController(sslServerSocket, miner);
            serverController.start();
            
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    
    static class ServerController extends Thread {
    	private Boolean isServerDone;
    	SSLServerSocket sslServerSocket;
    	Miner miner;
    	ServerController(SSLServerSocket sslServerSocket, Miner miner) throws IOException {
    		isServerDone = false;
    		this.sslServerSocket = sslServerSocket;
    		this.miner = miner;
    	}
    	
    	@Override
    	public void run() {
            System.out.println("SSL server started");

    		while(!isServerDone){
                SSLSocket sslSocket;
				try {
					sslSocket = (SSLSocket) sslServerSocket.accept();
					
					// Start the server thread
					new ServerThread(sslSocket, miner).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
    	}
    }
    
    
    // Thread handling the socket from client
    static class ServerThread extends Thread {
        private SSLSocket sslSocket;
        private Miner miner;
        

        ServerThread(SSLSocket sslSocket, Miner miner) {
            this.sslSocket = sslSocket;
            this.miner = miner;
        }

        @Override
		public void run(){
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            

            try{
                // Start handshake
                sslSocket.startHandshake();

                // Get session after the connection is established
                SSLSession sslSession = sslSocket.getSession();

                System.out.println("SSLSession :");
                System.out.println("\tProtocol : "+sslSession.getProtocol());
                System.out.println("\tCipher suite : "+sslSession.getCipherSuite());

                //Initialize streams
                ObjectOutputStream outputStream = new ObjectOutputStream(sslSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(sslSocket.getInputStream());
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                
                String line = null;
                Transaction message = null;
                while((line = bufferedReader.readLine()) != null){
            		System.out.println("Inut : "+line);
           
                    if(line.trim().equals("Request update")){
                        //Receive update request
                    	Certificate[] peerCertificate = sslSession.getPeerCertificates();
                    	ArrayList<Transaction> outgoingMessages = miner.getUpdatesForClient(peerCertificate[0].getPublicKey());
                    	if(outgoingMessages == null) {
                    		printWriter.println("No new messages for client");
                            printWriter.flush();
                    	} else {
                    		printWriter.println("Imbound message");
                        	printWriter.flush();
                        	
        	                outputStream.writeObject(outgoingMessages);
        	                outputStream.flush();
        	               
        	                line = null;
        	                while((line = bufferedReader.readLine()) != null){
        	                    System.out.println("Inut : "+line);
        	
        	                    if(line.trim().equals("Client received message")){
        	                        break;
        	                    }
        	                }
                    	}
                    	printWriter.println("No new messages for client");
                        printWriter.flush();
                    	break;
                    } else if(line.trim().equals("Imbound message")) {
                    	//receive message
                    	while((message = (Transaction) inputStream.readObject()) != null ){
                			miner.receiveTransaction(message);
                    		printWriter.println("Miner received message");
                            printWriter.flush();
                        	message = null;
                        	break;
                		}
                    } else if(line.trim().equals("No new messages for miner")) {
                		break;
                	}
            	}   
                sslSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
