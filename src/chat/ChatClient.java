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

//x버튼을 눌러서 끌 때 채팅 내역(gettext) 파일에 저장
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
		taChatList = new JTextArea(10, 30);// 옆30 밑으로10
		scrollPane = new ScrollPane();
		topJPanel = new JPanel();
		bottomJPanel = new JPanel();
	}

	private void seting() {
		setTitle("채팅 다대다 클라이언트");
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
		// while을 돌면서 서버로부터 메시지를 받아서 taChatList에 뿌리기
		public void run() {
			
			try {
				String input = null;
				//4. 서버의 for에서 받은 값을 클라이언트에 출력
				while((input = reader.readLine()) != null) {
					System.out.println("from server : "+input);
					taChatList.setText(input);
				}
			} catch (Exception e) {
				System.out.println(TAG+"ClientInfo캐치 : "+e.getMessage());
			}
		}
	}

	private void connect() {
		// 서버 소켓에 연결
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			ClientInfo rt = new ClientInfo();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG + "서버 연결 에러: " + e1.getMessage());
		}
	}
	
	private void send() {
		String chat = tfChat.getText();
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			ClientInfo rt = new ClientInfo();
			rt.start();
			
			//1. 서버한테 입력값 전송
			//System.out.println(TAG+"tfChat : "+tfChat);
			writer.println(chat);
			//writer.flush();
			tfChat.setText("");
			
		} catch (Exception e) {
			System.out.println("send 캐치");
		}
		
		//1번에 taChatList 뿌리기
		//taChatList.append("[내 메시지] " + chat + "\n");

		//2번 서버로 전송
		
	}

	public static void main(String[] args) {
		new ChatClient();
	}
}