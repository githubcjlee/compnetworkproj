package com.chat.client;

import java.net.*;

import java.io.*;
import java.util.Scanner;

public class ChatClient {

	private static int id;
	private int rand_cookie;

	// UDP variables - Port numbers and IP addresses
	private DatagramSocket clientUDP;
	private InetAddress serverIP = InetAddress.getLocalHost();
	private int clientUDPPort;
	private int serverUDPPort = 9918;

	private String serverName;
	private int serverPort;
	private Socket socket;
	private InputStream serverIn;
	private OutputStream serverOut;
	private BufferedReader bufferedIn;

	// Constructor
	public ChatClient(int _id) throws UnknownHostException, SocketException {
		// get Client ID based off 'login'***
		this.id = _id;
	}

	public int start_udp() throws IOException {

		this.clientUDPPort = 9918 + this.id;
		clientUDP = new DatagramSocket(this.clientUDPPort);

		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		int route = 1;
		String sentence = "";

		// variables that we need for authentication process
		int res = 0;
		int rand = 0;

		// Select message to be sent
		while (route > 0) {

			switch (route) {

			case 1:
				sentence = "HELLO(" + this.id + ")";
				break;
			case 2:
				sentence = "RESPONSE(" + this.id + "," + res + ")";
				break;

			default:
				break;
			}

			// Send message
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.serverIP,
					this.serverUDPPort);
			this.clientUDP.send(sendPacket);

			// Wait for incoming message
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			this.clientUDP.receive(receivePacket);
			sentence = new String(receivePacket.getData());

			// Split message to read protocol
			String array[] = sentence.split("\\(");

			// take off last parentheses
			array[1] = array[1].substring(0, array[1].indexOf(')'));

			if ("CHALLENGE".equals(array[0])) {
				rand = Integer.parseInt(array[1]);

				// **********************
				// use rand and Client's secret key to create 'res'
				// for now I am making this just an arbitrary integer equal to ID.
				res = this.id;
				route++;
			} else if ("AUTH_SUCC".equals(array[0])) {

				// split contents into rand_cookie and port number for server-TCP port #
				// array2[0] == rand_cookie ; array2[1] == TCP-Port#
				String array2[] = array[1].split(",");

				// Check save rand_cookie and send back with CONNECT message
				if (array2[0].equals(String.valueOf(rand))) {

					// Save port number and IP, for now hardcoding local host
					this.serverPort = Integer.parseInt(array2[1]);

				}
				route = -1;
				return (1);

			}

			else {
				// if authentication fails
				System.out.println("Authentication Failed.");
				return (0);
			}
		}
		return (1);
	}

	public void start_tcp() throws IOException {
		connect();
		startMessageReader();
	}

	public void msg(String msgBody) throws IOException {
		String cmd = "MSG(" + msgBody + ")\n";
		serverOut.write(cmd.getBytes());
	}

	public void logoff() throws IOException {
		String cmd = "logoff\n";
		serverOut.write(cmd.getBytes());
	}

	private void startMessageReader() {
		Thread t = new Thread() {
			@Override
			public void run() {
				readMessageLoop();
			}
		};
		t.start();
	}

	private void readMessageLoop() {
		try {
			String line;
			while ((line = bufferedIn.readLine()) != null) {
				
				String[] tokens = line.split("\\(");
				// take off last parentheses
				tokens[1] = tokens[1].substring(0, tokens[1].indexOf(')'));

				if (tokens != null && tokens.length > 0) {
					String cmd = tokens[0];
					if ("CONNECTED".equalsIgnoreCase(cmd)) {
						System.out.println("CONNECTED!");
					} else if ("MSG".equalsIgnoreCase(cmd)) {
						// String[] tokensMsg = StringUtils.split(line, null, 3);
						handleMessage(tokens[1]);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleMessage(String tokensMsg) {
		System.out.println("[Them]:" + tokensMsg);
	}

	public void connect() {
		try {
			this.socket = new Socket(serverName, serverPort);
			//System.out.println("Client port is " + socket.getLocalPort());
			this.serverOut = socket.getOutputStream();
			this.serverIn = socket.getInputStream();
			this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));

			String msg = "CONNECT(" + this.id + ")\n";
			serverOut.write(msg.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		System.out.println("\nWelcome to our Server-Based-Chat program.\n" + "Please select/enter your user ID."
				+ "\n\n\tUserID's" + "\n\t 100" + "\n\t 200" + "\n\t 300" + "\n\t 400" + "\n\t 500" + "\n\t 600"
				+ "\n\n");

		Scanner input = new Scanner(System.in);
		String login = input.nextLine();
		
		ChatClient client = new ChatClient(Integer.parseInt(login));

		System.out.println("\nSelected UserID = " + login);
		System.out.println("\nPlease enter a command....\n");
		while (true) {
			String line = input.nextLine();
			String[] tokens = line.split(" ", 2);
			if ("logon".equalsIgnoreCase(tokens[0])) {

				if (client.start_udp() != 1) {
					System.out.println("\t*Authentication failed.");
				} else {
					client.start_tcp();
				}
			} else if ("chat".equalsIgnoreCase(tokens[0])) {
				String msg = "CHAT(" + tokens[1] + ")\n";
				client.serverOut.write(msg.getBytes());

			} else if ("offline".equalsIgnoreCase(tokens[0])) {

			} else if ("end".equalsIgnoreCase(tokens[0]) && "chat".equalsIgnoreCase(tokens[1])) {

			} else {
				client.msg(line);
			}
		}
	}

}
