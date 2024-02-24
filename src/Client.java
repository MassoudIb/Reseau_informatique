import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Client {
	private static Socket socket;
	public static String serverAddress;
	public static int port;
	public static void main(String[] args) throws Exception {
		
		Scanner cin = new Scanner(System.in);
	
		/**
		  Le client demande à l’utilisateur d’entrer l'information suivante : 
		  adresse IP du poste sur lequel s’exécute le serveur. Une vérification 
		  est faite pour s'assurer que l'addresse IP est cohérente.
		 */
		do {
			System.out.println("Saisissez l'adresse IP du serveur");
			serverAddress = cin.nextLine();
			if(!Server.IPestValide(serverAddress)) {
				System.out.println("L'adresse IP est incohérente.");
			}
				
		}while(!Server.IPestValide(serverAddress));
		
		
		/**
		  Le client demande à l’utilisateur d’entrer l'information suivante : 
		  le port d’écoute sur lequel s’exécute le serveur. Une vérification 
		  est faite pour s'assurer que le port d'écoute est entre 5000 et 5050.
		 */
		do {
			System.out.println("Saisissez le port du serveur entre 5000 et 5050.");
			port = cin.nextInt();
			cin.nextLine();
			if(port < 5000 || port > 5050) {
				System.out.println("Le port d'écoute est incohérent.");
			}
		}while(port < 5000 || port > 5050);
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("Serveur lancé sur [%s:%d]%n", serverAddress, port);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		
		// À partir du client, l'utilisateur saisit le nom d'utilisateur.
		System.out.print("Saisissez le nom d'utilisateur: ");
		String utilisateur;
		utilisateur = cin.nextLine();
		out.writeUTF(utilisateur);
		
		// À partir du client, l'utilisateur saisit le mot de passe.
		System.out.print("Saisissez le mot de passe: ");
		String motDePasse;
		motDePasse = cin.nextLine();
		out.writeUTF(motDePasse);
		
		//Confirmation reçue du serveur si le client est dans la base de donnée ou non.
		Boolean clientNotFound = in.readBoolean();
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		
		// Dans le cas où le client est trouvé dans la base de donnée,
		// il/elle aura accès au traitement d'image. 
		if(clientNotFound) {
			socket.close();
		}
		else {
			out.writeUTF(serverAddress);
			out.writeUTF(""+port);
			System.out.print("Saisissez le nom de l'image à traitée: ");
			String nomImage = cin.nextLine();
			File fi = new File(nomImage);
			byte[] fileContent = Files.readAllBytes(fi.toPath());
			out.writeUTF(nomImage);
			//Envoi au serveur de la taille du fichier.
			out.writeInt(fileContent.length);
			out.write(fileContent);
			
			System.out.print("Saisissez le nom que vous voulez attribuer à l'image traitée: ");
			String imageTraitee = cin.nextLine();
			System.out.print("L’image a été envoyé au serveur. \n");
			
			out.writeUTF(imageTraitee);
			
			//Confirmation si l'image a été reçue par le serveur.
			Boolean imageRecue = in.readBoolean();
			if(imageRecue)
				System.out.print("L'image traitée a été reçue par le serveur. \n");
			
			
			//Réception de l'image par le serveur.
			int newImageSize = in.readInt();
			byte[] newfileContent = new byte[newImageSize];
		    in.readFully(newfileContent);
		    BufferedImage newimageBuffered = Server.createImageFromBytes(newfileContent);
		    
		    File imageFinal = new File(imageTraitee);
		    ImageIO.write(newimageBuffered, "PNG", imageFinal);
		    
		    String cheminImageFinal = imageFinal.getAbsolutePath();
		    System.out.print("Voici le chemin de l'image reçue: \n" + cheminImageFinal);
		    
			socket.close();
		}
		
		cin.close();
	}	
}


