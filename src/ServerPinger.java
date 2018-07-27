import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerPinger {
	public static String checkServer(String serverName, int port) throws IOException{
		Socket pingSocket = null;
		PrintWriter out = null;
		BufferedReader in = null; 
		try {
			pingSocket = new Socket(serverName, port);
			out = new PrintWriter(pingSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
		} catch (IOException e) {
			return "The server is down ;(";
		}
		out.close();
		in.close();
		pingSocket.close();
		return "The server is up! :)";
	}
}
