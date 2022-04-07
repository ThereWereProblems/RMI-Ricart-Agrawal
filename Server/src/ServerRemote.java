import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerRemote extends UnicastRemoteObject implements ServerInterface {

	private List<User> users;
	
	protected ServerRemote() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		users = new ArrayList<User>();
	}

	@Override
	public boolean RegisterNewUser(User user) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("register " + user.name);
		//Dać walidacje portu
		//Dać testowy ping
		
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
		
		//Dac liste userów
		//Wysłąć wiadomosc do reszty
		
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
		
		boolean a = users.removeIf(n -> (n.ip == ip));
		
		if(a) {
			System.out.println("deregister");

			//rozesłać wiadomość
		}
		
		return a;
	}

	@Override
	public boolean Request(Request req) throws RemoteException {
		// TODO Auto-generated method stub
		
		String ip;
		
		try{
		    ip = getClientHost();
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
	public boolean Reply(Reply rep) throws RemoteException {
		// TODO Auto-generated method stub
		
		User user = users.stream().filter((n) -> n.name == rep.to).findFirst().orElse(null);
		
		if(user == null)
			return false;
		else {
			//wysli odpowiedz
		}
		
		return true;
	}
	
	

}
