package com.chat.server;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jim on 4/18/17.
 */
public class ServerTCPWorker extends Thread {

    private final Socket clientSocket;
    private final ServerTCP server;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    public ServerTCPWorker(ServerTCP server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        
        System.out.println("[ServerTCPWorker] before while loop");
        
        while ( (line = reader.readLine()) != null) {
        	System.out.println("[ServerTCPWorker] entered while loop   line =" + line);
           // String[] tokens = StringUtils.split(line);
            String[] tokens = line.split("\\(");
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                System.out.println("[ServerTCPWorker] incoming string = " +cmd);
                
                // take off last parentheses
    			tokens[1] = tokens[1].substring(0 , tokens[1].indexOf(')'));
            
                
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("CONNECT".equalsIgnoreCase(cmd)) {
                	System.out.println("[ServerTCPWorker] call handlelogin");
                    handleLogin(outputStream, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);
        }
    }

    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    // format: "msg" "login" body...
    // format: "msg" "#topic" body...
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerTCPWorker> workerList = server.getWorkerList();
        for(ServerTCPWorker worker : workerList) {
            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "msg " + sendTo + ":" + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerTCPWorker> workerList = server.getWorkerList();

        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        for(ServerTCPWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        
        	
            String login = tokens[1];
            //String password = tokens[2];

         //   if ((login.equals("guest") && password.equals("guest")) || (login.equals("jim") && password.equals("jim")) ) {
          //      String msg = "ok login\n";
          //      outputStream.write(msg.getBytes());
          //      this.login = login;
          //      System.out.println("User logged in succesfully: " + login);

                List<ServerTCPWorker> workerList = server.getWorkerList();

                // send current user all other online logins
                for(ServerTCPWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                // send other online users current user's status
                String onlineMsg = "online " + login + "\n";
                for(ServerTCPWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
                
                
                System.out.println("[]HandleLogin complete");
        //    } else {
        //        String msg = "error login\n";
        //        outputStream.write(msg.getBytes());
        //       System.err.println("Login failed for " + login);
        //    }
        
    }

    private void send(String msg) throws IOException {
        if (login != null) {
            try {
                outputStream.write(msg.getBytes());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
