package com.razie.mutant.test;

import junit.framework.TestCase;

import com.razie.pub.base.NoStatics;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.LightAuth;

/**
 * test the light server
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class TestMutant extends TestCase {
    static Integer     PORT      = 4444;
    static String      MYURL     = "http://localhost:" + PORT;
    static String      MUTANTCMD = MYURL + "/cmd";
    static String      MOVIEREF  = "%3Cmutant%3A%2F%2FMovie%3A300.ISO%40mutant%3A%2F%2FTorLp-Razvanc%3A4444%3A%3Ac%3A%2Fvideo%2F%3E";

    static AgentHandle me        = new AgentHandle("localhost", "localhost", "127.0.0.1", PORT.toString(),
                                         "http://localhost:" + PORT.toString());

    public void setUp() {
        LightAuth.init(new LightAuth("mutant"));

        AgentCloud group = new AgentCloud();
        group.put(me);
        NoStatics.put(Agents.class, new Agents(group, me));

        // TODO if a mutant is not running, start a local mutant
        
       if (! me.isUpNow()) {
    	   
       }
    }

    public static String ref(String encoded) {
        return HttpUtils.fromUrlEncodedString(encoded);
    }

    static final Log logger = Log.Factory.create(TestMutant.class.getName());
}
