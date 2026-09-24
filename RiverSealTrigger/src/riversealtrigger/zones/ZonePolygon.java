package riversealtrigger.zones;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.Arrays;

import PamView.GeneralProjector;
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

}
