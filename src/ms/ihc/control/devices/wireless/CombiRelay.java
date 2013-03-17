package ms.ihc.control.devices.wireless;

import java.io.IOException;
import java.util.HashMap;

import ms.ihc.control.viewer.R;
import ms.ihc.control.viewer.SoapImpl;
import ms.ihc.control.viewer.ResourceAdapter.ViewHolder;
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

public class CombiRelay extends ioResource implements IHCResource, java.io.Serializable {

	private int deviceID;
	private int resourceID;
	private String name = "";
	private DeviceType type = DeviceType.INPUT_OUTPUT;
	private Boolean state = false;
	private int dimmerValue;
	private String position;
	private final static Boolean isDimmable = false;
	private int onOffId;
	private String location;
	
	private int upper_left_id;
	private int lower_left_id;
	private int upper_right_id;
	private int lower_right_id;
	
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
				else if(tagname.equals("airlink_input"))
				{
					String val = xpp.getAttributeValue(null, "id");
					if(xpp.getAttributeValue(null,"address_channel").equals("_0x1"))
						this.upper_left_id =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(xpp.getAttributeValue(null,"address_channel").equals("_0x2"))
						this.upper_right_id =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(xpp.getAttributeValue(null,"address_channel").equals("_0x3"))
						this.lower_left_id =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(xpp.getAttributeValue(null,"address_channel").equals("_0x4"))
						this.lower_right_id =  Integer.parseInt(val.replace("_0x", ""), 16);
				}
				else if(tagname.equals("airlink_relay"))
				{
					String val = xpp.getAttributeValue(null, "id");
					if(xpp.getAttributeValue(null,"address_channel").equals("_0x1"))
						this.onOffId = Integer.parseInt(val.replace("_0x", ""), 16);
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
		int resourceClicked = 0;
		if(inputID == 3)
			resourceClicked = this.upper_left_id;
		if(inputID == 4)
			resourceClicked = this.upper_right_id;
		if(inputID == 5)
			resourceClicked = this.lower_left_id;
		if(inputID == 6)
			resourceClicked = this.lower_right_id;
			
			
			
		String METHOD_NAME = "setResourceValues1";
		WSBooleanValue Val = new WSBooleanValue(OnOff);
	
		PropertyInfo prop = new PropertyInfo();
		prop.setName("value");
		prop.setValue(Val);
		prop.setType(Val);
		prop.setNamespace("utcs.values");
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty(prop);
		request.addProperty("resourceID", String.valueOf(resourceClicked));
		request.addProperty("isValueRuntime", "true");
		
		PropertyInfo arrayItem = new PropertyInfo();
		arrayItem.setName("arrayItem");
		arrayItem.setValue(request);
		
		SoapObject requests = new SoapObject(NAMESPACE, METHOD_NAME);
		requests.addProperty(arrayItem);

		ihcCtrl.setResourceBooleanValues(requests, "WSBooleanValue", Val);
		
	}
	
	@Override
	public void inputClicked(int inputID, SoapImpl ihcCtrl) {
		int resourceClicked = 0;
		
		
		if(inputID == 1 || inputID == 2)
		{
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
		else
		{
			if(inputID == 3)
				resourceClicked = this.upper_left_id;
			if(inputID == 4)
				resourceClicked = this.upper_right_id;
			if(inputID == 5)
				resourceClicked = this.lower_left_id;
			if(inputID == 6)
				resourceClicked = this.lower_right_id;
				
			String METHOD_NAME = "setResourceValues1";
			WSBooleanValue trueVal = new WSBooleanValue(true);
			WSBooleanValue falseVal = new WSBooleanValue(false);
		
			PropertyInfo propOn = new PropertyInfo();
			propOn.setName("value");
			propOn.setValue(trueVal);
			propOn.setType(trueVal);
			propOn.setNamespace("utcs.values");
			
			PropertyInfo propOff = new PropertyInfo();
			propOff.setName("value");
			propOff.setValue(falseVal);
			propOff.setType(falseVal);
			propOff.setNamespace("utcs.values");
	
			SoapObject requestOn = new SoapObject(NAMESPACE, METHOD_NAME);
			requestOn.addProperty(propOn);
			requestOn.addProperty("resourceID", String.valueOf(resourceClicked));
			requestOn.addProperty("isValueRuntime", "true");
			
			SoapObject requestOff = new SoapObject(NAMESPACE, METHOD_NAME);
			requestOff.addProperty(propOff);
			requestOff.addProperty("resourceID", String.valueOf(resourceClicked));
			requestOff.addProperty("isValueRuntime", "true");
			
			PropertyInfo arrayItem = new PropertyInfo();
			arrayItem.setName("arrayItem");
			arrayItem.setValue(requestOn);
			
			PropertyInfo arrayItem2 = new PropertyInfo();
			arrayItem2.setName("arrayItem");
			arrayItem2.setValue(requestOff);
			
			SoapObject requests = new SoapObject(NAMESPACE, METHOD_NAME);
			requests.addProperty(arrayItem);
			requests.addProperty(arrayItem2);
	
			ihcCtrl.setResourceBooleanValues(requests, "WSBooleanValue", falseVal);
		}
		
	}

	public View getView(LayoutInflater layoutInf, SoapImpl ihcCtrl) {
		View ConvertView = layoutInf.inflate(R.layout.combi4relay, null);
		ViewHolder holder = new ViewHolder();
		holder.position = (TextView)ConvertView.findViewById(R.id.position);
        holder.button1 = (Button)ConvertView.findViewById(R.id.Button01);
        holder.button2 = (Button)ConvertView.findViewById(R.id.Button02);
        holder.button3 = (Button)ConvertView.findViewById(R.id.Button03);
        holder.button4 = (Button)ConvertView.findViewById(R.id.Button04);
        holder.button5 = (Button)ConvertView.findViewById(R.id.Button05);
        holder.button6 = (Button)ConvertView.findViewById(R.id.Button06);
        holder.deviceName = (TextView)ConvertView.findViewById(R.id.devicename);
        holder.deviceName.setText(this.name);
        holder.favouriteImg = (ImageView)ConvertView.findViewById(R.id.favourite_img);
        if(isFavourite)
        	holder.favouriteImg.setVisibility(ImageView.VISIBLE);
        else
        	holder.favouriteImg.setVisibility(ImageView.GONE);
        
        // Set values
        if(this.state)
        	holder.button1.getBackground().setColorFilter(0xFF00FF00, Mode.MULTIPLY); 
        
        holder.button1.setText("On");
        holder.button1.setTag(ihcCtrl);
        holder.button2.setText("Off");
        holder.button2.setTag(ihcCtrl);
        holder.button3.setText(R.string.upperleft);
        holder.button3.setTag(ihcCtrl);
        holder.button4.setText(R.string.upperright);
        holder.button4.setTag(ihcCtrl);
        holder.button5.setText(R.string.lowerleft);
        holder.button5.setTag(ihcCtrl);
        holder.button6.setText(R.string.lowerright);
        holder.button6.setTag(ihcCtrl);
        holder.button1.setOnClickListener(this);
        holder.button2.setOnClickListener(this);
        holder.button3.setOnClickListener(this);
        holder.button4.setOnClickListener(this);
        holder.button5.setOnClickListener(this);
        holder.button6.setOnClickListener(this);

        holder.button3.setOnLongClickListener(this);
        holder.button4.setOnLongClickListener(this);
        holder.button5.setOnLongClickListener(this);
        holder.button6.setOnLongClickListener(this);
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
