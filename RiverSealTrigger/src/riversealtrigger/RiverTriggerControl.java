package riversealtrigger;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamController;
import PamController.PamSettingManager;
import PamController.PamSettings;
import offlineProcessing.OLProcessDialog;
import offlineProcessing.OfflineTaskGroup;
import riversealtrigger.offline.TriggerOfflineTask;
import riversealtrigger.swing.RiverTriggerDialog;
import tritechplugins.detect.track.TrackLinkDataBlock;

public class RiverTriggerControl extends PamControlledUnit implements PamSettings {

	public static final String unitType = "River Trigger Control";
	
	private RiverTriggerParams triggerParams = new RiverTriggerParams();

	private RiverTriggerProcess triggerProcess;
	
	public RiverTriggerControl(String unitName) {
		super(unitType, unitName);
		
		PamSettingManager.getInstance().registerSettings(this);
		
		triggerProcess = new RiverTriggerProcess(this);
		addPamProcess(triggerProcess);
	}

	@Override
	public Serializable getSettingsReference() {
		return triggerParams;
	}

	public RiverTriggerParams getTriggerParams() {
		return triggerParams;
	}

	@Override
	public long getSettingsVersion() {
		return RiverTriggerParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		this.triggerParams = (RiverTriggerParams) pamControlledUnitSettings.getSettings();
		this.triggerParams = this.triggerParams.clone();
		return true;
	}

	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenu menu = new JMenu(this.getUnitName());
		
		JMenuItem menuItem = new JMenuItem("Settings...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSettings(parentFrame);
			}

		});
		menu.add(menuItem);
		if (isViewer()) {
			menuItem = new JMenuItem("Run offline ...");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					runOffline(parentFrame);
				}
			});
			menu.add(menuItem);
		}
		
		return menu;
	}

	protected void runOffline(Frame parentFrame) {
		OfflineTaskGroup taskGroup = new OfflineTaskGroup(this, getUnitName());
		taskGroup.addTask(new TriggerOfflineTask(this, (TrackLinkDataBlock) triggerProcess.getParentDataBlock()));
		OLProcessDialog procdialog = new OLProcessDialog(parentFrame, taskGroup, "River sonar trigger");
		procdialog.setVisible(true);
	}

	public void showSettings(Frame parentFrame) {
		RiverTriggerParams newSettings = RiverTriggerDialog.showDialog(parentFrame, triggerParams);
		if (newSettings != null) {
			this.triggerParams = newSettings;
			triggerProcess.prepareProcess();
		}
		
	}

	@Override
	public void notifyModelChanged(int changeType) {
		super.notifyModelChanged(changeType);
		if (changeType == PamController.INITIALIZATION_COMPLETE) {
			triggerProcess.prepareProcess();
		}
		if (changeType == PamController.DATA_LOAD_COMPLETE) {
			triggerProcess.linkTriggerTracks();
		}
	}

	/**
	 * @return the triggerProcess
	 */
	public RiverTriggerProcess getTriggerProcess() {
		return triggerProcess;
	}

}
