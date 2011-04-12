/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.dist.db.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * suite to run all base
 * 
 * @author razvanc99
 */
public class SuiteDistDb extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteDistDb.class.getName());

        result.addTestSuite(TestInMemDb.class);
        result.addTestSuite(TestXmlDb.class);
        result.addTestSuite(TestDbLoadCreate.class);

        return result;
    }

}
