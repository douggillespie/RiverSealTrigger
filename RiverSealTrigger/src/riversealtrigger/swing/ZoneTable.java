package riversealtrigger.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import riversealtrigger.RiverTriggerParams;
import riversealtrigger.zones.TriggerZone;

/**
 * Table to show trigger zones, which can be used in the design dialog. 
 */
public class ZoneTable {

	JPanel mainPanel;
	private TableModel tableModel;

	private String[] colNames = {"Name", "Shape", "Threshold"};
	private RiverTriggerParams riverTriggerParams;
	
	private ZoneTableListener zoneTableListener;

	public ZoneTable(RiverTriggerParams riverTriggerParams) {
		this.riverTriggerParams = riverTriggerParams;
		tableModel = new TableModel();
		JTable table = new JTable(tableModel);
		mainPanel = new JPanel(new BorderLayout());
		JScrollPane scroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		mainPanel.add(scroller, BorderLayout.CENTER);

		table.addMouseListener(new TableMouse());
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

	private class TableMouse extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
		}
		
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

	}
}
