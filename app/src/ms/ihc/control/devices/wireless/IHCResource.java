package ms.ihc.control.devices.wireless;

import java.util.HashMap;

import ms.ihc.control.devices.dataline.OutputResource;
import ms.ihc.control.devices.dataline.Panel12Button;
import ms.ihc.control.devices.dataline.Panel1Button;
import ms.ihc.control.devices.dataline.Panel2Button_Fuga;
import ms.ihc.control.devices.dataline.Panel4Button_Fuga;
import ms.ihc.control.devices.dataline.Panel6Button_Fuga;
import ms.ihc.control.devices.dataline.Panel8Button;
import ms.ihc.control.devices.dataline.TemperatureSensor;
import ms.ihc.control.valueTypes.DeviceType;
import ms.ihc.control.viewer.IhcManager;

import org.xmlpull.v1.XmlPullParser;

import android.view.LayoutInflater;
import android.view.View;


public interface IHCResource extends View.OnClickListener  {
	
	public static final int LAMPEUDTAGDIMMER = 0x4304;
	public static final int KOMBIDIMMER4TAST = 0x4406;
	public static final int KOMBIRELAY4TAST = 0x4404;
	public static final int LAMPEUDTAGRELAY = 0x4202;
	public static final int STIKKONTAKT = 0x4201;
	public static final int PUCKRELAY = 0x4205;
	public static final int TASTTRYK2 = 0x4101;
	public static final int TASTTRYK4 = 0x4102;
	public static final int TASTTRYK6 = 0x4103;
	public static final int LAMPEDIMMERTOUCH = 0x4302;
	public static final int MOBIL_STIKKONTAKT_DIMMER = 0x4303;
	public static final int MOBIL_STIKKONTAKT_REL = 0x4204;
	public static final int UNIVERSALDIMMER = 0x4306;
	public static final int UNUVERSALDIMMER_NKR = 0x4305;
	public static final int UNIVERSALRELAY = 0x4203;
	public static final int KEYRING = 0x4105;
	public static final int FJERNBETJENING = 0x4104;
	// ---------- Dataline components
	public static final int TASTTRYK2_FUGA = 0x2101;
	public static final int TASTTRYK4_FUGA = 0x2102;
	public static final int TASTTRYK6_FUGA = 0x2103;
	public static final int TELESTAT = 0x220a;
	public static final int CIRKULATIONSPUMPE = 0x220b;
	public static final int VENTILATOR = 0x220c;
	public static final int VANDVARMER = 0x220d;
	public static final int RADIATOR = 0x220e;
	public static final int ELGULVVARME = 0x220f;
	public static final int STIKKONTAKT_DA = 0x2201;
	public static final int LAMPEUDTAG = 0x2202;
	public static final int MAGNETVENTIL_NC = 0x2205;
	public static final int MAGNETVENTIL_NO = 0x2206;
	
	public static final int DOORLOCK = 0x2208;
	public static final int RINGEKLOKKE = 0x2209;
	public static final int HEATER = 0x2210;
	public static final int LYDGIVER_EKSTERN = 0x2204;
	
	public static final int TASTTRYK2_1D_FUGA = 0x2104;
	public static final int TASTTRYK4_2D_FUGA = 0x2105;
	public static final int TASTTRYK4_4D_OPUS = 0x2106;
	public static final int TASTTRYK6_3D_FUGA = 0x2107;
	public static final int TASTTRYK4_4D_FUGA = 0x2108;
	public static final int ELKO_TEMPSENSOR = 0x2122;
	public static final int TASTTRYK2_4D_FUGA = 0x2130;
	public static final int TEMPSENSOR = 0x2124;
	
	public static final int TASTTRYK12 = 0x21312;
	public static final int GENERISK_INPUT = 0x2701;
	public static final int GENERISK_OUTPUT_WS = 0x2702;
	public static final int GENERISK_OUTPUT = 0x2703;
	
	public static final int TASTTRYK1 = 0x2119;
	public static final int TASTTRYK8_4D = 0x211e;
	
	
	// Needs implementation ---->
	public static final int TASTTRYK3_3D_FUGA = 0x2132;
	public static final int LYDGIVER_INTERN = 0x2203;
	public static final int OUTPUT_8MODUL = 0x2207;
	
	// <----------------- |
	public static final String NAMESPACE = "utcs";
	
