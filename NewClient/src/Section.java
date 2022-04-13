

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class Section {
	
	public String nameSection;
	public String sectionHost;
	public int sectionPort;
	
	public boolean inCriticalSection;
	public Request sendRequest;
	
	public List<User> users; //lista users
	public List<Reply> waitingForAnswers; //czekam na odpowiedz przed wejsciem do sekcji
	public List<Request> acceptedRequests; //historia zaakceptowanych
	public List<Request> waitingRequests; //czekają na akceptacje
	
	
	public DefaultListModel listUsers;
	public DefaultListModel listAcceptedRequests;
	public DefaultListModel listWaitingRequests;
	public DefaultListModel listWaitingForAnswers;
	
	public Section() {
		users = new ArrayList<User>();
		waitingForAnswers = new ArrayList<Reply>();
		acceptedRequests = new ArrayList<Request>();
		waitingRequests = new ArrayList<Request>();
		
		listUsers = new DefaultListModel();
		listAcceptedRequests = new DefaultListModel();
		listWaitingRequests = new DefaultListModel();
		listWaitingForAnswers = new DefaultListModel();
	}

}
