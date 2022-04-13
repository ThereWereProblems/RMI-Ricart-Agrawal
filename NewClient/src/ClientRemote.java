import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientRemote extends UnicastRemoteObject implements ClientInterface{
	
	private int myPort;
	private String myName;

	JFrame frame;
	JPanel mainPanel;
	
	JComboBox combo;
	
	List<Section> sections; 
	
	JList list1;
	JList list2;
	JList list3;
	JList list4;
	
	public ClientRemote(int port, String name) throws RemoteException {
		super();

		frame = new JFrame();
		mainPanel = new JPanel();
		
		JPanel upPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel downPanel = new JPanel();
		
		
		sections = new ArrayList<Section>(); 
		myPort = port;
		myName = name;
		
		
		//up panel
		JLabel wSection = new JLabel("Wybierz sekcje:");
		combo = new JComboBox();
		JButton buttonChange = new JButton("Zmień");
		JButton buttonJoin = new JButton("Dołącz do sekcji");
		JLabel listUsers = new JLabel("Inni użytkownicy w sekcji:");
		JLabel listAccepted = new JLabel("Zaakceptowane zapytania:");
		JLabel listWaitingToAccept = new JLabel("Zapytania do zaakceptowania:");
		JLabel listImWaiting = new JLabel("Oczekuję odpowiedzi od:");

		
		//center panel
		list1 = new JList(new DefaultListModel<>());
		list2 = new JList(new DefaultListModel<>());
		list3 = new JList(new DefaultListModel<>());
		list4 = new JList(new DefaultListModel<>());
		
		
		//down Panel
		JLabel timeLabel = new JLabel("Wejdz do sekcji na:");
		JTextField timeField = new JTextField();
		JButton enterButton = new JButton("Wejdź");
		JLabel maxWaitingLabel = new JLabel("Maksymalny czas oczekiwania:");
		JTextField maxWaitingField = new JTextField();
		JButton cancelButton = new JButton("Anuluj");
		JButton disconnectButton = new JButton("Rozłącz");


		upPanel.setLayout(new GridLayout(2, 4, 10, 10));
		upPanel.setBounds(10, 5, 800, 80);
		upPanel.add(wSection);
		upPanel.add(combo);
		upPanel.add(buttonChange);
		upPanel.add(buttonJoin);
		upPanel.add(listUsers);
		upPanel.add(listAccepted);
		upPanel.add(listWaitingToAccept);
		upPanel.add(listImWaiting);
		
		
		centerPanel.setLayout(new GridLayout(1, 4, 10, 10));
		centerPanel.setBounds(10, 85, 800, 400);
		centerPanel.add(list1);
		centerPanel.add(list2);
		centerPanel.add(list3);
		centerPanel.add(list4);
		
		
		downPanel.setLayout(new GridLayout(2, 4, 10, 10));
		downPanel.setBounds(10, 495, 800, 80);
		downPanel.add(timeLabel);
		downPanel.add(timeField);
		downPanel.add(enterButton);
		downPanel.add(new JLabel());
		downPanel.add(maxWaitingLabel);
		downPanel.add(maxWaitingField);
		downPanel.add(cancelButton);
		downPanel.add(disconnectButton);

		
		mainPanel.add(upPanel);
		mainPanel.add(centerPanel);
		mainPanel.add(downPanel);
		mainPanel.setLayout(null);
		
		
		frame.add(mainPanel);
		frame.setSize(830, 620);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
		
		buttonJoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Section s = new Section();
				s.nameSection = JOptionPane.showInputDialog("Wprowadź nazwę sekcji");
				s.sectionHost= JOptionPane.showInputDialog("Wprowadź ip serwera");
				s.sectionPort= Integer.valueOf(JOptionPane.showInputDialog("Wprowadź port serwera"));
				try {
					Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
					ServerInterface a = (ServerInterface)reg.lookup("rmi");
					a.Ping();
				}
				catch(Exception ex) {
					JOptionPane.showMessageDialog(frame,
						    "Serwer nie odpowiada.",
						    "Connecting error",
						    JOptionPane.ERROR_MESSAGE);
					return;
				}	
				User me = new User();
				me.name = myName;
				me.port = myPort;
				try {
					Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
					ServerInterface a = (ServerInterface)reg.lookup("rmi");
					String _users = a.RegisterNewUser(serializableToString(me));
					s.users = (ArrayList<User>)objectFromString(_users);
					for( User x : s.users) {
						s.listUsers.addElement(x.name);
					}
				}
				catch(Exception ex) {
					JOptionPane.showMessageDialog(frame,
						    ex,
						    "Connecting error",
						    JOptionPane.ERROR_MESSAGE);
					return;
				}
				sections.add(s);
				combo.addItem(s.nameSection);
				combo.setSelectedIndex(sections.size()-1);
				list1.setModel(s.listUsers);
				list2.setModel(s.listAcceptedRequests);
				list3.setModel(s.listWaitingRequests);
				list4.setModel(s.listWaitingForAnswers);
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Registry reg = LocateRegistry.createRegistry(Integer.parseInt(args[1]));
			ClientRemote ar = new ClientRemote(Integer.parseInt(args[1]), args[0]);
			reg.rebind("rmi", ar);
			System.out.println("Client is ready");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean RegisterNewUser(String _user, int sectionPort) throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			String sectionHost = getClientHost();
			Section s = sections.stream().filter((n) -> n.sectionHost == sectionHost && n.sectionPort == sectionPort).findFirst().orElse(null);
			
			
			User user = (User)objectFromString(_user);
			s.users.add(user);
			s.listUsers.addElement(user.name);
		}
		catch(Exception ex){
			System.out.println(ex);
			return false;
		}
		return true;
	}

	@Override
	public boolean DeregisterUser(String _user, int sectionPort) throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			String sectionHost = getClientHost();
			Section s = sections.stream().filter((n) -> n.sectionHost == sectionHost && n.sectionPort == sectionPort).findFirst().orElse(null);
			
			
			User user = (User)objectFromString(_user);
			boolean a = s.users.removeIf(n -> (n.ip == user.ip && n.port == user.port));
			s.waitingForAnswers.removeIf(n -> (n.fromHost == user.ip && n.fromPort == user.port));
			s.waitingRequests.removeIf(n -> (n.fromHost == user.ip && n.fromPort == user.port));

			if(a) {
				s.listUsers.removeAllElements();
		        for(User x : s.users) {
		        	s.listUsers.addElement(x.name);
		        }
		        s.listWaitingForAnswers.removeAllElements();
		        for(Reply x : s.waitingForAnswers) {
		        	s.listWaitingForAnswers.addElement(x.fromName);
		        }
		        s.listWaitingRequests.removeAllElements();
		        for(Request x : s.waitingRequests) {
		        	s.listWaitingRequests.addElement(x.fromName);
		        }
				
				if(s.inCriticalSection) {
					if(s.waitingForAnswers.size() == 0) {
						//wywołanie sekcji krytycznej async
					}
				}
			}
		}
		catch(Exception ex){
			System.out.println(ex);
			return false;
		}
		return true;
	}

	@Override
	public boolean Request(String _req, int sectionPort) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			String sectionHost = getClientHost();
			Section s = sections.stream().filter((n) -> n.sectionHost == sectionHost && n.sectionPort == sectionPort).findFirst().orElse(null);

			
			Request req = (Request)objectFromString(_req);
			if(s.inCriticalSection) {
				s.waitingRequests.add(req);
				s.waitingRequests.stream()
			  	.sorted((object1, object2) -> object1.date.compareTo(object2.date));
				s.listWaitingRequests.addElement(req.fromName);
				return false;
			}
			else {
				//wyslij opdowiedz
				//dodaj do tabelki
			}			
		}
		catch(Exception ex){
			System.out.println(ex);
			return false;
		}
		return true;
	}

	@Override
	public boolean Reply(String _rep, int sectionPort) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			String sectionHost = getClientHost();
			Section s = sections.stream().filter((n) -> n.sectionHost == sectionHost && n.sectionPort == sectionPort).findFirst().orElse(null);


			Reply rep = (Reply)objectFromString(_rep);
			if(rep.date == s.sendRequest.date) {
				s.waitingForAnswers.removeIf(n -> (n.fromHost == rep.fromHost && n.fromPort == rep.fromPort));
				s.listWaitingForAnswers.removeAllElements();
		        for(Reply x : s.waitingForAnswers) {
		        	s.listWaitingForAnswers.addElement(x.fromName);
		        }
				
				if(s.waitingForAnswers.size() == 0) {
					//wywołanie sekcji krytycznej async
				}
			}			
		}
		catch(Exception ex){
			System.out.println(ex);
			return false;
		}
		return true;
	}

	@Override
	public boolean Ping() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
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
	
	private static String serializableToString( User o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
	
	
}
