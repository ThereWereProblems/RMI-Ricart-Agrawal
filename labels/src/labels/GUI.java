package labels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI {
	
	public GUI() {
		JFrame frame = new JFrame();
		JPanel mainPanel = new JPanel();
		
		JPanel upPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel downPanel = new JPanel();
		
		
		//up panel
		JLabel wSection = new JLabel("Wybierz sekcje");
		JComboBox combo = new JComboBox();
		JButton buttonChange = new JButton();
		JButton buttonJoin = new JButton();
		JLabel listUsers = new JLabel("Inni użytkownicy w sekcji:");
		JLabel listAccepted = new JLabel("Zaakceptowane zapytania:");
		JLabel listWaitingToAccept = new JLabel("Zapytania do zaakceptowania:");
		JLabel listImWaiting = new JLabel("Oczekuję odpowiedzi od:");

		
		//center panel
		JList list1 = new JList();
		JList list2 = new JList();
		JList list3 = new JList();
		JList list4 = new JList();
		
		
		//down Panel
		JLabel timeLabel = new JLabel("Wejdz do sekcji na:");
		JTextField timeField = new JTextField();
		JButton enterButton = new JButton();
		JLabel maxWaitingLabel = new JLabel("Maksymalny czas oczekiwania:");
		JTextField maxWaitingField = new JTextField();
		JButton cancelButton = new JButton();
		JButton disconnectButton = new JButton();


		upPanel.setLayout(new GridLayout(2, 4, 10, 10));
		upPanel.setSize(800, 200);
		upPanel.add(wSection);
		upPanel.add(combo);
		upPanel.add(buttonChange);
		upPanel.add(buttonJoin);
		upPanel.add(listUsers);
		upPanel.add(listAccepted);
		upPanel.add(listWaitingToAccept);
		upPanel.add(listImWaiting);
		
		
		centerPanel.setLayout(new GridLayout(1, 4, 10, 10));
		centerPanel.setMinimumSize(new Dimension(100, 400));
		centerPanel.add(list1);
		centerPanel.add(list2);
		centerPanel.add(list3);
		centerPanel.add(list4);
		
		
		downPanel.setLayout(new GridLayout(2, 4, 10, 10));
		downPanel.add(timeLabel);
		downPanel.add(timeField);
		downPanel.add(enterButton);
		downPanel.add(new JLabel());
		downPanel.add(maxWaitingLabel);
		downPanel.add(maxWaitingField);
		downPanel.add(cancelButton);
		downPanel.add(disconnectButton);

		
		mainPanel.setLayout(new GridLayout(3, 1, 10, 10));
		mainPanel.add(upPanel);
		mainPanel.add(centerPanel);
		mainPanel.add(downPanel);
		
		frame.add(mainPanel);
		frame.pack();
		//frame.setSize(800, 600);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		

		
		
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GUI();
	}

}
