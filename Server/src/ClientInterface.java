import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	
	public boolean RegisterNewUser(String _user) throws RemoteException;
	public boolean DeregisterUser(String name) throws RemoteException;
	public boolean Request(String _req) throws RemoteException;
	public boolean Reply(String _rep) throws RemoteException;
	public boolean Ping() throws RemoteException;


}
