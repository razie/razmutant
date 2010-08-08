/******************************************************************
 *
 *	CyberUPnP for Java
 *
 *	Copyright (C) Satoshi Konno 2002
 *
 *	File : ClockDevice.java
 *
 ******************************************************************/

package com.razie.pub.media.upnp;

import java.io.IOException;
import java.util.UUID;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPStatus;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import razie.media.upnp.MediaServer;

import com.razie.pub.agent.*;
import com.razie.pub.comms.*;
import com.razie.pub.upnp.UpnpSoaBinding;

/**
 * upnp bridge for the agent
 * 
 * @author razvanc
 * 
 */
public class JukeboxUpnpDevice extends Device implements ActionListener, QueryListener {
    final static String         DESCRIPTION_FILE_NAME = "public/upnp/device/mutant-ms-description.xml";
	final static String DESCRIPTION_FILE_NAME2 = "public/upnp/device/JukeboxDevice.xml";
    private final static String PRESENTATION_URI      = "/presentation";

    public String  uuid                  = "uuid:"+UUID.randomUUID().toString();
    
    private StateVariable       timeVar;
    public MediaServer server = new MediaServer();
    private UpnpSoaBinding service = new UpnpSoaBinding(server, "MediaServer");
    private Agent myAgent = Agent.instance();

    public JukeboxUpnpDevice(String descfile, String frname) throws InvalidDescriptionException, IOException {
        super(JukeboxUpnpDevice.class.getClassLoader().getResource(descfile));

        this.setFriendlyName(frname + ":" + Agents.getMyHostName());
//        this.setHTTPPort(Devices.allocatePort());
        uuid="uuid:"+this.getFriendlyName();
        this.setUDN(uuid);
        
        this.setSerialNumber(this.getFriendlyName());
        this.setPresentationURL(Agents.instance().me().url+"/mutant");

        // TODO what's this do?
        this.setNMPRMode(true);

        // TODO split this in a service class, have multiple services to cover the spec...
        for (Object s : getServiceList()) {
            ((Service) s).setQueryListener(this);

            for (Object a : ((Service) s).getActionList()) {
                ((Action) a).setActionListener(this);
            }
        }

        timeVar = getStateVariable("Time");

        setLeaseTime(60);
    }

    // //////////////////////////////////////////////
    // ActionListener
    // //////////////////////////////////////////////

    public boolean actionControlReceived(Action action) {
       myAgent.context().enter();
        return service.actionControlReceived(action);
    }

    // //////////////////////////////////////////////
    // QueryListener
    // //////////////////////////////////////////////

    public boolean queryControlReceived(StateVariable stateVar) {
       myAgent.context().enter();
        stateVar.setValue("gigi");
        return true;
    }

    // //////////////////////////////////////////////
    // HttpRequestListner
    // //////////////////////////////////////////////

    public void httpRequestRecieved(HTTPRequest httpReq) {
       myAgent.context().enter();
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
       myAgent.context().enter();
        timeVar.setValue("gigi");
    }
}
