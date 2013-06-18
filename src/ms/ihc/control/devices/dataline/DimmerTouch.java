package ms.ihc.control.devices.dataline;

import java.io.IOException;
import java.util.HashMap;

import ms.ihc.control.viewer.R;
import ms.ihc.control.viewer.SoapImpl;
import ms.ihc.control.viewer.ResourceAdapter.ViewHolder;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.devices.wireless.ioResource;
import ms.ihc.control.valueTypes.DeviceType;
import ms.ihc.control.valueTypes.WSBooleanValue;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DimmerTouch extends ioResource implements IHCResource, java.io.Serializable{
	
	private int deviceID;
	private String name = "";
	private DeviceType type = DeviceType.INPUT_OUTPUT;
	private Boolean state = false;
	private int dimmerValue;
	private String position;
	private final static Boolean isDimmable = true;
	private int onOffId;
	private String location;
	
	public void setResourceValue(int resourceId, Object value) {
		if(resourceId == onOffId)
			this.state = (Boolean)value;
	}


	public HashMap<Integer, IHCResource> getResourceIds(HashMap<Integer, IHCResource> resourceIdsMap) {
		resourceIdsMap.put(this.onOffId,this);
		return resourceIdsMap;
	}
	
	public void setDeviceId(int id) {
		this.deviceID= id;
	}

	public int getDeviceId() {
		return this.deviceID;
	}

	public void setName(String name) {
		this.name=name;

	}

	public String getName() {
		return this.name;
	}

	public DeviceType type() {
		return this.type;
	}

	public void setState(Boolean state) {
		this.state=state;
	}

	public Boolean getState() {
		return this.state;
	}

	public void setDimmerValue(SoapImpl ihcCtrl) {
		//this.dimmerValue = dimmerValue;
	}

	public int getDimmerValue() {
		return this.dimmerValue;
	}

	public void setPosition(String position) {
		this.position=position;
	}

	public String getPosition() {
		return this.position;
	}

	public Boolean getIsDimmable() {
		return isDimmable;
	}

	public void setupResources(XmlPullParser xpp, String endTag) {
		String tagname = "";
		try 
		{
			do xpp.next();
			while(xpp.getName() == null);
			tagname = xpp.getName();
		
			while(tagname != endTag)
			{
				if(xpp.getEventType() == XmlPullParser.END_TAG)
				{
					
				}
				else if(xpp.getName().equals("dataline_output"))
				{
					if(xpp.getAttributeValue(null, "name").equals("Touch"))
						this.onOffId = Integer.parseInt(xpp.getAttributeValue(null, "id").replace("_0x", ""), 16);	
				}
			
				do xpp.next();
				while(xpp.getName() == null);
				tagname = xpp.getName();
			}
		}
		catch (XmlPullParserException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setInputClicked(boolean OnOff, int inputID, SoapImpl ihcCtrl) {
		
	}
	
	@Override
	public void inputClicked(int inputID, SoapImpl ihcCtrl) {
		Boolean value = true;
		
		if(inputID == 2)
		{
			value = false;			
		}
		
		String METHOD_NAME = "setResourceValue1";
		WSBooleanValue wsBooleanVal = new WSBooleanValue(value);
		PropertyInfo pi = new PropertyInfo();
        pi.setName("value");
        pi.setValue(wsBooleanVal);
        pi.setType(wsBooleanVal);
        pi.setNamespace("utcs.values");

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty(pi);
		request.addProperty("typeString", "resource_output");
		request.addProperty("resourceID", this.onOffId);
		request.addProperty("isValueRuntime", "true");
		ihcCtrl.setResourceBooleanValue(request, "WSBooleanValue", wsBooleanVal);
		
		
	}

	public View getView(LayoutInflater layoutInf, SoapImpl ihcCtrl) {
		View ConvertView = layoutInf.inflate(R.layout.panel2button, null);
		ViewHolder holder = new ViewHolder();
        holder.position = (TextView)ConvertView.findViewById(R.id.position);
        holder.button1 = (Button)ConvertView.findViewById(R.id.Button01);
        holder.button2 = (Button)ConvertView.findViewById(R.id.Button02);
        holder.button1.setOnClickListener(this);
        holder.button2.setOnClickListener(this);
        holder.deviceName = (TextView)ConvertView.findViewById(R.id.devicename);
        holder.deviceName.setText(this.name);
        holder.favouriteImg = (ImageView)ConvertView.findViewById(R.id.favourite_img);
        if(isFavourite)
        	holder.favouriteImg.setVisibility(ImageView.VISIBLE);
        else
        	holder.favouriteImg.setVisibility(ImageView.GONE);
        
        // Set values
        if(this.state)
        	holder.button1.setBackgroundResource(R.drawable.on_on);
        holder.button1.setTag(ihcCtrl);
        holder.button2.setTag(ihcCtrl);
        if(this.location !="")
        	holder.position.setText(this.position + " (" + this.location + ")");
        else
        	holder.position.setText(this.position);
        
        ConvertView.setTag(holder);
        return ConvertView;
	}
	
	public View updateView(View convertView) {
		return convertView;
	}


	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setAsFavourite() {
		isFavourite = true;		
	}

	public void removeAsFavourite() {
		isFavourite = false;	
	}

	public Boolean isFavourite() {
		return isFavourite;
	}

}
