/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * suite to run all base
 * 
 * @author razvanc99
 */
public class SuiteAgent extends TestSuite {
    public static Test suite() {
        TestSuite result = new TestSuite(SuiteAgent.class.getName());

        result.addTestSuite(com.razie.pub.agent.test.TestSimpleService.class);
        result.addTestSuite(com.razie.agent.test.TestControlService.class);
        
        result.addTestSuite(com.razie.pub.agent.test.TestMultipleAgents.class);
        
        result.addTestSuite(com.razie.pub.agent.test.TestMsgService.class);
        
        return result;
    }
}
