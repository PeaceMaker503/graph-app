package core ;

import java.io.* ;

import base.Readarg ;

import java.util.* ;
import java.awt.Color;

public class Pcc extends Algo {

    // Numero des sommets origine et destination
    protected int zoneOrigine ;
    protected int origine ;
    protected int zoneDestination ;
    protected int destination ;
    protected BinaryHeap<Label> tas;
	protected Graphe gr;
	protected HashMap<Integer, Label> hashmap;
    protected int maxSize;
    protected int currentSize;
    protected int nbexplorednodes;
    protected long time;
    protected boolean typePerson; //true=auto, false=pieton
    protected Chemin solution;
    protected double vit;
    protected double contrainte;
    protected int nbmarques;
    
    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, int mode) 
    {
		super(gr, sortie, readarg) ;
		this.gr = gr;
		this.tas = new BinaryHeap<Label>();
		this.zoneOrigine = gr.getZone () ;
		this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;
		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;
		this.destination = readarg.lireInt ("Numero du sommet destination ? ");
		this.hashmap = new HashMap<Integer, Label>();
		this.maxSize = 0;
		this.currentSize = 0;
		this.nbexplorednodes = 0;
		this.time=0;
		Label.mode=mode;
		this.typePerson=true;
		this.solution = new Chemin();
		this.vit=0;
		this.nbmarques=0;
    }
    
    public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, int origine, double vit, double contrainte) 
    {
		super(gr, sortie, readarg) ;
		System.gc();
		this.gr = gr;
		this.tas = new BinaryHeap<Label>();
		this.zoneOrigine = gr.getZone () ;
		this.origine = origine;
		// Demander la zone et le sommet destination.
		this.zoneOrigine = gr.getZone () ;
		this.destination = -1;
		this.hashmap = new HashMap<Integer, Label>();
		this.maxSize = 0;
		this.currentSize = 0;
		this.nbexplorednodes = 0;
		this.time=0;
		Label.mode=0;
		this.typePerson = false;
		this.solution = new Chemin();
		this.vit=vit;
		this.nbmarques=0;
		if(this.vit==0)
		{
			this.typePerson=true;
		}
		else
		{
			this.typePerson=false;
		}
		this.contrainte = contrainte;
    }
    
    public void run() {

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination);
		gr.getDessin().setColor(Color.BLUE);
		
		System.gc();
		long time = System.currentTimeMillis();
		
		Label destinationLabel= new Label(true, 0, 0, -1, destination);
		Label origineLabel = new Label(true, 0, 0, -1, origine); //sommet origine premier, -1 car pas de pere
		tas.insert(origineLabel);
		hashmap.put(origine, origineLabel);
		Chemin solution = new Chemin();
		while(!(tas.isEmpty()))
		{	
			Label currentLabel = tas.deleteMin();
			int currentNode = currentLabel.getCourant();
			gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 5);
			if(currentNode==destination)
			{
				destinationLabel = currentLabel;
				break;
			}
			currentLabel.setMarked();
			this.nbmarques++;
			hashmap.put(currentNode, currentLabel);

			int currentLong = currentLabel.getLongueur();
			double currentCost = currentLabel.getCout();
			this.currentSize--;
			ArrayList<Arc> succArc = gr.getSuccesseurs(currentNode);
			
		    int succNode=0;
		    Label succLabel = null;
		    
			for(Arc a : succArc)
			{
				succNode = a.getFin().getIndex();
				succLabel = hashmap.get(succNode);
				if(hashmap.containsKey(succNode) && succLabel.isMarked()==true) //si le noeud est dans la closed set
				{
					continue; //on passe à au successeur suivant
				}
					
				double arcCost = a.getCout();
				int arcLong = a.getLongueur();
				double costTentative; //tentative de mise à jour du coût
						

				if(Label.mode==0) //d�pend de quel mode on a choisit (distance ou temps)
				{
					costTentative = currentCost + arcCost;
				}
				else
				{
					costTentative = currentLong + arcLong;
				}

				
				if(!(hashmap.containsKey(succNode)) || (costTentative<succLabel.getCost())) //si le noeud n'est pas dans la openset ou que le cout actualisable est inférieur au cout actuel du Label
				{
					if(hashmap.containsKey(succNode)) //si c'est effectivement que le cout actualisable est inférieur
					{
						succLabel.setPere(currentNode);
						succLabel.setCout(currentCost + arcCost);
						succLabel.setLongueur(currentLong + arcLong);
						tas.update(succLabel);
						hashmap.put(succNode, succLabel);
					}
					else //si c'est en fait le noeud qui n'est pas dans la openset
					{
						succLabel = new Label(false, currentCost + arcCost, currentLong + arcLong, currentNode, succNode);
						tas.insert(succLabel);
						this.currentSize++;
						this.nbexplorednodes++;
						if(this.currentSize>this.maxSize)
						{
							this.maxSize=this.currentSize;
						}
						hashmap.put(succNode, succLabel);
					}
				}
			}
		}
		if(destinationLabel.getCourant()!=destination)
		{
			time = System.currentTimeMillis() - time;
			System.out.println("");
			System.out.println("Il n'y a pas de solution pour ce parcours !");
		}
		else
		{
			solution = this.reconstruct(destinationLabel);
			time = System.currentTimeMillis() - time;
			solution.print();
			solution.draw(gr.getDessin(), Color.GREEN);
			//on retrouve le chemin le plus court en partant du label destination
		}
		System.out.println("Taille max du tas: " + this.maxSize + ".");
		System.out.println("Nombre de sommets explores: " + this.nbexplorednodes + ".");
		System.out.println("Temps mis par l'algorithme: " + time + "ms");
		System.out.println("Nombre de labels marques: " + this.nbmarques);
		System.out.println("");
		

		String mode="";
		
		if(Label.mode==0)
		{
			mode="T";
		}
		else if(Label.mode==1)
		{
			mode="D";
		}
		int sizeC = solution.size();
		try
		{
		BufferedWriter s = new BufferedWriter(new FileWriter("Graphe.txt", true));
		s.write(gr.nomCarte + " (" + this.origine + " -> " + this.destination + ")\t" + mode + "\t" + time + "\t" + nbmarques + "\t" + nbexplorednodes + "\t" + String.format("%.2f", destinationLabel.getCout()) + "\t" + String.format("%.2f", destinationLabel.getLongueur()/1000.0) + "\t" + solution.get(sizeC/2).getIndex());
		s.newLine();
		s.close();
		}
		catch(IOException e){}
		
    }
    
    
    public HashMap<Integer, Label> runVersTous(HashMap<Integer, Label> zonePieton) {


		System.out.println("Run PCC-to-All de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination +" en mode " + this.typePerson);
		System.gc();
		gr.getDessin().setColor(Color.BLUE);
		this.time = System.currentTimeMillis();
		
	    Label destinationLabel=new Label(true, 0, 0, -1, destination);
		Label origineLabel = new Label(true, 0, 0, -1, origine); //sommet origine premier, -1 car pas de pere
		this.tas.insert(origineLabel);
		this.hashmap.put(origine, origineLabel);
		Label currentLabel = origineLabel;
		
		int size = zonePieton.size();
		int compteur = 0;
		
		while(!(tas.isEmpty()))
		{	
			currentLabel = tas.deleteMin();
			int currentNode = currentLabel.getCourant();
			if(!zonePieton.isEmpty() && zonePieton.containsKey(currentNode))
			{
				compteur++;
				if(compteur==size)
				{
					break;
				}
			}
			currentLabel.setMarked();
			hashmap.put(currentNode, currentLabel);
			if(this.typePerson)
			{
				gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 5);
			}
			int currentLong = currentLabel.getLongueur();
			double currentCost = currentLabel.getCout();
			this.currentSize--;
			ArrayList<Arc> succArc = gr.getSuccesseurs(currentNode);
		    int succNode=0;
		    Label succLabel = null;
			for(Arc a : succArc)
			{
				succNode = a.getFin().getIndex();
				succLabel = hashmap.get(succNode);
				if((hashmap.containsKey(succNode) && succLabel.isMarked()==true)) //si le noeud est dans la closed set ou bien que c'est un arc interdit si this=pieton
				{
					continue; //on passe à au successeur suivant
				}
				int arcLong = a.getLongueur();
				double arcCost;


				if(this.typePerson)
				{
					arcCost = a.getCout();
				}
				else
				{
					if(a.getDescripteur().vitesseMax()>=110)
					{
						arcCost=Double.POSITIVE_INFINITY;
					}
					else
					{
						arcCost = 60*((float)a.getLongueur())/(1000*this.vit);
					}
				}
				double costTentative = currentCost + arcCost; //tentative de mise à jour du coût
				if((this.typePerson==false) && (costTentative > contrainte))
				{
					continue;
				}
				if(this.typePerson==false)
				{
					gr.getDessin().setColor(Color.CYAN);
					gr.getDessin().drawPoint(gr.getSommet(succNode).getLongitude(), gr.getSommet(succNode).getLatitude(), 5);
				}
				if(!(hashmap.containsKey(succNode)) || (costTentative<succLabel.getCout())) //si le noeud n'est pas dans la openset ou que le cout actualisable est inférieur au cout actuel du Label
				{
					if(hashmap.containsKey(succNode)) //si c'est effectivement que le cout actualisable est inférieur
					{
						succLabel.setPere(currentNode);
						succLabel.setCout(currentCost + arcCost);
						succLabel.setLongueur(currentLong + arcLong);
						tas.update(succLabel);
						hashmap.put(succNode, succLabel);
					}
					else //si c'est en fait le noeud qui n'est pas dans la openset
					{
						succLabel = new Label(false, currentCost + arcCost, currentLong + arcLong, currentNode, succNode);
						tas.insert(succLabel);
						this.currentSize++;
						this.nbexplorednodes++;
						if(this.currentSize>this.maxSize)
						{
							this.maxSize=this.currentSize;
						}
						hashmap.put(succNode, succLabel);
					}
				}
			}
		}
		
		this.time = System.currentTimeMillis()- time;
		if(destinationLabel.getCourant()!=destination)
		{
			System.out.println("");
			System.out.println("Il n'y a pas de solution pour ce parcours !");
		}
		
		
		/*System.out.println("Taille max du tas: " + this.maxSize + ".");
		System.out.println("Nombre de sommets explorés: " + this.nbexplorednodes + ".");
		System.out.println("Temps mis par l'algorithme: " + time + "ms");
		System.out.println("Compteur: " + compteur);
		System.out.println("Size: " + zonePieton.size());
		System.out.println("Taille hashmap: " + hashmap.size());
		System.out.println();*/
		
		return hashmap;
    }
    
    public HashMap<Integer, Label> runVersTousInverse() {

    	System.gc();
    	
		System.out.println("Run PCC-to-All-Reversed de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination +" en mode " + this.typePerson);
		gr.getDessin().setColor(Color.MAGENTA);
		
		this.time = System.currentTimeMillis();
		
	    Label destinationLabel=new Label(true, 0, 0, -1, destination);
		Label origineLabel = new Label(true, 0, 0, -1, origine); //sommet origine premier, -1 car pas de pere
		tas.insert(origineLabel);
		hashmap.put(origine, origineLabel);
		Label currentLabel = origineLabel;
		
		while(!(tas.isEmpty()))
		{	
			currentLabel = tas.deleteMin();
			int currentNode = currentLabel.getCourant();
			currentLabel.setMarked();
			hashmap.put(currentNode, currentLabel);
			gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 2);
			int currentLong = currentLabel.getLongueur();
			double currentCost = currentLabel.getCout();
			this.currentSize--;
			ArrayList<Arc> succArc = gr.getPredecesseurs(currentNode);
		    int succNode=0;
		    Label succLabel = null;
			for(Arc a : succArc)
			{
				succNode = a.getDebut().getIndex();
				succLabel = hashmap.get(succNode);
				if((hashmap.containsKey(succNode) && succLabel.isMarked()==true)) //si le noeud est dans la closed set
				{
					continue; //on passe à au successeur suivant
				}
				int arcLong = a.getLongueur();
				double arcCost = a.getCout();
				double costTentative = currentCost + arcCost; //tentative de mise à jour du coût
				if(!(hashmap.containsKey(succNode)) || (costTentative<succLabel.getCout())) //si le noeud n'est pas dans la openset ou que le cout actualisable est inférieur au cout actuel du Label
				{
					if(hashmap.containsKey(succNode)) //si c'est effectivement que le cout actualisable est inférieur
					{
						succLabel.setPere(currentNode);
						succLabel.setCout(currentCost + arcCost);
						succLabel.setLongueur(currentLong + arcLong);
						tas.update(succLabel);
						hashmap.put(succNode, succLabel);
					}
					else //si c'est en fait le noeud qui n'est pas dans la openset
					{
						succLabel = new Label(false, currentCost + arcCost, currentLong + arcLong, currentNode, succNode);
						tas.insert(succLabel);
						this.currentSize++;
						this.nbexplorednodes++;
						if(this.currentSize>this.maxSize)
						{
							this.maxSize=this.currentSize;
						}
						hashmap.put(succNode, succLabel);
					}
				}
			}
		}
		time = System.currentTimeMillis() - time;
		if(destinationLabel.getCourant()!=destination)
		{
			System.out.println("");
			System.out.println("Il n'y a pas de solution pour ce parcours !");
		}
		System.out.println("Taille max du tas: " + this.maxSize + ".");
		System.out.println("Nombre de sommets explorés: " + this.nbexplorednodes + ".");
		System.out.println("Temps mis par l'algorithme: " + time + "ms");
		System.out.println("");
		
		return hashmap;
    }
    
    public Chemin reconstruct(Label destinationLabel)
    {
    	Chemin solution = new Chemin();
		solution.setCout(destinationLabel.getCout());
		solution.setLongueur(destinationLabel.getLongueur());
		while(destinationLabel!=null)
		{
			solution.addSommet(gr.getSommet(destinationLabel.getCourant()));
			destinationLabel = hashmap.get(destinationLabel.getPere());
		}
		solution.reverse();
		return solution;
    }
    
    protected Chemin getSolution()
    {
    	return this.solution;
    }
    
    public long getTime()
    {
    	return this.time;
    }
    
}


		
	
