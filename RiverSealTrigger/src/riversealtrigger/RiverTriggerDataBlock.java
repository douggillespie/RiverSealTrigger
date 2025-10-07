package riversealtrigger;

import PamView.GeneralProjector;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;

public class RiverTriggerDataBlock extends PamDataBlock<RiverTriggerDataUnit> {

	private RiverTriggerProcess riverTriggerProcess;

	public RiverTriggerDataBlock(String dataName, RiverTriggerProcess riverTriggerProcess) {
		super(RiverTriggerDataUnit.class, dataName, riverTriggerProcess, 0);
		this.riverTriggerProcess = riverTriggerProcess;
	}

	@Override
	public String getHoverText(GeneralProjector generalProjector, PamDataUnit dataUnit, int iSide) {
		return ((RiverTriggerDataUnit) dataUnit).getSummaryString(iSide);
	}

}
