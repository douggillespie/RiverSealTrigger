package riversealtrigger;

import java.util.ArrayList;

import PamDetection.PamDetection;
import PamUtils.PamCalendar;
import PamUtils.PamUtils;
import PamUtils.time.CalendarControl;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import tritechgemini.detect.DetectedRegion;
import tritechplugins.detect.track.TrackChain;
import tritechplugins.detect.track.TrackLinkDataUnit;

public class RiverTriggerDataUnit extends PamDataUnit<PamDataUnit, PamDataUnit> implements PamDetection {

	private double triggerX, triggerY;
	private double endX, endY; // end points of trigger when it extends over time. 
	private ArrayList<TrackLinkDataUnit> trackDataUnits;
	private long triggerEnd;
	private DataUnitBaseData basicData;
	private ArrayList<Long> trackUIDs = new ArrayList();

	public double getTriggerX() {
		return triggerX;
	}

	public double getTriggerY() {
		return triggerY;
	}

	public ArrayList<TrackLinkDataUnit> getTrackDataUnits() {
		return trackDataUnits;
	}

	public RiverTriggerDataUnit(long timeMilliseconds, double x, double y, TrackLinkDataUnit trackUnit) {
		super(timeMilliseconds);
		this.triggerX = endX = x;
		this.triggerY = endY = y;
		trackDataUnits = new ArrayList<>();
		triggerEnd = timeMilliseconds;
		if (trackUnit != null) {
			trackDataUnits.add(trackUnit);
			trackUIDs.add(trackUnit.getUID());
		}
	}

	public long getTriggerEnd() {
		return triggerEnd;
	}

	public void setTriggerEnd(long triggerEnd) {
		this.triggerEnd = triggerEnd;
	}

	public void setTriggerEnd(long endTime, double endX, double endY, TrackLinkDataUnit aotherUnit) {
		this.triggerEnd = endTime;
		this.endX = endX;
		this.endY = endY;
		if (aotherUnit != null) {
			long uid = aotherUnit.getUID();
			if (trackUIDs.contains(uid) == false) {
				trackUIDs.add(uid);
			}
			if (trackDataUnits.contains(aotherUnit) == false) {
				trackDataUnits.add(aotherUnit);
			}
		}
	}



	public String getSummaryString(int startEnd) {	
		PamDataBlock parentDataBlock = getParentDataBlock();
		basicData = getBasicData();
		String str = "<html>";
		str += "UID: " + getUID() + "<p>";
		if (parentDataBlock != null) {
			str += "<i>" + parentDataBlock.getLongDataName() + "</i><p>";
		}
		if (startEnd == 2) {
			str += "Trigger End Point<p>";
		}
		else {
			str += "Trigger Start Point<p>";
		}
		str += "Track UIDs " + getTrackUIDList() + "<p>";
		str += "Trigger Start Time: ";
		//	str += PamCalendar.formatDateTime(timeMilliseconds) + "<p>";
		str += String.format("%s %s %s<p>", PamCalendar.formatDate(basicData.getTimeMilliseconds(), true),
				PamCalendar.formatTime(basicData.getTimeMilliseconds(), 3, true),
				CalendarControl.getInstance().getTZCode(true));
		if (CalendarControl.getInstance().isUTC() == false) {
			str += String.format("%s %s %s<p>", PamCalendar.formatDate(basicData.getTimeMilliseconds(), false),
					PamCalendar.formatTime(basicData.getTimeMilliseconds(), 3, false),
					"UTC");
		}
		str += "Trigger End Time: ";
		long end = getTriggerEnd();
		str += String.format("%s %s %s<p>", PamCalendar.formatDate(getTriggerEnd(), true),
				PamCalendar.formatTime(getTriggerEnd(), 3, true),
				CalendarControl.getInstance().getTZCode(true));
		str += String.format("Trigger duration %3.1f seconds<p>", (getTriggerEnd()-getTimeMilliseconds())/1000.);
		if (getDatabaseIndex() > 0) {
			str += "Database Index : " + getDatabaseIndex() + "<p>";
		}
		return str;
	}

	/**
	 * @return the endX
	 */
	public double getEndX() {
		return endX;
	}

	/**
	 * @return the endY
	 */
	public double getEndY() {
		return endY;
	}

	/**
	 * @return the trackUIDs
	 */
	public ArrayList<Long> getTrackUIDs() {
		return trackUIDs;
	}

	/**
	 * Get list of uids as a string. 
	 * @return
	 */
	public String getTrackUIDList() {
		if (trackUIDs.size() == 0) {
			return null;
		}
		String str = ""+trackUIDs.get(0);
		for (int i = 1; i < trackUIDs.size(); i++) {
			str += "," + trackUIDs.get(i);
		}
		return str;
	}

	public void setTrackUIDList(String uidList) {
		trackUIDs.clear();
		if (uidList == null) {
			return;
		}
		String[] bits = uidList.split(",");
		for (int i = 0; i < bits.length; i++) {
			try {
				Long val = Long.valueOf(bits[i]);
				if (val != null) {
					trackUIDs.add(val);
				}
			}
			catch (NumberFormatException e) {
			}
		}
	}
	
	/**
	 * Does this track use this sonar ? 
	 * @param sonarId
	 * @return
	 */
	public boolean usesSonar(int sonarId) {
		if (trackDataUnits == null) {
			return false;
		}
		for (TrackLinkDataUnit tdu : trackDataUnits) {
			TrackChain trackChain = tdu.getTrackChain();
			for (DetectedRegion r : trackChain.getRegions()) {
				if (r.getSonarId() == sonarId) {
					return true;
				}
			}
		}
		return false;
	}


}
