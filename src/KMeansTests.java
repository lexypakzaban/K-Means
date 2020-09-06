import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class KMeansTests
{

	@Test
	public void testDistance()
	{
		VotingPoint vp1 = new VotingPoint(3.00300,3.00400);
		VotingPoint vp2 = new VotingPoint(2.1111,3.22222);
		Attractor att1 = new Attractor(3.00600,3.00800);
		Attractor att2 = new Attractor(2.1111,3.222222);
		// NOTE assertEquals with floats and doubles takes three values: expected, actual, margin of error
		assertEquals(0.005, att1.distanceToVotingPoint(vp1),0.0001);
		assertNotEquals(0.005,att1.distanceToVotingPoint(vp2),0.0001);
		assertEquals(0,att2.distanceToVotingPoint(vp2),0.00001);
		assertEquals(0.9182,att2.distanceToVotingPoint(vp1),0.00001);
	}

	@Test
	public void testLoadFile()
	{
		File theFile = new File("KinkaidHouseholds.txt");
		KMeansPanel kmp = new KMeansPanel();
		kmp.loadFile(theFile);
		assertEquals(1430,kmp.getData().size());
		assertEquals(-1.666944,kmp.getData().get(0).getX(),0.000001);
		assertEquals(0.51926,kmp.getData().get(0).getY(),0.000001);
		assertEquals(-1.666724,kmp.getData().get(5).getX(),0.000001);
		assertEquals(0.519974,kmp.getData().get(5).getY(),0.000001);
	}
	
	
}
