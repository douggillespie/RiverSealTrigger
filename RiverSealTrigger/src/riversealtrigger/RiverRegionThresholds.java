package riversealtrigger;

import java.io.Serializable;

public class RiverRegionThresholds implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Trigger if the track ends in this zone
	 */
	public static final int TRIGGER_ONEND = 1;
	/**
	 * Trigger immediately if the track enters this zone
	 */
	public static final int TRIGGER_IMMEDIATE = 0;
	
	/**
	 * Min link score to even consider a track. 
	 */
	public double minLinkScore  = 0.5;
	
	/**
	 * Min rise in radial coordinate. 
	 */
	public double minRSize = 0.12; 
	
	/**
	 * Min track length
	 */
	public double minLength = 2.5;
	
	/**
	 * Trigger immediately a track is in this zone.
	 * or only if the track ends in this zone 
	 */
	public int triggerType; 
}