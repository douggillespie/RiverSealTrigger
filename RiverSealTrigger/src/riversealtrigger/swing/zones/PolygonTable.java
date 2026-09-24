package riversealtrigger.swing.zones;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		tableModel.resetValues();
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
		List<Double> xPoints = new ArrayList<Double>();
		List<Double> yPoints = new ArrayList<Double>();

		for (int row = 0; row < tableModel.getRowCount(); row++) {
			Double x = parseNumber(tableModel.getValueAt(row, 0));
			Double y = parseNumber(tableModel.getValueAt(row, 1));
			// Ignore empty or incomplete rows. Only complete numeric pairs become points.
			if (x != null && y != null) {
				xPoints.add(x);
				yPoints.add(y);
			}
		}

		double[] xP = new double[xPoints.size()];
		double[] yP = new double[yPoints.size()];
		for (int i = 0; i < xP.length; i++) {
			xP[i] = xPoints.get(i);
			yP[i] = yPoints.get(i);
		}

		if (zone != null) {
			zone.setPoints(xP, yP);
		}
		return zone;
	}

	private Double parseNumber(Object value) {
		if (value == null || value.toString().trim().isEmpty()) {
			return null;
		}
		try {
			return Double.valueOf(value.toString().trim());
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	private class TableModel extends AbstractTableModel {

		private List<Object[]> values = new ArrayList<Object[]>();

		private void resetValues() {
			values.clear();
			if (zone != null) {
				double[] xPoints = zone.getxPoints();
				double[] yPoints = zone.getyPoints();
				for (int i = 0; i < zone.getNPoints(); i++) {
					values.add(new Object[] {xPoints[i], yPoints[i]});
				}
			}
			// Always keep one blank row available for the next point.
			values.add(new Object[] {null, null});
		}

		@Override
		public int getRowCount() {
			return values.size();
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return values.get(rowIndex)[columnIndex];
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			values.get(rowIndex)[columnIndex] = value;

			// Do not update the polygon until both cells in this row contain valid numbers.
			if (parseNumber(values.get(rowIndex)[0]) != null
					&& parseNumber(values.get(rowIndex)[1]) != null) {
				readTable();
				resetValues();
				fireTableDataChanged();
			}
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
