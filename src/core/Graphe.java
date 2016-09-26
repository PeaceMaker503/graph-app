package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.awt.Color;
import java.io.* ;

import base.* ;

import java.util.* ;
public class Graphe {

    // Nom de la carte utilisee pour construire ce graphe
    public final String nomCarte ;
    private int nb_nodes;
    // Fenetre graphique
    private final Dessin dessin ;
    private int vitMax;
    // Version du format MAP utilise'.
    private static final int version_map = 4 ;
    private static final int magic_number_map = 0xbacaff ;

    // Version du format PATH.
    private static final int version_path = 1 ;
    private static final int magic_number_path = 0xdecafe ;

    // Identifiant de la carte
    private int idcarte ;
    
    private float testvoisin;
    
    // Numero de zone de la carte
    private int numzone ;

    /*
     * Ces attributs constituent une structure ad-hoc pour stocker les informations du graphe.
     * Vous devez modifier et ameliorer ce choix de conception simpliste.
     */
    private Sommet[] sommets;
    private Descripteur[] descripteurs ;

    
    // Deux malheureux getters.
    public Dessin getDessin() { return dessin ; }
    public int getZone() { return numzone ; }

    // Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
    public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {
    this.testvoisin = 0f;
	this.nomCarte = nomCarte ;
	this.dessin = dessin ;
	Utils.calibrer(this.nomCarte, dessin) ;
	
	// Lecture du fichier MAP. 
	// Voir le fichier "FORMAT" pour le detail du format binaire.
	try {

	    // Nombre d'aretes
	    int edges = 0 ;

	    // Verification du magi)c number et de la version du format du fichier .map
	    int magic = dis.readInt () ;
	    int version = dis.readInt () ;
	    Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

	    // Lecture de l'identifiant de carte et du numero de zone, 
	    this.idcarte = dis.readInt () ;
	    this.numzone = dis.readInt () ;

	    // Lecture du nombre de descripteurs, nombre de noeuds.
	    int nb_descripteurs = dis.readInt () ;
	    nb_nodes = dis.readInt () ;
	    System.out.println("NB NODES : " + nb_nodes);
	    // Nombre de successeurs enregistrÃ©s dans le fichier.
	    int[] nsuccesseurs_a_lire = new int[nb_nodes];
	    
	    // En fonction de vos choix de conception, vous devrez certainement adapter la suite.
	    this.sommets = new Sommet[nb_nodes];
	    this.descripteurs = new Descripteur[nb_descripteurs] ;

	    // Lecture des noeuds
	    for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
		// Lecture du noeud numero num_node
	    this.sommets[num_node] = new Sommet( ((float)dis.readInt ()) / 1E6f, ((float)dis.readInt ()) / 1E6f, num_node );	
		
		nsuccesseurs_a_lire[num_node] = dis.readUnsignedByte() ;
		//this.testvoisin += (float)nsuccesseurs_a_lire[num_node];
	    }
	    
	    Utils.checkByte(255, dis) ;
	    
	    // Lecture des descripteurs
	    for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
		// Lecture du descripteur numero num_descr
		descripteurs[num_descr] = new Descripteur(dis) ;

		// On affiche quelques descripteurs parmi tous.
		if (0 == num_descr % (1 + nb_descripteurs / 400))
		    System.out.println("Descripteur " + num_descr + " = " + descripteurs[num_descr]) ;
	    }
	    
	    Utils.checkByte(254, dis) ;
	    
