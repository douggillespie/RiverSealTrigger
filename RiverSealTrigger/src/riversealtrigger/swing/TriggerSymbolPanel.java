package riversealtrigger.swing;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.StandardSymbolOptionsPanel;

public class TriggerSymbolPanel extends StandardSymbolOptionsPanel {

	private JPanel mainPanel;
	
	private JCheckBox showFlow;
	
	private RiverTriggerSymbolChooser triggerSymbolChooser;

	public TriggerSymbolPanel(StandardSymbolManager standardSymbolManager,
			RiverTriggerSymbolChooser triggerSymbolChooser) {
		super(standardSymbolManager, triggerSymbolChooser);
		this.triggerSymbolChooser = triggerSymbolChooser;
		mainPanel = new JPanel(new BorderLayout());
		JPanel sPanel = new JPanel(new BorderLayout());
		sPanel.setBorder(new TitledBorder("Additional options"));
		showFlow = new JCheckBox("Show trigger geometry");
		sPanel.add(showFlow);
		mainPanel.add(sPanel, BorderLayout.SOUTH);
		mainPanel.add(super.getMainPanel(), BorderLayout.CENTER);
	}

	@Override
	public void setParams() {
		if (triggerSymbolChooser == null) {
			return;
		}
		TriggerSymbolOptions params = (TriggerSymbolOptions) triggerSymbolChooser.getSymbolOptions();
		super.setParams();
		showFlow.setSelected(params.drawFlowDirection);
	}

	@Override
	public boolean getParams() {
		if (triggerSymbolChooser == null) {
			return true;
		}
		TriggerSymbolOptions params = (TriggerSymbolOptions) triggerSymbolChooser.getSymbolOptions();
		params.drawFlowDirection = showFlow.isSelected();
		
		return super.getParams();
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}



}
