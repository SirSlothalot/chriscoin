package main.wallet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class HTTPSClient {
    private String host = "127.0.0.1";
    private int port = 9999;
    
    private KeyStore keyStore;
    private Wallet wallet;
    private Message message;

    HTTPSClient(Wallet wallet, Message message, KeyStore keyStore, String host, int port){
        this.wallet = wallet;
    	this.message = message;
    	this.host = host;
        this.port = port;
        this.keyStore = keyStore;
    }

    // Create the and initialize the SSLContext
    private SSLContext createSSLContext(){
        try{
//            keyStore = KeyStore.getInstance("JKS");
//            keyStore.load(new FileInputStream("test.jks"),"passphrase".toCharArray());

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
            sslContext.init(km,  tm, null);

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
            // Create socket factory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Create socket
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(this.host, this.port);

            System.out.println("SSL client started:");
            new ClientThread(sslSocket, wallet, message).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Thread handling the socket to server
    static class ClientThread extends Thread {
        private SSLSocket sslSocket = null;
        private Wallet wallet = null;
        private Message message = null;

        ClientThread(SSLSocket sslSocket, Wallet wallet, Message message){
            this.sslSocket = sslSocket;
            this.wallet = wallet;
            this.message = message;
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
                
                //Request update
                printWriter.println("Request update");
                printWriter.flush();
                
                //Receive update
                String line = null;
                Message receivedMessage = null;
                
                
                while((line = bufferedReader.readLine()) != null) {
            		System.out.println("Inut : "+line);
                	if(line.trim().equals("Imbound message")) {
                		while((receivedMessage = (Message) inputStream.readObject()) != null ){
                    		//update records
                    		//update balance
                			wallet.receiveMessage(message);
                        	receivedMessage = null;
                        	break;
                		}
                	} else if(line.trim().equals("No new messages for client")) {
                		break;
                	}
                }  
                 
                //If there is a message to send, send it
                if(message != null) {
                	printWriter.println("Imbound message");
                	printWriter.flush();
                	
	                outputStream.writeObject(message);
	                outputStream.flush();
	               
	                line = null;
	                while((line = bufferedReader.readLine()) != null){
	                    System.out.println("Inut : "+line);
	
	                    if(line.trim().equals("Miner received message")){
	                        break;
	                    }
	                }
                } else {
                	printWriter.println("No new messages for miner");
                	printWriter.flush();
                }

                sslSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
