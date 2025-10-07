package riversealtrigger.swing;

import PamView.GeneralProjector;
import PamView.symbol.PamSymbolOptions;
import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.StandardSymbolOptions;
import PamView.symbol.SwingSymbolOptionsPanel;
import PamView.symbol.SymbolData;
import PamView.symbol.modifier.HydrophoneSymbolModifier;
import PamguardMVC.PamDataBlock;

public class RiverTriggerSymbolChooser extends StandardSymbolChooser {
	
	private TriggerSymbolOptions triggerSymbolOptions = new TriggerSymbolOptions();

	public RiverTriggerSymbolChooser(StandardSymbolManager standardSymbolManager, PamDataBlock pamDataBlock,
			String displayName, SymbolData defaultSymbol, GeneralProjector projector) {
		super(standardSymbolManager, pamDataBlock, displayName, defaultSymbol, projector);
	}

	@Override
	public void setSymbolOptions(PamSymbolOptions symbolOptions) {
		super.setSymbolOptions(symbolOptions);
		if (symbolOptions instanceof TriggerSymbolOptions) {
			triggerSymbolOptions = (TriggerSymbolOptions) symbolOptions;
		}
	}

	@Override
	public TriggerSymbolOptions getSymbolOptions() {
		return triggerSymbolOptions;
	}

	@Override
	public SwingSymbolOptionsPanel getSwingOptionsPanel(GeneralProjector projector) {
		return new TriggerSymbolPanel(getSymbolManager(), this);
	}

}
