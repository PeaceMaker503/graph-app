package core ;

import java.io.* ;
import java.util.HashMap;
import java.awt.Color;

import base.Readarg ;

public class Covoiturage extends Algo {
	
	private int pieton;
	private int automobiliste;
	private int destination;
	private Pcc pccPieton;
	private PccStar pccAutomobiliste;
	private PccStar pccDestination;
	private Graphe gr;
	protected HashMap<Integer, Label> hashmapP;
	protected HashMap<Integer, Label> hashmapA;
	protected HashMap<Integer, Label> hashmapD;
	private double minCout;
	private int rencontre;
	
    public Covoiturage(Graphe gr, PrintStream sortie, Readarg readarg) 
    {
    	super(gr, sortie, readarg) ;
    	this.pieton = readarg.lireInt ("Numero du sommet ou se trouve le pieton ? ");
    	this.automobiliste = readarg.lireInt ("Numero du sommet ou se trouve l'automobiliste ? ");
    	this.destination = readarg.lireInt ("Numero du sommet destination ? ");
    	System.out.println();
    	this.gr = gr;
    	
    	this.pccPieton = new Pcc(this.gr, sortie, readarg, this.pieton, 4, 15);
    	this.pccAutomobiliste = new PccStar(this.gr, sortie, readarg, this.automobiliste, this.pieton, 0, 0);
		this.pccDestination = new PccStar(this.gr, sortie, readarg, this.destination, this.pieton, 0, 0);
    	
    	this.hashmapP = new HashMap<Integer, Label>();
    	this.hashmapA = new HashMap<Integer, Label>();
    	this.hashmapD = new HashMap<Integer, Label>();
    	this.minCout=Double.POSITIVE_INFINITY;
    	this.rencontre = destination;
    }
    
    public void run()
    {
		System.out.println("Run Covoiturage:  pieton=" + this.pieton + "; automobiliste=" + this.automobiliste + "; destination=" + this.destination);
		this.hashmapP = this.pccPieton.runVersTous(new HashMap<Integer, Label>());
	    this.hashmapA = this.pccAutomobiliste.runVersTous(this.hashmapP);
		this.hashmapD = this.pccDestination.runVersTousInverse(this.hashmapP);
		System.gc();
		
		System.out.println("Run des calculs");
		long time = System.currentTimeMillis();
		for (int i=0; i<gr.getNbNodes(); i++)
		{
			try
			{
				double currentCout =  Math.max(hashmapP.get(i).getCout(), hashmapA.get(i).getCout())  + hashmapD.get(i).getCout();
				if(currentCout < minCout) 
				{
					minCout = currentCout;
					rencontre = i;
				}
			}
			catch(NullPointerException e){}
		}
			Chemin auto_rencontre = this.pccAutomobiliste.reconstruct(hashmapA.get(rencontre));
			Chemin pieton_rencontre = this.pccPieton.reconstruct(hashmapP.get(rencontre));
			Chemin rencontre_dest = this.pccDestination.reconstruct(hashmapD.get(rencontre));
			time =  System.currentTimeMillis() - time +  this.pccAutomobiliste.getTime() + this.pccDestination.getTime() + this.pccPieton.getTime();
			
			
			auto_rencontre.draw(gr.getDessin(), Color.CYAN);
			gr.getDessin().drawPoint(gr.getSommet(automobiliste).getLongitude(), gr.getSommet(automobiliste).getLatitude(), 7);
			pieton_rencontre.draw(gr.getDessin(), Color.MAGENTA);
			gr.getDessin().drawPoint(gr.getSommet(pieton).getLongitude(), gr.getSommet(pieton).getLatitude(), 10);
			rencontre_dest.draw(gr.getDessin(), Color.RED);
			gr.getDessin().drawPoint(gr.getSommet(rencontre).getLongitude(), gr.getSommet(rencontre).getLatitude(), 10);
			gr.getDessin().drawPoint(gr.getSommet(destination).getLongitude(), gr.getSommet(destination).getLatitude(), 7);
			
			try
			{
			System.out.println("Cout pieton de son origine a la destination: " + this.hashmapP.get(destination).getCout() + "min");
			}
			catch(NullPointerException e)//la dest n'est pas tjrs dans la zone du pieton
			{
				
			}
			
			System.out.println("Cout pieton de son origine au point de rencontre: " + this.hashmapP.get(rencontre).getCout() + "min");
			//System.out.println("Cout automobiliste de son origine a la destination: " + this.hashmapA.get(destination).getCout() + "min");
			System.out.println("Cout automobiliste de son origine au point de rencontre: " + this.hashmapA.get(rencontre).getCout() + "min");
			System.out.println("Cout du point de rencontre a la destination: " + this.hashmapD.get(rencontre).getCout() + "min");
			System.out.println("Cout du trajet pour le pieton: " + (this.hashmapP.get(rencontre).getCout() + this.hashmapD.get(rencontre).getCout()) + "min");
			System.out.println("Cout du trajet pour l'automobiliste: " + (this.hashmapA.get(rencontre).getCout() + this.hashmapD.get(rencontre).getCout()) + "min");
			System.out.println("Cout du trajet total: " + minCout);
			System.out.println("Point de rencontre: " + rencontre);
			System.out.println("Temps mis par l'algorithme: " + time + "ms");

    }
    
}



// pieton 68431 auto 119428 dest 44759
//midip 1 6 0 102850 119963 96676; 295.28616063936084 et 102850
//midip 1 6 0 102850 130905 96676
//midip 1 6 0 106307 130905 96676
//france 1 6 0 1370602 1485825 607873
//607873
//france 1 6 0 655765 806813 135924; i=655765 et t=557.896555710956
