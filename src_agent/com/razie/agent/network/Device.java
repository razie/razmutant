package com.razie.agent.network;


import razie.assets.AssetKey;

import razie.assets.*;
import razie.base.ActionItem;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentHandle;
import razie.draw.Renderer;
import com.razie.pub.resources.RazIcons;
import razie.draw.DrawStream;
import razie.draw.Drawable3;
import razie.draw.Technology;

/**
 * a device with some capabilities, available somewhere on the network
 * 
 * TODO 1- MODEL a device can run multiple agents and services at many ports...
 * 
 * @author razvanc
 * @version $Id$
 */
public interface Device extends AssetBase, Drawable3 {
   public static final String sCLASSNM_DEVICE = "Device";
   public static final ActionItem cmdDEVICEINFO  = new ActionItem("deviceInfo", RazIcons.UNKNOWN.name());
   public static final ActionItem cmdUPGRATETO   = new ActionItem("upgradeTo", RazIcons.UPLOAD.name());
   public static final ActionItem cmdUPGRADEFROM = new ActionItem("upgradeFrom", RazIcons.DOWNLOAD.name());
   public static final ActionItem cmdSTOP        = new ActionItem("Stop", RazIcons.POWER.name());
   public static final ActionItem cmdUPGRADE     = new ActionItem("upgrade", RazIcons.UPGRADE.name());
   public static final ActionItem cmdDIE         = new ActionItem("Die", RazIcons.POWER.name());

    public String getName();

    public AgentHandle getHandle();

    public void setHandle(AgentHandle h);

    public void setIp(String ip);

    public String getIp();

    /** get the url for this, without trailing '/' */
    public String getUrl();

    public void setPort(String port);

    public String getPort();

    public void setStatus(AgentHandle.DeviceStatus status);

    public AgentHandle.DeviceStatus getStatus();

    /** basic implementation of a Device */
    public class Impl extends AssetBaseImpl implements Device, Drawable3 {

        AgentHandle handle = null;

        public Impl(AssetKey ref) {
            super(new AssetBriefImpl());
            this.setKey(ref);
        }

        /**
         * @param ip the ip to set
         */
        public void setIp(String ip) {
            handle.ip = ip;
            handle.url = "http://" + ip + ":" + handle.port;
//            handle.getLocation().setURL(handle.url);
        }

        /**
         * @param ip the ip to set
         */
        public String getName() {
            return this.getKey().getId();
        }

        /**
         * @return the ip
         */
        public String getIp() {
            return getHandle().ip;
        }

        /**
         * @param status the status to set
         */
        public void setStatus(AgentHandle.DeviceStatus status) {
            this.handle.status = status;
        }

        /**
         * @return the status
         */
        public AgentHandle.DeviceStatus getStatus() {
            return getHandle().status;
        }

        public void setPort(String port) {
            this.handle.port = port;
        }

        public String getPort() {
            return getHandle().port;
        }

        /** get the url for this, without trailing '/' */
        public String getUrl() {
            String s = "http://" + getIp() + ":" + getPort();
            if (! s.equals(handle.url)) 
                Log.logThis("WARNING_INCONSISTENCY "+s+" <NEQ> "+handle.url);
            return handle.url;
        }

        public AgentHandle getHandle() {
            return handle;
        }

        public void setHandle(AgentHandle h) {
            handle = h;
            this.getBrief().setBriefDesc(h.toString());
        }
        
      @Override
      public Renderer getRenderer(Technology technology) {
         return Drawable3.DefaultRenderer.singleton;
      }

      @Override
      public Object render(Technology t, DrawStream out) {
         AssetKey gref = this.getKey();
         String who = gref.getId();

         String ip = getIp();
         String hostname = this.getName();
         
         String s = "Agent: " + who + " HOST="+hostname+" IP=" + "??" + " actualIP=" + ip + " status="
                 + getStatus();
         String port = getPort();

         out.write(s);
         return null;
      }

    }

}
