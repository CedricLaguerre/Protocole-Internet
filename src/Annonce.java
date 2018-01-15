public class Annonce {

	private int idClient;
	private String titre;
	private String description;
	
	public Annonce(int idClient,String titre, String description) {
		super();
		this.idClient = idClient;
		this.titre = titre;
		this.description = description;
	}
	
	public Annonce(int idClient,String titre) {
		super();
		this.idClient = idClient;
		this.titre = titre;
	}

	public int getIdClient() {
		return idClient;
	}

	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitre() {
		return titre;
	}
	
	public void setTitre(String titre){
		this.titre = titre;
	}
	
}
