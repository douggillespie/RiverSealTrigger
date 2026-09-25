package riversealtrigger;

import java.util.ArrayList;

import PamView.GeneralProjector;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import riversealtrigger.zones.TriggerZone;
import riversealtrigger.zones.ZonedData;

public class RiverTriggerDataBlock extends PamDataBlock<RiverTriggerDataUnit> implements ZonedData{

	private RiverTriggerProcess riverTriggerProcess;
	private RiverTriggerControl riverTriggerControl;

	public RiverTriggerDataBlock(String dataName, RiverTriggerProcess riverTriggerProcess) {
		super(RiverTriggerDataUnit.class, dataName, riverTriggerProcess, 0);
		this.riverTriggerProcess = riverTriggerProcess;
		this.riverTriggerControl = riverTriggerProcess.getRiverTriggerControl();
	}

	@Override
	public String getHoverText(GeneralProjector generalProjector, PamDataUnit dataUnit, int iSide) {
		return ((RiverTriggerDataUnit) dataUnit).getSummaryString(iSide);
	}

	@Override
	public ArrayList<TriggerZone> getTriggerZones() {
		return riverTriggerControl.getTriggerParams().getTriggerZones();
	}

}
