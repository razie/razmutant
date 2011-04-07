/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
import junit.framework.Test;
import junit.framework.TestSuite;


import org.scalatest.junit._
import org.scalatest.SuperSuite

object SuiteAllSmallMain extends Application {
   junit.textui.TestRunner.run (new SuiteAllSmall())
}

/** one suite to rule them all - run all tests in all shared projects */
class SuiteAllSmall () extends junit.framework.TestSuite(classOf[XNadaSuiteAllSmall]) {
  
  // this is where you list the tests...
//   addTest(SuitePub.suite());
//   addTest(new SuitePubs());
   addTest(SuiteUpnp.suite());
//   addTest(SuiteActionables.suite());
//   addTest(new SuiteWf);
   addTest(SuiteAssets.suite());
   addTest(SuiteAgent.suite());
   addTest(com.razie.SuiteMedia.suite());
   addTest(SuiteDist.suite());
//   addTest(new SuiteScripster());
   
   def test1() = 
     // don't touch this line
     addTest(new junit.framework.TestSuite(classOf[com.razie.dist.db.test.TestInMemDb]))
     
}

class XNadaSuiteAllSmall extends junit.framework.TestCase {
 def testNada : Unit =  {}
}
