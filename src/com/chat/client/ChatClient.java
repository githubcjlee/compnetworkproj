package com.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.chat.plugin.AESKeyGeneration;
import com.chat.plugin.StrongAES;

/*
 * @author Aarom Im
 * @contributor Ted Ahn
 * 
 */
public class ChatClient {

	private int id;
	private int rand_cookie;
	private String CKA;

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
	private StrongAES aes = new StrongAES();

	public static void main(String[] args) throws IOException, InterruptedException {

		System.out.println("\nWelcome to our Server-Based-Chat program.");
		Scanner input = new Scanner(System.in);
			String login = "";
			int idValue = 0;
			while (true) {
	            System.out.println("\nSelect a user ID between 100-999 \n");
	    			login = input.nextLine();
	            try {
	            		idValue = Integer.parseInt(login);
	            		if (idValue >= 100 && idValue <= 999) {
	            			break;
	            		}
	            } catch (NumberFormatException ne) {
	                //repeat
	            }
			}
	
			ChatClient client = new ChatClient(Integer.parseInt(login));
			System.out.println("\nSelected UserID = " + login);
	
			if (client.start_udp() != 1) {
				//Failed
			} else {
				client.start_tcp();
				while (true) {
					
					String line = input.nextLine();
					String[] tokens = line.split(" ", 2);
					if ("chat".equalsIgnoreCase(tokens[0])) {
						if (tokens.length < 2 || tokens[1] == null ) {
							System.out.println("Pick an online user.");
						} else {
							String msg = "CHAT_REQUEST(" + tokens[1] + ")\n";
							client.serverOut.write(msg.getBytes());
						}
					} else if ("logoff".equalsIgnoreCase(tokens[0])) {
						String msg = "LOGOFF()\n";
						client.serverOut.write(msg.getBytes());
						
						//Wait until logout is complete
						Thread.sleep(1000);
						client.logoff();
						break;
					} else if ("endchat".equalsIgnoreCase(tokens[0])) {
						String msg = "END_REQUEST()\n";
						client.serverOut.write(msg.getBytes());
					} else if ("history".equalsIgnoreCase(tokens[0])) {
						if (tokens.length < 2 || tokens[1] == null ) {
							System.out.println("Pick an user to look up history.");
						} else {
							String msg = "HISTORY_REQ(" + tokens[1] + ")\n";
							client.serverOut.write(msg.getBytes());
						}
					}  else if ("online".equalsIgnoreCase(tokens[0])) {
						String msg = "ONLINE_REQ()\n";
						client.serverOut.write(msg.getBytes());
					} else if ("help".equalsIgnoreCase(tokens[0])) {
						System.out.println("\n Commands List "
								+ "\n\tlogon \t logs on to server as selected user "
								+ "\n\tlogoff \t logs current user out "
								+ "\n\tchat -u \t starts a chat with inserted username"
								+ "\n\tendchat \t ends the current ongoing chat (if there is one)"
								+ "\n\thistory -u \t chat log with given user from last chat session"
								+ "\n\tonline \t show all currently online users");
					} else {
						client.msg(line);
					}
				}
				
			}
			
		input.close();
		System.out.println("\nEnd of Server-based chat program.");
	}

	// Constructor
	public ChatClient(int _id) throws UnknownHostException, SocketException {
		// get Client ID based off 'login'***
		this.id = _id;
	}

