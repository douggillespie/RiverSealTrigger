package riversealtrigger.swing;

import java.awt.Component;

import riversealtrigger.RiverTriggerControl;
import riversealtrigger.RiverTriggerDataBlock;
import userDisplay.UserDisplayComponent;

public class TriggerDisplayComponent implements UserDisplayComponent {

	private RiverTriggerControl riverTriggerControl;
	private RiverTriggerDataBlock riverTriggerDataBlock;
	private TriggerDisplayTable triggerTable;
	private String uniqueName;
	
	public TriggerDisplayComponent(RiverTriggerControl riverTriggerControl, RiverTriggerDataBlock riverTriggerDataBlock) {
		this.riverTriggerControl = riverTriggerControl;
		this.riverTriggerDataBlock = riverTriggerDataBlock;
		triggerTable = new TriggerDisplayTable(riverTriggerDataBlock, "Sonar Triggers");
	}

	@Override
	public Component getComponent() {
		return triggerTable.getComponent();
	}

	@Override
	public void openComponent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeComponent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyModelChanged(int changeType) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	@Override
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Override
	public String getFrameTitle() {
		return uniqueName;
	}

}
