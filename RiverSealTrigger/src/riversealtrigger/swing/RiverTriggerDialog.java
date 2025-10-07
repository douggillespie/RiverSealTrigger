package riversealtrigger.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamUtils.LatLong;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import PamView.dialog.SourcePanel;
import PamguardMVC.PamDataBlock;
import riversealtrigger.RiverTriggerParams;
import riversealtrigger.RiverTriggerParams.RiverRegionThresholds;
import tritechplugins.detect.track.TrackLinkDataUnit;

public class RiverTriggerDialog extends PamDialog {

	private SourcePanel dataSource;
	private JTextField riverFlow, minDirection, minRSize[], minLinkScore[], minLength[];
	private RiverTriggerParams params;
//	private JTextField tastDuration;
	private XYComponent ignorePoint, triggerPoint;
	private XYComponent midStreamPoint;
	private int nRegions;
	
	private static RiverTriggerDialog singleInstance;
	
	private RiverTriggerDialog(Window parentFrame) {
		super(parentFrame, "River Trigger Settings", true);
		dataSource = new SourcePanel(this, TrackLinkDataUnit.class, false, true);

		nRegions = RiverTriggerParams.regionNames.length;
		
		riverFlow = new JTextField(4);
		minDirection = new JTextField(4);
		riverFlow.setToolTipText("Flow angle, measured clockwise from vertical on sonar");
		minDirection.setToolTipText("Min angle to flow, i.e. >90 means upstream");
		minRSize = new JTextField[nRegions];
		minLinkScore = new JTextField[nRegions];
		minLength = new JTextField[nRegions];
		for (int i = 0; i  < nRegions; i++) {
			minRSize[i] = new JTextField(4);
			minLinkScore[i] = new JTextField(4);
			minLength[i] = new JTextField(4);
			minRSize[i].setToolTipText("Min object size in radial coordinate");
			minLinkScore[i].setToolTipText("Minimum track link quality");
			minLength[i].setToolTipText("Min length (striaght line end to end)");
		}
		ignorePoint = new XYComponent(null);
		triggerPoint = new XYComponent(null);
		midStreamPoint = new XYComponent("-");
		ignorePoint.xyPanel.setToolTipText("Ignore tracks which don't pass this line");
		triggerPoint.xyPanel.setToolTipText("Trigger immediately if tracks pass this line");
		midStreamPoint.xyPanel.setToolTipText("Distances from sonar to river banks");
		midStreamPoint.x.setToolTipText("Distances from sonar to edge of 'mid stream'");
		midStreamPoint.y.setToolTipText("Distances from sonar to edge of 'far bank'");
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel triggerPanel = new JPanel(new BorderLayout());
		triggerPanel.setBorder(new TitledBorder("Trigger settings"));
		triggerPanel.add(BorderLayout.NORTH, dataSource.getPanel());
		JPanel sPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		triggerPanel.add(BorderLayout.CENTER, sPanel);
		sPanel.add(new JLabel("Flow direction ", JLabel.RIGHT), c);
		c.gridx++;
		sPanel.add(riverFlow, c);
		c.gridx++;
		sPanel.add(new JLabel(" " + LatLong.deg), c);
		c.gridx = 0;
		c.gridy++;
		sPanel.add(new JLabel("Min seal direction ", JLabel.RIGHT), c);
		c.gridx++;
		sPanel.add(minDirection, c);
		c.gridx++;
		sPanel.add(new JLabel(" " + LatLong.deg), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		JLabel il;
		sPanel.add(il = new JLabel("Point on ignore line ", JLabel.RIGHT), c);
		c.gridx ++;
		c.gridwidth = 1;
		sPanel.add(ignorePoint.xyPanel,c);
		c.gridx++;
		sPanel.add(new JLabel(" m"), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		JLabel il2;
		sPanel.add(il2 = new JLabel("Point on trigger line ", JLabel.RIGHT), c);
		c.gridx ++;
		c.gridwidth = 1;
		sPanel.add(triggerPoint.xyPanel,c);
		c.gridx++;
		sPanel.add(new JLabel(" m"), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		JLabel il3;
		sPanel.add(il3 = new JLabel("Range of mid stream ", JLabel.RIGHT), c);
		c.gridx ++;
		c.gridwidth = 1;
		sPanel.add(midStreamPoint.xyPanel,c);
		c.gridx++;
		sPanel.add(new JLabel(" m"), c);

		// this bit needs to double up for different regions. 
		for (int i = 0; i < nRegions; i++) {
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 1;
			sPanel.add(new JLabel(RiverTriggerParams.regionNames[i], JLabel.RIGHT), c);
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy++;
			sPanel.add(new JLabel("Track quality ", JLabel.RIGHT), c);
			c.gridx++;
			sPanel.add(minLinkScore[i], c);
			c.gridx++;
			sPanel.add(new JLabel(" (about .5)"), c);
			c.gridx = 0;
			c.gridy++;
			sPanel.add(new JLabel("Minimum track length ", JLabel.RIGHT), c);
			c.gridx++;
			sPanel.add(minLength[i], c);
			c.gridx++;
			sPanel.add(new JLabel(" m"), c);
			c.gridx = 0;
			c.gridy++;
			sPanel.add(new JLabel("Minimum track size ", JLabel.RIGHT), c);
			c.gridx++;
			sPanel.add(minRSize[i], c);
			c.gridx++;
			sPanel.add(new JLabel(" m"), c);
		}
		
//		JPanel tastPanel = new JPanel(new GridBagLayout());
//		tastPanel.setBorder(new TitledBorder("TAST"));
//		c.gridx = c.gridy = 0;
//		tastPanel.add(new JLabel("Treatment duration ", JLabel.RIGHT), c);
//		c.gridx++;
//		tastPanel.add(tastDuration, c);
//		c.gridx++;
//		tastPanel.add(new JLabel(" s"), c);
		
		il.setToolTipText(ignorePoint.xyPanel.getToolTipText());
		il2.setToolTipText(triggerPoint.xyPanel.getToolTipText());
		
		mainPanel.add(BorderLayout.CENTER, triggerPanel);
//		mainPanel.add(BorderLayout.SOUTH, tastPanel);
		setDialogComponent(mainPanel);
	}
	
	public static RiverTriggerParams showDialog(Window parent, RiverTriggerParams riverTriggerParams) {
//		if (singleInstance == null || singleInstance.getOwner() != parent) {
			singleInstance = new RiverTriggerDialog(parent);
//		}
		singleInstance.setParams(riverTriggerParams);
		singleInstance.setVisible(true);
		return singleInstance.params;
	}

	private void setParams(RiverTriggerParams riverTriggerParams) {
		this.params = riverTriggerParams;
		dataSource.setSource(params.dataSourceName);
		riverFlow.setText(String.format("%3.1f", params.flowDirection));
		minDirection.setText(String.format("%3.1f", params.minUpstreamDirection));
		for (int i = 0; i < nRegions; i++) {
			RiverRegionThresholds regionTh = params.getRegionThreshold(i);
			minLinkScore[i].setText(String.format("%3.2f", regionTh.minLinkScore));
			minLength[i].setText(String.format("%3.2f", regionTh.minLength));
			minRSize[i].setText(String.format("%3.2f", regionTh.minRSize));
		}
		ignorePoint.setXY(params.getIgnorePoint());
		triggerPoint.setXY(params.getTriggerPoint());
		midStreamPoint.setXY(params.getMidRiverRange());
	}

	@Override
	public boolean getParams() {
		PamDataBlock source = dataSource.getSource();
		if (source == null) {
			return false;
		}
		params.dataSourceName = source.getLongDataName();
		try {
			params.flowDirection = Double.valueOf(riverFlow.getText());
			params.minUpstreamDirection = Double.valueOf(minDirection.getText());
			for (int i = 0; i < nRegions; i++) {
				RiverRegionThresholds regionTh = params.getRegionThreshold(i);
				regionTh.minLinkScore = Double.valueOf(minLinkScore[i].getText());
				regionTh.minLength = Double.valueOf(minLength[i].getText());
				regionTh.minRSize = Double.valueOf(minRSize[i].getText());
			}
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid numeric parameter in dialog");
		}
		double[] xy = ignorePoint.getXY();
		if (xy == null) {
			return showWarning("No ignore line set");
		}
		params.setIgnorePoint(xy);
		
		xy = triggerPoint.getXY();
		if (xy == null) {
			return showWarning("No trigger line set");
		}
		params.setTriggerPoint(xy);
		
		double[] rr = midStreamPoint.getXY();
		if (rr == null) {
			return showWarning("River bank areas not defined");
		}
		params.setMidRiverRange(rr);
		
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		params = null;
	}

	@Override
	public void restoreDefaultSettings() {
		setParams(new RiverTriggerParams());
	}
	
	private class XYComponent {
		
		private JPanel xyPanel;
		private JTextField x, y;
		
		private XYComponent(String separator) {
			xyPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new PamGridBagContraints();
			c.ipadx = 0;
			c.insets = new Insets(0,0,0,0);
			if (separator == null) {
				separator = ",";
			}
						
			x = new JTextField(4);
			y = new JTextField(4);
			x.setToolTipText("x coordinate");
			y.setToolTipText("y coordinate");
			xyPanel.add(x,c);
			c.gridx++;
			xyPanel.add(new JLabel(separator),c);
			c.gridx++;
			xyPanel.add(y,c);
		}
		
		private void setXY(double[] xy) {
			if (xy == null || xy.length < 2) {
				x.setText(null);
				y.setText(null);
			}
			x.setText(String.format("%3.1f", xy[0]));
			y.setText(String.format("%3.1f", xy[1]));
		}
		
		private double[] getXY() {
			double[] xy = new double[2];
			try {
				xy[0] = Double.valueOf(x.getText());
				xy[1] = Double.valueOf(y.getText());			
			}
			catch (NumberFormatException e) {
				return null;
			}
			return xy;
		}
	}

}
