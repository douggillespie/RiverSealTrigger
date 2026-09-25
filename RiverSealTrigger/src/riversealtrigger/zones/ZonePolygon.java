package riversealtrigger.zones;

import java.awt.Graphics;
import java.io.Serializable;

import PamView.GeneralProjector;
import javafx.collections.ObservableList;
import javafx.scene.shape.Polygon;

public class ZonePolygon implements RiverZone, Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private double[] xPoints;
	
	private double[] yPoints;

	private transient Polygon polygon; // not Serializable
	
	public ZonePolygon(String name, double[] xPoints, double[] yPoints) {
		this.name = name;
		setPoints(xPoints, yPoints);
	}
	
	public ZonePolygon(String name) {
		this.name = name;
		this.xPoints = new double[0];
		this.yPoints = new double[0];
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setPoints(double[] xPoints, double[] yPoints) {
		this.xPoints = xPoints;
		this.yPoints = yPoints;
		getPolygon();
	}
	
	private Polygon getPolygon() {
		if (polygon == null) {
			double[] interleaved = new double[xPoints.length * 2];
			for (int i = 0, j = 0; i < xPoints.length; i++, j+=2) {
				interleaved[j] = xPoints[i];
				interleaved[j+1] = yPoints[i];
			}
			polygon = new Polygon(interleaved);
		}
		return polygon;
	}

	@Override
	public boolean contains(double x, double y) {
		return getPolygon().contains(x, y);
	}

	@Override
	public void swingDraw(Graphics g, GeneralProjector projector) {
		// TODO Auto-generated method stub

	}

	@Override
	public final String getShape() {
		return "Polygon";
	}

	@Override
	public ZonePolygon clone() {
		try {
			ZonePolygon newZ = (ZonePolygon) super.clone();
			newZ.xPoints = xPoints.clone();
			newZ.yPoints = yPoints.clone();
			return newZ;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the xPoints
	 */
	public double[] getxPoints() {
		return xPoints;
	}

	/**
	 * @return the yPoints
	 */
	public double[] getyPoints() {
		return yPoints;
	}
	
	public int getNPoints() {
		return xPoints.length;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String validate() {
		if (xPoints == null || yPoints == null) {
			return "No polygon points have been defined";
		}
		if (xPoints.length < 3) {
			return "A polygon must have at least three points";
		}
		Polygon p = getPolygon();
		double a = calculateArea(p);
		if (a <= 0) {
			return "The polygon has zero area";
		}
		return null;
	}
	
	/**
	 * Get the polugon area
	 * @param polygon
	 * @return
	 */
	private double calculateArea(Polygon polygon) {
	    ObservableList<Double> points = polygon.getPoints();
	    int n = points.size() / 2; // Number of vertices
	    if (n < 3) return 0.0; // A polygon needs at least 3 points

	    double area = 0.0;
	    for (int i = 0; i < n; i++) {
	        double x1 = points.get(2 * i);
	        double y1 = points.get(2 * i + 1);
	        // Next vertex (wrap around to 0 at the end)
	        double x2 = points.get(2 * ((i + 1) % n));
	        double y2 = points.get(2 * ((i + 1) % n) + 1);

	        area += (x1 * y2) - (x2 * y1);
	    }
	    return Math.abs(area) / 2.0;
	}

}
