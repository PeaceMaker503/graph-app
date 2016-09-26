package core ;
import base.* ;

import java.util.* ;

public class Arc
{
	private Sommet debut;
	private Sommet fin;
	private Descripteur description;
	private int longueur;
	private ArrayList<Segment> segs;
	
	public Arc(Sommet debut, Sommet fin, Descripteur description, int longueur, ArrayList<Segment> newSegs)
	{
		this.debut = debut;
		this.fin = fin;
		this.description = description;
		this.longueur = longueur;
		this.segs = newSegs;
	}
	
	public Arc()
	{
		this.debut = null;
		this.fin = null;
		this.description = null;
		this.longueur = 0;
		this.segs = null;
	}
	public void addSegment(Segment s)
	{
		this.segs.add(s);
	}
	
	public Sommet getDebut()
	{
		return this.debut;
	}
	
	public Sommet getFin()
	{
		return this.fin;
	}
	
	public void drawArc(Dessin dessin)
	{
		    float current_long = this.debut.getLongitude();
		    float current_lat  = this.debut.getLatitude();
		    
			for(Segment newSeg : segs)
			{
				dessin.drawLine(current_long, current_lat, current_long + newSeg.getDeltaLong(), current_lat + newSeg.getDeltaLat()) ;
				current_long += newSeg.getDeltaLong() ;
				current_lat  += newSeg.getDeltaLat() ;
			}
	}
	
	public double getCout()
	{
		return  (60.0f*this.getLongueur())/(this.getDescripteur().vitesseMax()*1000.0);
	}
	
	public int getLongueur()
	{
		return this.longueur;
	}

	public Descripteur getDescripteur()
	{
		return this.description;
	}
	
	public String toString()
	{
		return "(Debut :" + this.getDebut().getIndex() + "; Fin: " + this.getFin().getIndex() + "; Cout: " + this.getCout() + ";\n";	
	}
}
