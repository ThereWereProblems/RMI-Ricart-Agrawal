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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;

public class ServerRemote extends UnicastRemoteObject implements ServerInterface {

	private int myPort;
	private List<User> users;
	
	ExecutorService pingThreadPool;
	Future pingTask;
	
	public ServerRemote(int port) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		myPort = port;
		users = new ArrayList<User>();
		pingThreadPool = Executors.newCachedThreadPool();
		pingTask = pingThreadPool.submit(() -> CheckPing());
	}
	
	private void CheckPing() {
		while(true) {
			try {
				TimeUnit.SECONDS.sleep(10);
			}catch(Exception ex) {}
			for(int i = 0; i < users.size(); i++) {
				User s = users.get(i);
				boolean res = false;
				for(int j = 0; j < 4; j++) {
					try {
						Registry reg = LocateRegistry.getRegistry(s.ip,s.port);
						ClientInterface a = (ClientInterface)reg.lookup("rmi");
						res = a.Ping();
					}catch(Exception ex) {}
					if(res)
						break;
					try {
						TimeUnit.SECONDS.sleep(1);
					}catch(Exception ex) {}
				}
				if(!res) {
					users.remove(i);
					for(User x : users) {
						try {
							Registry reg = LocateRegistry.getRegistry(x.ip,x.port);
							ClientInterface a = (ClientInterface)reg.lookup("rmi");
							a.DeregisterUser(serializableToString(s),myPort);
						}
						catch(Exception ex) {
							System.out.println(ex);
						}
					}
					i--;
				}
			}
		}
	}

	@Override
	public String RegisterNewUser(String _user) throws RemoteException {
		// TODO Auto-generated method stub
		
		//Dać testowy ping
		
		String r = "";
	    User user = new User();
	    
	    try {
	    	user = (User)objectFromString(_user);
	    	user.ip = getClientHost();
	    	r = serializableToString(users);
	    }
	    catch(Exception ex) {
	    	System.out.println(ex);
	    }
		 
		
		for(User x : users) {
			try {
				Registry reg = LocateRegistry.getRegistry(x.ip,x.port);
				ClientInterface a = (ClientInterface)reg.lookup("rmi");
				a.RegisterNewUser(serializableToString(user), myPort);
			}
			catch(Exception ex) {
				System.out.println(ex);
			}
		}
		
		users.add(user);
		
		return r;
	}

	@Override
	public boolean DeregisterUser(int port) throws RemoteException {
		// TODO Auto-generated method stub
		String host;
		User s = new User();
		boolean h = false;
		try {
			host = getClientHost();
			s = users.stream().filter((n) -> n.ip.compareTo(host) == 0 && n.port == port).findFirst().orElse(null);
			h = users.removeIf(n -> (n.ip.compareTo(host) == 0 && n.port == port));
		}
		catch(Exception ex){
			System.out.println(ex);
		}
		
		if(h) {
			for(User x : users) {
				try {
					Registry reg = LocateRegistry.getRegistry(x.ip,x.port);
					ClientInterface a = (ClientInterface)reg.lookup("rmi");
					a.DeregisterUser(serializableToString(s),myPort);
				}
				catch(Exception ex) {
					System.out.println(ex);
				}
			}
		}
		
		return h;
	}

	@Override
	public boolean Request(String _req) throws RemoteException {
		// TODO Auto-generated method stub
		
		Request req = new Request();
		
		try{
		    req = (Request)objectFromString(_req);
		    req.fromHost = getClientHost();
		}catch(Exception ex){
			System.out.println(ex);
		}
				
		for(User x : users) {
			
			if(x.ip.compareTo(req.fromHost) != 0 || x.port != req.fromPort)
			{
				try {
					Registry reg = LocateRegistry.getRegistry(x.ip,x.port);
					ClientInterface a = (ClientInterface)reg.lookup("rmi");
					a.Request(serializableToString(req), myPort);
				}
				catch(Exception ex) {
					System.out.println(ex);
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean Reply(String _rep) throws RemoteException {
		// TODO Auto-generated method stub
		
		Reply rep;

		User user = new User();
		try {
			rep = (Reply)objectFromString(_rep);
			rep.fromHost = getClientHost();
			user = users.stream().filter((n) -> n.ip.compareTo(rep.toHost) == 0 && n.port == rep.toPort).findFirst().orElse(null);
			Registry reg = LocateRegistry.getRegistry(user.ip,user.port);
			ClientInterface a = (ClientInterface)reg.lookup("rmi");
			a.Reply(serializableToString(rep), myPort);
		}catch(Exception ex) {
			System.out.println(ex);
		}
		return true;
	}
	
	@Override
	public boolean Ping() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Registry reg = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
			ServerRemote ar = new ServerRemote(Integer.parseInt(args[0]));
			reg.rebind("rmi", ar);
			System.out.println("Server is ready");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
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

	private static String serializableToString(List<User> o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
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
