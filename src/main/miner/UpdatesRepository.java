package main.miner;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.generic.Message;
import main.generic.Transaction;

@SuppressWarnings("serial")
public class UpdatesRepository implements Serializable{
	//array of messages because there could be multiple updates for a client
	HashMap<Integer, ArrayList<Message>> updates;
	
	public UpdatesRepository() {
		updates = new HashMap<Integer, ArrayList<Message>>();
	}
	
	//return message if there is an update, null otherwise
	public ArrayList<Message> getUpdate(PublicKey receiver) {
		return updates.remove(receiver.hashCode());
	}
	
	public void addUpdate(Message message, PublicKey receiver) {
		int hashCode = receiver.hashCode();
		ArrayList<Message> messages = updates.get(hashCode);
		if(messages == null) {
			messages = new ArrayList<Message>();
			messages.add(message);
			updates.put(hashCode, messages);
		} else {
			messages.add(message);
			updates.put(hashCode, messages);
		}
	}
	
	@Override
	public String toString() {
		String str = "-- Updates Repository --\n\n";
		int hashCode;
		ArrayList<Message> messages;
		
		
		for(Map.Entry<Integer,ArrayList<Message>> entry : updates.entrySet()){
		    hashCode = entry.getKey();
		    messages = entry.getValue();
		    
		    str += "Public key: " + hashCode + "\n\n";
		    int i = 1;
		    for(Message m : messages) {
		    	str += "Transaction " + i +"\n";
		    	str += m.getTransaction().toString() + "\n\n";
		    	i++;
		    }
		    str += "\n";
		}
		
		return str;
	}
	
}
