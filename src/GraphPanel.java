import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel {
	private PhysicsPanel heatPanel;
	private JPanel controlPanel, joyPanel;
	private JButton connectButton;
	private JLabel t1Info, t2Info, areaInfo, kConstantInfo;
	private JComboBox constantSelector;
	private double kConstant;
	private JTextField T1, T2, A;
	
	private Timer timer;
	private static final int DELAY = 0;
	private DatagramSocket dsocket;
	private DatagramPacket packet;
	private byte[] buffer;
	private int port;
	
	public GraphPanel(int width, int height) {
		// left
		heatPanel = new PhysicsPanel();
		heatPanel.setPreferredSize(new Dimension(710, 710));
		
		// right
		controlPanel = new JPanel(new GridLayout(5, 2));
		
		connectButton = new JButton("Connect!");
		connectButton.addActionListener(new ButtonListener());
		
		kConstantInfo = new JLabel("Select Object");
		String[] kConstant_Value = {"Air (gas) - 0.024", "Alcohol - 0.17", "Aluminum - 250",
									"Ammonia (gas) - 0.022", "Brass - 109", "Carbon - 1.7",
									"Carbon dioxide (gas) - 0.0146", "Copper - 401", "Cork - 0.07",
									"Cotton - 0.03", "Gasoline - 0.15", "Glass - 1.05",
									"Gold - 310", "Cotton - 0.03", "Hydrogen (gas) - 0.168",
									"Iron - 80", "Lead Pb - 35", "Mercury - 8",
									"Nickel - 91", "Nitrogen - 0.024", "Oxygen - 0.024",
									"Water - 0.58", "Wood, oak - 0.17"};
		
		constantSelector = new JComboBox(kConstant_Value);
		constantSelector.addActionListener(new ComboBoxListener());
		
		t1Info = new JLabel("Enter temperature 1:");
		t2Info = new JLabel("Enter temperature 2:");
		T1 = new JTextField("0");
		T2 = new JTextField("100");
		
		areaInfo = new JLabel("Enter cross section area:");
		A = new JTextField("5");
		
		controlPanel.add(connectButton);
		controlPanel.add(new JLabel());
		controlPanel.add(kConstantInfo);
		controlPanel.add(constantSelector);
		controlPanel.add(t1Info);
		controlPanel.add(T1);
		controlPanel.add(t2Info);
		controlPanel.add(T2);
		controlPanel.add(areaInfo);
		controlPanel.add(A);
		
		// joyPanel is the entire panel
		joyPanel = new JPanel(new FlowLayout());
		joyPanel.add(heatPanel);
		joyPanel.add(controlPanel);
		
		add(joyPanel);
		setSize(new Dimension(width,height));
	}	// end of ControlPanel
	
	private boolean initConnection() {
		try {			
			dsocket = new DatagramSocket(port);
			buffer = new byte[1024];
			packet = new DatagramPacket(buffer, buffer.length);
			
			timer = new Timer(DELAY, new MovingBallListener());
			timer.start();
		} catch(Exception e) {;
			System.err.println(e);
			return false;
		}	// end of try-catch
		
		return true;
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object action = event.getSource();
			
			if(action.equals(connectButton)) {
				boolean connected = false;
				
				//do {
					port = 21567;
					connected = initConnection();
					
					if(!connected) {
						JOptionPane.showMessageDialog(null, "Can't connect to Pendaphone, please check connection and try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
					}
				//} while(!connected);
			}
		}
	}	// end of ButtonListener
	
	private class ComboBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox comboBox = (JComboBox)e.getSource();
	        kConstant = Double.parseDouble((((String)comboBox.getSelectedItem()).split(" - "))[1]);
	        heatPanel.setKConstant(kConstant);
	    }
	}
	
	private class MovingBallListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				double[] pendaCoor = new double[6];
				dsocket.receive(packet);
				String msg = new String(buffer, 0, packet.getLength());
				String[] tokens = msg.split(",");
				
				for(int i = 0; i < 6; i++)
					pendaCoor[i] = Double.parseDouble(tokens[i]);

				packet.setLength(buffer.length);
				
				heatPanel.setAxis(pendaCoor[0], pendaCoor[1], pendaCoor[2], pendaCoor[3], pendaCoor[4], pendaCoor[5]);
				heatPanel.setTemp(Double.parseDouble(T1.getText()), Double.parseDouble(T2.getText()));
				heatPanel.setArea(Double.parseDouble(A.getText()));
			} catch(Exception ex) {
				System.err.println(ex);
			}
		}
	}	// end of MovingBallListener
}	// end of class ControlPanel
