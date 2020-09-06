import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class KMeans_Frame extends JFrame implements ActionListener, ChangeListener
{
	private KMeansPanel mainPanel;
	private JButton clearTextButton;
	private JButton loadDataButton,findClosestButton, reAverageButton, stepButton, clearTrailsButton;
	private JButton randomizeButton;
	private JRadioButton randomPointsRadio, randomLocationsRadio;
	private JSpinner KSpinner;
	private File theFile;
	private JTextArea theDescriptionArea;
	
	public KMeans_Frame()
	{
		super("K Means");
		setSize(1000,800);
		setResizable(true);
		setupLayout();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		theFile = new File(System.getProperty("user.home")+"/Documents");
	}
	
	/**
	 * builds the UI.
	 */
	public void setupLayout()
	{
		getContentPane().setLayout(new BorderLayout());
		
		mainPanel = new KMeansPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		JPanel southPanel = buildSouthPanel();
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		JPanel westPanel = buildWestPanel();
		getContentPane().add(westPanel, BorderLayout.WEST);
	}
	
	/**
	 * assembles the Panel in the south of the screen, creating UI elements and linking them
	 * to this window as the ActionListener and ChangeListener.
	 * @return the south panel.
	 */
	public JPanel buildSouthPanel()
	{
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new FlowLayout());
		Box sPanelLayout = Box.createHorizontalBox();
		
		Box loadNClearPanel = Box.createVerticalBox();
		loadNClearPanel.setBorder(new TitledBorder(""));
		loadDataButton = new JButton("Load Data");
		loadDataButton.addActionListener(this);
		loadNClearPanel.add(loadDataButton);
		
		clearTrailsButton = new JButton("Clear Trails");
		clearTrailsButton.addActionListener(this);
		loadNClearPanel.add(clearTrailsButton);
		
		clearTextButton = new JButton("Clear Text");
		clearTextButton.addActionListener(this);
		loadNClearPanel.add(clearTextButton);
		sPanel.add(loadNClearPanel);
		
		
		Box KPanel = Box.createHorizontalBox();
		KPanel.setBorder(new TitledBorder("Attractors"));
		JLabel KLabel = new JLabel("# Attractors");
		KPanel.add(KLabel);
		KSpinner = new JSpinner(new SpinnerNumberModel(4,2,10,1)); // start, min, max, step
		KSpinner.addChangeListener(this);
		KPanel.add(KSpinner);
		sPanel.add(KPanel);
		
		Box stepPanel = Box.createHorizontalBox();
		stepPanel.setBorder(new TitledBorder("Stepping"));
		Box halfStepsPanel = Box.createVerticalBox();
		findClosestButton = new JButton("1/2 step: Find Closest     ");
		reAverageButton   = new JButton("1/2 step: Recalculate means");
		findClosestButton.addActionListener(this);
		reAverageButton.addActionListener(this);
		halfStepsPanel.add(findClosestButton);
		halfStepsPanel.add(reAverageButton);
		stepPanel.add(halfStepsPanel);
		stepButton = new JButton("Step");
		stepButton.addActionListener(this);
		stepPanel.add(stepButton);
		sPanel.add(stepPanel);
		
		Box randomizePanel = Box.createVerticalBox();
		randomizePanel.setBorder(new TitledBorder("Randomize"));
		randomizeButton = new JButton("Randomize Attractors");
		randomizeButton.addActionListener(this);
		randomizePanel.add(randomizeButton);
		randomPointsRadio = new JRadioButton("pick from data");
		randomLocationsRadio = new JRadioButton("pick from any points");
		ButtonGroup randomBG = new ButtonGroup();
		randomBG.add(randomPointsRadio);
		randomBG.add(randomLocationsRadio);
		randomizePanel.add(randomPointsRadio);
		randomizePanel.add(randomLocationsRadio);
		randomLocationsRadio.setSelected(true);
		sPanel.add(randomizePanel);
		
		
		
		sPanel.add(sPanelLayout);
		return sPanel;
	}
	/**
	 * builds the "west" panel, which consists of a text area in a scroll panel.
	 * @return the west panel.
	 */
	public JPanel buildWestPanel()
	{
		theDescriptionArea = new JTextArea();
		theDescriptionArea.setText("");
		theDescriptionArea.setEditable(false);
		mainPanel.setDescriptionField(theDescriptionArea);
		JScrollPane scroller = new JScrollPane(theDescriptionArea);
		
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		result.add(Box.createHorizontalStrut(400),BorderLayout.NORTH);
		result.add(scroller,BorderLayout.CENTER);
		
		return result;
	}
	
	/**
	 * displays the "open file" dialog and (if the user doesn't cancel) sends this file location
	 * to the mainPanel to have it load the data.
	 */
	public void handleLoadData()
	{
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(theFile);
		int result = fc.showOpenDialog(this);
		if (result != JFileChooser.CANCEL_OPTION)
		{
			theFile = fc.getSelectedFile();
			mainPanel.loadFile(theFile);
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent actEvt)
	{
	     	if (actEvt.getSource() == loadDataButton)
	     		handleLoadData();
	     	if (actEvt.getSource() == findClosestButton)
	     		mainPanel.findClosest();
	     	if (actEvt.getSource() == reAverageButton)
	     		mainPanel.reAverage();
	     	if (actEvt.getSource() == stepButton)
	     	{
	     		mainPanel.findClosest();
	     		mainPanel.reAverage();
	     	}
	     	if (actEvt.getSource() == clearTrailsButton)
	     		mainPanel.clearTrails();
	     	if (actEvt.getSource() == clearTextButton)
	     		theDescriptionArea.setText("");
	     	if (actEvt.getSource() == randomizeButton)
	     		mainPanel.randomize(randomLocationsRadio.isSelected());
	
	}

	@Override
	public void stateChanged(ChangeEvent chgEvt)
	{
		mainPanel.setK(((SpinnerNumberModel)KSpinner.getModel()).getNumber().intValue());
	}
}
