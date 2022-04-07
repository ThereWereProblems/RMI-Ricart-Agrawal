import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI {
	
	JFrame frame;
	JPanel startPanel;
	int outPort;
	int inPort;
	String name;
	
	public GUI() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Our GUI");
		
		startPanel = new JPanel();
		startPanel = new JPanel();
		startPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		startPanel.setLayout(new GridLayout(0,1));
		JLabel label = new JLabel("Podaj nazwę");
		JLabel label1 = new JLabel("Podaj port do podłączenia");
		JLabel label2 = new JLabel("Udostępnij swój port");
		JTextField nameField = new JTextField();
		JTextField outPortText = new JTextField();
		JTextField inPortText = new JTextField();
		
		startPanel.add(label);
		startPanel.add(nameField);
		startPanel.add(label1);
		startPanel.add(outPortText);
		startPanel.add(label2);
		startPanel.add(inPortText);
		
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				name = nameField.getText().trim();
				outPort = Integer.parseInt(outPortText.getText());
				inPort = Integer.parseInt(inPortText.getText());
				frame.getContentPane().remove(startPanel);
				StartClient();
			}
		});
		startPanel.add(button);
		
		frame.add(startPanel);
		frame.pack();
		frame.setMinimumSize(new Dimension(500, 300));
		frame.setVisible(true);
	}
	
	public void StartClient() {
		
		User user = new User();
		user.name = name;
		user.port = inPort;
		try {
			Registry reg = LocateRegistry.getRegistry("localhost",outPort);
			ServerInterface a = (ServerInterface)reg.lookup("rmi");
			a.RegisterNewUser(user);
		}
		catch(Exception ex) {
			
		}
		
		JButton button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Registry reg = LocateRegistry.getRegistry("localhost",outPort);
					ServerInterface a = (ServerInterface)reg.lookup("rmi");
					a.DeregisterUser();
				}
				catch(Exception ex) {
					
				}
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		
				
		JPanel clientPanel = new JPanel();
		clientPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		clientPanel.setLayout(new GridLayout(0,1));
		clientPanel.add(button);
		
		frame.add(clientPanel, BorderLayout.CENTER);
		frame.pack();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GUI();
	}
	
	

}
