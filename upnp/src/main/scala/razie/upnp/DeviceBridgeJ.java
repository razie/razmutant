package razie.upnp;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import com.razie.pub.base.log.Log;

/** temp class - scala 2.8 has issues with using Vector[E] */
public class DeviceBridgeJ extends Device {
	public Service[] svcs () { return (Service[])getServiceList().toArray(); 	}
	
	public ServiceList getServiceList() {
		ServiceList sl = super.getServiceList();
	
		for (int i = 0; i < sl.size(); i++) {
		Service s = sl.getService(i);
		try {
			s.loadSCPD(razscpdStr(s.getServiceID()));
		} catch (InvalidDescriptionException e) {
			Log.alarmThis("", e);
		}
		}
		
		return sl;
	}
	
	public String razscpdStr (String id) { return "";}
}
