/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.dist.play.negociating.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * suite to run all base
 * 
 * @author razvanc99
 */
public class SuiteNegociating extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteNegociating.class.getName());

        result.addTestSuite(TestXANegociator.class);

        return result;
    }

}
