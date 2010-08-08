package com.razie.pub.agent;

import com.razie.pub.comms.AgentHandle;

/**
 * simple interface for whoever maintains the devices
 * 
 * @deprecated temp until i move code here
 */
public interface AgentDeviceStatusListener {
	public void notifyNewDevice(AgentHandle device);

	/**
	 * call this whenever you get a notification of some kind that a device is
	 * up. Host can be null - in which case i will ping it myself to make sure
	 * it's an agent
	 */
	public void notifiedUp(String name, String host, String ip, String port);

	/**
	 * call this whenever you get a notification of some kind that a device is
	 * shutting down. Host cannot
	 */
	public void notifiedDown(String name, String host, String ip, String port);

	/**
	 * call this whenever you have reason to believe a host may have a different
	 * ip...i.e. if you lookup DNS or something. The other device's status is
	 * unknown
	 */
	public void notifiedNewIp(String host, String ip);
}
