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
	
	public ClientRemote(int port) throws RemoteException {
		super();
		try {
			Registry reg = LocateRegistry.getRegistry("localhost",1099);
			ServerInterface a = (ServerInterface)reg.lookup("rmi");
			String z = a.RegisterNewUser(port);
			System.out.println(z);
		}
		catch(Exception ex) {
			System.out.println(ex);
		}	
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Registry reg = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
			ClientRemote ar = new ClientRemote(Integer.parseInt(args[0]));
			reg.rebind("rmi", ar);
			System.out.println("Client is ready");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	

	@Override
	public String Ping() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("ping");
		try {
			return getClientHost();
		}
		catch(Exception ex) {
			return ex.getMessage();
		}
	}
	
	
}
