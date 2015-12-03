package ms.ihc.control.valueTypes;

import java.util.Hashtable;

import ms.ihc.control.ksoap2.serialization.KvmSerializable;
import ms.ihc.control.ksoap2.serialization.PropertyInfo;

public class WSBooleanValue implements KvmSerializable {

	private Boolean value;
	
	
	public WSBooleanValue(Boolean val)
	{
		this.value = val;
	}
	
	public Object getProperty(int index) {
        switch(index)
        {
        case 0:
            return this.value;
        }
        
        return null;

	}

	public int getPropertyCount() {
		return 1;
	}

	public void setProperty(int index, Object value) {
        switch(index)
        {
        case 0:
            this.value = Boolean.valueOf(value.toString());
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
            info.type = PropertyInfo.BOOLEAN_CLASS;
            info.name = "value";
            break;
        default:break;
        }


	}

}
