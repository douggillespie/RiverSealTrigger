package riversealtrigger.swing.zones;

import javax.swing.JComponent;

import riversealtrigger.zones.RiverZone;

public interface ZoneDialogPanel<T extends RiverZone> {

	public void setZone(T zone);
	
	public T getZone();
	
	public JComponent getPanel();
	
}