	public int start_udp() throws IOException {


		try {
		this.clientUDPPort = 9918 + this.id;
			clientUDP = new DatagramSocket(this.clientUDPPort);

		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		int route = 1;
		String sentence = "";

		// variables that we need for authentication process
		String res = "";
		int rand = 0;

		// Select message to be sent
		while (route > 0) {

			switch (route) {

			case 1:
				sentence = "HELLO " + this.id;
				break;
			case 2:
				sentence = "RESPONSE " + this.id + " " + res;
				break;

			default:
				break;
			}

			// Send message
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.serverIP,
					this.serverUDPPort);
			this.clientUDP.send(sendPacket);

			this.clientUDP.setSoTimeout(51000);
			
			// Wait for incoming message
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			this.clientUDP.receive(receivePacket);
			sentence = new String(receivePacket.getData());

			// Split message to read protocol
			String array[] = sentence.split(" ");
			array[1] = array[1].trim();

			if ("CHALLENGE".equals(array[0])) {
				// Use rand from message and pass in key/password

				rand = Integer.parseInt(array[1]);
				AESKeyGeneration crypto = new AESKeyGeneration();
				String password = ""+this.id;
				res = crypto.generateSymmetricKey(rand + password);

				route++;
			} else if ("AUTH_SUCC".equals(array[0])) {
				array[2] = array[2].trim();
				// split contents into rand_cookie and port number for server-TCP port #
				// array[1] == rand_cookie ; array[2] == TCP-Port#
				
				rand_cookie = Integer.parseInt(array[1]);
				AESKeyGeneration crypto = new AESKeyGeneration();
				CKA = crypto.generateChatKey(""+rand_cookie);
				
				// Check save rand_cookie and send back with CONNECT message
				// if (array2[0].equals(String.valueOf(rand))) {

				// Save port number and IP, for now hardcoding local host
				this.serverPort = Integer.parseInt(array[2]);
				route = -1;
				return (1);
			}

			else if ("AUTH_FAIL".equals(array[0])) {
				System.out.println("[UDP Auth Fail]: Wrong AES key match.");
				return (0);
			}
			else {
				System.out.println("[UDP Unknown]: Unknown error " + array[0]);
				return (0);
			}
		}
		return (1);

		} catch (BindException e){
			System.out.println("User " + this.id + " is already logged in!");
			return (0);
		}
	}

	public void start_tcp() throws IOException {
		connect();
		startMessageReader();
	}

	public void msg(String msgBody) throws IOException {
		String encrypted = aes.encrypt(msgBody, CKA);
		String msg = "CHAT(" + encrypted + ")\n";
		serverOut.write(msg.getBytes());
	}

	public void logoff() throws IOException {
		String cmd = "LOGOFF()\n";
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
				tokens[1] = tokens[1].substring(0, tokens[1].lastIndexOf(')'));
				String[] params = tokens[1].split(",");

				if (tokens != null && tokens.length > 0) {
					String cmd = tokens[0];
					if ("CONNECTED".equalsIgnoreCase(cmd)) {
						System.out.println("CONNECTED!");
					} else if ("CHAT_STARTED".equalsIgnoreCase(cmd)) {
						System.out.println("\nChat has started under session ID: " + params[0] + " with " + params[1]);
					} else if ("UNREACHABLE".equalsIgnoreCase(cmd)) {
						System.out.println("Client ID " + params[0] + " is currently unreachable!");
					} else if ("END_NOTIF".equalsIgnoreCase(cmd)) {
						System.out.println("\nChat under session ID " + params[0] + " has ended.");
					} else if ("CHAT".equalsIgnoreCase(cmd)) {
						// String[] tokensMsg = StringUtils.split(line, null, 3);
						handleMessage(tokens[1]);
					} else if ("HISTORY_RESP".equalsIgnoreCase(cmd)) {
						System.out.println(tokens[1]);
					} else if ("ONLINE_RESP".equalsIgnoreCase(cmd)) {
						System.out.println(tokens[1]);
					} else if ("ALERT".equalsIgnoreCase(cmd)) {
						handleAlert(tokens[1]);
					} else if ("ERROR".equalsIgnoreCase(cmd)) {
						handleError(tokens[1]);
					} else if ("UNKNOWN".equalsIgnoreCase(cmd)) {
						System.out.println("Unknown command!");
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

	private void handleAlert(String tokensMsg) {
		System.out.println("[Alert]:" + tokensMsg);
	}

	private void handleError(String tokensMsg) {
		System.out.println("[Error]:" + tokensMsg);
	}

	private void handleMessage(String tokensMsg) {
		String decrypted = aes.decrypt(tokensMsg, CKA);
		System.out.println(decrypted);
	}

	public void connect() {
		try {
			this.socket = new Socket(serverName, serverPort);
			// System.out.println("Client port is " + socket.getLocalPort());
			this.serverOut = socket.getOutputStream();
			this.serverIn = socket.getInputStream();
			this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));

			String msg = "CONNECT(" + this.id + ")\n";
			serverOut.write(msg.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
