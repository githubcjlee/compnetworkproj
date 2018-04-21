package com.chat.client;

import org.apache.commons.lang3.StringUtils;
import java.net.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jim on 4/21/17.
 */
public class ChatClient {
	
    private String serverName;
    private int serverPort;
    private int id;
    private int rand_cookie;
    
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;
    
    
    
    // Server UDP IP and Port variables
    private final DatagramSocket clientUDP;
    private InetAddress serverIP;
    private int clientUDPPort;
    private int serverUDPPort; 

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    // Constructor
    public ChatClient(String serverName, int serverUDPport) throws UnknownHostException, SocketException{ 
        
    	this.serverName = serverName;
        this.serverUDPPort = serverUDPport;
        
        // get Client ID based off 'login'*** 
    	// for now make random 
    	Random rand = new Random();
    	int ID = (int)rand.nextInt(50)%10+1; 
    	this.id=ID;
        
        // Create unique UDP port for client based off ID
        this.clientUDPPort = 9918+this.id;			
        clientUDP = new DatagramSocket(this.clientUDPPort);
  
        serverIP = InetAddress.getLocalHost();
        
    }

    public void setID( int _id) {
    	this.id = _id;
    }
    public int getID() {
    	return this.id;
    }
    
    public int start_udp() throws IOException {

    	byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		int route = 1;
		String sentence = "";
		
		// variables that we need for authentication process
		int res=0;
		int rand =0;
		
		// Select message to be sent
		while( route > 0) {
			
			switch (route) {
			
				case 1: sentence = "HELLO(" + this.id + ")";
						break;
				case 2: sentence = "RESPONSE(" + this.id + "," + res + ")";
						break;
				
				default: break;
			}
			
			// Send message
			sendData = sentence.getBytes();
		    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.serverIP, this.serverUDPPort);
		    this.clientUDP.send(sendPacket);
				
		    // Wait for incoming message
		    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			this.clientUDP.receive(receivePacket);
			sentence = new String(receivePacket.getData());
			
			// Split message to read protocol 
			String array[] = sentence.split("\\(");
				
			// take off last parentheses
			array[1] = array[1].substring(0 , array[1].indexOf(')'));
			
			if ( "CHALLENGE".equals(array[0]) ) {
				rand = Integer.parseInt(array[1]);	
				System.out.println("CHALLENGE recieived");	
				
				//**********************
				// use rand and Client's secret key to create 'res'
				// for now I am making this just an arbitrary integer equal to ID. 
				res = this.id;
				route ++;
			}
			else if ( "AUTH_SUCC".equals(array[0]) ) {
				System.out.println("AUTH_SUCC received");
				
				// split contents into rand_cookie and port number for server-TCP
				String array2[] = array[1].split(",");
				
				// Check save rand_cookie and send back with CONNECT message
				if ( array2[0].equals(String.valueOf(rand))) {
					
				// Save port number and IP, for now hardcoding local host
					this.serverName = "localhost";
				this.serverPort = Integer.parseInt(array2[1]);
				System.out.println("TCP port # saved (" + this.serverPort + ")");
				}
				route =-1;
				System.out.println("Authentication completed!");
					
			}
			/*
			else if ( "CONNECTED".equals(array[0]) ) {
				System.out.println("Authentication completed!");
				route = -1;
			}*/
			else {
				// if authentication fails
				System.out.println("sentence == " + array[0]);
				System.out.println("Authentication Failed.");
				return (1);
			}
		}
		return (1);
	}
    
    public void start_tcp () throws IOException {
    	
    	
    	// Start of TCP connection
        //ChatClient client = new ChatClient("localhost", 8818);
    	//this.socket = new Socket ("localhost" , serverPort );
    	
    	if (!this.connect()) {
            System.err.println("Connect failed.");
        } else {
            System.out.println("Connect successful");
            /*
            if (this.login("guest", "guest")) {
                System.out.println("Login successful");

                //this.msg("jim", "Hello World!");
            } else {
                System.err.println("Login failed");
            }
             */
            //client.logoff();
        }
    	
    	
    	
    	
    	startMessageReader();  // Originally taken from 'login' 
        this.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        this.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + " ===>" + msgBody);
            }
        });
        
      /*  //Send server CONNECT Message
        String msg = "CONNECT(" + this.id + ")";
    	serverOut.write(msg.getBytes());
*/
        
        System.out.println("[] END OF START_TCP");
       
    	
    }
    

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String login, String password) throws IOException {
    	
    	
    	byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
    	
		String response =  "";
    	String cmd = "LOGIN(" + login + ")(" + password + ")\n";
        //cmd = "login(1)(2)";
    	// Original code would send login through TCP
    	//serverOut.write(cmd.getBytes());

    	
        /////
        // Send message (Through UDP)
     	sendData = cmd.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.serverIP, this.serverUDPPort);
        this.clientUDP.send(sendPacket);
        
        System.out.println("***After send message");
        
        
        // Wait for incoming message
	    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		this.clientUDP.receive(receivePacket);
		response = new String(receivePacket.getData());
        ////
        
        
        //System.out.println("Response Line:_" + response+  "__");
        
        response = "OK";    // Temporary patch***
        
     
        if ("OK".equals(response)) {
            
            return true;
        } else {
        	System.out.println("Inside OK-LOGIN(false)");
            return false;
        }
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }
/*
    private void startServerConsoleChat() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readServerMessageLoop();
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                sendServerMessageLoop();
            }
        };
        t.start();
        t2.start();
    }
    */
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
            	System.out.println("[]readMessageLoop  line = " + line  );
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
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

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            
            
            //Thread.sleep(8000);
            
            String msg = "CONNECT(" + this.id + ")\n";
        	serverOut.write(msg.getBytes());
        	//serverOut.flush();
        	System.out.println("[] CONNECT message sent");
        	
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

}
