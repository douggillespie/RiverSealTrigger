package riversealtrigger;

import PamModel.PamDependency;
import PamModel.PamPluginInterface;

public class RiverTriggerPlugin implements PamPluginInterface {

	private String jarFile;

	@Override
	public String getDefaultName() {
		return "River seal trigger";
	}

	@Override
	public String getHelpSetName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;		
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "Doug Gillespie";
	}

	@Override
	public String getContactEmail() {
		return "dg50@st-andrews.ac.uk";
	}

	@Override
	public String getVersion() {
		return "1.3";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "V2.2.17";
	}

	@Override
	public String getPamVerTestedOn() {
		return getPamVerDevelopedOn();
	}

	@Override
	public String getAboutText() {
		return "Trigger decision for seals in rivers";
	}

	@Override
	public String getClassName() {
		return RiverTriggerControl.class.getName();
	}

	@Override
	public String getDescription() {
		return getDefaultName();
	}

	@Override
	public String getMenuGroup() {
		return "Tritech";
	}

	@Override
	public String getToolTip() {
		return getAboutText();
	}

	@Override
	public PamDependency getDependency() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNInstances() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isItHidden() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int allowedModes() {
		// TODO Auto-generated method stub
		return 0;
	}


}
