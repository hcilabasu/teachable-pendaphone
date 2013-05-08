/*
 * Z-axis of the ball is represented by the size of the object.
 */

import java.awt.*;
import javax.swing.*;
import java.lang.Math;

@SuppressWarnings("serial")
public class PhysicsPanel extends JPanel {
	private int circleDiameterOne, circleDiameterTwo;
	private int x1, y1, x2, y2;
	private double z1, z2;
	private double distance, kConstant, t1, t2, area;
	private String qOvert;
	
	public PhysicsPanel() {
		setAxis(0.2, 0.2, 0.5, 0.5, 0.5, 1);
		
		setKConstant(10);
		setArea(10);
		setTemp(0, 100);
		
		setBackground(Color.BLACK);
	}
	
	public PhysicsPanel(int ballOneX, int ballOneY, int ballOneZ, int ballTwoX, int ballTwoY, int ballTwoZ) {
		setAxis(ballOneX, ballOneY, ballOneZ, ballTwoX, ballTwoY, ballTwoZ);

		setKConstant(0);
		setArea(0);
		setTemp(0, 0);
		
		setBackground(Color.BLACK);
	}
	
	public PhysicsPanel(int ballOneX, int ballOneY, int ballOneZ, int ballTwoX, int ballTwoY, int ballTwoZ, double newKConstant, double newArea, double tempOne, double tempTwo) {
		setAxis(ballOneX, ballOneY, ballOneZ, ballTwoX, ballTwoY, ballTwoZ);
		
		setKConstant(newKConstant);
		setArea(newArea);
		setTemp(tempOne, tempTwo);
		
		setBackground(Color.BLACK);
	}
	
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		
		circleDiameterOne = treatLength(z1);
		circleDiameterTwo = treatLength(z2);
		
		System.out.println(circleDiameterOne + " " + circleDiameterTwo);
		
		page.setColor(Color.YELLOW);
		page.drawLine(x1 + circleDiameterOne / 2, y1 + circleDiameterOne / 2,
						x2 + circleDiameterOne / 2, y2 + circleDiameterOne / 2);
		page.drawString("Heat rate of change: " + qOvert + "Watts", 0, 650);

		page.setColor(Color.WHITE);
		page.drawString("Distance between T1 and T2: " + distance + "Meters", 0, 575);
		
		page.setColor(Color.CYAN);
		page.fillOval(x1, y1, circleDiameterOne, circleDiameterOne);
		page.drawString("Temp. 1 (T1): " + t1 + "K", 0, 600);
		
		page.setColor(Color.RED);
		page.fillOval(x2, y2, circleDiameterTwo, circleDiameterTwo);
		page.drawString("Temp. 2 (T2): " + t2 + "K", 0, 625);
		
		/*
		page.setColor(Color.WHITE);
		page.drawString(qOvert, (x1 + x2 + (circleDiameterOne + circleDiameterOne) / 2) / 2, (y1 + y2 + (circleDiameterOne + circleDiameterOne) / 2) / 2);
		*/
	}
	 
	public void setAxis(double ballOneX, double ballOneY, double ballOneZ, double ballTwoX, double ballTwoY, double ballTwoZ) {
		x1 = (int)(ballOneZ * 700 * Math.cos(treatAngle(ballOneY)) * Math.sin(treatAngle(ballOneX))) + 350;
		y1 = (int)(ballOneZ * 700 * Math.cos(treatAngle(ballOneX)) * Math.sin(treatAngle(ballOneY))) + 350;
		z1 = ballOneZ * Math.abs(Math.cos(treatAngle(ballOneX))) * Math.abs(treatAngle(Math.cos(ballOneY)));
		
		System.out.println(treatAngle(ballOneX) + " " + treatAngle(ballOneY));
		System.out.println(x1 + " " + y1 + " " + z1);
		
		x2 = (int)(ballTwoZ * 700 * Math.cos(treatAngle(ballTwoY)) * Math.sin(treatAngle(ballTwoX))) + 350;
		y2 = (int)(ballTwoZ * 700 * Math.cos(treatAngle(ballTwoX)) * Math.sin(treatAngle(ballTwoY))) + 350;
		z2 = ballTwoZ * Math.abs(Math.cos(treatAngle(ballTwoX))) * Math.abs(Math.cos(treatAngle(ballTwoY)));
		
		distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) + Math.pow((z2 - z1), 2));
			
		updateUI();
	 }
	
	public void setTemp(double tempOne, double tempTwo) {
		t1 = tempOne;
		t2 = tempTwo;
		
		// Q/t = k*A*(T1-T2)/L
		qOvert = "Q over t = " + (kConstant * area * (t1 - t2) / distance);
	}
	
	public void setKConstant(double newK) {
		kConstant = newK;
	}
	
	public void setArea(double newArea) {
		area = newArea;
	}

	private double treatAngle(double pendaAngle) {
		// Y = (X-A)/(B-A) * (D-C) + C
		// Re-map value [-1, 1] to [-30, 30]
		return ((constrainPendaAngle(pendaAngle) + 1) / 2 * 60 - 30) * Math.PI / 180;
	}
	
	private int treatLength(double pendaZ) {
		// Y = (X-A)/(B-A) * (D-C) + C
		// Re-map value [0, 1] to [1, 5]
		return (int)(pendaZ * 20 + 10);
	}
	
	private double constrainPendaAngle(double pendaAngle) {
		if(pendaAngle < -1)
			return -1;
		else if(pendaAngle >1)
			return 1;
		else
			return pendaAngle;
	}
}
