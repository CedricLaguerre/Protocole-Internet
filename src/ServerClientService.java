import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;

public class ServerClientService implements Runnable {
	
	public Socket socketClient;
    public int idClient;
    public static final int PORT_INVITATION[]= {1028, 1029, 1030, 1032};
    private static HashMap<Integer, Socket> mapConnectedClients = new HashMap<Integer, Socket>(); // cle:idClient valeur:socket
    public static HashMap<Integer, Integer> mapInvitations = new HashMap<Integer, Integer>(); //cle : inviteur, valeur : invite
    public static HashMap<Integer, Annonce> mapAnnonces = new HashMap<Integer, Annonce>(); // cle:idAnnonce, valeur:Annonce
	public int portIncr = 0;

    public ServerClientService(Socket s, int idClient) {
    	this.socketClient = s;
    	this.idClient = idClient;
    	System.out.println("cpt id_client="+idClient);
    	synchronized(ServerClientService.mapConnectedClients) {
    		mapConnectedClients.put(idClient, socketClient);
    	}
	}
    
    public static void sendMsg(String msg, PrintWriter... pws) {
    	for(PrintWriter pw : pws) {
    	    pw.println(msg);
    	    pw.flush();
    	}
    }
    
    public void deconnexionClient() throws IOException {
    	synchronized(ServerClientService.mapInvitations) {
    		mapConnectedClients.remove(idClient); //On supprime le client de la liste des clients connectes    	    
    	    mapInvitations.remove(idClient); //On supprime l'invitation du client
    	    //On supprime toutes les invitations a ce client
    	    if(mapInvitations.containsValue(idClient)) {
	    		Iterator it = mapInvitations.keySet().iterator();
	    		while(it.hasNext()) {
	    		    int clef = (int)it.next();
	    		    if(mapInvitations.get(clef) == idClient)
	    		    	mapInvitations.remove(clef);
	    		}
    	    }
    	    // On supprime aussi ses annonces
    	    for (int i = 1; i <= mapAnnonces.size(); i++) {
				if(mapAnnonces.get(i).getIdClient() == idClient){
					mapAnnonces.remove(i);
				}
			}
    	    
//		    try {
//	    		this.socketClient.close();
//		    } catch (SocketException e){
//		    }
		}
    }
    
