package ms.ihc.control.devices.wireless;

import java.io.IOException;

import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;
import ms.ihc.control.resource.ResourceAdapter.ViewHolder;
import ms.ihc.control.valueTypes.DeviceType;
import ms.ihc.control.valueTypes.WSBooleanValue;
import ms.ihc.control.valueTypes.WSIntegerValue;

import ms.ihc.control.ksoap2.serialization.PropertyInfo;
import ms.ihc.control.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class LampDimTouch extends ioResource implements IHCResource, java.io.Serializable, OnSeekBarChangeListener {
	private int deviceID;
	private String name = "NA";
	private DeviceType type = DeviceType.OUTPUT;
	private Boolean state = false;
	private int dimmerValue;
	private int lightIndicationID;
	private String position;
	private final static Boolean isDimmable = true;
	private int dimmableId;
	private String location;
	private int onOffId;
	
	public void setResourceValue(int resourceId, Object value) {
		if(resourceId == lightIndicationID)
			this.state = (Boolean)value;
		else if(resourceId == dimmableId)
			this.dimmerValue = (Integer) value;
	}

	public SparseArray<IHCResource> getResourceIds(SparseArray<IHCResource> resourceIdsMap) {
		resourceIdsMap.put(lightIndicationID,this);
		resourceIdsMap.put(dimmableId,this);
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

	@Override
	public void setDimmerValue(ConnectionManager ihcCtrl) {
		String METHOD_NAME = "setResourceValue1";
		WSIntegerValue wsIntegerVal = new WSIntegerValue(this.dimmerValue, 0, 100);
		PropertyInfo pi = new PropertyInfo();
        pi.setName("value");
        pi.setValue(wsIntegerVal);
        pi.setType(wsIntegerVal);
        pi.setNamespace("utcs.values");

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty(pi);
		request.addProperty("typeString", "airlink_dimming");
		request.addProperty("resourceID", this.dimmableId);
		request.addProperty("isValueRuntime", "true");
		ihcCtrl.setResourceBooleanValue(request, "WSIntegerValue", wsIntegerVal);
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
				else if(xpp.getName().equals("airlink_dimmer_touch"))
				{
					this.onOffId = Integer.parseInt(xpp.getAttributeValue(null, "id").replace("_0x", ""), 16);
				}
				else if(xpp.getName().equals("airlink_dimming"))
				{
					this.dimmableId = Integer.parseInt(xpp.getAttributeValue(null, "id").replace("_0x", ""), 16);			
				}			
				else if(xpp.getName().equals("light_indication"))
				{
					this.lightIndicationID = Integer.parseInt(xpp.getAttributeValue(null, "id").replace("_0x", ""), 16);	
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
	public void inputClicked(int inputID, ConnectionManager ihcCtrl) {
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

	public View getView(LayoutInflater layoutInf, ConnectionManager ihcCtrl) {
		View ConvertView = layoutInf.inflate(R.layout.dimmable1button, null);
		ViewHolder holder = new ViewHolder();
		holder.position = (TextView)ConvertView.findViewById(R.id.position);
        holder.button1 = (Button)ConvertView.findViewById(R.id.Button01);
        holder.seekbar = (SeekBar)ConvertView.findViewById(R.id.SeekBar01);
        holder.deviceName = (TextView)ConvertView.findViewById(R.id.devicename);
        holder.deviceName.setText(this.name);
        holder.favouriteImg = (ImageView)ConvertView.findViewById(R.id.favourite_img);
        if(isFavourite)
        	holder.favouriteImg.setVisibility(ImageView.VISIBLE);
        else
        	holder.favouriteImg.setVisibility(ImageView.GONE);
        
        // Set values
        if(this.state)
        	holder.button1.setBackgroundResource(R.drawable.round_button_io_on);
        	
        holder.button1.setTag(ihcCtrl);
        holder.button1.setOnClickListener(this);
        holder.button1.setOnLongClickListener(this);
        if(this.location !="")
        	holder.position.setText(this.position + " (" + this.location + ")");
        else
        	holder.position.setText(this.position);
        
        holder.seekbar.setOnSeekBarChangeListener(this);
        holder.seekbar.setProgress(this.dimmerValue);
        holder.seekbar.setTag(ihcCtrl);
        ConvertView.setTag(holder); 
        return ConvertView;
	}
	
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser)
			this.dimmerValue = progress;
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		ConnectionManager ihcCtrl = (ConnectionManager)seekBar.getTag();
		ihcCtrl.isInTouchMode = true;
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		ConnectionManager ihcCtrl = (ConnectionManager)seekBar.getTag();
		ihcCtrl.isInTouchMode = false;
		new SetDimmerEvent().execute(ihcCtrl);
	}

	public View updateView(View convertView) {
		((ViewHolder)convertView.getTag()).seekbar.setProgress(this.dimmerValue);
		return convertView;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public void setInputClicked(boolean OnOff, int inputID, ConnectionManager ihcCtrl) {
		// TODO Auto-generated method stub
		
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
