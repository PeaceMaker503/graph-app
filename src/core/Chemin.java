package core ;
import java.awt.Color;
import java.util.* ;
import base.* ;

public class Chemin
{
	private ArrayList<Sommet> chem;
	private double cout;
	private int longueur;
	
	public Chemin()
	{
		chem = new  ArrayList<Sommet>();
		this.cout = 0;
		this.longueur = 0;
	}
	
	public void setCout(double cout)
	{
		this.cout = cout;
	}
	
	public double getCout()
	{
		return this.cout;
	}
	
	public Sommet get(int i)
	{
		return chem.get(i);
	}
	
	public ArrayList<Sommet> getChem()
	{
		return this.chem;
	}
	
	public int getLongueur()
	{
		return this.longueur;
	}
	
	public void setLongueur(int l)
	{
		this.longueur=l;
	}
	public void addSommet(Sommet s)
	{
		chem.add(s);
	}
	
	public void reverse()
	{
		Collections.reverse(chem);
	}
	
	public int size()
	{
		return this.chem.size();
	}
	
	public void print()
	{
		System.out.println("");
		for(int i=0;i<this.size();i++)
		{
			System.out.println("--> " + this.chem.get(i).getIndex());
		}
		System.out.println("");
		System.out.println("L'algorithme a trouve qu'il existe un plus court chemin de " + this.getLongueur()/1000.0 + "km realisable en " + (float)this.getCout() + "min." );
	}
	
	public void realDraw(Dessin d, Color arc)
	{
		if(d!=null)
		{
			d.setWidth(2);
			d.setColor(arc);
			for(int i=1;i<this.size();i++)
			{
				Arc a = this.chem.get(i-1).findArcsSortant(this.chem.get(i).getIndex());
				a.drawArc(d);
			}
		}
	}
	
	public void draw(Dessin d, Color arc)
	{
		if(d!=null)
		{
			d.setWidth(2);
			d.setColor(arc);
			for(int i=1;i<this.size();i++)
			{
				d.drawLine(this.chem.get(i-1).getLongitude(), this.chem.get(i-1).getLatitude(), this.chem.get(i).getLongitude(), this.chem.get(i).getLatitude());
			}
		}
	}
}