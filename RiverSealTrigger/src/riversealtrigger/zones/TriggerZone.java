package riversealtrigger.zones;

import java.awt.Graphics;
import java.io.Serializable;

import PamView.GeneralProjector;
import riversealtrigger.RiverRegionThresholds;

/*
 * Wrapper around RiverZone objects to add additional trigger parameters
 * such as immediate triggering, or triggering on track end. 
 */
public class TriggerZone implements Serializable, RiverZone {

	private static final long serialVersionUID = 1L;

	private RiverZone riverZone;
	
	private RiverRegionThresholds riverRegionThresholds;
	
	public TriggerZone(RiverZone riverZone, RiverRegionThresholds riverRegionThresholds) {
		this.riverZone = riverZone;
		this.setRiverRegionThresholds(riverRegionThresholds);
	}

	@Override
	public String getName() {
		return riverZone.getName();
	}

	@Override
	public boolean contains(double x, double y) {
		return riverZone.contains(x, y);
	}

	@Override
	public void swingDraw(Graphics g, GeneralProjector projector) {
		riverZone.swingDraw(g, projector);
	}

	public RiverRegionThresholds getRiverRegionThresholds() {
		if (riverRegionThresholds == null) {
			riverRegionThresholds = new RiverRegionThresholds();
		}
		return riverRegionThresholds;
	}

	public void setRiverRegionThresholds(RiverRegionThresholds riverRegionThresholds) {
		this.riverRegionThresholds = riverRegionThresholds;
	}

	@Override
	public final String getShape() {
		return riverZone.getShape();
	}

	/**
	 * @param riverZone the riverZone to set
	 */
	public void setRiverZone(RiverZone riverZone) {
		this.riverZone = riverZone;
	}

	@Override
	public void setName(String name) {
		riverZone.setName(name);
	}

	@Override
	public String validate() {
		return riverZone.validate();
	}

	/**
	 * @return the riverZone
	 */
	public RiverZone getRiverZone() {
		return riverZone;
	}
}
