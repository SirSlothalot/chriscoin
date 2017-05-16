package main.miner;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import main.wallet.Message;

public class HTTPSServer {
    private int port = 9999;
    private boolean isServerDone = false;

    public static void main(String[] args){
        HTTPSServer server = new HTTPSServer();
        server.run();
    }

    HTTPSServer(){
    }

    HTTPSServer(int port){
        this.port = port;
    }

    // Create the and initialize the SSLContext
    private SSLContext createSSLContext(){
        try{
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream("./src/data/miner/key-store.jks"),"pass1".toCharArray());

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
            // Create server socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);

            System.out.println("SSL server started");
            while(!isServerDone){
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

                // Start the server thread
                new ServerThread(sslSocket).start();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Thread handling the socket from client
    static class ServerThread extends Thread {
        private SSLSocket sslSocket = null;

        ServerThread(SSLSocket sslSocket){
            this.sslSocket = sslSocket;
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
                
                //Receive update request
                String line = null;
                while((line = bufferedReader.readLine()) != null){
            		System.out.println("Inut : "+line);
                    if(line.trim().equals("Request update")){
                        //query blockchain for this user??
                    	//determine if a message/s is waiting to be sent to user
                    		//Yes - send message/s to user
                    	printWriter.println("No new messages for client");
                        printWriter.flush();
                    	break;
                    }
            	}
                
                //Receive new messages
                Message message = null;
                while((line = bufferedReader.readLine()) != null) {
            		System.out.println("Inut : "+line);
                	if(line.trim().equals("Imbound message")) {
                		while((message = (Message) inputStream.readObject()) != null ){
                    		//process message
                			System.out.println(message.getAmount());
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
