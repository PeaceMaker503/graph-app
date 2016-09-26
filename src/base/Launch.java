/*
MIT License

Copyright (c) 2016 Halim Chellal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package base ;

/*
 * Ce programme propose de lancer divers algorithmes sur les graphes
 * a partir d'un menu texte, ou a partir de la ligne de commande (ou des deux).
 *
 * A chaque question posee par le programme (par exemple, le nom de la carte), 
 * la reponse est d'abord cherchee sur la ligne de commande.
 *
 * Pour executer en ligne de commande, ecrire les donnees dans l'ordre. Par exemple
 *   "java base.Launch insa 1 1 /tmp/sortie 0"
 * ce qui signifie : charge la carte "insa", calcule les composantes connexes avec une sortie graphique,
 * ecrit le resultat dans le fichier '/tmp/sortie', puis quitte le programme.
 */

import core.* ;
import java.io.* ;

public class Launch {

    private final Readarg readarg ;

    public Launch(String[] args) {
	this.readarg = new Readarg(args) ;
    }

    public void afficherMenu () {
	System.out.println () ;
	System.out.println ("MENU") ;
	System.out.println () ;
	System.out.println ("0 - Quitter") ;
	System.out.println ("1 - Composantes Connexes") ;
	System.out.println ("2 - Plus court chemin standard") ;
	System.out.println ("3 - Plus court chemin A-star") ;
	System.out.println ("4 - Cliquer sur la carte pour obtenir un numero de sommet.") ;
	System.out.println ("5 - Charger un fichier de chemin (.path) et le verifier.") ;
	System.out.println ("6 - Algorithme de covoiturage") ;
	System.out.println () ;
    }

    public static void main(String[] args) {
	Launch launch = new Launch(args) ;
	launch.go () ;
    }

    public void go() {

	try {

		  System.out.println ("**") ;
		    System.out.println ("** Programme de test des algorithmes de graphe.");
		    System.out.println ("**") ;
		    System.out.println () ;
		    
		    // On obtient ici le nom de la carte a utiliser.
		    String nomcarte = this.readarg.lireString ("Nom du fichier .map a utiliser ? ") ;
		    DataInputStream mapdata = Openfile.open (nomcarte) ;
		    
		    boolean display = (1 == this.readarg.lireInt ("Voulez-vous une sortie graphique (0 = non, 1 = oui) ? ")) ;	    
		    Dessin dessin = (display) ? new DessinVisible(800,600, nomcarte) : new DessinInvisible() ;
		    
		    Graphe graphe = new Graphe(nomcarte, mapdata, dessin) ;
		    int choix ;
	    // Boucle principale : le menu est accessible 
	    // jusqu'a ce que l'on quitte.
	    boolean continuer = true ;

	    while (continuer) {
	    	
		  
		this.afficherMenu () ;
		choix = this.readarg.lireInt ("Votre choix ? ") ;
		
		// Algorithme a executer
		Algo algo = null ;
		int mode = 0;
		
		// Le choix correspond au numero du menu.
		switch (choix) {
		case 0 : continuer = false ; break ;

		case 1 : algo = new Connexite(graphe, this.fichierSortie (), this.readarg) ; break ;
		
		case 2 : System.out.println("");
				 System.out.println("MODE");
				 System.out.println("");
				 System.out.println("0 - Temps (min)");
				 System.out.println("1 - Distance (m)");
				 System.out.println("");
				 mode = this.readarg.lireInt ("Coût en ? ") ; 
			 	 algo = new Pcc(graphe, this.fichierSortie (), this.readarg, mode) ; 
			 	 break ;
		
		case 3 : System.out.println("");
				 System.out.println("MODE");
		 		 System.out.println("");
		 		 System.out.println("0 - Coût en temps (min)");
		 		 System.out.println("1 - Coût en distance (m)");
		 		 System.out.println("");
		 		 mode = this.readarg.lireInt ("Coût en ? ") ;
		 		 algo = new PccStar(graphe, this.fichierSortie (), this.readarg, mode) ; 
		 		 break ;
	
		case 4 : graphe.situerClick() ; break ;

		case 5 :
		    String nom_chemin = this.readarg.lireString ("Nom du fichier .path contenant le chemin ? ") ;
		    graphe.verifierChemin(Openfile.open (nom_chemin), nom_chemin) ;
		    graphe.creerChemin(Openfile.open(nom_chemin), nom_chemin);
		    break ;
		    
		case 6 : algo = new Covoiturage(graphe, this.fichierSortie (), this.readarg) ; 
		 		 break ;

		default:
		    System.out.println ("Choix de menu incorrect : " + choix) ;
		    System.exit(1) ;
		}
		
		if (algo != null) { algo.run() ; }
	    }
	    
	    System.out.println ("Programme terminé.") ;
	    System.exit(0) ;
	    
	    
	} catch (Throwable t) {
	    t.printStackTrace() ;
	    System.exit(1) ;
	}
    }

    // Ouvre un fichier de sortie pour ecrire les reponses
    public PrintStream fichierSortie () {
	PrintStream result = System.out ;

	String nom = this.readarg.lireString ("Nom du fichier de sortie ? ") ;

	if ("".equals(nom)) { nom = "/dev/null" ; }

	try { result = new PrintStream(nom) ; }
	catch (Exception e) {
	    System.err.println ("Erreur a l'ouverture du fichier " + nom) ;
	    System.exit(1) ;
	}

	return result ;
    }

}
