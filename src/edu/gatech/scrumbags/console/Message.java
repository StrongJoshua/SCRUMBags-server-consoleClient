
package edu.gatech.scrumbags.console;

public class Message {
	public enum MessageType {
		console
	}

	private MessageType type;
	private String[] payload;

	public Message (MessageType type, String... payload) {
		this.type = type;
		this.payload = payload;
	}

	public MessageType getType () {
		return type;
	}

	public String[] getPayload () {
		return payload;
	}
}
