package riversealtrigger.zones;

import java.awt.Graphics;

import PamView.GeneralProjector;

/**
 * Spatial zone. Start with an arbitrary interface, then make something
 * sensible for rectangles and polygons. 
 */
public interface RiverZone {

	/**
	 * GEt the name of the zone (e.g. 'Far Bank', 'Mid river', etc.
	 * @return
	 */
	public String getName();
	
	/**
	 * Set the name
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Get if the zone contains the point x,y
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(double x, double y);
	
	public void swingDraw(Graphics g, GeneralProjector projector);
	
	/**
	 * Return a generic shape name, e.g. rectangle, square, circle, etc. 
	 * @return
	 */
	public String getShape();
	
	/**
	 * Validate the zone, e.g. check it has enough points and a non zero area, etc. 
	 * @return null if it's OK, or an informative String error if it isn't. 
	 */
	public String validate();
	
}
