package riversealtrigger.swing;

import riversealtrigger.RiverTriggerControl;
import riversealtrigger.RiverTriggerDataBlock;
import userDisplay.UserDisplayComponent;
import userDisplay.UserDisplayControl;
import userDisplay.UserDisplayProvider;

public class TriggerDisplayProvider implements UserDisplayProvider {

	private RiverTriggerControl riverTriggerControl;
	private RiverTriggerDataBlock riverTriggerDataBlock;
	
	public TriggerDisplayProvider(RiverTriggerControl riverTriggerControl, RiverTriggerDataBlock riverTriggerDataBlock) {
		this.riverTriggerControl = riverTriggerControl;
		this.riverTriggerDataBlock = riverTriggerDataBlock;
	}

	@Override
	public String getName() {
		return "River sonar triggers";
	}

	@Override
	public UserDisplayComponent getComponent(UserDisplayControl userDisplayControl, String uniqueDisplayName) {
		return new TriggerDisplayComponent(riverTriggerControl, riverTriggerDataBlock);
	}

	@Override
	public Class getComponentClass() {
		return TriggerDisplayComponent.class;
	}

	@Override
	public int getMaxDisplays() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canCreate() {
		return true;
	}

	@Override
	public void removeDisplay(UserDisplayComponent component) {
		// TODO Auto-generated method stub

	}

}
