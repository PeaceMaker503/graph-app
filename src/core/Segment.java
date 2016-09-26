package core ;

public class Segment
{
	float delta_long;
	float delta_lat;
	
	public Segment(float delta_long, float delta_lat)
	{
		this.delta_long = delta_long;
		this.delta_lat = delta_lat;
	}
	
	public float getDeltaLong()
	{
		return delta_long;
	}
	
	public float getDeltaLat()
	{
		return delta_lat;
	}
	
}