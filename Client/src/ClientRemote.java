import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClientRemote extends UnicastRemoteObject implements ClientInterface {

	private boolean inCriticalSection;
	private Request sendRequest;
	
	private List<User> users;
	private List<User> sendToUsers;
	private List<Reply> answers;
	private List<Request> acceptedRequests;
	private List<Request> waitingRequests;
	
	protected ClientRemote() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		users = new ArrayList<User>();
		sendToUsers = new ArrayList<User>();
		answers = new ArrayList<Reply>();
		acceptedRequests = new ArrayList<Request>();
		waitingRequests = new ArrayList<Request>();
		
		inCriticalSection = false;
	}

	@Override
	public boolean RegisterNewUser(User user) throws RemoteException {
		// TODO Auto-generated method stub
		users.add(user);
		
		return true;
	}

	@Override
	public boolean DeregisterUser(String name) throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean a = users.removeIf(n -> (n.name == name));
		answers.removeIf(n -> (n.from == name));
		sendToUsers.removeIf(n -> (n.name == name));
		
		if(inCriticalSection) {
			if(answers.size()==sendToUsers.size()) {
				//wywołanie sekcji krytycznej
			}
		}
		
		return a;
	}

	@Override
	public boolean Request(Request req) throws RemoteException {
		// TODO Auto-generated method stub
		if(inCriticalSection) {
			waitingRequests.add(req);
			waitingRequests.stream()
			  .sorted((object1, object2) -> object1.date.compareTo(object2.date));
			return false;
		}
		else {
			//wyslij opdowiedz
		}
		return true;
	}

	@Override
	public boolean Reply(Reply rep) throws RemoteException {
		// TODO Auto-generated method stub
		
		if(rep.date == sendRequest.date) {
			answers.add(rep);
			if(answers.size()==sendToUsers.size()) {
				//wywołanie sekcji krytycznej
			}
		}
		
		return true;
	}

	@Override
	public boolean Ping() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}
	

}
