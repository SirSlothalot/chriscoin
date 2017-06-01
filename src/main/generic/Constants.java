package main.generic;

public class Constants {
	public static final int PORT = 9999;
	
	public static final String DESKTOP_DIR 			= 	System.getProperty("user.home") + "/Desktop/ChrisCoin";

	public static final String TRUSTED_CERTS_DIR	= 	"/wallet/trusted-certificates/";
	public static final String KEY_STORE_NAME		= 	"/wallet/key-store.jks";
	public static final String PRIV_KEY_FILE		= 	"/wallet/private-key.pem";
	public static final String CERT_FILE			= 	"/wallet/certificate.pem";
	public static final String PRIVATE_KEY_NAME		=	"my-private-key";
	public static final String CERT_NAME			= 	"my-certificate";
	public static final String WALLLET_CERT_GEN		= 	"/wallet/gen-certs-wallet.sh";
	
	public static final String MINER_CERT_GEN		= 	"/miner/gen-certs-miner.sh";
	
}
