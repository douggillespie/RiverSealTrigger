package riversealtrigger.swing.zones;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import riversealtrigger.RiverRegionThresholds;
import riversealtrigger.zones.RiverZone;
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
		topPanel.setBorder(new TitledBorder("Zone options"));	
		
		JPanel zonePanel = new JPanel();
		zoneDialogPanel = new PolygonTable(this);
		zonePanel.add(zoneDialogPanel.getPanel());
		mainPanel.add(BorderLayout.NORTH, topPanel);
		mainPanel.add(BorderLayout.CENTER, zonePanel);
		
		name = new JTextField(20);
		minLinkScore = new JTextField(5);
		minRSize = new JTextField(5);
		minTrackLength = new JTextField(5);
		GridBagConstraints c = new PamGridBagContraints();
		topPanel.add(new JLabel("Zone name ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 2;
		topPanel.add(name, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		topPanel.add(new JLabel("Min score ", JLabel.RIGHT), c);
		c.gridx++;
		topPanel.add(minLinkScore, c);
		c.gridx = 0;
		c.gridy++;
		topPanel.add(new JLabel("Min radial dim ", JLabel.RIGHT), c);
		c.gridx++;
		topPanel.add(minRSize, c);
		c.gridx++;
		topPanel.add(new JLabel(" m", JLabel.LEFT), c);
		c.gridx = 0;
		c.gridy++;
		topPanel.add(new JLabel("Min track length ", JLabel.RIGHT), c);
		c.gridx++;
		topPanel.add(minTrackLength, c);
		c.gridx++;
		topPanel.add(new JLabel(" m", JLabel.LEFT), c);
		c.gridx = 0;
		c.gridy++;
		
		
		
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
		RiverZone zone = zoneDialogPanel.getZone();
		if (zone == null) {
			return false;
		}
		if (triggerZone == null) {
			triggerZone = new TriggerZone(zone, new RiverRegionThresholds());
		}
		else {
			triggerZone.setRiverZone(zone);
		}
		RiverRegionThresholds thresholds = triggerZone.getRiverRegionThresholds();
		if (thresholds == null) {
			triggerZone.setRiverRegionThresholds(thresholds = new RiverRegionThresholds());
		}
		String tName = name.getText();
		if (tName == null || tName.length() == 0) {
			return showWarning("You must specify a name for the Trigger Zone");
		}
		zone.setName(tName);
		
		return true;
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
