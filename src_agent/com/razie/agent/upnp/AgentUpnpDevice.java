/******************************************************************
 *
 *	CyberUPnP for Java
 *
 *	Copyright (C) Satoshi Konno 2002
 *
 *	File : ClockDevice.java
 *
 ******************************************************************/

package com.razie.agent.upnp;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import com.razie.pub.comms.Agents;
import com.razie.pub.upnp.UpnpSoaBinding;

/**
 * upnp bridge for the agent
 * 
 * @author razvanc
 * 
 */
public class AgentUpnpDevice extends Device implements ActionListener, QueryListener {
    private final static String DESCRIPTION_FILE_NAME = "public/upnp/device/agent-description.xml";
    private final static String PRESENTATION_URI      = "/presentation";

    public String               uuid                  = "uuid:" + UUID.randomUUID().toString();

    private StateVariable       timeVar;

    List<UpnpSoaBinding>        bindings              = new ArrayList<UpnpSoaBinding>();

    public AgentUpnpDevice() throws InvalidDescriptionException, IOException {
        // super(FileUtils.fileFromUrl(AgentUpnpDevice.class.getClassLoader().getResource(DESCRIPTION_FILE_NAME)));
        super(AgentUpnpDevice.class.getClassLoader().getResource(DESCRIPTION_FILE_NAME));

        this.setFriendlyName("MutantAgent:" + Agents.getMyHostName());
        // this.setHTTPPort(Devices.allocatePort());
        uuid = "uuid:" + this.getFriendlyName();
        this.setUDN(uuid);

        this.setSerialNumber(this.getFriendlyName());
        this.setPresentationURL(Agents.instance().me().url + "/mutant");

        Action getTimeAction = getAction("Ping");
        getTimeAction.setActionListener(this);

        ServiceList serviceList = getServiceList();
        Service service = serviceList.getService(0);
        service.setQueryListener(this);

        timeVar = getStateVariable("Time");

        setLeaseTime(60);
    }

    // //////////////////////////////////////////////
    // ActionListener
    // //////////////////////////////////////////////

    public boolean actionControlReceived(Action action) {
        String actionName = action.getName();
        if (actionName.equals("GetTime") == true) {
            String dateStr = new Date(System.currentTimeMillis()).toString();
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

        for (UpnpSoaBinding b : bindings) {
            if (b.getSoaMethods().contains(actionName)) {
                return b.actionControlReceived(action);
            }
        }

        return false;
    }

    // //////////////////////////////////////////////
    // QueryListener
    // //////////////////////////////////////////////

    public boolean queryControlReceived(StateVariable stateVar) {
        stateVar.setValue("gigi");
        return true;
    }

    // //////////////////////////////////////////////
    // HttpRequestListner
    // //////////////////////////////////////////////

    public void httpRequestRecieved(HTTPRequest httpReq) {
        String uri = httpReq.getURI();
        if (uri.startsWith(PRESENTATION_URI) == false) {
            super.httpRequestRecieved(httpReq);
            return;
        }

        String contents = "<HTML><BODY><H1>" + "R A T A T O U I L E" + "</H1></BODY></HTML>";

        HTTPResponse httpRes = new HTTPResponse();
        httpRes.setStatusCode(HTTPStatus.OK);
        httpRes.setContent(contents);
        httpReq.post(httpRes);
    }

    // //////////////////////////////////////////////
    // update
    // //////////////////////////////////////////////

    public void update() {
        timeVar.setValue("gigi");
    }
}
