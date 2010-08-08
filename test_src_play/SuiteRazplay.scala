

import junit.framework.Test;
import junit.framework.TestSuite;


import org.scalatest.junit._
import org.scalatest.SuperSuite

object SuiteRazPlayMain extends Application {
   junit.textui.TestRunner.run (new SuiteAllBig())
}

/** one suite to rule them all - run all tests in all shared projects */
class SuiteRazPlay () extends junit.framework.TestSuite(classOf[XNada]) {
  
  // this is where you list the tests...
   addTest(com.razie.valueadd.security.SuiteSecu.suite());
   
   def test1() = 
     // don't touch this line
     addTest(new junit.framework.TestSuite(classOf[com.razie.pub.test.TestAssetMgrTrait]))
     
}

class XXNada extends junit.framework.TestCase {
 def testNada : Unit =  {}
}
