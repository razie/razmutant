/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.agent;

import razie.base.data.XmlDoc;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;

/**
 * bad name - this is the main configuration for the agent network
 * 
 * @author razvanc
 */
public class AgentConfig extends XmlDoc {
    public static final String DFLT_CATALOG = "/cfg/agent.xml";
    public static final String AGENT_CONFIG = "AgentConfig";
    static AgentConfig         singleton    = null;
    private static int         lastPortUsed;

    public AgentCloud          homeGroup;
    public AgentHandle         myHandle;

    public static AgentConfig instance() {
        if (singleton == null) {
            singleton = new AgentConfig();
            singleton.load(AGENT_CONFIG, singleton.getClass().getResource(DFLT_CATALOG));
            Reg.docAdd(AGENT_CONFIG, singleton);
        } else
        	singleton.checkFile();
        return singleton;
    }

    // TODO inline once settled on this model
    public static String getMutantDir() {
        return Agents.me().localdir;
    }

    // TODO what's the idea here?
    public static int allocatePort() {
        return ++lastPortUsed;
    }

    private static final Log logger = Log.factory.create("Agent", "Devices");

}
