package com.razie.mutant;

import com.razie.comm.commands.CmdLinks;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log.Log;
import com.razie.pub.draw.DrawList;
import com.razie.pub.draw.Drawable;
import com.razie.pub.lightsoa.HttpSoaBinding;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.media.players.SdkPlayer;

/**
 * the mutant itself, registered as a service on top of the agent
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class AgentMutantService extends AgentService {

    public AgentMutantService() {
        Log.logThis("CREATE_SERVICE " + "AgentMutantService");
    }

    protected void onStartup() {
        AgentHttpService.registerSoa(new HttpSoaBinding(new CmdLinks()));
        
        // stupid way to decouple the playground...
        try {
           Object p = (SdkPlayer) Class.forName("com.razie.playground.Init").newInstance();
           Log.logThis("PLAYGROUND_INITIALIZED ");
        } catch (Exception e) {
           Log.logThis("WARN_PLAYGROUND_NOT_FOUND");
        }
        
        // initialize diagnostics
        new DiagAsset("1");
        new DiagAsset("2");
        new DiagAsset("3");
    }

    protected void onShutdown() {
    }

    @SoaMethod(descr = "list the beings and what they're doing")
    public Drawable display() {
        DrawList levels = new DrawList();

        return levels;
    }

    static final Log logger = Log.Factory.create("", AgentMutantService.class.getName());
}
