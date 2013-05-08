import org.apache.commons.math.geometry.Vector3D;

/**
 * Keeps track of the location of the agent's location on the plane
 * and converts pendaphone coordinates to Geogebra coordinates for 
 * Geogebra application
 * 
 * @author elissa
 */

public class TeachableAgent {
	
	private Vector3D location;	//agent's location in terms of pendaphone coordinates
	private double xUnitScale;
	private double yUnitScale;
	public Vector3D origin;
	private Vector3D graphCoorLocation;	//agent's location in terms of graph coordinates
	
	/**
	 * Constructor
	 */
	public TeachableAgent(Vector3D locationOnGraph, double xUnit, double yUnit, Vector3D o)
	{
		location = locationOnGraph;
		xUnitScale = xUnit;
		yUnitScale = yUnit;
		origin = o;
	}
	
	public Vector3D getLocation()
	{
		return location;
	}
	
	public void setLocation( Vector3D newLocation )
	{
		// convert new pendaphone location to graph coordinates
		graphCoorLocation = new Vector3D(newLocation.getY() / xUnitScale, 
				(newLocation.getZ()-origin.getZ()) / yUnitScale, 0);
		//System.out.println(graphCoorLocation.getX() + " : " + graphCoorLocation.getY());
		location = graphCoorLocation;
	}

	public Vector3D getGraphCoorLocation() {
		return graphCoorLocation;
	}
}