    public void run() {
    	try(BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
    		    PrintWriter pw = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()))) {
    		sendMsg("connexion client: " + idClient, pw);
    		sendMsg("Les commandes sont : help, quit, list clients, add votre_annonce, list annonces"
					+ " connect id_client, accept id_client, refuse id_client", pw);
    		boolean isChat = false;
		    while(true) {
		    	while(isChat) {
		    		String msgChat = br.readLine();
		    		Scanner sc = new Scanner(System.in);
		    		String msgChat2 =  sc.nextLine();
		    		if(msgChat.equals("fin")){
		    			isChat = false;
		    		}
//	    			System.out.println("ca marche !!");
//		    		if(msgChat.equals("exit") || msgChat2.equals("exit")){
//		    			System.out.println("ca marche !!");
//		    			isChat = false;
//		    		}
		    	}
				String msg = br.readLine();
				if(msg == null) break;
				System.out.println("Message recu: " + msg);
				String[] msgTab = msg.split(" ");
				switch (msgTab.length){
				  case 1:
					  switch (msgTab[0]) {
						case "help":
							sendMsg("Les commandes sont : help, quit, list clients, add votre_annonce, list annonces,"
									+ " list mes_annonces, connect id_client,"
									+ " accept id_client, refuse id_client", pw);
							break;
						case "quit":
					    	System.out.println("Deconnexion du client: "+ idClient);
							deconnexionClient();
							break;
						default:
							sendMsg("ERROR: COMMANDE INCONNUE", pw);
							break;
					  }
					break;
				  case 2:
					  if(msgTab[0].equals("list") && msgTab[1].equals("clients")) {
					    	String envoi = "";
							for(int i : mapConnectedClients.keySet()){
							    envoi += i + " | ";					
							}
						    sendMsg(envoi, pw);
					  }
					  else if(msgTab[0].equals("list") && msgTab[1].equals("annonces")) {
						  String msg_annonce = "";
						  Set<Entry<Integer, Annonce>> setHm = mapAnnonces.entrySet();
					      Iterator<Entry<Integer, Annonce>> it = setHm.iterator();
					      while(it.hasNext()){
					         Entry<Integer, Annonce> e = it.next();
					         msg_annonce += "idAnnonce=" + e.getKey() + " titre="+ e.getValue().getTitre()+ " | ";
					      }
				    	sendMsg(msg_annonce, pw);
					  }
					  
					  else if(msgTab[0].equals("list") && msgTab[1].equals("mes_annonces")) {
						  String msg_annonce = "";
						  Set<Entry<Integer, Annonce>> setHm = mapAnnonces.entrySet();
					      Iterator<Entry<Integer, Annonce>> it = setHm.iterator();
					      while(it.hasNext()){
					         Entry<Integer, Annonce> e = it.next();
					    	  if(e.getValue().getIdClient() == idClient){
						         msg_annonce += "idAnnonce=" + e.getKey() + " titre="+ e.getValue().getTitre()+ " | ";
					    	  }
					      }
				    	sendMsg(msg_annonce, pw);
					  }
					  
					  else if(msgTab[0].equals("info")){
						  mapAnnonces.get(msgTab[1]);
						  sendMsg("idAnnonce="+msgTab[1]+" titre="+mapAnnonces.get(msgTab[1]).getTitre()
								  +" contenu="+mapAnnonces.get(msgTab[1]).getTitre(), pw);
					  }
					  
					  else if(msgTab[0].equals("list") && msgTab[1].equals("invitations")) {
						  String msg_annonce = mapInvitations.get(idClient)+ "|";
						  sendMsg(msg_annonce, pw);
					  }
						else if(msgTab[0].equals("connect")) {
							int idInviteAnnonce = -1;
							try {
								idInviteAnnonce = Integer.parseInt(msgTab[1]);
							} catch(NumberFormatException e) {
								System.out.println("Mauvais argument, entier attendu :\n" + e);
							}
							Annonce annonceClient = mapAnnonces.get(idInviteAnnonce);
							if(annonceClient.getIdClient() != idClient && mapConnectedClients.containsKey(annonceClient.getIdClient())) {
								mapInvitations.put(idClient, annonceClient.getIdClient() ); // ajoute une invitation
							    Socket socketInvite = mapConnectedClients.get(annonceClient.getIdClient() );
							    PrintWriter pwInvite = new PrintWriter(new OutputStreamWriter(socketInvite.getOutputStream()));
							    sendMsg("Invitation envoye par le client " + this.idClient, pwInvite);
							    sendMsg("En attente de la reponse du client " + annonceClient.getIdClient() + "...", pw);
							    isChat = true;
							}
							else {
							    String envoi = (annonceClient.getIdClient() == idClient) ? "ERROR: VOUS NE POUVEZ PAS VOUS INVITER" : 
							    			"ERROR: LE CLIENT " + annonceClient.getIdClient()  + " N'EXISTE PAS";
							    sendMsg(envoi, pw);
							}
					    }
						else if(msgTab[0].equals("add")) {
							synchronized (ServerClientService.mapAnnonces) {
						    	Annonce annonce = new Annonce(this.idClient, msgTab[1]);
						    	mapAnnonces.put(mapAnnonces.size()+1, annonce);
//						    	idAnnonce++;
							}
					    	sendMsg("Annonce ajoute", pw);
//					    	sendMsg("Attention, ajouter un contenu (contenu id_annonce votre_contenu", pw);
					    }
							
						else if(msgTab[0].equals("supp")){
							int idAnnonceSup = -1;
							try {
							    idAnnonceSup  = Integer.parseInt(msgTab[1]);
							} catch(NumberFormatException e) {
								System.out.println("Mauvais argument, entier attendu :\n" + e);
							}
							Annonce myAnnonce = mapAnnonces.get(idAnnonceSup);
							if(myAnnonce.getIdClient() == idClient){
								synchronized (ServerClientService.mapAnnonces) {
									ServerClientService.mapAnnonces.remove(idAnnonceSup);
								}
								sendMsg("Annonce supprime", pw);
							} else {
								sendMsg("Impossible: l'annonce "+myAnnonce.getIdClient()+"ne vous appartient", pw);
							}
						}
						
						else if(msgTab[0].equals("accept") || msgTab[0].equals("refuse")) {
							boolean acceptConnexion = (msgTab[0].equals("accept")) ? true : false;
							int idClientInvite = -1;
							try {
							    idClientInvite = Integer.parseInt(msgTab[1]);
							} catch(NumberFormatException e) {
								System.out.println("Mauvais argument, entier attendu :\n" + e);
							}
							if(mapInvitations.containsKey(idClientInvite)) {
							    Socket socketInvite = mapConnectedClients.get(idClientInvite);
							    PrintWriter pwInvite = new PrintWriter(new OutputStreamWriter(socketInvite.getOutputStream()));
							    String envoi = (acceptConnexion)? "Invitation accepte par le client " + idClient : "Invitation refusee par le client" + idClient;
							    sendMsg(envoi, pwInvite);
							    if(acceptConnexion){							    	
							    	InetAddress adressInvite = mapConnectedClients.get(idClientInvite).getInetAddress();
					    			InetAddress adressInvitant = mapConnectedClients.get(idClient).getInetAddress();
							    	sendMsg("host_chat "+idClient+" "+PORT_INVITATION[portIncr], pwInvite);
							    	sendMsg("go_chat "+idClientInvite+" "+PORT_INVITATION[portIncr], pw);
							    	portIncr++;
									isChat = true; //go_chat
							    }
								mapInvitations.remove(idClientInvite); // On supprime l'invitation
							}						
							else {
								sendMsg("ERROR: LE CLIENT " + idClientInvite + " NE VOUS A JAMAIS INVITE", pw);
							}											
						}
				    break;
				  case 3:
					  if(msgTab[0].equals("contenu")){
						  String chaine = "";
						  for (int i = 2; i < msgTab.length; i++) {
							chaine += msgTab[i];
						  }
						  mapAnnonces.get(msgTab[1]).setDescription(chaine);
					  }
				    break;
				  default:
					  sendMsg("ERROR: COMMANDE INCONNUE", pw);
				}								
		    }
	    } catch(IOException e) {
//		    System.out.println("Une exception a ete leve lors de l'execution d'un ClientReadService:\n" + e);
		}
    }
}
