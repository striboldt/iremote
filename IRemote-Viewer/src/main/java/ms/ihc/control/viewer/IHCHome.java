package ms.ihc.control.viewer;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;

import ms.ihc.control.devices.wireless.IHCResource;

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

	public SparseArray<IHCResource> getAllResourceIDs(){
		SparseArray<IHCResource> resourceMap = new SparseArray<>();
		Iterator<IHCLocation> iLocations = getLocations().iterator();
		while (iLocations.hasNext()) {
			IHCLocation location = iLocations.next();
			Iterator<IHCResource> iResources = location.getResources().iterator();
			while (iResources.hasNext()) {
				IHCResource resource = iResources.next();
				resourceMap = resource.getResourceIds(resourceMap);
			}
		}
		return resourceMap;
	}




}
