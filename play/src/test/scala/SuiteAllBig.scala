/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
import junit.framework.Test;
import junit.framework.TestSuite;


import org.scalatest.junit._
import org.scalatest.SuperSuite

object SuiteAllBigMain extends Application {
   junit.textui.TestRunner.run (new SuiteAllBig())
}

/** one suite to rule them all - run all tests in all shared projects */
class SuiteAllBig () extends junit.framework.TestSuite(classOf[XNada]) {
  
  // this is where you list the tests...
   addTest(new SuiteAllSmall());
   addTest(com.razie.valueadd.security.SuiteSecu.suite());
   
   def test1() = 
     // don't touch this line
     addTest(new junit.framework.TestSuite(classOf[com.razie.pub.test.TestAssetMgrTrait]))
     
}

class XNada extends junit.framework.TestCase {
 def testNada : Unit =  {}
}
