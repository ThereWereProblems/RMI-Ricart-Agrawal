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

public class ServerRemote extends UnicastRemoteObject implements ServerInterface {

	private List<User> users;
	
	protected ServerRemote() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		users = new ArrayList<User>();
	}
	public void Message(String s) {
		System.out.println("register " + s);
	}

	@Override
	public boolean RegisterNewUser(String _user) throws RemoteException {
		// TODO Auto-generated method stub
		//Dać walidacje portu
		//Dać testowy ping
		
		
	    User user = new User();
	    try {
	    	user = (User)objectFromString(_user);
	    }
	    catch(Exception ex) {
	    	
	    }
	    		
		
		if(user.name == null || user.name.trim() == "")
			return false;
		
		user.name = user.name.trim();
		
		for(User x : users) {
			if(x.name == user.name)
				return false;
		}
		
		try{
			user.ip = getClientHost();
		}catch(Exception e){return false;}
		
		for(User x : users) {
			try {
				Registry reg = LocateRegistry.getRegistry("localhost",x.port);
				ClientInterface a = (ClientInterface)reg.lookup("rmi");
				a.RegisterNewUser(serializableToString(user));
			}
			catch(Exception ex) {
				System.out.println(ex);
			}
		}
		
		users.add(user);
		
		return true;
	}

	@Override
	public boolean DeregisterUser() throws RemoteException {
		// TODO Auto-generated method stub
		
		String ip;
		
		try{
		    ip = getClientHost();
		}catch(Exception e){return false;}
		
		String s = users.stream().filter((n) -> n.ip == ip).findFirst().orElse(null).name;
		boolean h = users.removeIf(n -> (n.ip == ip));
		
		if(h) {
			System.out.println("deregister");

			for(User x : users) {
				try {
					Registry reg = LocateRegistry.getRegistry("localhost",x.port);
					ClientInterface a = (ClientInterface)reg.lookup("rmi");
					a.DeregisterUser(s);
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
		
		String ip;
		Request req = new Request();
		
		try{
		    ip = getClientHost();
		    req = (Request)objectFromString(_req);
		}catch(Exception e){return false;}
		
		req.from = users.stream().filter((n) -> n.ip == ip).findFirst().orElse(null).name;
		
		for(User x : users) {
			
			if(x.ip != ip)
			{
				//wysłac zapytanie
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
			user = users.stream().filter((n) -> n.name == rep.to).findFirst().orElse(null);
		}catch(Exception ex) {		}
		
		
		
		if(user == null)
			return false;
		else {
			//wysli odpowiedz
		}
		
		return true;
	}
	
	public String GetUsers() throws RemoteException{
		
		String ip;
		List<String> names = new ArrayList<String>();
		try{
		    ip = getClientHost();
		    for(User x : users) {
		    	if(x.ip != ip)
		    		names.add(x.name);
		    }
		    return serializableToString(names);
		    
		}catch(Exception e){
			return "";
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

	private static String serializableToString(List<String> o ) throws IOException {
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

}
