import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.math.geometry.Vector3D;

import py4j.GatewayServer;

/** 
 * Begins the Calibration process for the pendaphone.
 *  Currently contains the main class for the whole set up 
 *  (so the name should probably be changed)
 * @author erin
 * 
 * Edited to allow: 
 * 		click & move buffers to be set automatically
 * 		pendaphone to be calibrated to Geogebra plane
 * 		Geogebra coordinates to be converted by TeachableAgent class
 * @author elissa
 *
 */
public class CalibrationPanel extends JPanel
{
	PendaphoneGestures gest;
	
	// dimensions of calibration box
	int height = -1;
	int width = -1;
	int xPos = -1;
	int yPos = -1;
	
	// buffers for pendaphone motion 
	public static double move_buffer = 0.1;	  //initial value for calibration purposes
	public static double click_buffer;
	
	//modes of callibration
	public static final int POSITIONING_MODE = 0;
	public static final int CALIBRATION_1 = 1;
	public static final int CALIBRATION_2 = 2;
	public static final int CALIBRATION_3 = 3;
	
	// messages related to the calibration
	public static final String[] CALIBRATION_MESSAGES = 
		{"Using the Geogebra\nplane resize this\nwindow to be a\nsquare 3 units tall\nby 3 units wide",
		"Pull up the Geogebra\nwindow. Move the\npendaphone to (0, 0)",
		"Pull up the Geogebra\nwindow. Move the\npendaphone to (3, 0).",
		"Pull up the Geogebra\nwindow. Move the\npendaphone to (0, 3)."
		};


	/** 
	 * Constructor. Each calibration panel needs an assorted set of pendaphone gestures. 
	 */
	public CalibrationPanel(PendaphoneGestures pg)
	{
		gest = pg;
	}
	
	//todo - could move text somewhere else
	private JDialog showDialog(int mode)
	{
		  JOptionPane pane = new JOptionPane(CALIBRATION_MESSAGES[mode]);
		  JDialog dialog = pane.createDialog(this, "Calibration");
		  	  
		  // need to get the position of the dialog
		  // bug... sometimes the next dialog shows up in the wrong position
		  if (mode == POSITIONING_MODE)  
		  	  dialog.addComponentListener(new CalibrationListener(dialog, this));
		  else
			  dialog.setModal(false); //can't be modal to read pendaphone position
		  
		  dialog.setSize(175, 170); // this size is aribtrary
		  dialog.setResizable(true);
		  
		  // sets the location to the location of the previous dialog
		  if (xPos >= 0 && yPos >= 0) 
			  dialog.setLocation(xPos, yPos);
		  
		  dialog.setVisible(true);
		  return dialog;
		
	}
	
	/** 
	 * Sets the location of the calibration dialog. 
	 */
	public void setCalibrationCoordinates(int x, int y, int width, int height)
	{
		this.xPos = x;
		this.yPos = y;
		this.width = width;
		this.height = height;
	}
	
	/** 
	 * A listener to read the coordinates of the first calibration dialog. 
	 */
	private class CalibrationListener implements ComponentListener
	{
		JDialog dialog;
		CalibrationPanel panel;
		
		public CalibrationListener(JDialog dialog, CalibrationPanel panel)
		{
			this.dialog = dialog;
			this.panel = panel;
		}
		
		/**
		 * Based on size of first dialog in calibration, sets calibration coordinates
		 */
		public void componentHidden(ComponentEvent e) {
			//only do if height & width not yet set
			if(height == -1)
			{
				panel.setCalibrationCoordinates(dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight());
			}
		}

	    public void componentMoved(ComponentEvent e) {
			
	    }

	    public void componentResized(ComponentEvent e) {
	    }

	    public void componentShown(ComponentEvent e) {
	    }
	}
	
