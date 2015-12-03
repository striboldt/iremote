package ms.ihc.control.devices.wireless;

import java.io.IOException;

import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;
import ms.ihc.control.Resource.ResourceAdapter.ViewHolder;
import ms.ihc.control.valueTypes.DeviceType;
import ms.ihc.control.valueTypes.WSBooleanValue;

import ms.ihc.control.ksoap2.serialization.PropertyInfo;
import ms.ihc.control.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Keyring extends ioResource implements IHCResource, java.io.Serializable {

	private int deviceID;
	private int resourceID;
	private String name = "";
	private DeviceType type = DeviceType.INPUT;
	private Boolean state = false;
	private int dimmerValue;
	private String position;
	private final static Boolean isDimmable = false;
	private int dimmableId;
	private String location;
	
	private int upper_id;
	private int lower_id;
	private int left_id;
	private int right_id;
	
	public void setResourceValue(int resourceId, Object value) {
		this.resourceID = resourceId;
	}

	public SparseArray<IHCResource> getResourceIds(SparseArray<IHCResource> resourceIdsMap) {
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

	public void setDimmerValue(ConnectionManager ihcCtrl) {
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

	public void setDimmableId(int dimmableId) {
		this.dimmableId=dimmableId;

	}

	public int getDimmableId() {
		return this.dimmableId;
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
						this.upper_id =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(xpp.getAttributeValue(null,"address_channel").equals("_0x2"))
						this.lower_id =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(xpp.getAttributeValue(null,"address_channel").equals("_0x3"))
						this.left_id =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(xpp.getAttributeValue(null,"address_channel").equals("_0x4"))
						this.right_id =  Integer.parseInt(val.replace("_0x", ""), 16);							
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
	public void setInputClicked(boolean OnOff, int inputID, ConnectionManager ihcCtrl) {
		int resourceClicked = 0;
		if(inputID == 1)
			resourceClicked = this.upper_id;
		if(inputID == 2)
			resourceClicked = this.lower_id;
		if(inputID == 3)
			resourceClicked = this.left_id;
		if(inputID == 4)
			resourceClicked = this.right_id;
			
			
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
	public void inputClicked(int inputID, ConnectionManager ihcCtrl) {
		int resourceClicked = 0;
		
		if(inputID == 1)
			resourceClicked = this.upper_id;
		if(inputID == 2)
			resourceClicked = this.lower_id;
		if(inputID == 3)
			resourceClicked = this.left_id;
		if(inputID == 4)
			resourceClicked = this.right_id;
			
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


	public View getView(LayoutInflater layoutInf, ConnectionManager ihcCtrl) {
		View ConvertView = layoutInf.inflate(R.layout.panel4button, null);
		ViewHolder holder = new ViewHolder();
		holder.position = (TextView)ConvertView.findViewById(R.id.position);
        holder.button1 = (Button)ConvertView.findViewById(R.id.Button01);
        holder.button2 = (Button)ConvertView.findViewById(R.id.Button02);
        holder.button3 = (Button)ConvertView.findViewById(R.id.Button03);
        holder.button4 = (Button)ConvertView.findViewById(R.id.Button04);
        holder.deviceName = (TextView)ConvertView.findViewById(R.id.devicename);
        holder.deviceName.setText(this.name);
        holder.favouriteImg = (ImageView)ConvertView.findViewById(R.id.favourite_img);
        if(isFavourite)
        	holder.favouriteImg.setVisibility(ImageView.VISIBLE);
        else
        	holder.favouriteImg.setVisibility(ImageView.GONE);
        
        // Set values
        holder.button1.setText(R.string.up);
        holder.button1.setTag(ihcCtrl);
        holder.button2.setText(R.string.down);
        holder.button2.setTag(ihcCtrl);
        holder.button3.setText(R.string.left);
        holder.button3.setTag(ihcCtrl);
        holder.button4.setText(R.string.right);
        holder.button4.setTag(ihcCtrl);
        holder.button1.setOnClickListener(this);
        holder.button2.setOnClickListener(this);
        holder.button3.setOnClickListener(this);
        holder.button4.setOnClickListener(this);
        holder.button1.setOnLongClickListener(this);
        holder.button2.setOnLongClickListener(this);
        holder.button3.setOnLongClickListener(this);
        holder.button4.setOnLongClickListener(this);

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
