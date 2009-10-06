package com.razie.comm.commands;

import com.razie.dist.db.AgentDb;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.AttrAccess;
import com.razie.pub.lightsoa.SoaAllParms;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;

/**
 * control commands
 * 
 * @author razvanc
 */
@SoaService(name = "links", bindings={"http"}, descr = "link management")
public class AgentServiceLinks extends AgentService {

    @SoaMethod(descr="capturelinks")
    @SoaAllParms
    public String capturelink(AttrAccess parms) {
        AgentDb.db("links").xml().add("/db", "link", parms);
        AgentDb.db("links").save(false, true);
        return "Ok...";
    }
}
