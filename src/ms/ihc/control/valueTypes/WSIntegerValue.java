package ms.ihc.control.valueTypes;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class WSIntegerValue implements KvmSerializable {

	private int integer;
	private int maximumValue;
	private int minimumValue;

	public WSIntegerValue(int integer, int maximumValue, int minimumValue)
	{
		this.integer = integer;
		this.maximumValue = maximumValue;
		this.minimumValue = minimumValue;
	}
	
	public Object getProperty(int index) {
        switch(index)
        {
        case 0:
            return this.integer;
        case 1:
            return this.maximumValue;
        case 2:
            return this.minimumValue;
        }
        
        return null;

	}

	public int getPropertyCount() {
		return 3;
	}

	public void setProperty(int index, Object value) {
        switch(index)
        {
        case 0:
            this.integer = (Integer)value;
            break;
        case 1:
            this.maximumValue = (Integer)value;
            break;
        case 2:
            this.minimumValue = (Integer)value;
            break;
        default:
            break;
        }
	}

	public void getPropertyInfo(int index, Hashtable properties,
			PropertyInfo info) {
        switch(index)
        {
        case 0:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "integer";
            break;
        case 1:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "maximumValue";
            break;
        case 2:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "minimumValue";
            break;
        default:break;
        }


	}

}
