package riversealtrigger.zones;

import java.util.ArrayList;

/**
 * Interface to add to a datablock that can provide a list of zones. 
 * Primarily intended for use with the Seal River Trigger and TAST firing modules. 
 * But hope to move to PAMGUard core so it could potentially be used by other detectors
 */
public interface ZonedData {

	public ArrayList<TriggerZone> getTriggerZones();
	
}
