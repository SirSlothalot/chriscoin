package main.miner;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdatesRepository {
	//array of messages because there could be multiple updates for a client
	HashMap<PublicKey, ArrayList<Message>> updates;
	
	public UpdatesRepository() {
		updates = new HashMap<PublicKey, ArrayList<Message>>();
	}
	
	//return message if there is an update, null otherwise
	public ArrayList<Message> getUpdate(PublicKey receiver) {
		ArrayList<Message> messages = updates.get(receiver);
		updates.put(receiver, null);
		return messages;
	}
	
	public void addUpdate(PublicKey receiver, Message newMessage) {
		ArrayList<Message> messages = updates.get(receiver);
		if(messages == null) {
			messages = new ArrayList<Message>();
			messages.add(newMessage);
			updates.put(receiver, messages); //is this necessary - object references
		} else {
			messages.add(newMessage);
			updates.put(receiver, messages); //is this necessary - object references 
		}
	}
	
}
