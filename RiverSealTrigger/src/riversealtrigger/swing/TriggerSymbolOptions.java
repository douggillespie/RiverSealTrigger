package riversealtrigger.swing;

import java.io.Serializable;

import PamView.symbol.StandardSymbolOptions;

public class TriggerSymbolOptions extends StandardSymbolOptions implements Cloneable, Serializable {
	
	public static final long serialVersionUID = 1L;

	public boolean drawFlowDirection = true;
	
	public boolean drawTriggerboundaries = true;
	
	public boolean showLabels = true;

	public TriggerSymbolOptions() {
		super(RiverTriggerGraphics.defaultSymbol.getSymbolData());
	}

}
