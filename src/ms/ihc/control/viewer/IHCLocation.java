package ms.ihc.control.viewer;

import java.util.ArrayList;

import ms.ihc.control.devices.wireless.IHCResource;

public class IHCLocation implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int ID;
	String Name;
	ArrayList<IHCResource> resources = new ArrayList<IHCResource>(); 

}
