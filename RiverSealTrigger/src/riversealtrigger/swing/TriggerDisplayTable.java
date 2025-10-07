package riversealtrigger.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import PamUtils.PamCalendar;
import PamView.component.DataBlockTableView;
import PamguardMVC.PamDataBlock;
import pamScrollSystem.AbstractScrollManager;
import riversealtrigger.RiverTriggerDataBlock;
import riversealtrigger.RiverTriggerDataUnit;
import tritechplugins.detect.track.TrackLinkDataUnit;

public class TriggerDisplayTable extends DataBlockTableView<RiverTriggerDataUnit> {
	
	private static final String[] colNames = {"Id", "Trigger Start", "Track Start", "Trigger End", "Duration", "Track UID"};
	
	private RiverTriggerDataBlock riverTriggerDataBlock;

	public TriggerDisplayTable(RiverTriggerDataBlock pamDataBlock, String displayName) {
		super(pamDataBlock, displayName);
		this.riverTriggerDataBlock = pamDataBlock;
	}

	@Override
	public String[] getColumnNames() {
		return colNames;
	}
	
//	private class TableMouse extends MouseAdapter {
//
//		@Override
//		public void mousePressed(MouseEvent e) {
//			if (e.isPopupTrigger()) {
//				showMenu(e);
//			}
//		}
//
//		@Override
//		public void mouseReleased(MouseEvent e) {
//			if (e.isPopupTrigger()) {
//				showMenu(e);
//			}
//		}
//		
//	}
	@Override
	public void popupMenuAction(MouseEvent e, RiverTriggerDataUnit dataUnit, String colName) {
		super.popupMenuAction(e, dataUnit, colName);
		if (dataUnit == null) {
			return;
		}
		long utc = dataUnit.getTimeMilliseconds();
		int[] tbs = {0, -30, -10, 10, 30};
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItem;
		String txt;
		for (int i = 0; i < tbs.length; i++) {
			long tScroll = utc+tbs[i]*1000;
			if (tbs[i] < 0) {
				txt = String.format("Scroll to %ds before trigger : %s", Math.abs(tbs[i]),  PamCalendar.formatDBDateTime(tScroll));
			}
			else if (tbs[i]  > 0) {
				txt = String.format("Scroll to %ds after trigger : %s", Math.abs(tbs[i]),  PamCalendar.formatDBDateTime(tScroll));
			}
			else {
				txt = String.format("Scroll to trigger at %s", PamCalendar.formatDBDateTime(tScroll));
			}
			menuItem = new JMenuItem(txt);
			menuItem.addActionListener(new ScrollMenuAction(tScroll));
			menu.add(menuItem);
		}
		menu.show(e.getComponent(), e.getX(), e.getY());
		
	}
	
	private class ScrollMenuAction implements ActionListener {
		long scrollTime;

		public ScrollMenuAction(long scrollTime) {
			super();
			this.scrollTime = scrollTime;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractScrollManager scrollManager = AbstractScrollManager.getScrollManager();
			scrollManager.scrollToTime(riverTriggerDataBlock, scrollTime);
		}
	}

	@Override
	public Object getColumnData(RiverTriggerDataUnit dataUnit, int column) {
		ArrayList<TrackLinkDataUnit> tracks = dataUnit.getTrackDataUnits();
		switch (column) {
		case 0:
			return dataUnit.getDatabaseIndex();
		case 1:
			return PamCalendar.formatDBDateTime(dataUnit.getTimeMilliseconds());
		case 2:
			if (tracks.size() > 0) {
				long t = tracks.get(0).getTimeMilliseconds();
				return PamCalendar.formatDBDateTime(t);
			}
			else {
				return "Track(s) not loaded";
			}
		case 3:
			return PamCalendar.formatDBDateTime(dataUnit.getTriggerEnd());
		case 4:
			if (tracks.size() > 0) {
				long t = tracks.get(tracks.size()-1).getEndTimeInMilliseconds()-tracks.get(0).getTimeMilliseconds();
				return String.format("%3.1fs", (double) t / 1000.);
			}
			else {
				long dur = dataUnit.getTriggerEnd()-dataUnit.getTimeMilliseconds();
				return String.format("%3.1fs", (double) dur / 1000.);
			}
		case 5:
			return dataUnit.getTrackUIDList();	
			
			
		}
		return null;
	}



}
