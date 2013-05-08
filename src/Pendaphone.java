/**
 * Receive UDP package from OLPC Python script.
 * Convert relative data to actual x, y, z coordinates.
 * Plot on GUI.
 * 
 * Coded by Wenyang (Derek) Li
 * Jan 2011
 * 
 * 2/1/11:
 * Added graph curve.
 * 
 * In progress:
 * Data information page.
 * Add port selection to GUI.
 * 
 */

import javax.swing.JApplet;

@SuppressWarnings("serial")
public class Pendaphone extends JApplet {
	private int WIDTH = 1200, HEIGHT = 710;
	
	public void init() {
		GraphPanel panel = new GraphPanel(WIDTH, HEIGHT);
		getContentPane().add(panel);
		setSize(WIDTH, HEIGHT);
	}
}
