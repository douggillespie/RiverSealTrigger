package riversealtrigger.swing;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.StandardSymbolOptionsPanel;

public class TriggerSymbolPanel extends StandardSymbolOptionsPanel {

	private JPanel mainPanel;
	
	private JCheckBox showFlow, showBounds, showText;
	
	private RiverTriggerSymbolChooser triggerSymbolChooser;

	public TriggerSymbolPanel(StandardSymbolManager standardSymbolManager,
			RiverTriggerSymbolChooser triggerSymbolChooser) {
		super(standardSymbolManager, triggerSymbolChooser);
		this.triggerSymbolChooser = triggerSymbolChooser;
		mainPanel = new JPanel(new BorderLayout());
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new BoxLayout(sPanel, BoxLayout.Y_AXIS));
		sPanel.setBorder(new TitledBorder("Additional options"));
		showFlow = new JCheckBox("Show flow");
		showBounds = new JCheckBox("Show boundaries");
		showText = new JCheckBox("Show labels");
		sPanel.add(showFlow);
		sPanel.add(showBounds);
		sPanel.add(showText);
		
		showFlow.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				enableText();
			}
		});
		showBounds.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				enableText();
			}
		});
		
		mainPanel.add(sPanel, BorderLayout.SOUTH);
		mainPanel.add(super.getMainPanel(), BorderLayout.CENTER);
	}

	protected void enableText() {
		boolean e = showFlow.isSelected() | showBounds.isSelected();
		showText.setEnabled(e);
		if (e == false)  {
			showText.setSelected(false);
		}
	}

	@Override
	public void setParams() {
		if (triggerSymbolChooser == null) {
			return;
		}
		TriggerSymbolOptions params = (TriggerSymbolOptions) triggerSymbolChooser.getSymbolOptions();
		super.setParams();
		showFlow.setSelected(params.drawFlowDirection);
		showBounds.setSelected(params.drawTriggerboundaries);
		showText.setSelected(params.showLabels);
	}

	@Override
	public boolean getParams() {
		if (triggerSymbolChooser == null) {
			return true;
		}
		TriggerSymbolOptions params = (TriggerSymbolOptions) triggerSymbolChooser.getSymbolOptions();
		params.drawFlowDirection = showFlow.isSelected();
		params.drawTriggerboundaries = showBounds.isSelected();
		params.showLabels = showText.isSelected();
		
		return super.getParams();
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}



}
