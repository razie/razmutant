/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.agent.pres;

import java.util.Arrays;

import org.w3c.dom.Element;

import razie.assets.AssetActionToInvoke;
import razie.assets.AssetKey;

import com.razie.agent.AgentConfig;
import com.razie.agent.network.Device;
import com.razie.agent.network.Devices;
import com.razie.agent.webservice.TempAgentNetworkService;
import com.razie.pub.assets.JavaAssetMgr;
import razie.base.ActionItem;
import razie.base.data.XmlDoc;
import razie.base.data.XmlDoc.Reg;
import razie.draw.DrawList;
import razie.draw.Drawable;
import razie.draw.widgets.DrawError;
import razie.draw.widgets.DrawToString;
import razie.draw.widgets.NavButton;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.*;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;

/**
 * keeps all agents in sync and monitors networks etc
 * 
 * TODO make this an asset
 * 
 * @author razvanc
 */
@SoaService(name = "network", bindings = { "http", "upnp" }, descr = "agent's network service")
public class AgentNetworkService extends TempAgentNetworkService {

    public AgentNetworkService() {
        super(true);
    }

    protected void onStartup() {
        super.onStartup();
    }

    protected void onShutdown() {
        super.onShutdown();
    }

    @SoaMethod(descr = "format the network view page", perm = PermType.VIEW)
    public Drawable Network() {
        DrawList[] levels = { new DrawList(), new DrawList(), new DrawList(), new DrawList() };

        // identify the current network based on my current IP
        String myIp = Devices.getMyIp();
        Element net = null;

        for (Element e : Reg.doc(AgentConfig.AGENT_CONFIG).xpl("/config/network")) {
            if (myIp.startsWith(e.getAttribute("ipPrefix"))) {
                net = e;
            }
        }

        if (net == null) {
            if (Comms.isLocalhost(myIp)) {
                // simulate whichever is the default network
                net = Reg.doc(AgentConfig.AGENT_CONFIG).xpe("/config/network[@home='true']");
            } else {
                return new DrawToString("ERR could not determine current network... myIp=" + myIp);
            }
        }

        if (net.getAttribute("home").equals("false")) {
            // it's a proxy network
            String name = net.getAttribute("proxy");
            String type = "desktop";
            AssetKey ref = new AssetKey("Device", name);
            levels[0].getList().add(
                new NavButton(new ActionItem(name, type), 
                		new AssetActionToInvoke(ref, new ActionItem("details")).makeActionUrl()));

            // now draw whichever is the default network
            net = Reg.doc(AgentConfig.AGENT_CONFIG).xpe("/config/network[@home='true']");
        } else {
            levels[0].getList().add("");
        }

//        String netIpPrefix = net.getAttribute("ipPrefix");

        for (Element host : XmlDoc
                .listEntities(Reg.doc(AgentConfig.AGENT_CONFIG).xpe("/config/clouds/cloud[@name='home']"))) {
//            if (host.getAttribute("ip").startsWith(netIpPrefix)) {
                String type = host.getNodeName();
                String name = host.getAttribute("name");
                AssetKey ref = new AssetKey("Device", name);

                Object butnew = new NavButton(new ActionItem(name, JavaAssetMgr.getBrief(ref).getIconImgUrl()), 
                		new AssetActionToInvoke(ref, new ActionItem("details")).makeActionUrl());

                if (whatLevel(type) == 3) {
                    DrawList no = new DrawList();
                    no.write(butnew);
                    no.write(briefInfo(ref));
                    levels[whatLevel(type)].getList().add(no);
                } else {
                    levels[whatLevel(type)].getList().add(butnew);
                }
                levels[whatLevel(type)].isVertical = true;
//            }
        }

        DrawList l = new DrawList();
        l.getList().addAll(Arrays.asList(levels));
        return l;
    }

    private Object briefInfo(AssetKey ref) {
        Device device = Devices.device(ref.getId());
        if (device == null) {
            return new DrawError("Cannot find device info for " + ref);
        }

        return new DrawToString("host="+device.getHandle().hostname+"<br>actualIP=" + device.getIp() + "<br> status=" + device.getStatus());
    }

    private int whatLevel(String type) {
        if ("laptop".equals(type) || "desktop".equals(type) || "server".equals(type))
            return 3;
        else if ("router".equals(type))
            return 1;
        else
            return 2;
    }

    static final Log               logger         = Log.factory.create("", AgentNetworkService.class
                                                          .getName());
}
