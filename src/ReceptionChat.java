
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ReceptionChat implements Runnable {

	private BufferedReader in;
	private String msg = null;
	private int idClient;
	private Socket socket;
	private PrintWriter out;
	protected volatile boolean running = true;
	
	public ReceptionChat(BufferedReader in, PrintWriter out, int idClient, Socket socket){	
		this.in = in;
		this.out = out;
		this.idClient = idClient;
		this.socket = socket;
	}
	
	public void run() {		
		while(running){
	        try {	        	
	        	msg = in.readLine();
	        	if(msg.equals("exit")){
	        		running = false;
	        		System.out.println("Le client "+idClient+" s'est deconnecte");
	        		in.close();
//	        		out.close();
//	        		socket.close();
	        	}
	        	else{
	        		System.out.println("Le client "+idClient+ " dit: " +msg);
	        	}
		    } catch (IOException e) {				
//				e.printStackTrace();
			}
		}
	}
}
