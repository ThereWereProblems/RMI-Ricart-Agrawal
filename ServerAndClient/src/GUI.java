import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI{
	
	private JFrame frame;
	private int outPort;
	private int inPort;
	
	private JPanel startPanel;
	private JTextField outPortText;
	private JTextField inPortText;
	
	private JPanel clientPanel;
	private JTextField numberA;
	private JTextField numberB;
	private JLabel sum;
	
	public GUI() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Our GUI");
		
		startPanel = new JPanel();
		startPanel = new JPanel();
		startPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		startPanel.setLayout(new GridLayout(0,1));
		JLabel label1 = new JLabel("Podaj port do podłączenia");
		JLabel label2 = new JLabel("Udostępnij swój port");
		outPortText = new JTextField();
		inPortText = new JTextField();
		
		startPanel.add(label1);
		startPanel.add(outPortText);
		startPanel.add(label2);
		startPanel.add(inPortText);
		
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				outPort = Integer.parseInt(outPortText.getText());
				inPort = Integer.parseInt(inPortText.getText());
				frame.getContentPane().remove(startPanel);
				StartServer();
			}
		});
		startPanel.add(button);
		
		frame.add(startPanel);
		frame.pack();
		frame.setMinimumSize(new Dimension(500, 300));
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GUI();
	}
	public void StartServer() {
		try {
			Registry reg = LocateRegistry.createRegistry(inPort);
			AdderRemote ar = new AdderRemote();
			reg.rebind("adding", ar);
			System.out.println("Server is ready");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		StartClient();
	}
	public void StartClient() {
		
		sum = new JLabel("sum");
		numberA = new JTextField();
		numberB = new JTextField();
		
		JButton button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Registry reg = LocateRegistry.getRegistry("localhost",outPort);
					AdderInterface a = (AdderInterface)reg.lookup("adding");
					int f=a.Add(Integer.parseInt(numberA.getText()), Integer.parseInt(numberB.getText()));
				
					sum.setText(String.valueOf(f));
				}
				catch(Exception ex) {
					
				}
			}
		});
		
				
		clientPanel = new JPanel();
		clientPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		clientPanel.setLayout(new GridLayout(0,1));
		clientPanel.add(numberA);
		clientPanel.add(numberB);
		clientPanel.add(button);
		clientPanel.add(sum);
		
		frame.add(clientPanel, BorderLayout.CENTER);
		frame.pack();
	}
}
