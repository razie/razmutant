package com.razie.comm.commands;

import com.razie.dist.db.AgentDb;
import com.razie.pub.base.AttrAccess;
import com.razie.pub.lightsoa.SoaAllparms;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;

/**
 * control commands
 * 
 * @author razvanc
 */
@SoaService(name = "links", descr = "link management")
public class CmdLinks {

    public CmdLinks() {
    }
   
    @SoaMethod(descr="capturelinks")
    @SoaAllparms
    public String capturelink(AttrAccess parms) {
        AgentDb.db("links").xml().add("/db", "link", parms);
        AgentDb.db("links").save(false, true);
        return "Ok...";
    }
}
