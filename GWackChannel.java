import java.util.*;
import java.net.*;
import java.io.*;

public class GWackChannel {
	public ServerSocket serverSock;
	public volatile Queue<String> incomingMessages;
	public LinkedList<ServerClient> clients;

	public GWackChannel(int port) {
		try {
			serverSock = new ServerSocket(port);
			incomingMessages = new LinkedList<String>();
			clients = new LinkedList<ServerClient>();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public void serve() {
		(new DistributionThread()).start();
		BufferedReader credReader;
		try {
			while (true) {
				Socket clientSock = serverSock.accept();
				credReader = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				String username = credReader.readLine();
				
				ServerClient newClient = new ServerClient(username, clientSock);
				System.out.println("New Connection: [" + username + "]");
				clients.add(newClient);


				String memberlist = "&&";
				for (ServerClient client : clients) {
					memberlist += client.username + "&&";
				}
				sendToAll(memberlist);
	
				// start thread
				(new ClientInputThread(newClient)).start();
			}

		} catch (Exception e) {
			System.out.println("this");
			System.err.println(e);
			System.exit(1);
		}
	}

	public void sendToAll(String msg) {
		try {
			for (ServerClient client : clients) {
				client.send(msg);
			}
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	private class ClientInputThread extends Thread {
		ServerClient client;
		
		public ClientInputThread(ServerClient cl) {
			try {
				client = cl;
			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}
		}
		
		public void run() {
			try {
				while (true) {
					String msg = client.reader.readLine();
					if (msg == null) break;
					incomingMessages.add(msg);
				}
			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}

			System.out.println("Connection lost: [" + client.username + "]");

			clients.remove(client);
			String memberlist = "&&";
			for (ServerClient client : clients) {
				memberlist += client.username + "&&";
			}
			sendToAll(memberlist);

			try {
				client.reader.close();
				client.writer.close();
				client.sock.close();
			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}
		}
	}
	
	private class DistributionThread extends Thread {	
		public DistributionThread() {
			// 
		}
		public void run() {
			try {
				while (true) {
					if (!incomingMessages.isEmpty()) {
						String msg = incomingMessages.poll();
						sendToAll(msg);
					}
				}
			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}
		}
	}

	private class ServerClient {
		public String username;
		public Socket sock;
		public BufferedReader reader;
		public PrintWriter writer;

		public ServerClient(String u, Socket s) {
			try {
				username = u;
				sock = s;
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				writer = new PrintWriter(sock.getOutputStream());
			} catch (Exception e ){ 
				System.err.println(e);
				System.exit(1);
			}
		}

		public void send(String str) {
			writer.println(str);
			writer.flush();
		}

	}
	public static void main(String[] args) {
		int port = (args.length > 0) ? Integer.parseInt(args[0]) : 2022; 
		(new GWackChannel(port)).serve();
	}
}