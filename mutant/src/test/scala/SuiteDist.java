

import junit.framework.Test;
import junit.framework.TestSuite;

import com.razie.dist.db.test.SuiteDistDb;
import com.razie.dist.play.negociating.test.SuiteNegociating;

/**
 * suite to run all base
 * 
 * @author razvanc99
 */
public class SuiteDist extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteDist.class.getName());

        result.addTest(SuiteDistDb.suite());
        result.addTest(SuiteNegociating.suite());

        return result;
    }

}
