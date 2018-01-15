
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.util.Scanner;


public class EmissionChat implements Runnable {

	private PrintWriter out;
	private BufferedReader in;
	private String msg = null;
	private Scanner sc = null;
	private int idClient;
	protected volatile boolean running = true;
	
	public EmissionChat(PrintWriter out, BufferedReader in, int idCLient) {
		this.out = out;
		this.in = in;
		this.idClient = idCLient;
	}

	public void run() {		
	  while(running){
	    sc = new Scanner(System.in);
	    System.out.println("Votre message:");
	    msg = sc.nextLine();
		out.println(msg);
	    out.flush();
	    if(msg.equals("exit")){
//	    	try {
				running = false;
				System.out.println("Deconnexion...");
	    		out.close();	    	
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    }
	  }
	}
}
