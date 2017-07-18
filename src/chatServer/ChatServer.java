package chatServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ChatServer implements Runnable {

	public static int port = 9999;
	static ServerSocket server;
	static Vector<ChatServer> connections;
	
	private Socket client;
	private Scanner in;
	private PrintWriter out;
	String userFrom, userTo;
	
	public static void main(String[] args) {
		connections = new Vector<ChatServer>();
		
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Can't create ServerSocket for Server!");
			System.exit(1);
		}

		Socket socket = null;
		Thread t = null;
		
		while (true) {
			try {
				socket = server.accept();
				t = new Thread(new ChatServer(socket));
				t.start();
			} 
			catch (IOException e) {
				System.out.println("Can't create Socket for User!");
			}
		}
	}
	
	public ChatServer(Socket client) {
		this.client = client;
		
		try {
			in = new Scanner(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			try {
				userFrom = in.nextLine().toString();
				userTo = in.nextLine().toString();
			} catch (Exception e) {
				System.out.println("Mistake");
			}
			connections.addElement(this);
		} catch (IOException e) {
			System.out.println("Can't initialize Input/Output for Client!");
		}
	}

	@Override
	public void run() {
		while (true) {
			String msg;
			while (in.hasNext()) {
				msg = in.nextLine().toString();
				if (msg.equals("quit") || msg.equals("exit")) break;
				for (ChatServer server : connections) {
					if (server.userFrom.equals(userTo) && server.userTo.equals(userFrom)) {
							server.sendMsg(msg);
					}
				}
			}
			break;
		}
		
		try {
			in.close();
			out.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMsg(String msg) {
		out.println(msg);
	}

}
