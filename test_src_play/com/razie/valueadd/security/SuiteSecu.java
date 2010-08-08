package com.razie.valueadd.security;

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
public class SuiteSecu extends TestSuite {
   public static Test suite() {
      TestSuite result = new TestSuite(SuiteSecu.class.getName());

      result.addTestSuite(TestStoreSimple.class);
      result.addTestSuite(TestStoreJKS.class);
      result.addTestSuite(TestSecuLightAuth.class);

      return result;
   }

}
