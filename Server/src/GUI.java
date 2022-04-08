import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JFrame;

public class GUI {
	
	JFrame frame;
	
	GUI(int port){
		try {
			Registry reg = LocateRegistry.createRegistry(port);
			ServerRemote ar = new ServerRemote();
			reg.rebind("rmi", ar);
			System.out.println("Server is ready");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Server");
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args[0] == null)
			System.out.println("Podaj port"); 
		else
			new GUI(Integer.parseInt(args[0]));
	}

}
