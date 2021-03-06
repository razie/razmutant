package com.razie.mutant.test;

import java.io.IOException;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.SimpleActionToInvoke;

/**
 * test the light server
 * 
 * @version $Revision: 1.63 $
 * @author $Author: davidx $
 * @since $Date: 2005/04/01 16:22:12 $
 */
public class TestCmdAssets extends TestMutant {

    public void testCmdBrowse() throws IOException, InterruptedException {
        // send echo command
        ActionToInvoke action = new SimpleActionToInvoke(
                MUTANTCMD,
                new ActionItem("browse"),
                "ref",
                ref("%3Cmutant%3A%2F%2FFolder%3Ac%253A%255Cvideo%40mutant%3A%2F%2FTorLp-Razvanc%3A4444%3A%3A%3E"));
        String result = (String) action.act(null);

        assertTrue(result, result.contains(MOVIEREF));
    }

    public void testListMovies() throws IOException, InterruptedException {
        // send echo command
        ActionToInvoke action = new SimpleActionToInvoke(MUTANTCMD, new ActionItem("list"), "type", "Movie");
        String result = (String) action.act(null);

        assertTrue(result.contains(MOVIEREF));
    }

    static final Log logger = Log.factory.create(TestCmdAssets.class.getName());
}
