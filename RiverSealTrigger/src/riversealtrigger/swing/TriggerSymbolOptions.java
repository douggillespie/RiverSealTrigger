package riversealtrigger.swing;

import java.io.Serializable;

import PamView.symbol.StandardSymbolOptions;

public class TriggerSymbolOptions extends StandardSymbolOptions implements Cloneable, Serializable {
	
	public boolean drawFlowDirection = true;

	public TriggerSymbolOptions() {
		super(RiverTriggerGraphics.defaultSymbol.getSymbolData());
	}

}
