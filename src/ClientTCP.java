import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientTCP {
	
    private static ServerClientService servR;

    public static void main(String[] args) throws InterruptedException {   	
		Scanner sc = new Scanner(System.in);
//		InetAddress.getLocalHost();
		try(Socket socket = new Socket("192.168.43.72", ServerTCP.PORT)) {
			ClientReadService servR = new ClientReadService(socket);
		    Thread t = new Thread(servR);
		    t.start();
	
		    try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
				while(true) {
				    String input = sc.nextLine();
				    String inputToSend = "";
				    for(int i = 0; i < input.length(); i++)
					if(input.charAt(i) != '\0')
					    inputToSend += input.charAt(i);
				    pw.print(inputToSend + "\n");
				    pw.flush();
				    if(input.equals("quit")){
				    	Thread.sleep(500);
				    	break;
				    }
//				    String msgTab[] = input.split(" ");
				}
				pw.close();
				servR.interrupt();
				socket.close();
		    } catch (SocketException e) {
			}
		    }catch(IOException e) {
				System.out.println("Erreur lors de l'execution d'un ClientTCP\n :" + e);
		    }
//		}catch(UnknownHostException uhe) {
//		    System.out.println("L'hote n'a pas ete reconnu:\n" + uhe);
//		}catch(IOException ioe) {
//		    System.out.println("Une erreur de flux a ete detecte:\n" + ioe);
//		}
    }
}
