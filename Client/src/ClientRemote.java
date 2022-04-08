import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class ClientRemote extends UnicastRemoteObject implements ClientInterface {

	private boolean inCriticalSection;
	private Request sendRequest;
	
	private List<User> users;
	private List<User> sendToUsers;
	private List<Reply> answers;
	private List<Request> acceptedRequests;
	private List<Request> waitingRequests;
	
	private int outPort;
	
	private JFrame frame;
	private JPanel clientPanel;
	private JList listUsers;
	
	protected ClientRemote(int _port) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		users = new ArrayList<User>();
		sendToUsers = new ArrayList<User>();
		answers = new ArrayList<Reply>();
		acceptedRequests = new ArrayList<Request>();
		waitingRequests = new ArrayList<Request>();
		inCriticalSection = false;
		
		outPort = _port;
		frame = new JFrame();
		
		try {
			Registry reg = LocateRegistry.getRegistry("localhost",outPort);
			ServerInterface a = (ServerInterface)reg.lookup("rmi");
			String s = a.GetUsers();
			List<String> names = (ArrayList<String>)objectFromString(s);
			for(String x : names) {
				User u = new User();
				u.name=x;
				users.add(u);
			}
		}
		catch(Exception ex) {
			System.out.println(ex);
		}
		
		
		InitFrame();
		
	}

	@Override
	public boolean RegisterNewUser(String _user) throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			User user = (User)objectFromString(_user);
			users.add(user);
			DefaultListModel listModel = (DefaultListModel) listUsers.getModel();
	        listModel.addElement(user.name);
		}
		catch(Exception ex){
			return false;
		}
		
		
		return true;
	}

	@Override
	public boolean DeregisterUser(String name) throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean a = users.removeIf(n -> (n.name == name));
		answers.removeIf(n -> (n.from == name));
		sendToUsers.removeIf(n -> (n.name == name));
		if(a) {
			DefaultListModel listModel = (DefaultListModel) listUsers.getModel();
	        listModel.removeAllElements();
	        for(User x : users) {
	        	listModel.addElement(x.name);
	        }
			
			if(inCriticalSection) {
				if(answers.size()==sendToUsers.size()) {
					//wywołanie sekcji krytycznej
				}
			}
		}
		
		return a;
	}

	@Override
	public boolean Request(String _req) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			Request req = (Request)objectFromString(_req);
			
			
			if(inCriticalSection) {
				waitingRequests.add(req);
				waitingRequests.stream()
			  	.sorted((object1, object2) -> object1.date.compareTo(object2.date));
				return false;
			}
			else {
				//wyslij opdowiedz
			}
		}
		catch(Exception ex) {
			return false;
		}
		return true;
	}

	@Override
	public boolean Reply(String _rep) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			Reply rep = (Reply)objectFromString(_rep);
			
			if(rep.date == sendRequest.date) {
				answers.add(rep);
				if(answers.size()==sendToUsers.size()) {
					//wywołanie sekcji krytycznej
				}
			}
		}
		catch(Exception ex) {
			return false;
		}
		
		
		
		return true;
	}

	@Override
	public boolean Ping() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void InitFrame() {
		frame.setSize(600, 500);
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
		button.setBounds(470, 420, 100, 50);
		clientPanel = new JPanel();
		clientPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		clientPanel.setLayout(new GridLayout(0,1));
		clientPanel.add(button);
		
		JLabel label = new JLabel();
		label.setBounds(30, 20, 200, 50);
		label.setText("Others users");
		DefaultListModel<String> l1 = new DefaultListModel<>();  
		for(User x : users) {
			l1.addElement(x.name);
		}
		listUsers = new JList(l1);
		listUsers.setBounds(30, 100, 200, 350);
		clientPanel.add(label);
		clientPanel.add(listUsers);
		
		
		frame.add(clientPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setTitle("Client GUI");
		frame.setVisible(true);
	}
	
	private static Object objectFromString(String s) throws IOException, ClassNotFoundException 
	   {
	        byte [] data = Base64.getDecoder().decode(s);
	        ObjectInputStream ois = new ObjectInputStream( 
	                                        new ByteArrayInputStream(data));
	        Object o  = ois.readObject();
	        ois.close();
	        return o;
	   }


}
