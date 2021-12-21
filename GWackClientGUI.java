import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GWackClientGUI extends JFrame {
	public ConnectionSession session;

	public JPanel topP = new JPanel();
	public JPanel bodyP = new JPanel();
	public JPanel leftP = new JPanel();
	public JPanel rightP = new JPanel();
 
	public JLabel nameLabel = new JLabel("Name", 10);
	public JLabel ipLabel = new JLabel("IP");
	public JLabel portLabel = new JLabel("Port");
	public JLabel membersLabel = new JLabel("Members");
	public JLabel messagesLabel = new JLabel("Messages");
	
	public JLabel composeLabel = new JLabel("Compose");
	public JTextField nameField = new JTextField();
	public JTextField ipField = new JTextField();
	public JTextField portField = new JTextField();
	public JTextField composeText = new JTextField();

	public JTextArea membersText = new JTextArea();
	public JTextArea messagesText = new JTextArea();

	public JButton connectButton = new JButton("Connect");
	public JButton sendButton = new JButton("Send");


	public GWackClientGUI() {
		super();

		// fundamentals
        this.setSize(1000, 1000);
        this.setLocation(1000, 200);
        this.setTitle("GWack");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// top
		topP.setLayout(new BoxLayout(topP, BoxLayout.LINE_AXIS));
		nameField.setPreferredSize(new Dimension (200, 20));
		ipField.setPreferredSize(new Dimension (200, 20));
		portField.setPreferredSize(new Dimension (200, 20));
		topP.add(Box.createRigidArea(new Dimension(20,0)));
		topP.add(nameLabel);
		topP.add(nameField);
		topP.add(Box.createRigidArea(new Dimension(20,0)));
		topP.add(ipLabel);
		topP.add(ipField);
		topP.add(Box.createRigidArea(new Dimension(20,0)));
		topP.add(portLabel);
		topP.add(portField);
		topP.add(Box.createRigidArea(new Dimension(20,0)));
		topP.add(connectButton);
		topP.add(Box.createRigidArea(new Dimension(20,0)));
		
		// left
		leftP.setLayout(new BoxLayout(leftP, BoxLayout.PAGE_AXIS));
		membersText.setColumns(20);
		membersText.setRows(20);
		leftP.add(membersLabel);
		leftP.add(membersText);

		// right
		rightP.setLayout(new BoxLayout(rightP, BoxLayout.PAGE_AXIS));
		messagesLabel.setAlignmentX(CENTER_ALIGNMENT);
		messagesText.setColumns(50);
		messagesText.setRows(50);
		composeText.setColumns(50);

		rightP.setLayout(new BoxLayout(rightP, BoxLayout.PAGE_AXIS));
		rightP.add(Box.createRigidArea(new Dimension(0,20)));
		rightP.add(messagesText);
		JPanel composeP = new JPanel();
		composeP.add(composeText);
		composeP.add(sendButton);
		rightP.add(composeP);

		// Body
		bodyP.setLayout(new BoxLayout(bodyP, BoxLayout.LINE_AXIS));
		bodyP.add(Box.createRigidArea(new Dimension(20,0)));
		bodyP.add(leftP);
		bodyP.add(Box.createRigidArea(new Dimension(20,0)));
		bodyP.add(rightP);
		bodyP.add(Box.createRigidArea(new Dimension(20,0)));

		// Window
		this.add(topP, BorderLayout.NORTH);
		this.add(bodyP, BorderLayout.CENTER);

		// functions
		this.connectButton.addActionListener((e) -> {
			try {
				if (this.connectButton.getText().equals("Connect")) {
					this.session = new ConnectionSession(this, nameField.getText(), ipField.getText(), Integer.parseInt(portField.getText()));
					if (this.session.sock == null) return;
					this.session.connect(nameField.getText());
					this.connectButton.setText("Disconnect");
					this.nameField.setEditable(false);
					this.ipField.setEditable(false);
					this.portField.setEditable(false);
				}
				else {
					this.session.disconnect();
					this.connectButton.setText("Connect");
					this.nameField.setEditable(true);
					this.ipField.setEditable(true);
					this.portField.setEditable(true);
				}
			} catch (Exception exp){ 
				System.err.println(exp);
				System.exit(1);
			}
		});

		sendButton.addActionListener((l) -> {
			session.sendMessage(composeText.getText());
			composeText.setText("");
		});

		KeyListener k = new GuiKeyListener();
		composeText.addKeyListener(k);

		this.nameField.setEditable(true);
		this.ipField.setEditable(true);
		this.portField.setEditable(true);
		this.membersText.setEditable(false);
		this.messagesText.setEditable(false);
	}

	public void updateMembers(String s) {
		String list[] = s.split("&&");
		String list_str = "";
		for (int i = 1; i < list.length; i++) {
			list_str += list[i] + "\n";
		}
		membersText.setText(list_str);
	}

	public void getMessage(String s) {
		if (s.charAt(0) == '[')
			this.messagesText.append(s + "\n");
	}

	private class GuiKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e){
			if (e.getKeyCode() == 10) {
				session.sendMessage(composeText.getText());
				composeText.setText("");
			}
		}
		public void keyTyped(KeyEvent e){}
		public void keyReleased(KeyEvent e) {}
	}

	public static void main(String[] args) {
		GWackClientGUI client = new GWackClientGUI();
		client.setVisible(true);
	}

}