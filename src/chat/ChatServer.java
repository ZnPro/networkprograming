package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {
	private static final String TAG = "ChatServer: ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���(������ ������ ���¸� ����������)

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			// main�������ǿ��� =>while
			while (true) {
				Socket socket = serverSocket.accept();// Ŭ���̾�Ʈ ���� ���
				System.out.println("��û ����");
				ClientInfo clientinfo = new ClientInfo(socket);
				
				clientinfo.start();
				vc.add(clientinfo);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	class ClientInfo extends Thread {

		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ� ���� �������� �Լ��� ����

		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				System.out.println("���� ���� ����: " + e.getMessage());// ��͸�
			}
		}
		@Override
		public void run() {
			// ����: Ŭ���̾�Ʈ�κ��� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
			// ������ 2���� �����带 run���� ����
			try {
				String input = null;
				//2. while(true)���� ���� �Է°� ������ ���
				while((input = reader.readLine()) != null) {
					System.out.println("from client : "+input);
					//3. for : ������ ���� �Է°��� Ŭ���̾�Ʈwhile((line = reader.readLine())�� ����
					for (ClientInfo socketThread : vc) {
						socketThread.writer.println(input);
					}
				}
			} catch (Exception e) {
				System.out.println("���� ĳġ");
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}
}