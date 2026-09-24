package riversealtrigger.swing.zones;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import riversealtrigger.zones.ZonePolygon;

public class PolygonTable implements ZoneDialogPanel<ZonePolygon>{
	
	private JPanel mainPanel;
	
	private String[] colNames = {"x (m)", "y (m)"};

	private TableModel tableModel;

	private JTable table;

	private ZonePolygon zone;
	
	public PolygonTable(Window parent) {
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new TitledBorder("Polygon boundary"));
		tableModel = new TableModel();
		table = new JTable(tableModel);
		mainPanel.add(BorderLayout.CENTER, new JScrollPane(table));
	}

	@Override
	public void setZone(ZonePolygon zone) {
		if (zone == null) {
			this.zone = null;
		}
		else {
			this.zone = zone.clone();
		}
		tableModel.fireTableDataChanged();
	}

	@Override
	public ZonePolygon getZone() {
		return zone;
	}

	@Override
	public JComponent getPanel() {
		return mainPanel;
	}
	
	private ZonePolygon readTable() {
		int n = tableModel.getRowCount();
		double[] xP = new double[n];
		double[] yP = new double[n];
		int goodRows = 0;
		for (int i = 0; i < n; i++) {
			try {
				Object xValue = table.getValueAt(i, 0);
				Object yValue = table.getValueAt(i, 1);
				if (xValue == null || yValue == null) {
					continue;
				}
				xP[goodRows] = Double.parseDouble(xValue.toString());
				yP[goodRows] = Double.parseDouble(yValue.toString());
				goodRows++;
			}
			catch (NumberFormatException e) {
				// Ignore incomplete or invalid rows while the user is entering data.
			}
		}
		xP = Arrays.copyOf(xP, goodRows);
		yP = Arrays.copyOf(yP, goodRows);
		if (zone != null) {
			zone.setPoints(xP, yP);
		}
		return zone;
	}

	private class TableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			if (zone == null) {
				return 1;
			}
			return zone.getNPoints()+1;
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (zone == null) {
				return null;
			}
			double[] v = null;
			switch (columnIndex) {
			case 0:
				v = zone.getxPoints();
				break;
			case 1:
				v = zone.getyPoints();
				break;
			}
			if (v == null || rowIndex >= v.length) {
				return null;
			}
			return v[rowIndex];
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (zone == null) {
				return;
			}
			readTable();
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			return colNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
	}
}
