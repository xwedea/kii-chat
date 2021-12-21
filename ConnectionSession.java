import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;


public class ConnectionSession {
	GWackClientGUI gui;
	String clientName;
	Socket sock;
	PrintWriter write;
	BufferedReader read;
	readingThread r_thread;
	public boolean closed = false;

	public ConnectionSession(GWackClientGUI g, String name, String ip_addr, int port_no) {
		try {
			gui = g;
			clientName = name;
			sock = new Socket(ip_addr, port_no);
			write = new PrintWriter(sock.getOutputStream());
			read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			r_thread = new readingThread();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(gui, "Connection failed!\n Exiting...");
			// System.err.println("Catch 1 " + e);
			// System.exit(1);
		}
	}

	public void handleError() {

	}

	public void sendMessage(String s) {
		write.println("[" + clientName + "] " + s);
		write.flush();
	}

	public void connect(String name) {
		try {
			if(name == null) {
				return;
			}
			r_thread = new readingThread();
			r_thread.start();

			// send name to server
			write.println(name);
			write.flush();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}
	public void disconnect() {
		try {
			closed = true;
			write.println(clientName + " disconnected!");
			write.flush();
			read.close();
			write.close();
		} catch (Exception e) {
			System.out.println("disconnect() error");
			System.err.println(e);
			System.exit(1);
		}
	}

	private class readingThread extends Thread {
		public void run() {
			try {
				while (!closed) {
					String msg = read.readLine();
					if (msg != null && msg.length() > 0) {
						if (msg.charAt(0) == '&') gui.updateMembers(msg);
						else gui.getMessage(msg);
					}
					if (msg == null) {
						break;
					}
				}
				write.close();
				read.close();
				sock.close();

			} catch (Exception e) {
				System.err.println(e);
				System.exit(1);
			}
		}
	}
}