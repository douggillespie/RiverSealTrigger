package riversealtrigger.logging;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;
import riversealtrigger.RiverTriggerDataBlock;
import riversealtrigger.RiverTriggerDataUnit;
import tritechplugins.detect.track.TrackLinkDataUnit;

public class RiverTriggerLogging extends SQLLogging {
	
	private PamTableItem nTracks, trackStart, triggerEnd, triggerX, triggerY, endX, endY, trackUIDs;

	public RiverTriggerLogging(RiverTriggerDataBlock pamDataBlock) {
		super(pamDataBlock);
		
		PamTableDefinition table = new PamTableDefinition(pamDataBlock.getDataName());
		table.addTableItem(triggerEnd = new PamTableItem("Trigger End", Types.TIMESTAMP));
		table.addTableItem(trackStart = new PamTableItem("Track Start", Types.TIMESTAMP));
		table.addTableItem(triggerX = new PamTableItem("Trigger X", Types.REAL));
		table.addTableItem(triggerY = new PamTableItem("Trigger Y", Types.REAL));
		table.addTableItem(endX = new PamTableItem("End X", Types.REAL));
		table.addTableItem(endY = new PamTableItem("End Y", Types.REAL));
		table.addTableItem(trackUIDs = new PamTableItem("Track UIDs", Types.VARCHAR));
		
		
		setTableDefinition(table);
	}

	
	
	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		RiverTriggerDataUnit rtDU = (RiverTriggerDataUnit) pamDataUnit;
		triggerX.setValue(rtDU.getTriggerX());
		triggerY.setValue(rtDU.getTriggerY());
		endX.setValue(rtDU.getEndX());
		endY.setValue(rtDU.getEndY());
		ArrayList<TrackLinkDataUnit> trgDU = rtDU.getTrackDataUnits();
		if (trgDU == null) {
			trackStart.setValue(null);
		}
		else {
			trackStart.setValue(sqlTypes.getTimeStamp(rtDU.getTimeMilliseconds()));
		}
		triggerEnd.setValue(sqlTypes.getTimeStamp(rtDU.getTriggerEnd()));
		trackUIDs.setValue(rtDU.getTrackUIDList());
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		double x = triggerX.getDoubleValue();
		double y = triggerY.getDoubleValue();
		double eX = this.endX.getDoubleValue();
		double eY = this.endY.getDoubleValue();
		String uids = trackUIDs.getDeblankedStringValue();
		long trigTime = timeMilliseconds;
		long trackTime = SQLTypes.millisFromTimeStamp(trackStart.getValue());
		long trigEnd = SQLTypes.millisFromTimeStamp(triggerEnd.getValue());
		/*
		 *  there was a mess up in the time zone written for the trigEnd. Times shold be pretty short, so
		 *  it should be close to the trig start.  Now fixed, so this only applies to a small amount of 2024 data. 
		 */
		while (trigEnd - timeMilliseconds > 180000) {
			trigEnd -= 3600000L;
		}
		RiverTriggerDataUnit rtdu = new RiverTriggerDataUnit(timeMilliseconds, x, y, null);
		rtdu.setTriggerEnd(trigEnd, eX, eY, null);
		rtdu.setTrackUIDList(uids);
		return rtdu;
	}

}
