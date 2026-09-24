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
		Double x , y;
		int goodRows = 0;
		for (int i = 0; i < n; i++) {
			x = y = null;
			try {
				x = Double.valueOf(table.getCellEditor(i, 0).toString());
				y = Double.valueOf(table.getCellEditor(i, 1).toString());
			}
			catch (Exception e) {
				
			}
			if (x != null && y != null) {
				xP[goodRows] = x;
				yP[goodRows] = y;
				goodRows++;
			}
		}
		xP = Arrays.copyOf(xP, goodRows);
		yP = Arrays.copyOf(yP, goodRows);
		zone.setPoints(xP, yP);
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
		public String getColumnName(int column) {
			return colNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
	}
}