	/** 
	 * Calibrates the Pendaphone coordinates. 
	 */
	public DisplayPlane startCalibration()
	{
		// should wait until it starts receiving data
		//while (gest.getRightLocation().getNorm() >= 1); 
		//	System.out.println(gest.getRightLocation());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Vector3D firstLocation = gest.getRightLocation();  
		gest.setFL(firstLocation);
		showDialog(POSITIONING_MODE);
		
		JDialog dialog = showDialog(CALIBRATION_1);
		Vector3D origin = calibrateEndpoint(1, firstLocation);
		dialog.setVisible(false);
		
		dialog = showDialog(CALIBRATION_2);
		Vector3D agentX = calibrateEndpoint(2, firstLocation);
		dialog.setVisible(false);
		
		dialog = showDialog(CALIBRATION_3);
		Vector3D agentY = calibrateEndpoint(3, firstLocation);
		dialog.setVisible(false);

		//adjust 2nd parameter to account for calibration with rightBall (easier for users)
		TeachableAgent ta = new TeachableAgent(gest.getLeftLocation(), (agentX.getY()-origin.getY())/3, 
				(agentY.getZ()-origin.getZ())/3, origin);
		DisplayPlane dp = new DisplayPlane(agentX, origin, agentY, height, width, xPos, yPos, ta);

		//automatically set move & click buffers
		computeClickBuffer(dp, origin);
		computeMoveBuffer(dp, origin);
		
		return dp;
	}
	
	/** Process to get each calibration point.
	 * 
	 * @param i - the calibration phase, for debugging
	 * @param firstLocation - the initial location of the vector
	 * @return
	 */
	public Vector3D calibrateEndpoint(int i, Vector3D firstLocation)
	{
		Vector3D oldLocation, newLocation;
		//wait until calibrator starts moving pendaphone down
		do
		{
			oldLocation = gest.getRightLocation();
			System.out.println("Phase 1 - " + i + " - " + oldLocation.getNorm() + " < " + firstLocation.getNorm() + " + " + move_buffer);
		}
		while ( oldLocation.getNorm() < firstLocation.getNorm() + move_buffer);
		
		newLocation = gest.getRightLocation();
		// while pendaphone still moving down, not up
		// (don't get position while still pendaphone moving down)
		while (oldLocation.getNorm() <= newLocation.getNorm())
		{
			oldLocation = newLocation;
			newLocation = gest.getRightLocation();
			System.out.println("Phase 2 - " + i + " - " + oldLocation.getNorm() + firstLocation.getNorm());
		}
		//...
		while (oldLocation.getNorm()  > firstLocation.getNorm() + move_buffer)
		{
			oldLocation = gest.getRightLocation();
			System.out.println("Phase 3 - " + i + " - " + oldLocation.getNorm() + " - " + firstLocation.getNorm());
		}
		return newLocation;
	}

	/**
	 * Function to automatically set the click buffer based on calibration points.
	 * Click buffer set to be approximately 75% of distance between pendaphone and floor.
	 *
	 * @param dp - display plane object created after calibration
	 * @param ptOnFloor - any point taken where pendaphone is fully extended to floor
	 */
	public void computeClickBuffer(DisplayPlane dp, Vector3D ptOnFloor)
	{
		click_buffer = dp.onPlane(ptOnFloor) * 1.25;
	}
	
	/**
	 * Function to automatically set the move buffer based on calibration points.
	 * Move buffer set to be approximately 10% of distance between pendaphone and floor.
	 *
	 * @param dp - display plane object created after calibration
	 * @param ptOnFloor - any point taken where pendaphone is fully extended to floor.
	 */
	public void computeMoveBuffer(DisplayPlane dp, Vector3D ptOnFloor)
	{
		move_buffer = dp.onPlane(ptOnFloor) * 1.90;
	}	
	
	/**
	 * Entry point for application
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		PendaphoneGestures pg = new PendaphoneGestures();
		/*while (pg.getRightLocation().getNorm() == 1.0)
			System.out.println(pg.getRightLocation().getNorm());*/
		CalibrationPanel cp = new CalibrationPanel(pg);
		frame.add(cp);
		DisplayPlane dp = cp.startCalibration();
		pg.setDisplayPlane(dp);	
		GatewayServer server = new GatewayServer(dp.agent);
		server.start();
		//System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
	}
	
}
