import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdderInterface extends Remote {

	public int Add(int a, int b) throws RemoteException;

}
