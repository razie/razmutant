package com.razie.agent.webservice;

import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import razie.base.ActionItem;
import razie.base.AttrAccessImpl;
import razie.draw.DrawStream;

import com.razie.agent.network.Devices;
import com.razie.pub.agent.Agent;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.ChannelEndPoint;
import com.razie.pub.comms.PermType;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pubstage.comms.HtmlContents;

/**
 * keeps all agents in sync and monitors networks etc
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaService(name = "network", bindings = { "http" }, descr = "agent's network service")
public class TempAgentNetworkService extends AgentService {
   Thread updaterThread = null;
   UpdaterWrkRq updaterWrkRq = null;
   boolean active = true;

   /**
    * initialize the service
    * 
    * @param active
    *           indicates if this should ping for other agents and maintain their status or not
    */
   public TempAgentNetworkService(boolean active) {
      this.active = active;
   }

   protected void onStartup() {
      updaterWrkRq = new UpdaterWrkRq(agent.getContext());

      if (active) {
         // TODO replace with a scheduling service
         updaterThread = new Thread(updaterWrkRq, "AgentUpdaterThread");
         updaterThread.setDaemon(true);
         updaterThread.start();
      }
   }

   protected void onShutdown() {
      if (updaterWrkRq == null)
         throw new IllegalStateException("this was not started - please call the agent's onStartup()");

      updaterWrkRq.shutdown();

      if (updaterThread != null) {
         updaterThread.interrupt();
         try {
            updaterThread.join();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   // TODO SECURITY ping responds to PUBLIC - is that ok?
   @SoaMethod(descr = "Pinging service", perm = PermType.PUBLIC, args = { "srcName", "srcHost", "srcIp",
         "srcPort" })
   @SoaStreamable
   public void firstping(DrawStream out, String name, String host, String ip, String port) {
      ping(out, name, host, ip, port);
   }

   // TODO SECURITY ping responds to PUBLIC - is that ok?
   @SoaMethod(descr = "agents ping eachother", perm = PermType.PUBLIC, args = { "srcName", "srcHost",
         "srcIp", "srcPort" })
   @SoaStreamable(mime = "application/json")
   public void ping(DrawStream out, String srcName, String srcHost, String srcIp, String srcPort) {
      srcIp = ((ChannelEndPoint) out.getEndPoint()).getIp();
      Devices.getInstance().notifiedUp(srcName, srcHost, srcIp, srcPort);
      AgentHandle me = Agents.me();
      if (me.name.equals(srcName))
         logger.alarm("WARN_PINGING: pinged self...orig=" + me + " Agent=" + Agent.instance().getHandle());
      out.write(Agents.me());
   }

   /** helper to do the ping above */
   public static AgentHandle doPing(AgentHandle remote, AgentHandle me, String pingcmd) {
      // make sure it's up:
      Socket server;
      try {
         logger.trace(3, "Trying to connect to " + remote);
         // timeout quickly
         // TODO assuming default port
         // server = new RazClientSocket(remote.ip, Integer .valueOf(remote.port), 250);
         server = new Socket();
         server.connect(new InetSocketAddress(remote.ip, Integer.valueOf(remote.port)), 250);
         server.close();

         if (me.name.equals(remote.name))
            logger.trace(2, "DEVICES_PINGING myself remote=" + remote + " me=" + me);
         else
            logger.trace(2, "DEVICES_PINGING otheragent remote=" + remote + " me=" + me);

         // now ping it and confirm remote host
         ServiceActionToInvoke ati = new ServiceActionToInvoke(remote.url, "network",
               new ActionItem(pingcmd), new AttrAccessImpl("srcName", me.name, "srcHost", me.hostname,
                     "srcIp", me.ip, "srcPort", me.port));

         // String url = "http://" + remote.ip + ":" + remote.port
         // + "/mutant/network/" + pingcmd;
         // url = new AttrAccessImpl("srcName", me.name, "srcHost",
         // me.hostname, "srcIp", me.ip, "srcPort", me.port)
         // .addToUrl(url);

         // String response = Comms.readUrl(url);
         String response = ati.act(null).toString();

         response = HtmlContents.justBody(response);
         logger.log("DEVICES_PINGED handle=" + remote + " cmd=" + pingcmd + " response=" + response);

         try {
            AgentHandle resp = AgentHandle.fromString(response.replaceFirst(". ", ""));
            if (!resp.name.equals(remote.name))
               logger.alarm("WARN_PINGING: response is not expected, orig=" + remote + " received=" + resp);
            return resp;
         } catch (Throwable t) {
            logger.alarm("ERR_PINGING: response is not expected, orig=" + remote + " exception: ", t);
         }
      } catch (SocketTimeoutException e2) {
         logger.log("WARN_DEVICES_PING SOCKETTIMEOUT handle=" + remote);
      } catch (NoRouteToHostException e2) {
         logger.log("WARN_DEVICES_PING noroutetohost, handle=" + remote);
      } catch (java.net.ConnectException e3) {
         logger.log("WARN_DEVICES_PING CONNETIONREFUSED, handle=" + remote + " EXCMSG=" + e3.getMessage());
      } catch (Exception e1) {
         logger.log("ERR_DEVICES_PING some other error when pinging handle=" + remote, e1.getMessage());
         logger.trace(1, "ERR_DEVICES_PING some other error when pinging handle=" + remote, e1);
      }
      return null;
   }

   @SoaMethod(descr = "update all agents")
   public String updateNetwork() {
      updaterWrkRq.updateAllMutants();
      return "Update started...check back!";
   }

   static final Log logger = Log.factory.create("", TempAgentNetworkService.class.getSimpleName());
}
