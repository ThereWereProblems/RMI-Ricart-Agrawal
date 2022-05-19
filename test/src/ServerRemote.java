import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerRemote extends UnicastRemoteObject implements ServerInterface {

	
	public ServerRemote(int port) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String Ping() throws RemoteException {
		// TODO Auto-generated method stub
		String z = "";
		try {
			System.out.println(InetAddress.getLocalHost());
			z = InetAddress.getLocalHost().getHostAddress();
			System.out.println(getClientHost());
		}catch(Exception ex) {
			System.out.println(ex);
		}
		
		return z;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.setProperty("java.rmi.server.hostname", args[1]);
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
}
