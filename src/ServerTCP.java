import java.net.*;
import java.io.*;

public class ServerTCP {
	
    public static final int PORT = 1027;
	public static int countIdClient = 0;

    public static void main(String[] args) {
		try(ServerSocket server = new ServerSocket(PORT)) {
	    	InetAddress address = InetAddress.getLocalHost();
			System.out.println(address);
		    while(true) {
				Socket socket = server.accept();
				System.out.println("connexion acceptee");
				countIdClient++;
				ServerClientService servC = new ServerClientService(socket, countIdClient);
				Thread tr = new Thread(servC);
				tr.start();
		    }
		} catch(IOException e) {
		    System.out.println("Erreur lors de l'execution du ServerTCP:\n" + e);
		}
    }
}