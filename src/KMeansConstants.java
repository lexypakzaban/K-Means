import java.awt.Color;

public interface KMeansConstants
{
	// color codes for the various attractors
	public final Color[] COLOR_LIST = {Color.RED,Color.GREEN,Color.BLUE,Color.MAGENTA,Color.YELLOW,Color.CYAN,new Color(255,128,128), new Color(192,0,128), Color.DARK_GRAY, new Color(255,128,0)};
	// radius of the circles for the voting points on screen
	public final int VP_RADIUS = 2;
	// "radius" of the plus signs for the attractors.
	public final int ATT_RADIUS = 4;
}
