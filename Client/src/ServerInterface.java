import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	
	public boolean RegisterNewUser(User user) throws RemoteException;
	public boolean DeregisterUser() throws RemoteException;
	public boolean Request(Request req) throws RemoteException;
	public boolean Reply(Reply rep) throws RemoteException;

}
