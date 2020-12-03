package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import client.SocketClient.SocketThread;

//x��ư�� ������ �� �� ä�� ����(gettext) ���Ͽ� ����
public class ChatClient extends JFrame {

	private ChatClient chatclient = this;
	private static final String TAG = "ChatClient: ";

	private static final int PORT = 10000;

	private JButton btnConnect, btnSend;
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;

	private JPanel topJPanel, bottomJPanel;

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	public ChatClient() {
		init();
		seting();
		batch();
		listener();
		
		setVisible(true);
	}

	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20);// coulm 20
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30);// ��30 ������10
		scrollPane = new ScrollPane();
		topJPanel = new JPanel();
		bottomJPanel = new JPanel();
	}

	private void seting() {
		setTitle("ä�� �ٴ�� Ŭ���̾�Ʈ");
		setSize(350, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		taChatList.setBackground(Color.orange);
		taChatList.setForeground(Color.blue);

	}

	private void batch() {
		topJPanel.add(tfHost);
		topJPanel.add(btnConnect);
		bottomJPanel.add(tfChat);
		bottomJPanel.add(btnSend);
		scrollPane.add(taChatList);

		add(topJPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomJPanel, BorderLayout.SOUTH);
	}

	private void listener() {
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});

		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
	}

	class ClientInfo extends Thread {
		@Override
		// while�� ���鼭 �����κ��� �޽����� �޾Ƽ� taChatList�� �Ѹ���
		public void run() {
			
			try {
				String input = null;
				//4. ������ for���� ���� ���� Ŭ���̾�Ʈ�� ���
				while((input = reader.readLine()) != null) {
					System.out.println("from server : "+input);
					taChatList.setText(input);
				}
			} catch (Exception e) {
				System.out.println(TAG+"ClientInfoĳġ : "+e.getMessage());
			}
		}
	}

	private void connect() {
		// ���� ���Ͽ� ����
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			ClientInfo rt = new ClientInfo();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG + "���� ���� ����: " + e1.getMessage());
		}
	}
	
	private void send() {
		String chat = tfChat.getText();
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			ClientInfo rt = new ClientInfo();
			rt.start();
			
			//1. �������� �Է°� ����
			//System.out.println(TAG+"tfChat : "+tfChat);
			writer.println(chat);
			//writer.flush();
			tfChat.setText("");
			
		} catch (Exception e) {
			System.out.println("send ĳġ");
		}
		
		//1���� taChatList �Ѹ���
		//taChatList.append("[�� �޽���] " + chat + "\n");

		//2�� ������ ����
		
	}

	public static void main(String[] args) {
		new ChatClient();
	}
}