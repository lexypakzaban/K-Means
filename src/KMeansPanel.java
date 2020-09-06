import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class KMeansPanel extends JPanel implements KMeansConstants
{
	int K; // how many attractors?
	boolean needsClear; // next repaint - should we wipe out the previous attractor locations?
	double minX, minY, maxX, maxY; // the range of the data we've loaded.
	BufferedImage lastScreen; // used to store the previous appearance of the screen so we 
							  //   can show trails of + signs, indicating the motion of the attractors
	JTextArea descriptionTA; // the textArea in the west pane of the layout, so we can show data there.
	
	ArrayList<Attractor> tenAttractors; // we will always have ten attractors 
										//   in the list, but only use K of them.
	
	ArrayList<VotingPoint> data; // this is whare we store all the GPS coordinates.
	
	KMeansPanel()
	{
		super();
		K = 4;
		setBackground(Color.BLACK);
		tenAttractors = new ArrayList<Attractor>();
		for (int i=0; i<10; i++)
			tenAttractors.add(new Attractor(0,0));
		
		data = new ArrayList<VotingPoint>();
		
		needsClear = true;
		minX = -1;
		minY = -1;
		maxX = 1;
		maxY = 1;
		randomize(true);
	}
	
	public void paintComponent(Graphics g)
	{
		// handle the trails... a bit fancy.
		if (needsClear)
		{
			System.out.println("Clearing.");
			super.paintComponent(g);
			needsClear = false;
			lastScreen = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
			
		}
		else if (lastScreen != null)
		{
			g.drawImage(lastScreen, 0, 0,null);
		}
		else
			lastScreen = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		
		// capture this image for next time.
		Graphics g2 = lastScreen.createGraphics();
				
		// Draw Voting Points
		for (VotingPoint datum:data)
		{
			g2.setColor(COLOR_LIST[datum.getWhichAttractor()]);
			g2.fillOval(interpolateX(datum.getX())-VP_RADIUS, interpolateY(datum.getY())-VP_RADIUS, 2*VP_RADIUS, 2*VP_RADIUS);
		}
		// Draw Attractors
		for (int k=0; k<K; k++)
		{
			int x = interpolateX(tenAttractors.get(k).getX());
			int y = interpolateY(tenAttractors.get(k).getY());
			g2.setColor(Color.BLACK);
			g2.fillRect(x-ATT_RADIUS, y-1, 2*ATT_RADIUS, 3);
			g2.fillRect(x-1, y-ATT_RADIUS, 3, 2*ATT_RADIUS);
			g2.setColor(COLOR_LIST[k]);
			g2.drawLine(x-ATT_RADIUS,y,x+ATT_RADIUS,y);
			g2.drawLine(x, y-ATT_RADIUS, x,y+ATT_RADIUS);
		}
		
		g.drawImage(lastScreen, 0, 0, null);
		
		// circle latest version of attractor. (not in stored image, so these don't trail.)
		for (int k=0; k<K; k++)
		{
			int x = interpolateX(tenAttractors.get(k).getX());
			int y = interpolateY(tenAttractors.get(k).getY());
			g.setColor(Color.WHITE);
			g.drawOval(x-ATT_RADIUS-1,y-ATT_RADIUS-1,2*ATT_RADIUS+2,2*ATT_RADIUS+2);
		}
			
	}
	/**
	 * finds the relative position of xVal within the range of minX <--> maxX, and returns the value
	 * at the same relative position within the range of 0 <--> width of screen. For instance, if xVal
	 * was a quarter of the way from minX to maxX, then we would return a number a quarter of the way
	 * from 0 to width.
	 * This allows us to scale values in an unusual range to the screen.
	 * @param xVal - the number in range (minX, maxX)
	 * @return - the corresponding number in range (0,width)
	 */
	public int interpolateX(double xVal)
	{
		return (int)((xVal-minX)/(maxX-minX)*(this.getWidth()));
	}
	
	/**
	 * finds the relative position of yVal within the range of minY <--> maxY, and returns the value
	 * at the same relative position within the range of 0 <--> height of screen. For instance, if yVal
	 * was a quarter of the way from minY to maxY, then we would return a number a quarter of the way
	 * from 0 to height.
	 * This allows us to scale values in an unusual range to the screen.
	 * @param yVal - the number in range (minY, maxY)
	 * @return - the corresponding number in range (0,height)
	 */
	public int interpolateY(double yVal)
	{
		return (int)((yVal-minY)/(maxY-minY)*(this.getHeight()));
	}
	
	/**
	 * determine the min and max values of the x and y coordinates of the Voting points in the "data" ArrayList.
	 */
	public void findRangeOfData()
	{
		minX = +9e9; // these are member variables. I've reset them for you.
		minY = +9e9;
		maxX = -9e9;
		maxY = -9e9;
		//TODO: enter your code here.

		for(VotingPoint vp: data){
			if (vp.getX() > maxX){
				maxX = vp.getX();
			}

			if(vp.getY() > maxY){
				maxY = vp.getY();
			}

			if(vp.getX() < minX){
				minX = vp.getX();
			}

			if(vp.getY() < minY){
				minY = vp.getY();
			}
		}

		//--------------------------------------------
		if (minX == maxX || minY == maxY)
		{
			throw new RuntimeException("Range not large enough.");
		}
	}
	
	/**
	 * we've just been given a file location, so open that tab-delimited file and read the latitude and longitude
	 * data into VotingPoints' y and x locations, creating an ArrayList of VotingPoints.
	 * @param theFile
	 */
	public void loadFile(File theFile)
	{
		System.out.println("Loading: "+theFile);
		BufferedReader fileIn;
		try
		{
			fileIn = new BufferedReader(new FileReader(theFile));
		}
		catch (FileNotFoundException fnfExp)
		{
			System.out.println("File not found. Since this came from a file dialog, that's pretty weird.");
			return;
		}
		int i =1;
		String line = null;
		try
		{
			line = fileIn.readLine(); // read the header row. We're going to ignore it.
			i = 2;
			line = fileIn.readLine(); // read the first row of actual data
		}
		catch (IOException ioExp)
		{
			System.out.println("Error trying to read file at line: "+i+".");
			ioExp.printStackTrace();
		}
		while(line != null)
		{
			try
			{
				i++;
				// you now have a line that corresponds to a row of the file. split it up by tabs,
				// parse the latitude and longitude as double values, create a new VotingPoint instance,
				// and add it to the array list "data."
				// NOTE: Latitude is a north-south measure; Longitude is an east-west measure.
				//TODO: insert your code here.

				String[] latLongArray = line.split("\t");
				double lat = Double.valueOf(latLongArray[0]);
				double lon = Double.valueOf(latLongArray[1]);
				VotingPoint vp = new VotingPoint(lon,lat);
				data.add(vp);


				//------------------------------
				line = fileIn.readLine();
			}
			catch (IOException ioExp)
			{
				System.out.println("Error trying to read file at line: "+i+".");
				ioExp.printStackTrace();
			}
			catch (NumberFormatException nfExp)
			{
				System.out.println("Trouble parsing line "+i+": \""+line+"\"");
				continue;
			}
		}
		try
		{
			fileIn.close();
		}
		catch (IOException ioExp)
		{
			System.out.println("Error trying to close file.");
			ioExp.printStackTrace();
		}
		
		findRangeOfData();
		clearTrails();
	}
	public ArrayList<VotingPoint> getData()
	{
		return data;
	}
	/**
	 * for each point in the data list, figure out which of the active K attractors is closest
	 * to it, and set the data point's "whichAttractor" variable to the number of that attractor.
	 */
	public void findClosest()
	{
		System.out.println("calling findClosest();");
		//TODO: Enter your code here.

		for(VotingPoint vp: data){

			double minDistance = +9e9;
			int foundIndex = -1;

			for(int i = 0; i < K; i++){
				if (tenAttractors.get(i).distanceToVotingPoint(vp) < minDistance){
					minDistance = tenAttractors.get(i).distanceToVotingPoint(vp);
					foundIndex = i;
				}
			}
			vp.setWhichAttractor(foundIndex);
		}
		

		//------------------------------
		repaint();
	}
	/**
	 * for each active K attractor, survey all the VotingPoints in data, and find the average of the
	 * locations for the ones that have their allegiance to this attractor. Change this attractor's
	 * location to this average.
	 */
	public void reAverage()
	{
		System.out.println("calling reAverage();");
		// TODO: insert your code here.

		for (int i = 0; i < K; i++){
			double sumX = 0;
			double sumY = 0;
			int count = 0;

			for(VotingPoint vp: data){

				if (vp.getWhichAttractor() == i){
					sumX += vp.getX();
					sumY += vp.getY();
					count++;
				}
			}

			double averageX = sumX/count;
			double averageY = sumY/count;

			tenAttractors.get(i).setX(averageX);
			tenAttractors.get(i).setY(averageY);
		}

		
		//-------------------------------
		updateTA();
		repaint();
	}
	
	/**
	 * modifier for the variable K, called from the KMeans_Frame in response to an action by the
	 * user.
	 * @param inK
	 */
	public void setK(int inK)
	{
		K = inK;
		System.out.println("Setting K to: "+inK);
		clearTrails();
	}
	/**
	 * randomizes the locations of all the attractors, either from all locations in the range of the
	 * data or exclusively from those locations where the data are.
	 * @param shouldPickLocations
	 */
	public void randomize(boolean shouldPickLocations)
	{
		System.out.println("Randomizing. Using locations = "+shouldPickLocations);
		for (Attractor att:tenAttractors)
		{
			if (shouldPickLocations || data.isEmpty())
			{
				// set the attractor's location to a random location from anywhere 
				//      in (minX<-->maxX) x (minY<-->maxY)
				//  TODO: add your code here.
				att.setX((Math.random()*(maxX-minX)) + minX);
				att.setY((Math.random()*(maxY-minY)) + minY);

				//---------------------------------------
			}
			else
			{
				// set the attractor's location to match that of a random VotingPoint in the data Al. 
				// TODO: add your code here.
				int randomIndex = (int)(Math.random() * data.size());
				att.setX(data.get(randomIndex).getX());
				att.setY(data.get(randomIndex).getY());
				
				//------------------------------------------
			}
		}
		updateTA();
		clearTrails();
		
	}
	/**
	 * we've just been told to clear the trails by the user vi UI. 
	 */
	public void clearTrails()
	{
		System.out.println("calling clear.");
		needsClear = true;
		repaint();
	}
	
	/**
	 * modifier to tell this class about the TextArea in the West panel, so that we can modify its contents.
	 * @param TA - the JTextArea on the left side of the window.
	 */
	public void setDescriptionField(JTextArea TA)
	{
		descriptionTA = TA;
	}
	/**
	 * appends a description of the K active attractors to the contents of the TextArea in the West panel.
	 */
	public void updateTA()
	{
		if (descriptionTA == null)
			return;
		String output = descriptionTA.getText();
		DecimalFormat numberFormat = new DecimalFormat("0.000000");
		/* note: if you say 
		  		numberFormat.format(<some float or double>)
		   it will give you a string version of the number with exactly six digits after the decimal place
		 */
		
		// For each of the K active attractors, print its location and the number of VotingPoints that have selected it.
		//TODO: insert your code here.
		
		//-------------------------------------
		output+="-----------------------\n";
		descriptionTA.setText(output);
	}
	
}
