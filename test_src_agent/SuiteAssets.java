/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * suite to run all base
 * 
 * @author razvanc99
 */
public class SuiteAssets extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteAssets.class.getName());

        result.addTestSuite(com.razie.pub.assets.samples.TestDefinedAssets.class);

        return result;
    }

}
