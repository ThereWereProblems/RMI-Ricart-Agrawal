import java.rmi.*;
import java.rmi.server.*;

public class AdderRemote extends UnicastRemoteObject implements AdderInterface{

	protected AdderRemote() throws RemoteException {
		super();
	}

	public int Add(int a, int b) throws RemoteException {
		try{
		    System.out.println(getClientHost()); // display message
		}catch(Exception e){}
		return a+b;
	}

}
