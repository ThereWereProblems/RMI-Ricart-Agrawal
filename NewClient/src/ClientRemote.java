import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	int selectedInCombo;
	
	List<Section> sections; 
	
	JList list1;
	JList list2;
	JList list3;
	JList list4;
	
	JTextField timeField;
	JTextField maxWaitingField;
	JLabel info;
	
	JButton cancelButton;
	
	ExecutorService pingThreadPool;
	Future pingTask;
	
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
		timeField = new JTextField();
		JButton enterButton = new JButton("Wejdź");
		info = new JLabel();
		JLabel maxWaitingLabel = new JLabel("Maksymalny czas oczekiwania:");
		maxWaitingField = new JTextField();
		cancelButton = new JButton("Anuluj");
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
		downPanel.add(info);
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
		
		pingThreadPool = Executors.newCachedThreadPool();
		pingTask = pingThreadPool.submit(() -> CheckPing());		
		
		buttonJoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Section s = new Section();
				s.nameSection = JOptionPane.showInputDialog("Wprowadź nazwę sekcji");
				s.sectionHost= JOptionPane.showInputDialog("Wprowadź ip serwera");
				s.sectionPort= Integer.valueOf(JOptionPane.showInputDialog("Wprowadź port serwera"));
				try {
					if(s.sectionHost.compareTo("localhost")==0) {
						s.sectionHost = InetAddress.getLocalHost().getHostAddress();
					}
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
				selectedInCombo = sections.size()-1;
				list1.setModel(s.listUsers);
				list2.setModel(s.listAcceptedRequests);
				list3.setModel(s.listWaitingRequests);
				list4.setModel(s.listWaitingForAnswers);
				info.setText(s.info.getText());
			}
		});
		
		buttonChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedInCombo = combo.getSelectedIndex();
				if(selectedInCombo < 0) {
					list1.setModel(new DefaultListModel());
					list2.setModel(new DefaultListModel());
					list3.setModel(new DefaultListModel());
					list4.setModel(new DefaultListModel());
				}
				else {
					list1.setModel(sections.get(selectedInCombo).listUsers);
					list2.setModel(sections.get(selectedInCombo).listAcceptedRequests);
					list3.setModel(sections.get(selectedInCombo).listWaitingRequests);
					list4.setModel(sections.get(selectedInCombo).listWaitingForAnswers);
					info.setText(sections.get(selectedInCombo).info.getText());
				}
					
				
			}
		});

		disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedInCombo == -1)
					return;
				Section s = sections.get(selectedInCombo);
				try {
					Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
					ServerInterface a = (ServerInterface)reg.lookup("rmi");
					a.DeregisterUser(myPort);
				}
				catch(Exception ex) {
					System.out.println(ex);
				}	
				sections.remove(selectedInCombo);
				combo.removeItemAt(selectedInCombo);
				selectedInCombo = -1;
				combo.setSelectedIndex(-1);
				list1.setModel(new DefaultListModel());
				list2.setModel(new DefaultListModel());
				list3.setModel(new DefaultListModel());
				list4.setModel(new DefaultListModel());
				info.setText("");
			}
		});
		
		enterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Section s = sections.get(selectedInCombo);
				if(s.waitingForCriticalSection)
					return;
				if(s.threadpool == null)
					s.threadpool = Executors.newCachedThreadPool();
				s.taskWaiting = s.threadpool.submit(() -> WaitingForCriticSection());
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedInCombo == -1)
					return;
				Section s = sections.get(selectedInCombo);
				
				s.waitingForAnswers.clear();
				s.listWaitingForAnswers.clear();
				
				if(s.taskWaiting!=null)
					s.taskWaiting.cancel(true);
				s.taskWaiting = null;
				
				if(s.taskSection!=null)
					s.taskSection.cancel(true);
				s.taskSection = null;
				
				s.info.setText("Poza sekcją");
				if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
					info.setText(s.info.getText());
				s.waitingForCriticalSection = false;
				s.inCriticalSection = false;
				
				for(Request x : s.waitingRequests) {
					Reply rep = new Reply();
					rep.fromPort = myPort;
					rep.toHost = x.fromHost;
					rep.toPort = x.fromPort;
					rep.date = x.date;
					try {
						Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
						ServerInterface a = (ServerInterface)reg.lookup("rmi");
						a.Reply(serializableToString(rep));
					}
					catch(Exception ex) {
						System.out.println(ex);
					}
					s.acceptedRequests.add(x);
					s.listAcceptedRequests.addElement(x.fromName);
				}
				s.waitingRequests.clear();
				s.listWaitingRequests.clear();
			}
		});
		
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int i=JOptionPane.showConfirmDialog(null, "Seguro que quiere salir?");
                if(i==0)
                {
                	for(Section s : sections) {
                		try {
        					Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
        					ServerInterface a = (ServerInterface)reg.lookup("rmi");
        					a.DeregisterUser(myPort);
        				}
        				catch(Exception ex) {
        					
        				}
                	}
                    System.exit(0);//cierra aplicacion
                }
            }
        });

	}
	
	private void CheckPing() {
		while(true) {
			try {
				TimeUnit.SECONDS.sleep(10);
			}catch(Exception ex) {}
			for(int i = 0; i < sections.size(); i++) {
				Section s = sections.get(i);
				boolean res = false;
				for(int j = 0; j < 4; j++) {
					try {
						Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
						ServerInterface a = (ServerInterface)reg.lookup("rmi");
						res = a.Ping();
					}catch(Exception ex) {}
					if(res)
						break;
					try {
						TimeUnit.SECONDS.sleep(1);
					}catch(Exception ex) {}
				}
				if(!res) {
					if(i == selectedInCombo) {
						selectedInCombo = -1;
						combo.setSelectedIndex(-1);
						list1.setModel(new DefaultListModel());
						list2.setModel(new DefaultListModel());
						list3.setModel(new DefaultListModel());
						list4.setModel(new DefaultListModel());
						info.setText("");
					}
					sections.remove(i);
					combo.removeItemAt(i);
					i--;
				}
			}
		}
	}
	
	private void WaitingForCriticSection() {
		Section s = sections.get(selectedInCombo);
		s.waitingForCriticalSection = true;
		int maxWait = 0;
		try {
			s.enterFor = Integer.parseInt(timeField.getText().trim());
		}catch(Exception ex) {s.enterFor = 0;}
		try{
			maxWait = Integer.parseInt(maxWaitingField.getText().trim());
		}catch(Exception ex) {}
		
		Request req = new Request();
		req.fromPort = myPort;
		req.fromName = myName;
		req.date = LocalDateTime.now();
		s.sendRequest = req;
		
		
		for(User x : s.users) {
			Reply rep  = new Reply();
			rep.fromHost = x.ip;
			rep.fromPort = x.port;
			rep.fromName = x.name;
			rep.date = s.sendRequest.date;
			s.waitingForAnswers.add(rep);
			s.listWaitingForAnswers.addElement(x.name);
		}
		
		
		try {
			Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
			ServerInterface a = (ServerInterface)reg.lookup("rmi");
			a.Request(serializableToString(req));
		}
		catch(Exception ex) {
			System.out.println(ex);
		}
		
		
		if(maxWait > 0 && !s.inCriticalSection) {
			while(maxWait>0 && !s.inCriticalSection) {
				s.info.setText("Oczekuję: " + maxWait);
				if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
					info.setText(s.info.getText());
				System.out.println(s.info.getText());
				maxWait--;
				try {
					TimeUnit.SECONDS.sleep(1);
				}catch(Exception ex) {}
			}
			if(!s.inCriticalSection)
			{
				s.waitingForCriticalSection = false;
				s.waitingForAnswers.clear();
				s.listWaitingForAnswers.clear();
				s.info.setText("Poza sekcją");
				if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
					info.setText(s.info.getText());
				System.out.println(s.info.getText());
				//odp
				for(Request x : s.waitingRequests) {
					Reply rep = new Reply();
					rep.fromPort = myPort;
					rep.toHost = x.fromHost;
					rep.toPort = x.fromPort;
					rep.date = x.date;
					try {
						Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
						ServerInterface a = (ServerInterface)reg.lookup("rmi");
						a.Reply(serializableToString(rep));
					}
					catch(Exception ex) {
						System.out.println(ex);
					}
					s.acceptedRequests.add(x);
					s.listAcceptedRequests.addElement(x.fromName);
				}
				s.waitingRequests.clear();
				s.listWaitingRequests.clear();
			}
		}
		else {
			if(!s.inCriticalSection)
				s.info.setText("Oczekuję");
			if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
				info.setText(s.info.getText());
			System.out.println(s.info.getText());
		}
		return;
	}
	
	private void CriticSection(Section s) {
		s.inCriticalSection = true;
		if(s.enterFor > 0) {
			while(s.enterFor>0) {
				s.info.setText("W sekcji: " + s.enterFor);
				if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
					info.setText(s.info.getText());
				s.enterFor--;
				try {
					TimeUnit.SECONDS.sleep(1);
				}catch(Exception ex) {}
			}
			s.waitingForAnswers.clear();
			s.listWaitingForAnswers.clear();
			s.info.setText("Poza sekcją");
			if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
				info.setText(s.info.getText());
			s.waitingForCriticalSection = false;
			s.inCriticalSection = false;
			
			for(Request x : s.waitingRequests) {
				Reply rep = new Reply();
				rep.fromPort = myPort;
				rep.toHost = x.fromHost;
				rep.toPort = x.fromPort;
				rep.date = x.date;
				try {
					Registry reg = LocateRegistry.getRegistry(s.sectionHost,s.sectionPort);
					ServerInterface a = (ServerInterface)reg.lookup("rmi");
					a.Reply(serializableToString(rep));
				}
				catch(Exception ex) {
					System.out.println(ex);
				}
				s.acceptedRequests.add(x);
				s.listAcceptedRequests.addElement(x.fromName);
			}
			s.waitingRequests.clear();
			s.listWaitingRequests.clear();
		}
		else {
			s.info.setText("W sekcji");
			if(sections.get(selectedInCombo).sectionHost.compareTo(s.sectionHost) == 0 && sections.get(selectedInCombo).sectionPort == s.sectionPort)
				info.setText(s.info.getText());
		}
		return;
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
			Section s = sections.stream().filter((n) -> n.sectionHost.compareTo(sectionHost) == 0 && n.sectionPort == sectionPort).findFirst().orElse(null);
			
			
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
			Section s = sections.stream().filter((n) -> n.sectionHost.compareTo(sectionHost) == 0 && n.sectionPort == sectionPort).findFirst().orElse(null);
			
			
			User user = (User)objectFromString(_user);
			boolean a = s.users.removeIf(n -> (n.ip.compareTo(user.ip) == 0 && n.port == user.port));
			s.waitingForAnswers.removeIf(n -> (n.fromHost.compareTo(user.ip) == 0 && n.fromPort == user.port));
			s.waitingRequests.removeIf(n -> (n.fromHost.compareTo(user.ip) == 0 && n.fromPort == user.port));

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
				
				if(s.waitingForCriticalSection) {
					if(s.waitingForAnswers.size() == 0) {
						s.taskSection = s.threadpool.submit(() -> CriticSection(s));
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
			Section s = sections.stream().filter((n) -> n.sectionHost.compareTo(sectionHost) == 0 && n.sectionPort == sectionPort).findFirst().orElse(null);

			
			Request req = (Request)objectFromString(_req);
			if(s.waitingForCriticalSection && s.sendRequest.date.isBefore(req.date)) {
				s.waitingRequests.add(req);
				//s.waitingRequests.stream()
			  	//.sorted((object1, object2) -> object1.date.compareTo(object2.date));
				s.listWaitingRequests.addElement(req.fromName);
				return false;
			}
			else {
				Reply rep = new Reply();
				rep.fromPort = myPort;
				rep.toHost = req.fromHost;
				rep.toPort = req.fromPort;
				rep.date = req.date;
				try {
					Registry reg = LocateRegistry.getRegistry(sectionHost,sectionPort);
					ServerInterface a = (ServerInterface)reg.lookup("rmi");
					a.Reply(serializableToString(rep));
				}
				catch(Exception ex) {
					System.out.println(ex);
				}
				s.acceptedRequests.add(req);
				s.listAcceptedRequests.addElement(req.fromName);
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
			Section s = sections.stream().filter((n) -> n.sectionHost.compareTo(sectionHost) == 0 && n.sectionPort == sectionPort).findFirst().orElse(null);

			if(!s.waitingForCriticalSection)
				return true;

			Reply rep = (Reply)objectFromString(_rep);

			System.out.println("dostałem odp");
			if(rep.date.isEqual(s.sendRequest.date)) {

				System.out.println("dobra odp");
				boolean la = s.waitingForAnswers.removeIf(n -> (n.fromHost.compareTo(rep.fromHost) == 0 && n.fromPort == rep.fromPort));
				if(la)
					System.out.println("znalazł");
				s.listWaitingForAnswers.clear();
		        for(Reply x : s.waitingForAnswers) {
		        	System.out.println("dodałem do listy odp");
		        	s.listWaitingForAnswers.addElement(x.fromName);
		        }
				
				if(s.waitingForAnswers.size() == 0) {
					s.taskSection = s.threadpool.submit(() -> CriticSection(s));
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
	
	private static String serializableToString( Reply o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
	
	private static String serializableToString( Request o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
	
}
