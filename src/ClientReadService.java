import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class ClientReadService extends Thread {
	
	public Socket socket;

    public ClientReadService(Socket s) {
    	this.socket = s;
	}
    
    public void close() {
		try {
			this.socket.close();
		}
		catch(IOException e) {
//			System.out.println("Une exception a ete leve lors de la fermeture ClientReadService:\n" + e);
		}
    }
    
    public static void sendMsg(String msg, PrintWriter... pws) {
    	for(PrintWriter pw : pws) {
    	    pw.println(msg);
    	    pw.flush();
    	}
    }
    
    public void run() {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
		    while(true) {
				String msg = br.readLine(); //msg recu par le serveur
				System.out.println(msg);
				String msgTab[] = msg.split(" ");
				if(msgTab[0].equals("host_chat")){
					br.close();
					pw.close();
					ServerSocket serverChat = new ServerSocket(Integer.valueOf(msgTab[2]));
			    	Socket socketChat = serverChat.accept();
			    	System.out.println("Chat en cours...");
			    	try(BufferedReader in = new BufferedReader(new InputStreamReader(socketChat.getInputStream()));
							PrintWriter out = new PrintWriter(new OutputStreamWriter(socketChat.getOutputStream()))) {
				    	Thread t1 = new Thread(new ReceptionChat(in, out, Integer.parseInt(msgTab[1]), socketChat));
						t1.start();
						Thread t2 = new Thread(new EmissionChat(out, in, Integer.parseInt(msgTab[1])));
						t2.start();					
						if(in.readLine().equals("exit")){
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							socketChat.close();
							serverChat.close();
						}
			    	} catch(IOException e) {} 			    	
				}
				if(msgTab[0].equals("go_chat")){
					//Integer.valueOf(msgTab[2])					
					Socket socketChat = new Socket(InetAddress.getLocalHost(), 1028);
					System.out.println("Chat en cours...");
					BufferedReader in = new BufferedReader(new InputStreamReader(socketChat.getInputStream()));
			    	PrintWriter out = new PrintWriter(socketChat.getOutputStream());				
					Thread t1 = new Thread(new ReceptionChat(in, out, Integer.parseInt(msgTab[1]), socketChat));
					t1.start();
					Thread t2 = new Thread(new EmissionChat(out, in, Integer.parseInt(msgTab[1])));
					t2.start();
					if(in.readLine().equals("exit")){
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						socketChat.close();
					}
				}
		    }
		} catch(IOException e) {
//		    System.out.println("Deconnexion d'un client\n" + e);
		}
    }
}
