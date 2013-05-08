import java.awt.event.ActionEvent; 
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.Timer;
import java.awt.event.ActionListener;

//This class gets the data from joy.py to send to the other classes
//It listens to actions from the pendaphone

public class MovingBallListener implements ActionListener {
	private Timer timer;
	private static final int DELAY = 0;
	private DatagramSocket dsocket;
	private DatagramPacket packet;
	private byte[] buffer;
	private int port = 21567;
	
	private PendaphoneGestures pendaphoneGesture;
	
	public MovingBallListener(PendaphoneGestures pendaphoneGesture)
	{
		this.pendaphoneGesture = pendaphoneGesture;
	}
	
	public boolean initConnection() {
		try {			
			dsocket = new DatagramSocket(port);
			buffer = new byte[1024];
			packet = new DatagramPacket(buffer, buffer.length);
			
			timer = new Timer(DELAY, this);
			timer.start();
		} catch(Exception e) {;
			System.err.println(e);
			return false;
		}	// end of try-catch
		
		return true;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		try {
			double[] pendaCoor = new double[6];
			dsocket.receive(packet);
			String msg = new String(buffer, 0, packet.getLength());
			String[] tokens = msg.split(",");
			for(int i = 0; i < 6; i++)
				pendaCoor[i] = Double.parseDouble(tokens[i]);
			pendaphoneGesture.updateLocation(pendaCoor);

			packet.setLength(buffer.length);
			
			
		} catch(Exception ex) {
			System.err.println(ex);
		}
	}
}
