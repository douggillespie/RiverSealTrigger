package riversealtrigger.swing.zones;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
	private JRadioButton trigOnEnd, trigImmediate;

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
		trigOnEnd = new JRadioButton("On track end");
		trigImmediate = new JRadioButton("Immediate");
		ButtonGroup bg = new ButtonGroup();
		bg.add(trigImmediate);
		bg.add(trigOnEnd);
		minLinkScore.setToolTipText("Minimum track quality score");
		minRSize.setToolTipText("Min radial object dimension");
		minTrackLength.setToolTipText("Min end-to-end track length");
		trigOnEnd.setToolTipText("Trigger only if the track ends in this zone");
		trigImmediate.setToolTipText("Trigger immediately if track detected in this zone");
		
		GridBagConstraints c = new PamGridBagContraints();
		topPanel.add(new JLabel("Zone name ", JLabel.RIGHT), c);
		c.gridx++;
		c.gridwidth = 2;
		topPanel.add(name, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		topPanel.add(new JLabel("Trigger ", JLabel.RIGHT), c);
		c.gridx++;
		topPanel.add(trigImmediate, c);
		c.gridx++;
		topPanel.add(trigOnEnd, c);
		c.gridx = 0;
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
		if (triggerZone == null) {
			zoneDialogPanel.setZone(null);
		}
		else {
			zoneDialogPanel.setZone(triggerZone.getRiverZone());
		}
		if (triggerZone == null) {
			return;
		}
		name.setText(triggerZone.getName());
		RiverRegionThresholds th = triggerZone.getRiverRegionThresholds();
		trigOnEnd.setSelected(th.triggerType == RiverRegionThresholds.TRIGGER_ONEND);
		trigImmediate.setSelected(th.triggerType == RiverRegionThresholds.TRIGGER_IMMEDIATE);
		minLinkScore.setText(Double.valueOf(th.minLinkScore).toString());
		minRSize.setText(Double.valueOf(th.minRSize).toString());
		minTrackLength.setText(Double.valueOf(th.minLength).toString());
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
		if (trigImmediate.isSelected()) {
			thresholds.triggerType = RiverRegionThresholds.TRIGGER_IMMEDIATE;
		}
		else {
			thresholds.triggerType = RiverRegionThresholds.TRIGGER_ONEND;
		}
		try {
			thresholds.minLinkScore = Double.valueOf(minLinkScore.getText().trim());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid link score value");
		}
		try {
			thresholds.minRSize = Double.valueOf(minRSize.getText().trim());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid radial size value");
		}
		try {
			thresholds.minLength = Double.valueOf(minTrackLength.getText().trim());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid min track length value");
		}
		
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
