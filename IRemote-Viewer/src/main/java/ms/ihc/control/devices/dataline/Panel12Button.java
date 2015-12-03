package ms.ihc.control.devices.dataline;

import java.io.IOException;

import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;
import ms.ihc.control.Resource.ResourceAdapter.ViewHolder;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.devices.wireless.ioResource;
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

public class Panel12Button extends ioResource implements IHCResource, java.io.Serializable {

	private int deviceID;
	private String name = "";
	private DeviceType type = DeviceType.INPUT;
	private Boolean state = false;
	private int dimmerValue;
	private String position;
	private final static Boolean isDimmable = false;
	private String location;
	
	private int one;
	private int two;
	private int three;
	private int four;
	private int five;
	private int six;
	private int seven;
	private int eight;
	private int nine;
	private int ten;
	private int eleven;
	private int twelve;
	
	public void setResourceValue(int resourceId, Object value) {
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


	public void setupResources(XmlPullParser xpp, String endTag) 
	{
		int datalineId = 1;
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
				else if(tagname.equals("dataline_input"))
				{
					String val = xpp.getAttributeValue(null, "id");
					if(datalineId == 1)
						this.one =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 2)
						this.four =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 3)
						this.two =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 4)
						this.five =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 5)
						this.three =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 6)
						this.six =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 7)
						this.seven =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 8)
						this.eight =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 9)
						this.nine =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 10)
						this.ten =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 11)
						this.eleven =  Integer.parseInt(val.replace("_0x", ""), 16);
					else if(datalineId == 12)
						this.twelve =  Integer.parseInt(val.replace("_0x", ""), 16);
					
					datalineId++;
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
			resourceClicked = this.one;
		if(inputID == 2)
			resourceClicked = this.four;
		if(inputID == 3)
			resourceClicked = this.two;
		if(inputID == 4)
			resourceClicked = this.five;
		if(inputID == 5)
			resourceClicked = this.three;
		if(inputID == 6)
			resourceClicked = this.six;
		if(inputID == 7)
			resourceClicked = this.seven;
		if(inputID == 8)
			resourceClicked = this.eight;
		if(inputID == 9)
			resourceClicked = this.nine;
		if(inputID == 10)
			resourceClicked = this.ten;
		if(inputID == 11)
			resourceClicked = this.eleven;
		if(inputID == 12)
			resourceClicked = this.twelve;
		
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
			resourceClicked = this.one;
		if(inputID == 2)
			resourceClicked = this.four;
		if(inputID == 3)
			resourceClicked = this.two;
		if(inputID == 4)
			resourceClicked = this.five;
		if(inputID == 5)
			resourceClicked = this.three;
		if(inputID == 6)
			resourceClicked = this.six;
		if(inputID == 7)
			resourceClicked = this.seven;
		if(inputID == 8)
			resourceClicked = this.eight;
		if(inputID == 9)
			resourceClicked = this.nine;
		if(inputID == 10)
			resourceClicked = this.ten;
		if(inputID == 11)
			resourceClicked = this.eleven;
		if(inputID == 12)
			resourceClicked = this.twelve;
			
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
		View ConvertView = layoutInf.inflate(R.layout.panel12button, null);
		ViewHolder holder = new ViewHolder();
		holder.position = (TextView)ConvertView.findViewById(R.id.position);
        holder.button1 = (Button)ConvertView.findViewById(R.id.Button01);
        holder.button2 = (Button)ConvertView.findViewById(R.id.Button02);
        holder.button3 = (Button)ConvertView.findViewById(R.id.Button03);
        holder.button4 = (Button)ConvertView.findViewById(R.id.Button04);
        holder.button5 = (Button)ConvertView.findViewById(R.id.Button05);
        holder.button6 = (Button)ConvertView.findViewById(R.id.Button06);
        holder.button7 = (Button)ConvertView.findViewById(R.id.Button07);
        holder.button8 = (Button)ConvertView.findViewById(R.id.Button08);
        holder.button9 = (Button)ConvertView.findViewById(R.id.Button09);
        holder.button10 = (Button)ConvertView.findViewById(R.id.Button10);
        holder.button11 = (Button)ConvertView.findViewById(R.id.Button11);
        holder.button12 = (Button)ConvertView.findViewById(R.id.Button12);
        holder.deviceName = (TextView)ConvertView.findViewById(R.id.devicename);
        holder.deviceName.setText(this.name);
        holder.favouriteImg = (ImageView)ConvertView.findViewById(R.id.favourite_img);
        if(isFavourite)
        	holder.favouriteImg.setVisibility(ImageView.VISIBLE);
        else
        	holder.favouriteImg.setVisibility(ImageView.GONE);

        // Set values
        holder.button1.setText("1");
        holder.button1.setTag(ihcCtrl);
        holder.button2.setText("2");
        holder.button2.setTag(ihcCtrl);
        holder.button3.setText("3");
        holder.button3.setTag(ihcCtrl);
        holder.button4.setText("4");
        holder.button4.setTag(ihcCtrl);
        holder.button5.setText("5");
        holder.button5.setTag(ihcCtrl);
        holder.button6.setText("6");
        holder.button6.setTag(ihcCtrl);
        holder.button7.setText("7");
        holder.button7.setTag(ihcCtrl);
        holder.button8.setText("8");
        holder.button8.setTag(ihcCtrl);
        holder.button9.setText("9");
        holder.button9.setTag(ihcCtrl);
        holder.button10.setText("10");
        holder.button10.setTag(ihcCtrl);
        holder.button11.setText("11");
        holder.button11.setTag(ihcCtrl);
        holder.button12.setText("12");
        holder.button12.setTag(ihcCtrl);
        holder.button1.setOnClickListener(this);
        holder.button2.setOnClickListener(this);
        holder.button3.setOnClickListener(this);
        holder.button4.setOnClickListener(this);
        holder.button5.setOnClickListener(this);
        holder.button6.setOnClickListener(this);
        holder.button7.setOnClickListener(this);
        holder.button8.setOnClickListener(this);
        holder.button9.setOnClickListener(this);
        holder.button10.setOnClickListener(this);
        holder.button11.setOnClickListener(this);
        holder.button12.setOnClickListener(this);
        holder.button1.setOnLongClickListener(this);
        holder.button2.setOnLongClickListener(this);
        holder.button3.setOnLongClickListener(this);
        holder.button4.setOnLongClickListener(this);
        holder.button5.setOnLongClickListener(this);
        holder.button6.setOnLongClickListener(this);
        holder.button7.setOnLongClickListener(this);
        holder.button8.setOnLongClickListener(this);
        holder.button9.setOnLongClickListener(this);
        holder.button10.setOnLongClickListener(this);
        holder.button11.setOnLongClickListener(this);
        holder.button12.setOnLongClickListener(this);
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
