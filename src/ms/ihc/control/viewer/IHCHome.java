package ms.ihc.control.viewer;

import java.util.ArrayList;

public class IHCHome implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String Owner;
	ArrayList<IHCLocation> locations = new ArrayList<IHCLocation>();
	
	public ArrayList<IHCLocation> getLocations() {
		return locations;
	}
	public void setLocations(ArrayList<IHCLocation> locations) {
		this.locations = locations;
	}

}