	    // Lecture des successeurs
	    for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
		// Lecture de tous les successeurs du noeud num_node
		for (int num_succ = 0 ; num_succ < nsuccesseurs_a_lire[num_node] ; num_succ++) {
		    // zone du successeur
		    int succ_zone = dis.readUnsignedByte() ;

		    // numero de noeud du successeur
		    int dest_node = Utils.read24bits(dis) ;

		    // descripteur de l'arete
		    int descr_num = Utils.read24bits(dis) ;

		    // longueur de l'arete en metres
		    int longueur  = dis.readUnsignedShort() ;

		    // Nombre de segments constituant l'arete
		    int nb_segm   = dis.readUnsignedShort() ;

		    edges++ ;
		    
		    Couleur.set(dessin, descripteurs[descr_num].getType()) ;

		    float current_long = this.sommets[num_node].getLongitude();
		    float current_lat  = this.sommets[num_node].getLatitude() ;

		    
		    ArrayList<Segment> newSegs = new ArrayList<Segment>();
		    
		    if(descripteurs[descr_num].vitesseMax()>this.vitMax)
		    {
		    	this.vitMax = descripteurs[descr_num].vitesseMax();
		    }
		    
		    // Chaque segment est dessine'
		  /*  for (int i = 0 ; i < nb_segm ; i++) {
		    	
		    Segment newSeg = new Segment((dis.readShort()) / 2.0E5f, (dis.readShort()) / 2.0E5f);
		    newSegs.add(newSeg);
			current_long += newSeg.getDeltaLong() ;
			current_lat  += newSeg.getDeltaLat() ;
		    }*/
		    
		    // Le dernier trait rejoint le sommet destination.
		    // On le dessine si le noeud destination est dans la zone du graphe courant.
		   // if (succ_zone == numzone) {
		   // Segment newSeg = new Segment(-current_long + this.sommets[dest_node].getLongitude(), -current_lat + this.sommets[dest_node].getLatitude());
		   // newSegs.add(newSeg);
		    //}
		    
		    Arc newArc = new Arc(this.sommets[num_node], this.sommets[dest_node], descripteurs[descr_num], longueur, newSegs);
		    this.sommets[num_node].addArcSortant(newArc);
		    
		    if(!descripteurs[descr_num].isSensUnique() && succ_zone == numzone)//sens pas unique
		    {
		    	Arc arcNew = new Arc(this.sommets[dest_node], this.sommets[num_node], descripteurs[descr_num], longueur, newSegs);
		    	this.sommets[dest_node].addArcSortant(arcNew);
		    }
		    
		    //	newArc.drawArc(this.getDessin());

		    	
		    	//this.sommets[dest_node].addArcEntrant(newArc);

		    	//System.out.println("num_node:" + dest_node);
		    	
		    	//newArc.drawArc(this.getDessin());
		    	
		    	/*for (int i = 0 ; i < nb_segm ; i++) 
		    	{
				    Segment newSeg = newSegs.get(i);
				    newSegs2.add(new Segment(-newSeg.getDeltaLong(), -newSeg.getDeltaLat()));
				    current_long -= newSeg.getDeltaLong() ;
					current_lat  -= newSeg.getDeltaLat() ;
				}
		    
		    	if (succ_zone == numzone) 
		    	{
				    Segment newSeg = new Segment(current_long - this.sommets[dest_node].getLongitude(), current_lat -this.sommets[dest_node].getLatitude());
				    newSegs2.add(newSeg);
			    }*/

		    
		 // Chaque segment se dessine
			for (int i = 0 ; i < nb_segm ; i++) 
			{
				float delta_lon = (dis.readShort()) / 2.0E5f ;
				float delta_lat = (dis.readShort()) / 2.0E5f ;
				dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
				current_long += delta_lon ;
				current_lat  += delta_lat ;
			}

			// Le dernier trait rejoint le sommet destination.
			// On le dessine si le noeud destination est dans la zone du graphe courant.
			if (succ_zone == numzone) 
			{
				dessin.drawLine(current_long, current_lat, this.sommets[dest_node].getLongitude(), this.sommets[dest_node].getLatitude()) ;
			}
			
		}
	    }
	    
	    for (int num_node = 0 ; num_node < nb_nodes ; num_node++) 
	    {
			for (Arc a : sommets[num_node].getArcSortant()) 
			{
				Arc a2= new Arc(this.sommets[a.getFin().getIndex()], this.sommets[num_node], a.getDescripteur(), a.getLongueur(), new ArrayList<Segment>());
				this.sommets[a.getFin().getIndex()].addArcEntrant(a2);
			}
		}
	    
	    Utils.checkByte(253, dis) ;
	    
	    for (int num_node = 0 ; num_node < nb_nodes ; num_node++) 
	    {
	    	this.testvoisin += (float)this.sommets[num_node].getArcSortant().size();
	    }
	    
	    this.testvoisin = (float)(this.testvoisin/nb_nodes);
	    
	    System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
			       + nb_descripteurs + " descripteurs" + ", Mnbvoisin : " + this.testvoisin) ;

	    
	    
	} catch (IOException e) {
	    e.printStackTrace() ;
	    System.exit(1) ;
	}

    }

    // Rayon de la terre en metres
    private static final double rayon_terre = 6378137.0 ;

    /**
     *  Calcule de la distance orthodromique - plus court chemin entre deux points à la surface d'une sphère
     *  @param long1 longitude du premier point.
     *  @param lat1 latitude du premier point.
     *  @param long2 longitude du second point.
     *  @param lat2 latitude du second point.
     *  @return la distance entre les deux points en metres.
     *  Methode Ã©crite par Thomas Thiebaud, mai 2013
     */
    public static double distance(double long1, double lat1, double long2, double lat2) {
        double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
        double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
        double cosLong = Math.cos(Math.toRadians(long2-long1));
        return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
    }

    /**
     *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
     *  A n'utiliser que pour faire du debug ou des tests ponctuels.
     *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
     */
    public void situerClick() {

	System.out.println("Allez-y, cliquez donc.") ;
	
	if (dessin.waitClick()) {
	    float lon = dessin.getClickLon() ;
	    float lat = dessin.getClickLat() ;
	    
	    System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

	    // On cherche le noeud le plus proche. O(n)
	    float minDist = Float.MAX_VALUE ;
	    int   noeud   = 0 ;
	    
	    for (int num_node = 0 ; num_node < sommets.length ; num_node++) {
		float londiff = (this.sommets[num_node].getLongitude() - lon) ;
		float latdiff = (this.sommets[num_node].getLatitude() - lat) ;
		float dist = londiff*londiff + latdiff*latdiff ;
		if (dist < minDist) {
		    noeud = num_node ;
		    minDist = dist ;
		}
	    }

	    System.out.println("Noeud le plus proche : " + noeud) ;
	    System.out.println() ;
	    dessin.setColor(java.awt.Color.red) ;
	  //  dessin.drawPoint(this.sommets[noeud].getLongitude(), this.sommets[noeud].getLatitude(), 5) ;
	}
    }

    /**
     *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
     *  Verifie que le chemin est empruntable et calcule le temps de trajet.
     */
    public void verifierChemin(DataInputStream dis, String nom_chemin) {
	
	try {
	    
	    // Verification du magic number et de la version du format du fichier .path
	    int magic = dis.readInt () ;
	    int version = dis.readInt () ;
	    Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

	    // Lecture de l'identifiant de carte
	    int path_carte = dis.readInt () ;

	    if (path_carte != this.idcarte) {
		System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
		System.exit(1) ;
	    }

	    int nb_noeuds = dis.readInt () ;

	    // Origine du chemin
	    int first_zone = dis.readUnsignedByte() ;
	    int first_node = Utils.read24bits(dis) ;
	    
	    
	    // Destination du chemin
	    int last_zone  = dis.readUnsignedByte() ;
	    int last_node = Utils.read24bits(dis) ;

	    System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

	    int current_zone = 0 ;
	    int current_node = 0 ;

	    // Tous les noeuds du chemin
	    for (int i = 0 ; i < nb_noeuds ; i++) {
		current_zone = dis.readUnsignedByte() ;
		current_node = Utils.read24bits(dis) ;
		System.out.println(" --> " + current_zone + ":" + current_node) ;
	    }

	    if ((current_zone != last_zone) || (current_node != last_node)) {
		    System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
		    System.exit(1) ;
		}

	} catch (IOException e) {
	    e.printStackTrace() ;
	    System.exit(1) ;
	}
		
		
    }
    
    public Sommet getSommet(int nbsommet)
    {
    	return this.sommets[nbsommet];
    }
    
    public Chemin creerChemin(DataInputStream dis, String nom_chemin) {
    	
    	
    	Chemin chem = new Chemin();
	    int longueur_m= 0;
	    float cout_m=0;
	    
    	try {
    	    
    	    // Verification du magic number et de la version du format du fichier .path
    	    int magic = dis.readInt () ;
    	    int version = dis.readInt () ;
    
    	    int nodeBefore = 0;
    	    
    	    Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

    	    // Lecture de l'identifiant de carte
    	    int path_carte = dis.readInt () ;

    	    if (path_carte != this.idcarte) {
    		System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
    		System.exit(1) ;
    	    }

    	    int nb_noeuds = dis.readInt () ;

    	    // Origine du chemin
    	    int first_zone = dis.readUnsignedByte() ;
    	    int first_node = Utils.read24bits(dis) ;
    	    
    	    nodeBefore = first_node;
    	    chem.addSommet(this.sommets[nodeBefore]);
    	    // Destination du chemin
    	    int last_zone  = dis.readUnsignedByte() ;
    	    int last_node = Utils.read24bits(dis) ;

    	    System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

    	    int current_zone = 0 ;
    	    int current_node = 0 ;
    	    current_zone = dis.readUnsignedByte() ;
    		current_node = Utils.read24bits(dis) ;//on lit deja le premier sommet car on l'a deja lu
    		
    	    // Tous les noeuds du chemin
    	    for (int i = 1 ; i < nb_noeuds ; i++) {
    		current_zone = dis.readUnsignedByte() ;
    		current_node = Utils.read24bits(dis) ;
    		
    		chem.addSommet(this.sommets[current_node]);
    		int longueur = this.sommets[nodeBefore].findArcsSortant(current_node).getLongueur();
    		int vit = this.sommets[nodeBefore].findArcsSortant(current_node).getDescripteur().vitesseMax();
    		longueur_m += longueur;
    		float longueurf = ((float)longueur)/1000f;
    		float vitf = ((float)vit)/60f;
    		float coutf = longueurf/vitf;
    		cout_m += coutf;
    		nodeBefore = current_node;
    	    }

    	    if ((current_zone != last_zone) || (current_node != last_node)) {
    		    System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
    		    System.exit(1) ;
    		}

    	} 
    	  catch (IOException e) 
    	{
    	    e.printStackTrace() ;
    	    System.exit(1) ;
    	}
    	  finally 
    	  {
    		  chem.setCout(cout_m);
    		  chem.setLongueur(longueur_m);
    		  chem.print();
    		  chem.draw(this.getDessin(), Color.GREEN);
    	  }
		  return chem;
        }
    
   
    public ArrayList<Arc> getSuccesseurs(int num_sommet)
    {
    	return this.sommets[num_sommet].getArcSortant();
    }
    
    public ArrayList<Arc> getPredecesseurs(int num_sommet)
    {
    	return this.sommets[num_sommet].getArcEntrant();
    }
    
    public ArrayList<Arc> getSuccesseursPieton(int num_sommet)
    {
    	ArrayList<Arc> sol = new ArrayList<Arc>();
    	for(Arc a : this.sommets[num_sommet].getArcSortant())
    	{
    		if(a.getDescripteur().vitesseMax()<110)
    		{
    			sol.add(a);
    		}
    	}
    	return sol;
    }
    
    public double distanceSomm(int origine, int destination)
    {
    	return Graphe.distance(this.sommets[origine].getLongitude(), this.sommets[origine].getLatitude(), this.sommets[destination].getLongitude(), this.sommets[origine].getLatitude());
    }
    
    public int getNbNodes()
    {
    	return this.nb_nodes;
    }
    
    public int getVitMax()
    {
    	return this.vitMax;
    }
    
}