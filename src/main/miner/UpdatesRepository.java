package main.miner;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import main.generic.Transaction;

@SuppressWarnings("serial")
public class UpdatesRepository implements Serializable{
	//array of messages because there could be multiple updates for a client
	HashMap<PublicKey, ArrayList<Transaction>> updates;
	
	public UpdatesRepository() {
		updates = new HashMap<PublicKey, ArrayList<Transaction>>();
	}
	
	//return message if there is an update, null otherwise
	public ArrayList<Transaction> getUpdate(PublicKey receiver) {
		ArrayList<Transaction> messages = updates.get(receiver);
		updates.put(receiver, null);
		return messages;
	}
	
	public void addUpdate(PublicKey receiver, Transaction newMessage) {
		ArrayList<Transaction> messages = updates.get(receiver);
		if(messages == null) {
			messages = new ArrayList<Transaction>();
			messages.add(newMessage);
			updates.put(receiver, messages); //is this necessary? - object references
		} else {
			messages.add(newMessage);
			updates.put(receiver, messages); //is this necessary? - object references 
		}
	}
	
}
