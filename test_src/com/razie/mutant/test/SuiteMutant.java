package com.razie.mutant.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * this suite runs a battery of tests on a running mutant on localhost
 * 
 * @author razvanc
 * 
 */
public class SuiteMutant extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteMutant.class.getName());

        result.addTestSuite(TestMutantBasic.class);
        result.addTestSuite(TestCmdNetwork.class);
        result.addTestSuite(TestCmdControl.class);
        result.addTestSuite(TestCmdAssets.class);

        return result;
    }

}
