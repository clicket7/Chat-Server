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
	static Vector<String> nicknames;
	
	private Socket client;
	private Scanner in;
	private PrintWriter out;
	
	public static void main(String[] args) {
		connections = new Vector<ChatServer>();
		nicknames = new Vector<String>();
		
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
		connections.addElement(this);
		nicknames.addElement("Client name - John Doe");
		
		try {
			in = new Scanner(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
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
				for (ChatServer server : connections) server.sendMsg(msg);
				if (msg.equals("quit") || msg.equals("exit")) break;
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
		out.println(nicknames.get(nicknames.size() - 1) + ": " + msg);
	}

}
