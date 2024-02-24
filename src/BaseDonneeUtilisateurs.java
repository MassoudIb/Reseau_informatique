import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BaseDonneeUtilisateurs {
	private Map<String, String> identifiants;
	private File baseDonneeFichier;
	
	/**
	 * Création d'une base de donnée en la populant avec les 3 membres de l'équipe.
	 */
	public  BaseDonneeUtilisateurs() {
		identifiants = new HashMap<>();
		baseDonneeFichier = new File("BaseDeDonnées.txt");
		ajouterUtilisateur("Massoud", "1234");
		ajouterUtilisateur("Axelle",  "1235");
		ajouterUtilisateur("Loris",   "1236");
	}
	
	/** 
	 * Fonction qui permet d'ajouter un utilisateur à la base de donnée.
	 * @param nom
	 * @param motDePasse
	 */
	public void ajouterUtilisateur(String nom, String motDePasse) {
		identifiants.put(nom, motDePasse);
		sauvegarder();
	}
	
	/**
	 * Fonction qui permet d'identifier si un utilisateur correspond avec son mot de passe.
	 * @param utilisateur
	 * @param motDePasse
	 * @return vrai si l'utilisateur correspond à son mot de passe.
	 */
	public boolean identificationValide(String utilisateur, String motDePasse) {
		String passe_BD = identifiants.get(utilisateur);
		return passe_BD!= null && passe_BD.equals(motDePasse);
	}
	
	/**
	 * Fonction qui permet de vérifier si un utilisateur existe dans la base de donnée. 
	 * @param nom
	 * @return vrai si l'utilisateur existe.
	 */
	public boolean utilisateurExiste(String nom) {
		return identifiants.containsKey(nom);
	}
	
	/**
	 * Fonction qui permet de sauvegarder l'utilisateur dans le fichier 
	 * texte BaseDeDonnées.txt .
	 */
	private void sauvegarder() {
		try(BufferedWriter ecriture = new BufferedWriter(new FileWriter(baseDonneeFichier))){
			for(Map.Entry<String, String> entry : identifiants.entrySet()) {
				ecriture.write(entry.getKey() + " : " + entry.getValue());
				ecriture.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
