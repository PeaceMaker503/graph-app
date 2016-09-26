package core;

public class Label implements Comparable<Label> {

	private double estimation;
	private boolean marquage;
	private double cout;
	private int pere;
	private int courant;
	private int longueur;
	static public int mode;
		
	public Label(boolean marquage, double cout, int longueur, int pere, int courant, double estimation, double u2)
	{
		this.marquage = marquage;
		this.cout = cout;
		this.pere = pere;
		this.courant = courant;
		this.longueur = longueur;
		this.estimation = estimation;
	}
	
	public Label(boolean marquage, double cout, int longueur, int pere, int courant, double estimation)
	{
		this.marquage = marquage;
		this.cout = cout;
		this.pere = pere;
		this.courant = courant;
		this.longueur = longueur;
		this.estimation = estimation;
	}
	
	public Label(boolean marquage, double cout, int longueur, int pere, int courant)
	{
		this.marquage = marquage;
		this.cout = cout;
		this.pere = pere;
		this.courant = courant;
		this.longueur = longueur;
		this.estimation = 0;
	}
	
	public int compareTo(Label other) 
	{	

		if(mode==0)
		{
			if(this.getCout() + this.getEstimation()  < other.getCout()+other.getEstimation())
			{
			    return -1;
			}
			else if(this.getCout() + this.getEstimation()  > other.getCout()+other.getEstimation())
			{
			 	return 1;
			}
			else
			{
				if(this.getEstimation()  < other.getEstimation() )
				{
				    return -1;
				}
				else if(this.getEstimation()  > other.getEstimation() )
				{
				 	return 1;
				}
				else
				{
					return 0;
				}
			}
		}
		else
		{
			
			if(this.getLongueur() + this.getEstimation()< other.getLongueur()+other.getEstimation())
			{
			    return -1;
			}
			else if(this.getLongueur() + this.getEstimation() > other.getLongueur()+other.getEstimation())
			{
			 	return 1;
			}
			else
			{
				if(this.getEstimation() < other.getEstimation())
				{
				    return -1;
				}
				else if(this.getEstimation()  > other.getEstimation())
				{
				 	return 1;
				}
				else
				{
					return 0;
				}
			}
		}
	}

	
	
	public double getEstimation()
	{
		return this.estimation;
	}
	
	public void setEstimation(double e)
	{
		this.estimation = e;
	}
	

	public boolean isMarked()
	{
		return this.marquage;
	}
	
	public double getCout()
	{
		return this.cout;
	}
	
	public int getPere()
	{
		return this.pere;
	}
	
	public int getCourant()
	{
		return this.courant;
	}
	
	public void setMarked()
	{
		this.marquage = true;
	}
	
	public void setCout(double cout)
	{
		this.cout = cout;
	}
	
	public void setPere(int pere)
	{
		this.pere = pere;
	}
	
	public void setCourant(int courant)
	{
		this.courant = courant;
	}
	
	public void setLongueur(int longueur)
	{
		this.longueur = longueur;
	}
	
	public int getLongueur()
	{
		return this.longueur;
	}
	
	public double getCost()
	{
		if(mode==0)
		{
			return this.getCout();
			
		}
		else
		{
			return this.getLongueur();
			
		}
	}
	
	public String toString()
	{
		return "Label(m=" + isMarked() + ", c="  + getCout() + ", l=" + getLongueur() + ", p=" + getPere() + ", cu=" + getCourant() + ", e=" + getEstimation() + ", l+e=" + (getCost() + getEstimation()) + ")";
	}
}
