import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class Server {
		private static ServerSocket Listener;  
		public static void main(String[] args) throws Exception {
		
		Scanner cin = new Scanner(System.in); 
		
		// Création de la base de donnée.
		BaseDonneeUtilisateurs	baseDonnee = new BaseDonneeUtilisateurs();
		
		int clientNumber = 0;
		String serverAddress;
		int serverPort;
		
		/**
		  Le serveur demande à l’utilisateur d’entrer l'information suivante : 
		  adresse IP du poste sur lequel s’exécute le serveur. Une vérification 
		  est faite pour s'assurer que l'addresse IP est cohérente.
		 */
		do {
			System.out.println("Saisissez l'adresse IP du poste sur lequel s'exécute le serveur");
			serverAddress = cin.nextLine();
			if(!IPestValide(serverAddress)) {
				System.out.println("L'adresse IP est incohérente.");
			}
				
		}while(!IPestValide(serverAddress));
		
		/**
		  Le serveur demande à l’utilisateur d’entrer l'information suivante : 
		  le port d’écoute sur lequel s’exécute le serveur. Une vérification 
		  est faite pour s'assurer que le port d'écoute est entre 5000 et 5050.
		 */
		do {
			System.out.println("Saisissez le port d'écoute entre 5000 et 5050.");
			serverPort = cin.nextInt();
			if(serverPort < 5000 || serverPort > 5050) {
				System.out.println("Le port d'écoute est incohérent.");
			}
		}while(serverPort < 5000 || serverPort > 5050);
		
		cin.close();
		
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
	
		Listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		try {
		
			while (true) {
				new ClientHandler(Listener.accept(), clientNumber++, baseDonnee).start();
			}
		}
		finally {
	
			Listener.close();
		} 
	} 
		
		/**
		 * La fonction qui vérifie si l'address IP est valide.
		 * @param ip
		 * @return
		 */
		public static boolean IPestValide(final String ip) {
		    String sequence = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

		    return ip.matches(sequence);
		}
		
		/**
		 * Création d'une image à partir d'un array de bytes.	
		 * @param imageData
		 * @return
		 */
		public static BufferedImage createImageFromBytes(byte[] imageData) {
		    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		    try {
		        return ImageIO.read(bais);
		    } catch (IOException e) {
		        throw new RuntimeException(e);
		    }
		}

}

	


