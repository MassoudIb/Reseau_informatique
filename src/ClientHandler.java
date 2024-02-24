import java.io.DataOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import javax.imageio.ImageIO;
 


public class ClientHandler extends Thread { 
	private Socket socket;
	private int clientNumber;
	BaseDonneeUtilisateurs	baseDonnee;
	
	
	public ClientHandler(Socket socket, int clientNumber, BaseDonneeUtilisateurs baseDonnee) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		this.baseDonnee = baseDonnee;
		
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}

	public void run() { 
		
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			String utilisateur = in.readUTF();
			String motDePasse = in.readUTF();
			
			// Le serveur connecte l’utilisateur au service de traitement d’image
			// si l'usager est dans la base de donnée et qu'il s'est identifié.
			if(baseDonnee.identificationValide(utilisateur, motDePasse)) {
				
				out.writeBoolean(false);
				out.writeUTF("Hello from server - you are client#" + clientNumber +
						"\n" + "Connexion au service de traitement d'image");	
				// Obtention de la date et l'heure
				long millis = System.currentTimeMillis(); 
				java.util.Date date = new java.util.Date(millis);
				
				String serverAddress = in.readUTF();
				String port = in.readUTF();
				String nomImage = in.readUTF();
				
				System.out.print("[" + utilisateur + " - " + serverAddress + ":" 
				+ port + " - " + date + "] : Image " + nomImage + " reçue pour traitement.\n");
				
				// Traitement de l'image
			    int fileSize = in.readInt();
			    byte[] fileContent = new byte[fileSize];
			    in.readFully(fileContent);

				BufferedImage imageBuffered = Server.createImageFromBytes(fileContent);
				BufferedImage NB_image = Sobel.process(imageBuffered);
				String imageTraitee = in.readUTF();
				File f = new File(imageTraitee);
				
				ImageIO.write(NB_image, "PNG", f);
				out.writeBoolean(true);
				
				byte[] imageByte = Files.readAllBytes(f.toPath());
				out.writeInt(imageByte.length);
				out.write(imageByte);

				// Création d'un compte dans la base de donné dans le cas où
				// l'utilisateur n'existe pas.
			}else if(!baseDonnee.utilisateurExiste(utilisateur)) {
				
				out.writeBoolean(true);
				out.writeUTF("Erreur dans la saisie du mot de passe" + "\n"
							 + "Création automatique d'un compte pour vous." );
				baseDonnee.ajouterUtilisateur(utilisateur, motDePasse);
			}
		} 
		catch (IOException e) {
				System.out.println("Error handling client# " + clientNumber + ": " + e);
		}
		finally {
				try {
					socket.close();
				}
				catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");}
					System.out.println("Connection with client# " + clientNumber+ " closed");
				}
	}
}
