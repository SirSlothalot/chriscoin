package main.miner;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.generic.Transaction;

@SuppressWarnings("serial")
public class UpdatesRepository implements Serializable{
	//array of messages because there could be multiple updates for a client
	HashMap<Integer, ArrayList<Transaction>> updates;
	
	public UpdatesRepository() {
		updates = new HashMap<Integer, ArrayList<Transaction>>();
	}
	
	//return message if there is an update, null otherwise
	public ArrayList<Transaction> getUpdate(PublicKey receiver) {
		return updates.remove(receiver.hashCode());
//		updates.put(receiver, null);
//		return messages;
	}
	
	public void addUpdate(PublicKey receiver, Transaction newMessage) {
		int hashCode = receiver.hashCode();
		ArrayList<Transaction> messages = updates.get(hashCode);
		if(messages == null) {
			messages = new ArrayList<Transaction>();
			messages.add(newMessage);
			updates.put(hashCode, messages);
		} else {
			messages.add(newMessage);
			updates.put(hashCode, messages);
		}
	}
	
	@Override
	public String toString() {
		String str = "-- Updates Repository --\n\n";
		int hashCode;
		ArrayList<Transaction> transactions;
		
		
		for(Map.Entry<Integer,ArrayList<Transaction>> entry : updates.entrySet()){
		    hashCode = entry.getKey();
		    transactions = entry.getValue();
		    
		    str += "Public key: " + hashCode + "\n\n";
		    int i = 1;
		    for(Transaction t : transactions) {
		    	str += "Transaction " + i +"\n";
		    	str += t.toString() + "\n\n";
		    	i++;
		    }
		    str += "\n";
		}
		
		return str;
	}
	
}
