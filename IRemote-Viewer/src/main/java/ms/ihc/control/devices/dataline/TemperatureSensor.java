package ms.ihc.control.devices.dataline;

import java.io.IOException;
import java.math.BigDecimal;

import ms.ihc.control.valueTypes.DeviceType;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;
import ms.ihc.control.resource.ResourceAdapter.ViewHolder;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.devices.wireless.ioResource;
import ms.ihc.control.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TemperatureSensor extends ioResource implements IHCResource, java.io.Serializable{
	
	private int deviceID;
	private String name = "";
	private DeviceType type = DeviceType.INPUT;
	private Boolean state = false;
	private int dimmerValue;
	private String position;
	private int roomRsID;
	private int floorRsID;
	private String roomTemp = "0";
	private String floorTemp = "0";
	private final static Boolean isDimmable = false;
	private String location;
	
	public void setResourceValue(int resourceId, Object value) {
		if(resourceId == roomRsID)
		{
			this.roomTemp = ((SoapPrimitive)value).toString();
		}
		else if(resourceId == floorRsID)
		{
			this.floorTemp = ((SoapPrimitive)value).toString();
		}
	}

	public SparseArray<IHCResource> getResourceIds(SparseArray<IHCResource> resourceIdsMap) {
		resourceIdsMap.put(this.roomRsID,this);
		resourceIdsMap.put(this.floorRsID,this);
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

	public void setupResources(XmlPullParser xpp, String endTag) {
		String tagname = "";
		int i = 0;
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
				else if(xpp.getName().equals("resource_temperature"))
				{
				//	if(xpp.getAttributeValue(null, "name").equals("Rumtemperatur") || xpp.getAttributeValue(null, "name").equals("Room temperature"))
					//else if(xpp.getAttributeValue(null, "name").equals("Gulvtemperatur") || xpp.getAttributeValue(null, "name").equals("Floor temperature"))
					if(i==0)
					{
						this.roomRsID = Integer.parseInt(xpp.getAttributeValue(null, "id").replace("_0x", ""), 16);	
						i++;
					}
					else if(i==1)
					{
						this.floorRsID = Integer.parseInt(xpp.getAttributeValue(null, "id").replace("_0x", ""), 16);
						break;
					}
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
		
	}
	
	@Override
	public void inputClicked(int inputID, ConnectionManager ihcCtrl) {
	}

	public View getView(LayoutInflater layoutInf, ConnectionManager ihcCtrl) {
		View ConvertView = layoutInf.inflate(R.layout.temperature, null);
		ViewHolder holder = new ViewHolder();
        holder.position = (TextView)ConvertView.findViewById(R.id.position);
        holder.room = (TextView)ConvertView.findViewById(R.id.room);
        holder.floor = (TextView)ConvertView.findViewById(R.id.floor);
        holder.deviceName = (TextView)ConvertView.findViewById(R.id.devicename);
        holder.deviceName.setText(this.name);
        holder.favouriteImg = (ImageView)ConvertView.findViewById(R.id.favourite_img);
        if(isFavourite)
        	holder.favouriteImg.setVisibility(ImageView.VISIBLE);
        else
        	holder.favouriteImg.setVisibility(ImageView.GONE);
        
        // Set values
        if(this.roomTemp == null || this.roomTemp == "")
        	this.roomTemp = "0";
        BigDecimal bd = new BigDecimal( this.roomTemp );
        bd = bd.setScale( 1, BigDecimal.ROUND_HALF_UP );
        holder.room.setText(bd.toPlainString());
        
        if(this.floorTemp == null || this.floorTemp == "")
        	this.floorTemp = "0";
        bd = new BigDecimal( this.floorTemp );
        bd = bd.setScale( 1, BigDecimal.ROUND_HALF_UP );
        holder.floor.setText(bd.toPlainString());
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
