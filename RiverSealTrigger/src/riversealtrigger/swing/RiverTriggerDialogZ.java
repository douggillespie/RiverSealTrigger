package riversealtrigger.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamUtils.LatLong;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import PamView.dialog.SourcePanel;
import PamView.dialog.warn.WarnOnce;
import riversealtrigger.RiverTriggerParams;
import riversealtrigger.swing.zones.PolygonDialog;
import riversealtrigger.zones.TriggerZone;
import riversealtrigger.zones.ZonePolygon;
import tritechplugins.detect.track.TrackLinkDataUnit;

public class RiverTriggerDialogZ extends PamDialog implements ZoneTableListener{

	private static final long serialVersionUID = 1L;
	private SourcePanel dataSource;
	private JTextField riverFlow, minDirection;
	private RiverTriggerParams params;
	private JPanel zonesPanel;
	private ZoneTable zoneTable;
	
	private JButton addButton, editButton, rmButton, upButton, dnButton;
	
	private static RiverTriggerDialogZ singleInstance;

	private RiverTriggerDialogZ(Window parentFrame, RiverTriggerParams riverTriggerParams) {
		super(parentFrame, "River trigger settings", false);
		this.params = riverTriggerParams;
		JPanel mainPanel = new JPanel(new BorderLayout()); 
		
		zoneTable = new ZoneTable(riverTriggerParams);
		zoneTable.setZoneTableListener(this);
		JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.setBorder(new TitledBorder("General settings"));
		zonesPanel = new JPanel(new BorderLayout());
		zonesPanel.add(zoneTable.getComponent(), BorderLayout.CENTER);
		JPanel zonesOuterPanel = new JPanel(new BorderLayout());
		zonesOuterPanel.add(zonesPanel, BorderLayout.CENTER);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(zonesOuterPanel, BorderLayout.CENTER);
		zonesOuterPanel.setBorder(new TitledBorder("Trigger Zones"));
		JPanel zonesCtrlPanel = new JPanel(new FlowLayout());
		
		addButton = new JButton("Add zone");
		addButton.setToolTipText("Create a new trigger zone");
		zonesCtrlPanel.add(addButton);
		editButton = new JButton("Edit zone");
		editButton.setToolTipText("Edit trigger zone");
		zonesCtrlPanel.add(editButton);
		rmButton = new JButton("Remove zone");
		rmButton.setToolTipText("Remove trigger zone");
		zonesCtrlPanel.add(rmButton);
		upButton = new JButton("Move up");
		upButton.setToolTipText("Move zone up - note that zones are tested in order and the first is always selected");
		zonesCtrlPanel.add(upButton);
		dnButton = new JButton("Move down");
		dnButton.setToolTipText("Move zone down - note that zones are tested in order and the first is always selected");
		zonesCtrlPanel.add(dnButton);
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addZone();
			}
		});
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editZone();
			}
		});
		rmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rmZone();
			}
		});
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				upZone();
			}
		});
		dnButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dnZone();
			}
		});
		
		
		zonesOuterPanel.add(zonesCtrlPanel, BorderLayout.SOUTH);
		
		GridBagConstraints c = new PamGridBagContraints();
		dataSource = new SourcePanel(this, TrackLinkDataUnit.class, false, true);
		riverFlow = new JTextField(4);
		minDirection = new JTextField(4);
		riverFlow.setToolTipText("Flow angle, measured clockwise from vertical on sonar");
		minDirection.setToolTipText("Min angle to flow, i.e. >90 means upstream");
		
		c.gridwidth = 4;
		topPanel.add(dataSource.getPanel(), c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;

		topPanel.add(new JLabel("Flow direction ", JLabel.RIGHT), c);
		c.gridx++;
		topPanel.add(riverFlow, c);
		c.gridx++;
		topPanel.add(new JLabel(" " + LatLong.deg), c);
		c.gridx = 0;
		c.gridy++;
		topPanel.add(new JLabel("Min seal direction ", JLabel.RIGHT), c);
		c.gridx++;
		topPanel.add(minDirection, c);
		c.gridx++;
		topPanel.add(new JLabel(" " + LatLong.deg), c);
		
		zoneSelected(null);
		setDialogComponent(mainPanel);
	}

	protected void dnZone() {
		ArrayList<TriggerZone> zones = params.getTriggerZones();
		int selRow = zoneTable.getSelectedRow();
		if (selRow < 0 || selRow >= zones.size()-1) {
			return;
		}
		TriggerZone removed = zones.remove(selRow);
		if (removed != null) {
			zones.add(selRow+11, removed);
		}
		zoneTable.update();
		zoneTable.selectZone(removed);
	}

	protected void upZone() {
		ArrayList<TriggerZone> zones = params.getTriggerZones();
		int selRow = zoneTable.getSelectedRow();
		if (selRow < 1) {
			return;
		}
		TriggerZone removed = zones.remove(selRow);
		if (removed != null) {
			zones.add(selRow-1, removed);
		}
		zoneTable.update();
		zoneTable.selectZone(removed);
	}

	/**
	 * Remove selected zone. 
	 */
	protected void rmZone() {
		TriggerZone selZone = zoneTable.getSelectedZone();
		if (selZone == null) {
			return;
		}
		String msg = String.format("Do you want to permmanently remove the %s trigger zone ?", selZone.getName());
		int ans = WarnOnce.showNamedWarning("Trigger zone removal", this, "Remove trigger zone", msg, WarnOnce.OK_CANCEL_OPTION);
		if (ans == WarnOnce.CANCEL_OPTION) {
			return;
		}
		params.getTriggerZones().remove(selZone);
		zoneTable.update();
	}

	protected void editZone() {
		TriggerZone selZone = zoneTable.getSelectedZone();
		if (selZone == null) {
			return;
		}
		TriggerZone updatedZone = PolygonDialog.showDialog(this, selZone);
		if (updatedZone != null) {
			ArrayList<TriggerZone> zones = params.getTriggerZones();
			int currInd = zones.indexOf(selZone);
			if (currInd >= 0) {
				zones.remove(currInd);
				zones.add(currInd, updatedZone);
			}
			else {
				zones.add(updatedZone);
			}
		}
		zoneTable.update();
	}

	protected void addZone() {
		TriggerZone newZone = PolygonDialog.showDialog(this, null);
		if (newZone != null) {
			params.addTriggerZone(newZone);
			zoneTable.update();
		}
	}

	@Override
	public void zoneSelected(TriggerZone triggerZone) {
		if (triggerZone == null) {
			upButton.setEnabled(false);
			dnButton.setEnabled(false);
			editButton.setEnabled(false);
			rmButton.setEnabled(false);
			return;
		}
		rmButton.setEnabled(true);
		editButton.setEnabled(true);
		rmButton.setEnabled(true);
		int nZ = params.getTriggerZones().size();
		int pos = params.zonePosition(triggerZone);
		upButton.setEnabled(pos > 0);
		dnButton.setEnabled(pos < nZ-1);
		
	}

	public static RiverTriggerParams showDialog(Window parent, RiverTriggerParams riverTriggerParams) {
//		if (singleInstance == null || singleInstance.getOwner() != parent) {
			singleInstance = new RiverTriggerDialogZ(parent, riverTriggerParams);
//		}
		singleInstance.setParams(riverTriggerParams);
		singleInstance.setVisible(true);
		return singleInstance.params;
	}

	private void setParams(RiverTriggerParams riverTriggerParams) {
		this.params = riverTriggerParams;
		dataSource.setSource(params.dataSourceName);
		riverFlow.setText(Double.valueOf(params.flowDirection).toString());
		minDirection.setText(Double.valueOf(params.minUpstreamDirection).toString());
	}

	@Override
	public boolean getParams() {
		params.dataSourceName = dataSource.getSourceName();
		try {
			params.flowDirection = Double.valueOf(riverFlow.getText());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid flow diretion");
		}
		try {
			params.minUpstreamDirection = Double.valueOf(minDirection.getText());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid seal diretion");
		}
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub
		
	}

}
