package riversealtrigger;

import java.io.Serializable;

public class RiverRegionThresholds implements Serializable {
	/**
	 * Min link score to even consider a track. 
	 */
	public double minLinkScore  = 0.5;
	
	/**
	 * Min rise in radial coordinate. 
	 */
	public double minRSize = 0.12; 
	
	public double minLength = 2.5;
}