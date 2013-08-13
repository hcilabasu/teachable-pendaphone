import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.apache.commons.math.geometry.Vector3D;

public class PendaphoneGestures {
	Vector3D rightBall;
	Vector3D leftBall;
	Vector3D firstLocation;

	Robot robot; // Generates native system events for controlling inputs
					// (controlling the mouse)
	DisplayPlane plane;

	public final static int CALIBRATION = 0;
	public final static int INTERACTION = 1;
	long time = 0;

	public static int mode = CALIBRATION; // Initially set

	boolean pressed = false;

	public PendaphoneGestures() {

		// Instantiating right and left ball with default vectors
		rightBall = new Vector3D(0, 0, 1);
		leftBall = new Vector3D(0, 0, 1);

		MovingBallListener mbl = new MovingBallListener(this);
		mbl.initConnection();

		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setFL(Vector3D fl) {
		firstLocation = fl;
	}

	/**
	 * For RightBall -
	 * 		Updates current pendaphone coordinates based on user movement.
	 * 		Determines if cursor movements or clicks should be performed.
	 * 
	 * For LeftBall -
	 * 		Updates current position of robot needed by Geogebra application
	 */
	public void updateLocation(double[] tokens) throws InterruptedException {
		// have to reverse the direction of the x
		rightBall = new Vector3D(1 - tokens[2], new Vector3D(
				toRadians(-tokens[0]), toRadians(-tokens[1])));
																
		leftBall = new Vector3D(1 - tokens[5], new Vector3D(
				toRadians(-tokens[3]), toRadians(-tokens[4])));

		//If mouse below move threshold and above click threshold, 
		//adjust cursor position according to user movement
		if (mode == INTERACTION
				&& plane.onPlane(rightBall) < CalibrationPanel.move_buffer
				&& plane.onPlane(rightBall) > CalibrationPanel.click_buffer ) 
		{
			Vector3D projection = plane.projectToPlane(rightBall);
			robot.mouseMove((int) plane.translatePlaneX(projection),
					(int) plane.translatePlaneY(projection));
		}
		
		// If mouse below click threshold, handle click
		// (To disable clicking, comment out the following block of code)
		if (mode == INTERACTION && pressed == false
				&& plane.onPlane(rightBall) < CalibrationPanel.click_buffer
				&& (System.currentTimeMillis() > (1000 + time) 
						)) {
			System.out.println("MOUSEPRESS " + plane.onPlane(rightBall));
			robot.mousePress(InputEvent.BUTTON1_MASK);
			pressed = true;
			time = System.currentTimeMillis();
			Thread.sleep(10);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			System.out.println("Mouse has been released");
			pressed = false;
		} // end of clicking code

		/*
		 * else if (mode == INTERACTION && pressed == true &&
		 * plane.onPlane(rightBall) < CalibrationPanel.click_buffer) {
		 * System.out.println("MOUSERELEASED");
		 * robot.mouseRelease(InputEvent.BUTTON1_MASK); pressed = false; }
		 */
		
		//update the location of the teachable agent for Geogebra to access
		plane.agent.setLocation( leftBall );
	}
	
	public Vector3D getLeftLocation() {
		return leftBall;
	}

	public Vector3D getRightLocation() {
		return rightBall;
	}

	public void setDisplayPlane(DisplayPlane dp) {
		this.plane = dp;
		mode = INTERACTION;
	}

	public double toRadians(double angle) {
		return angle * Math.PI / 3;
	}
}
