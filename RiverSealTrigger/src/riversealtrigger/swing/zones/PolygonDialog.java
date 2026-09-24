package riversealtrigger.swing.zones;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JTextField;

import PamView.dialog.PamDialog;
import riversealtrigger.zones.TriggerZone;


public class PolygonDialog extends PamDialog {
	
	private TriggerZone triggerZone;
	private static PolygonDialog singleInstance;
	private ZoneDialogPanel zoneDialogPanel;
	private JTextField name, minLinkScore, minRSize, minTrackLength;

	public PolygonDialog(Window parentFrame) {
		super(parentFrame, "Polygon Editor", true);
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new GridBagLayout());
		JPanel zonePanel = new JPanel();
		zoneDialogPanel = new PolygonTable(this);
		zonePanel.add(zoneDialogPanel.getPanel());
		mainPanel.add(BorderLayout.NORTH, topPanel);
		mainPanel.add(BorderLayout.CENTER, zonePanel);
		setDialogComponent(mainPanel);
	}
	
	public static TriggerZone showDialog(Window parent, TriggerZone zone) {
		singleInstance = new PolygonDialog(parent);	
		singleInstance.setParams(zone);
		singleInstance.setVisible(true);
		return singleInstance.triggerZone;
	}

	private void setParams(TriggerZone triggerZone) {
		this.triggerZone = triggerZone;
		zoneDialogPanel.setZone(triggerZone);
		if (triggerZone == null) {
			return;
		}
	}

	@Override
	public boolean getParams() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelButtonPressed() {
		triggerZone = null;
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub
		
	}

}
