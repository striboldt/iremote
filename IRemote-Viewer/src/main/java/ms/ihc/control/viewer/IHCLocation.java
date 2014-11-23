package ms.ihc.control.viewer;

import java.util.ArrayList;

import ms.ihc.control.devices.wireless.IHCResource;

public class IHCLocation implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ID;
	private String Name;
    private ArrayList<IHCResource> resources = new ArrayList<IHCResource>();

    public ArrayList<IHCResource> getResources() {
        return resources;
    }

	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	} 

}
