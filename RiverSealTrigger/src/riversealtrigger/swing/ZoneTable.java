package riversealtrigger.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import PamView.tables.SwingTableColumnWidths;
import riversealtrigger.RiverRegionThresholds;
import riversealtrigger.RiverTriggerParams;
import riversealtrigger.zones.TriggerZone;

/**
 * Table to show trigger zones, which can be used in the design dialog. 
 */
public class ZoneTable {

	JPanel mainPanel;
	private TableModel tableModel;

	private String[] colNames = {"Name", "Type", "Min Score", "Min Radius", "Min Length"};
	private RiverTriggerParams riverTriggerParams;
	
	private ZoneTableListener zoneTableListener;
	private JTable table;

	public ZoneTable(RiverTriggerParams riverTriggerParams) {
		this.riverTriggerParams = riverTriggerParams;
		tableModel = new TableModel();
		table = new JTable(tableModel);
		mainPanel = new JPanel(new BorderLayout());
		JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		mainPanel.add(scroller, BorderLayout.CENTER);

		table.addMouseListener(new TableMouse());
		new SwingTableColumnWidths("Trigger zone table view", table);
	}
	
	/**
	 * Update table after a data change
	 */
	public void update() {
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * Highlight a row for a given zone. 
	 * @param triggerZone
	 */
	public void selectZone(TriggerZone triggerZone) {
		int row = rowForZone(triggerZone);
		try {
			table.setRowSelectionInterval(row, row);
		}
		catch (Exception e) {
			
		}
	}

	public JComponent getComponent() {
		return mainPanel;
	}
	
	public ZoneTableListener getZoneTableListener() {
		return zoneTableListener;
	}

	public void setZoneTableListener(ZoneTableListener zoneTableListener) {
		this.zoneTableListener = zoneTableListener;
	}

	/**
	 * Get the row index for a zone
	 * @param zone
	 * @return
	 */
	private int rowForZone(TriggerZone zone) {
		if (zone == null) {
			return -1;
		}
		return riverTriggerParams.getTriggerZones().indexOf(zone);
	}
	private TriggerZone zoneForRow(int iRow) {
		ArrayList<TriggerZone> zones = riverTriggerParams.getTriggerZones();
		if (iRow >= 0 && iRow < zones.size()) {
			return zones.get(iRow);
		}
		else {
			return null;
		}
	}

	private class TableMouse extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			int selRow = table.getSelectedRow();
			if (zoneTableListener != null) {
				zoneTableListener.zoneSelected(zoneForRow(selRow));
			}
		}
		
	}
	
	/**
	 * Get a zone selected in the table, or null if none selected. 
	 * @return
	 */
	public TriggerZone getSelectedZone() {
		int selRow = table.getSelectedRow();
		return zoneForRow(selRow);
	}
	
	/**
	 * Get the selected row - useful for re-ordering commands. 
	 * @return
	 */
	public int getSelectedRow() {
		return table.getSelectedRow();
	}
	
	private class TableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return riverTriggerParams.getTriggerZones().size();
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TriggerZone zone = zoneForRow(rowIndex);
			if (zone == null) {
				return null;
			}
			RiverRegionThresholds rth = zone.getRiverRegionThresholds();
			switch (columnIndex) {
			case 0:
				return zone.getName();
			case 1:
				return rth.triggerType == RiverRegionThresholds.TRIGGER_IMMEDIATE ? "Immediate" : "Track End";
			case 2:
				return rth.minLinkScore;
			case 3:
				return rth.minRSize;
			case 4:
				return rth.minLength;
			}
			return null;
		}

		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

	}
}