	public static final HashMap<Integer, Class> resourceTypes = new HashMap<Integer, Class>()
	{
		{
			put(STIKKONTAKT, Outlet.class); 
			put(LAMPEUDTAGDIMMER,LampOutletDimable.class);
			put(LAMPEUDTAGRELAY,LampOutlet.class);
			put(PUCKRELAY,PuckRelay.class);
			put(TASTTRYK2,Panel2Button.class);
			put(TASTTRYK4,Panel4Button.class);
			put(TASTTRYK6,Panel6Button.class);
			put(UNIVERSALRELAY,UniRelay.class);
			put(KEYRING,Keyring.class);
			put(FJERNBETJENING,RemoteControl.class);
			put(UNIVERSALDIMMER,UniDimmer.class);
			put(KOMBIDIMMER4TAST,CombiDimmer.class);
			put(KOMBIRELAY4TAST,CombiRelay.class);
			put(TASTTRYK2_FUGA,Panel2Button_Fuga.class);
			put(TASTTRYK4_FUGA,Panel4Button_Fuga.class);
			put(TASTTRYK6_FUGA,Panel6Button_Fuga.class);
			put(TASTTRYK2_1D_FUGA, Panel2Button_Fuga.class);
			put(TASTTRYK4_2D_FUGA, Panel4Button_Fuga.class);
			put(TASTTRYK4_4D_OPUS, Panel4Button_Fuga.class);
			put(TASTTRYK6_3D_FUGA, Panel6Button_Fuga.class);
			put(TASTTRYK4_4D_FUGA, Panel4Button_Fuga.class);
			put(TASTTRYK2_4D_FUGA, Panel2Button_Fuga.class);
			put(TELESTAT,OutputResource.class);
			put(CIRKULATIONSPUMPE,OutputResource.class);
			put(VENTILATOR,OutputResource.class);
			put(VANDVARMER,OutputResource.class);
			put(RADIATOR,OutputResource.class);
			put(ELGULVVARME,OutputResource.class);
			put(STIKKONTAKT_DA,OutputResource.class);
			put(LAMPEUDTAG,OutputResource.class);
			put(LYDGIVER_EKSTERN,OutputResource.class);
			put(MAGNETVENTIL_NC,OutputResource.class);
			put(MAGNETVENTIL_NO,OutputResource.class);
			put(DOORLOCK,OutputResource.class);
			put(RINGEKLOKKE,OutputResource.class);
			
			put(TEMPSENSOR,TemperatureSensor.class);
			put(ELKO_TEMPSENSOR,TemperatureSensor.class);
			// New added 16-02-2011
			put(MOBIL_STIKKONTAKT_DIMMER,UniDimmer.class);
			put(TASTTRYK12,Panel12Button.class);
			put(GENERISK_OUTPUT_WS,OutputResource.class);
			put(GENERISK_OUTPUT,OutputResource.class);
			put(GENERISK_INPUT,Panel1Button.class);
			put(TASTTRYK1,Panel1Button.class);
			put(TASTTRYK8_4D,Panel8Button.class);
			// New added 29-03-2011
			put(MOBIL_STIKKONTAKT_REL,Outlet.class);
			put(UNUVERSALDIMMER_NKR,UniDimmer_NKR.class);
			put(HEATER,OutputResource.class);
			put(LAMPEDIMMERTOUCH, LampDimTouch.class);
			//put(TASTTRYK3_3D_FUGA, Panel3Button_Fuga.class);
		}
	};

	
	public void setupResources(XmlPullParser xpp, String endTag);
	public void setResourceValue(int resourceId, Object value);
	public HashMap<Integer, IHCResource> getResourceIds(HashMap<Integer, IHCResource> resourceIdsMap);
	
	public void setDeviceId(int id);
	public int getDeviceId();
	
	public void setName(String name) ;
	
	public String getName();
	public DeviceType type();
	public void setState(Boolean state);
	public Boolean getState();
	public void setAsFavourite();
	public void removeAsFavourite();
	public Boolean isFavourite();
	
	
	public void setDimmerValue(IhcManager ihcCtrl);
	public int getDimmerValue();
	
	public void setPosition(String position);
	public String getPosition();
	
	public void setLocation(String location);

	public Boolean getIsDimmable();
	
	public void inputClicked(int inputID, IhcManager ihcCtrl);
	
	public View getView(LayoutInflater layoutInf, IhcManager ihcCtrl);
	public View updateView(View convertView);

}
