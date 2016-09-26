package core ;

import java.io.* ;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

import base.Readarg ;

public class PccStar extends Pcc {
	
    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg, int mode) {
	super(gr, sortie, readarg, mode) ;
    }
    
    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg, int origine, int pieton, double vit, double contrainte) 
    {
    	super(gr, sortie, readarg, origine, vit, contrainte);
    	destination = pieton;
    }
    
    public void run() {

		System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination);
		gr.getDessin().setColor(Color.BLUE);
		System.gc();
		
		long time = System.currentTimeMillis();
	    Label destinationLabel=new Label(true, 0, 0, -1, destination, 0);
	    
		double totalEstimation = heuristic(origine);
		Label origineLabel = new Label(true, 0, 0, -1, origine, totalEstimation); //sommet origine premier, -1 car pas de pere
		tas.insert(origineLabel);
		hashmap.put(origine, origineLabel);
		Chemin solution = new Chemin();
		while(!(tas.isEmpty()))
		{	
			Label currentLabel = tas.deleteMin();
			int currentNode = currentLabel.getCourant();
			gr.getDessin().setColor(Color.BLUE);
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
						double estimation = heuristic(succNode);
						succLabel = new Label(false, currentCost + arcCost, currentLong + arcLong, currentNode, succNode, estimation);
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
		System.out.println("Nombre de sommets explorés: " + this.nbexplorednodes + ".");
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

    	System.gc();
    	
		System.out.println("Run PCC-Star-to-zonePieton de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination);
		
		gr.getDessin().setColor(Color.RED);
		gr.getDessin().drawPoint(gr.getSommet(destination).getLongitude(), gr.getSommet(destination).getLatitude(), 5);
		gr.getDessin().setColor(Color.BLUE);

		
		this.time = System.currentTimeMillis();
		int compteur=0;
		int sizeP=zonePieton.size();
	    Label destinationLabel=new Label(true, 0, 0, -1, destination, 0);
		double totalEstimation = heuristic(origine);
		Label origineLabel = new Label(true, 0, 0, -1, origine, totalEstimation); //sommet origine premier, -1 car pas de pere
		tas.insert(origineLabel);
		hashmap.put(origine, origineLabel);
		
		while(!(tas.isEmpty()))
		{	
			Label currentLabel = tas.deleteMin();
			int currentNode = currentLabel.getCourant();
			if(currentNode==destination)
			{
				destinationLabel = currentLabel;
			}
			
			if(zonePieton.containsKey(currentNode))
			{
				gr.getDessin().setColor(Color.RED);
				gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 5);
				compteur++;
				if(compteur==sizeP)
				{
					break;
				}
			}
			else
			{
			gr.getDessin().setColor(Color.BLUE);
			gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 2);
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
						double estimation = heuristic(succNode);
						succLabel = new Label(false, currentCost + arcCost, currentLong + arcLong, currentNode, succNode, estimation);
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
		System.out.println("Nombre de labels marques: " + this.nbmarques);
		System.out.println("Compteur: " + compteur);
		System.out.println("Size: " + sizeP);
		System.out.println("");*/
		
		return hashmap;
    }
    
    public HashMap<Integer, Label> runVersTousInverse(HashMap<Integer, Label> zonePieton) {

    	System.gc();
    	
		System.out.println("Run PCC-Star-to-zonePieton-Reverse de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination);
		gr.getDessin().setColor(Color.RED);
		gr.getDessin().drawPoint(gr.getSommet(destination).getLongitude(), gr.getSommet(destination).getLatitude(), 5);
		gr.getDessin().setColor(Color.BLUE);

		
		this.time = System.currentTimeMillis();
		int compteur=0;
		int sizeP=zonePieton.size();
	    Label destinationLabel=new Label(true, 0, 0, -1, destination, 0);
		double totalEstimation = heuristic(origine);
		Label origineLabel = new Label(true, 0, 0, -1, origine, totalEstimation); //sommet origine premier, -1 car pas de pere
		tas.insert(origineLabel);
		hashmap.put(origine, origineLabel);
		
		while(!(tas.isEmpty()))
		{	
			Label currentLabel = tas.deleteMin();
			int currentNode = currentLabel.getCourant();
			if(currentNode==destination)
			{
				destinationLabel = currentLabel;
			}
			if(zonePieton.containsKey(currentNode))
			{
				gr.getDessin().setColor(Color.BLACK);
				gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 5);
				compteur++;
				//System.out.println("Compteur: " + compteur);
				if(compteur==sizeP)
				{
					break;
				}
			}
			else
			{
			gr.getDessin().setColor(Color.CYAN);
			gr.getDessin().drawPoint(gr.getSommet(currentNode).getLongitude(), gr.getSommet(currentNode).getLatitude(), 2);
			}
			currentLabel.setMarked();
			this.nbmarques++;
			hashmap.put(currentNode, currentLabel);

			int currentLong = currentLabel.getLongueur();
			double currentCost = currentLabel.getCout();
			this.currentSize--;
			ArrayList<Arc> succArc = gr.getPredecesseurs(currentNode);
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
						double estimation = heuristic(succNode);
						succLabel = new Label(false, currentCost + arcCost, currentLong + arcLong, currentNode, succNode, estimation);
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
			time = System.currentTimeMillis() - time;
			System.out.println("");
			System.out.println("Il n'y a pas de solution pour ce parcours !");
		}
		
		
		/*System.out.println("Taille max du tas: " + this.maxSize + ".");
		System.out.println("Nombre de sommets explorés: " + this.nbexplorednodes + ".");
		System.out.println("Temps mis par l'algorithme: " + time + "ms");
		System.out.println("Nombre de labels marques: " + this.nbmarques);
		System.out.println("Compteur: " + compteur);
		System.out.println("Size: " + sizeP);
		System.out.println("");*/
		return hashmap;
    }
    
    public double heuristic(int succNode)
    {
    		if(Label.mode==0)
        	{
        		double d = Graphe.distance(gr.getSommet(succNode).getLongitude(), gr.getSommet(succNode).getLatitude(), gr.getSommet(destination).getLongitude(), gr.getSommet(destination).getLatitude());
        		return (60.0*d)/(this.gr.getVitMax()*1000.0);
        	}
        	else
        	{
        		return Graphe.distance(gr.getSommet(succNode).getLongitude(), gr.getSommet(succNode).getLatitude(), gr.getSommet(destination).getLongitude(), gr.getSommet(destination).getLatitude());
        	}
    }
}



	
