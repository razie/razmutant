/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File : ClockDevice.java
*
******************************************************************/

package com.razie.upnp.clock;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPStatus;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import com.razie.pub.FileUtils;

public class ClockDevice extends Device implements ActionListener, QueryListener
{
	private final static String DESCRIPTION_FILE_NAME = "com/razie/upnp/clock/description/description.xml";
	private final static String PRESENTATION_URI = "/presentation";
	
	private StateVariable timeVar;
	
	public ClockDevice() throws InvalidDescriptionException
	{
		super(FileUtils.fileFromUrl(ClockDevice.class.getClassLoader().getResource(DESCRIPTION_FILE_NAME)));

		Action getTimeAction = getAction("GetTime");
		getTimeAction.setActionListener(this);
		
		Action setTimeAction = getAction("SetTime");
		setTimeAction.setActionListener(this);
		
		ServiceList serviceList = getServiceList();
		Service service = serviceList.getService(0);
		service.setQueryListener(this);

		timeVar = getStateVariable("Time");
		
		setLeaseTime(60);
	}

	////////////////////////////////////////////////
	// ActionListener
	////////////////////////////////////////////////

	public boolean actionControlReceived(Action action)
	{
		String actionName = action.getName();
		if (actionName.equals("GetTime") == true) {
			Clock clock = Clock.getInstance();
			String dateStr = clock.getDateString();
			Argument timeArg = action.getArgument("CurrentTime");
			timeArg.setValue(dateStr);
			return true;
		}
		if (actionName.equals("SetTime") == true) {
			Argument timeArg = action.getArgument("NewTime");
			String newTime = timeArg.getValue();
			Argument resultArg = action.getArgument("Result");
			resultArg.setValue("Not implemented (" + newTime + ")");
			return true;
		}
		return false;
	}

	////////////////////////////////////////////////
	// QueryListener
	////////////////////////////////////////////////

	public boolean queryControlReceived(StateVariable stateVar)
	{
		Clock clock = Clock.getInstance();
		stateVar.setValue(clock.getDateString());
		return true;
	}

	////////////////////////////////////////////////
	// HttpRequestListner
	////////////////////////////////////////////////
	
	public void httpRequestRecieved(HTTPRequest httpReq)
	{
		String uri = httpReq.getURI();
		if (uri.startsWith(PRESENTATION_URI) == false) {
			super.httpRequestRecieved(httpReq);
			return;
		}
			 
		Clock clock = Clock.getInstance();
		String contents = "<HTML><BODY><H1>" + clock.toString() + "</H1></BODY></HTML>";
		
		HTTPResponse httpRes = new HTTPResponse();
		httpRes.setStatusCode(HTTPStatus.OK);
		httpRes.setContent(contents);
		httpReq.post(httpRes);
	}

	////////////////////////////////////////////////
	// update
	////////////////////////////////////////////////

	public void update()
	{
			Clock clock = Clock.getInstance();
			String timeStr = clock.toString();
			timeVar.setValue(timeStr);
	}			
}

