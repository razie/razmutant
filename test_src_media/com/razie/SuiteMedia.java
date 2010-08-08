/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * suite to run all base
 * 
 * @author razvanc99
 */
public class SuiteMedia extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteMedia.class.getName());

//        result.addTest(SuitePub.suite());

        return result;
    }

}
