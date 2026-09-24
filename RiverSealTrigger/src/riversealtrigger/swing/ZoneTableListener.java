package riversealtrigger.swing;

import riversealtrigger.zones.TriggerZone;

public interface ZoneTableListener {

	/**
	 * A zone has been selected in the table. 
	 * @param triggerZone
	 */
	public void zoneSelected(TriggerZone triggerZone);
	
}
