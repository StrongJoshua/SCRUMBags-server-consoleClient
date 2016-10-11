
package edu.gatech.scrumbags.console;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

import edu.gatech.scrumbags.console.Message.MessageType;

public class Client extends Thread {
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Scanner sysIn;
	private Gson gson;
	private boolean running;

	public Client () {
		sysIn = new Scanner(System.in);
		gson = new Gson();

		try {
			socket = new Socket(InetAddress.getByName("ec2-52-25-113-216.us-west-2.compute.amazonaws.com"),
				63400);
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());

			out.writeObject(gson.toJson(new Message(MessageType.console, "Console", "scrumc0ns0le")));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void run () {
		running = true;

		new Thread() {
			@Override
			public void run () {
				while (running)
					try {
						System.out
							.print(gson.fromJson((String)in.readObject(), Message.class).getPayload()[0]);
					} catch (ClassNotFoundException | IOException e) {
					}
			}
		}.start();

		while (running) {
			if (sysIn.hasNext()) {
				String txt = sysIn.nextLine();
				if (txt.equals("quit"))
					quit();
				else {
					try {
						out.writeObject(gson.toJson(new Message(MessageType.console, txt)));
					} catch (IOException e) {
						System.out.println("Failed to send message. Please try again.");
					}
				}
			}
		}
	}

	private void quit () {
		running = false;
		try {
			in.close();
			out.close();
			sysIn.close();
			socket.close();
		} catch (IOException e) {
		}
	}

	public static void main (String[] args) {
		Client client = new Client();
		client.start();
	}
}
