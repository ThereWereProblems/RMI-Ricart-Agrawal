import java.rmi.RemoteException;

public interface ClientInterface {
	
	public boolean RegisterNewUser(User user) throws RemoteException;
	public boolean DeregisterUser(String name) throws RemoteException;
	public boolean Request(Request req) throws RemoteException;
	public boolean Reply(Reply rep) throws RemoteException;
	public boolean Ping() throws RemoteException;


}
