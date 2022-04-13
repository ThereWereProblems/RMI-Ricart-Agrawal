import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{
	
	public boolean RegisterNewUser(String _user, int sectionPort) throws RemoteException;
	public boolean DeregisterUser(String name, int sectionPort) throws RemoteException;
	public boolean Request(String _req, int sectionPort) throws RemoteException;
	public boolean Reply(String _rep, int sectionPort) throws RemoteException;
	public boolean Ping() throws RemoteException;


}
