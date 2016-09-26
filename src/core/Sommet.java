package core ;
import java.util.* ;

public class Sommet
{
	private float longitude;
	private float latitude;
	private ArrayList<Arc> arcsSortant;
	private ArrayList<Arc> arcsEntrant;
	private int index;
	
	public Sommet(float longitude, float latitude, int index)
	{
		this.longitude = longitude;
		this.latitude = latitude;
		this.arcsSortant = new ArrayList<Arc>();
		this.arcsEntrant = new ArrayList<Arc>();
		this.index = index;
	}

	public Sommet()
	{
		this.longitude = 0;
		this.latitude = 0;
		this.arcsSortant = new ArrayList<Arc>();
		this.arcsEntrant = new ArrayList<Arc>();
		this.index = 0;
	}

	public void setCoordonnees(float longitude, float latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public void addArcSortant(Arc a)
	{
		this.arcsSortant.add(a);
	}

	public void addArcEntrant(Arc a)
	{
		this.arcsEntrant.add(a);
	}
	public float getLongitude()
	{
		return this.longitude;
	}
	
	public void setArcsSortant(ArrayList<Arc> arcsSortant)
	{
		this.arcsSortant = arcsSortant;
	}
	
	public void setArcsEntrant(ArrayList<Arc> arcsEntrant)
	{
		this.arcsEntrant = arcsEntrant;
	}
	
	public float getLatitude()
	{
		return this.latitude;
	}
	
	public ArrayList<Arc> getArcSortant()
	{
		return this.arcsSortant;
	}
	
	public ArrayList<Arc> getArcEntrant()
	{
		return this.arcsEntrant;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	public Arc findArcsSortant(int index) //trouve l'arc le plus court entre this et un sommet
	{
		int longueur=50000;
		Arc a = new Arc();
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		
		for(Arc newArc : this.arcsSortant)
		{
			if((newArc.getDebut().getIndex() == this.getIndex()) && (newArc.getFin().getIndex() == index))
			{
				arcs.add(newArc);
			}
		}
		
		for(Arc newArc : arcs)
		{
			if(newArc.getLongueur() < longueur)
			{
				longueur=newArc.getLongueur();
				a = newArc;
			}
		}
		return a;
	}
	
	public Arc findArcsEntrant(int index) //trouve l'arc le plus court entre this et un sommet
	{
		int longueur=50000;
		Arc a = new Arc();
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		
		for(Arc newArc : this.arcsEntrant)
		{
			if((newArc.getDebut().getIndex() == index) && (newArc.getFin().getIndex() == this.getIndex()))
			{
				arcs.add(newArc);
			}
		}
		
		for(Arc newArc : arcs)
		{
			if(newArc.getLongueur() < longueur)
			{
				longueur=newArc.getLongueur();
				a = newArc;
			}
		}
		return a;
	}
	
	public void printArcSortant()
	{
		for(Arc newArc : this.arcsSortant)
		{
			System.out.println("Arc de " + newArc.getDebut().getIndex() + " vers " + newArc.getFin().getIndex());
		}
	}
	
	public void printArcEntrant()
	{
		for(Arc newArc : this.arcsEntrant)
		{
			System.out.println("Arc de " + newArc.getDebut().getIndex() + " vers " + newArc.getFin().getIndex());
		}
	}
}
	
