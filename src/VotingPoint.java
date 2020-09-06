
public class VotingPoint
{
	private double x,y;
	int whichAttractor;
	
	public VotingPoint(double x, double y)
	{
		super();
		this.x = x;
		this.y = y;
		whichAttractor = 0;
	}
	public double getX()
	{
		return x;
	}
	public void setX(double x)
	{
		this.x = x;
	}
	public double getY()
	{
		return y;
	}
	public void setY(double y)
	{
		this.y = y;
	}
	public int getWhichAttractor()
	{
		return whichAttractor;
	}
	public void setWhichAttractor(int whichAttractor)
	{
		this.whichAttractor = whichAttractor;
	}
	/**
	 * returns the euclidean distance from this voting point to the given attractor. 
	 * This is a convenience method so you can ask for distance vp --> a or a --> vp.
	 * @param a
	 * @return distance
	 */
	public double distanceToAttractor(Attractor a)
	{
		return a.distanceToVotingPoint(this);
	}
	
}
