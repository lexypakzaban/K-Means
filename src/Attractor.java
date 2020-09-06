
public class Attractor
{
	private double x,y;

	public Attractor(double x, double y)
	{
		super();
		this.x = x;
		this.y = y;
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
	/**
	 * returns the Euclidean distance from this attractor to the given voting point.
	 * @param vp - a voting point at some position
	 * @return the distance from this Attractor's (x,y) to the VP's (x,y)
	 */
	public double distanceToVotingPoint(VotingPoint vp)
	{
		double distance=-1;
		//TODO: insert your code here.

		distance = Math.sqrt(Math.pow(vp.getX()-x,2) + Math.pow(vp.getY()-y,2));

		//---------------------------
		return distance;
	}
}
