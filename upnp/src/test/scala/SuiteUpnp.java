/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * suite for upnp tests
 * 
 * @author razvanc99
 */
public class SuiteUpnp extends TestSuite {

   public static Test suite() {
      TestSuite result = new TestSuite(SuiteUpnp.class.getName());

      result.addTestSuite(com.razie.pub.upnp.test.TestUpnp.class);

      return result;
   }
}
