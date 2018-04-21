package com.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by jim on 4/24/17.
 */
public class LoginWindow extends JFrame {
	
	// Create Client class
    private ChatClient client;
    
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    public LoginWindow() throws UnknownHostException, SocketException {
    	
    	
    	super("Login");
    	
      ///client.connect  
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(loginField);
        p.add(passwordField);
        p.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        getContentPane().add(p, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();
        //String password = passwordField.toString();
        
       
        try {
        	
        	 this.client = new ChatClient("localhost", 9918);
        	 
        	 if (this.client.login(login, password) ) {  
        	
            	//Start authentication process using UDP
            	
            	if (this.client.start_udp() == 1 ) {
            		this.client.start_tcp();
            	}
            	else {
            		System.out.println("Authentication failed.");
            	}
            
                // bring up the user list window
                UserListPane userListPane = new UserListPane(client);
                System.out.println("[]LoginWindow userlistpane created");
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);
 
                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);

                setVisible(false);
            } else {
                // show error message
                JOptionPane.showMessageDialog(this, "Invalid login/password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, SocketException   {
        LoginWindow loginWin = new LoginWindow();
        loginWin.setVisible(true);
    }
}
