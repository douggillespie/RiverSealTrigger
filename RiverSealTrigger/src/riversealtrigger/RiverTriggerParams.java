package riversealtrigger;

import java.io.Serializable;
import java.util.HashMap;

public class RiverTriggerParams implements Cloneable, Serializable {

	public static final long serialVersionUID = 1L;
	
	public static final int RIVER_BANK = 0;
	public static final int RIVER_MIDDLE = 1;
	
	public static final String[] regionNames = {"Near bank", "Mid stream"};
	
	/**
	 * Name of incoming tracks data source. 
	 */
	public String dataSourceName;

	/**
	 * Flow direction in degrees from straight up
	 */
	public double flowDirection = 54;
	
	/**
	 * Min link score to even consider a track. 
	 */
	private double minLinkScore  = 0.5;
	
	/**
	 * Min rise in radial coordinate. 
	 */
	private double minRSize = 0.12; 
	
	private double minLength = 2.5;
	
	/**
	 * Multiple thresholds for different regions. 
	 */
	private HashMap<Integer, RiverRegionThresholds> regionThresholds;
	
	public RiverRegionThresholds getRegionThreshold(int riverRegion) {
		if (regionThresholds == null) {
			regionThresholds = new HashMap<>();
		}
		RiverRegionThresholds rt = regionThresholds.get(riverRegion);
		if (rt == null) {
			rt = new RiverRegionThresholds();
			rt.minLength = this.minLength;
			rt.minLinkScore = this.minLinkScore;
			rt.minRSize = this.minRSize;
			regionThresholds.put(riverRegion, rt);
		}
		return rt;
	}
	
	public void setRegionThresholds(int riverRegion, RiverRegionThresholds regionThreshold) {
		if (regionThresholds == null) {
			regionThresholds = new HashMap<>();
		}
		regionThresholds.put(riverRegion, regionThreshold);
	}
	
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
	
	/**
	 * Minimum angle to the flow. 
	 */
	public double minUpstreamDirection = 90;
	
	private double[] ignorePoint = {13., 36.};
	
	private double[] triggerPoint = {-16., 19.}; 
	
	/**
	 * Range of distances that count as mid river Outside this range is bank. 
	 */
	private double[] midRiverRange = {0., 50.};
	
	/**
	 * Tast treatment duration (seconds)
	 */
//	public double tastDuration = 30;

	@Override
	protected RiverTriggerParams clone() {
		try {
			return (RiverTriggerParams) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Two element vector for x,y coordinate.<p>
	 * point on map where tracks will be ignored if they only have points
	 * downstream of that point according to the flow direction.
	 * @return the ignorePoint
	 */
	public double[] getIgnorePoint() {
		if (triggerPoint == null) {
			RiverTriggerParams dum = new RiverTriggerParams();
			ignorePoint = dum.ignorePoint;
		}
		return ignorePoint;
	}

	/**
	 * Two element vector for x,y coordinate.<p>
	 * point on map where tracks will be ignored if they only have points
	 * downstream of that point according to the flow direction.  
	 * @param ignorePoint the ignorePoint to set
	 */
	public void setIgnorePoint(double[] ignorePoint) {
		this.ignorePoint = ignorePoint;
	}

	/**
	 * Two element vector for x,y coordinate.<p>
	 * point on map where tracks will trigger anyway whether they are
	 * complete or incomplete if they are upstream of this point. 
	 * @return the triggerPoint
	 */
	public double[] getTriggerPoint() {
		if (triggerPoint == null) {
			RiverTriggerParams dum = new RiverTriggerParams();
			triggerPoint = dum.triggerPoint;
		}
		return triggerPoint;
	}

	/**
	 * Two element vector for x,y coordinate.<p>
	 * point on map where tracks will trigger anyway whether they are
	 * complete or incomplete if they are upstream of this point. 
	 * @param triggerPoint the triggerPoint to set
	 */
	public void setTriggerPoint(double[] triggerPoint) {
		this.triggerPoint = triggerPoint;
	}

	/**
	 * @return the midRiverRange
	 */
	public double[] getMidRiverRange() {
		if (midRiverRange == null) {
			// if null set to default. 
			midRiverRange = new RiverTriggerParams().midRiverRange;
		}
		return midRiverRange;
	}

	/**
	 * @param midRiverRange the midRiverRange to set
	 */
	public void setMidRiverRange(double[] midRiverRange) {
		this.midRiverRange = midRiverRange;
	}
}
