package riversealtrigger.swing;

import PamView.GeneralProjector;
import PamView.symbol.PamSymbolChooser;
import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.SymbolData;
import PamView.symbol.modifier.HydrophoneSymbolModifier;
import PamguardMVC.PamDataBlock;
import riversealtrigger.RiverTriggerDataBlock;

public class RiverTriggerSymbolManager extends StandardSymbolManager {

	private RiverTriggerDataBlock triggerDataBlock;

	public RiverTriggerSymbolManager(RiverTriggerDataBlock pamDataBlock) {
		super(pamDataBlock, RiverTriggerGraphics.defaultSymbol.getSymbolData());	
		this.triggerDataBlock = pamDataBlock;
	}

	protected StandardSymbolChooser createSymbolChooser(String displayName, GeneralProjector projector) {
		return new RiverTriggerSymbolChooser(this, triggerDataBlock,  displayName, getDefaultSymbol(), projector);
	}

	@Override
	public void addSymbolModifiers(PamSymbolChooser psc) {
		psc.removeSymbolModifier(HydrophoneSymbolModifier.class);
	}
}
