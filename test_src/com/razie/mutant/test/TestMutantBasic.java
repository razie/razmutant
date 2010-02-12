package com.razie.mutant.test;

import java.io.IOException;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.pub.comms.LightAuth;
import com.razie.pub.comms.SimpleActionToInvoke;

/**
 * basic mutant server tests
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class TestMutantBasic extends TestMutant {

    public void setUp() {
        super.setUp();
    }

    public void testEcho() throws IOException, InterruptedException {
        // send echo command
        LightAuth.init(new LightAuth("nonmutant"));
        ActionToInvoke action = new SimpleActionToInvoke(MYURL, new ActionItem("xxx"));
        String result = (String) action.act(null);
        LightAuth.init(new LightAuth("mutant"));

        assertTrue(result.contains("Echo"));
    }

    public void testMainPage() throws IOException, InterruptedException {
        // send echo command
        LightAuth.init(new LightAuth(""));
        ActionToInvoke action = new SimpleActionToInvoke(MYURL, new ActionItem("mutant"));
        String result = (String) action.act(null);
        LightAuth.init(new LightAuth("mutant"));

        assertTrue(result.contains("Jukebox"));
    }

    public void testManage() throws IOException, InterruptedException {
        // send echo command
        ActionToInvoke action = new SimpleActionToInvoke(MYURL, new ActionItem("manage.html"));
        String result = (String) action.act(null);

        assertTrue(result.contains("Manage assets"));
    }
}
