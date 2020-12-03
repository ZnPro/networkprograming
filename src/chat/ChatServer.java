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
	private Vector<ClientInfo> vc; // 연결된 클라이언트 클래스(소켓)을 담는 컬렉션(소켓을 담으면 상태를 담을수없음)

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "클라이언트 연결 대기중...");
			// main스레드의역할 =>while
			while (true) {
				Socket socket = serverSocket.accept();// 클라이언트 연결 대기
				System.out.println("요청 받음");
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
		PrintWriter writer; // BufferedWriter와 다른 점은 내려쓰기 함수를 지원

		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				System.out.println("서버 연결 실패: " + e.getMessage());// 요것만
			}
		}
		@Override
		public void run() {
			// 역할: 클라이언트로부터 받은 메시지를 모든 클라이언트한테 재전송
			// 생성된 2개의 스레드를 run에서 제어
			try {
				String input = null;
				//2. while(true)에서 받은 입력값 서버에 출력
				while((input = reader.readLine()) != null) {
					System.out.println("from client : "+input);
					//3. for : 위에서 받은 입력값을 클라이언트while((line = reader.readLine())에 전송
					for (ClientInfo socketThread : vc) {
						socketThread.writer.println(input);
					}
				}
			} catch (Exception e) {
				System.out.println("서버 캐치");
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}
}