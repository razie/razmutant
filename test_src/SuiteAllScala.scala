

import junit.framework.Test;
import junit.framework.TestSuite;


import org.scalatest.junit._
import org.scalatest.SuperSuite

object SuiteAllScalaMain extends Application {
   junit.textui.TestRunner.run (new SuiteAllScala())
//   junit.swingui.TestRunner.run (new SuiteAllScala())
}

/** TODO this is sooooooooooooo messed up... */
class SuiteAllScala () extends junit.framework.TestSuite(classOf[XNada]) {
  
  // this is where you list the tests...
   addTest(SuitePub.suite());
   addTest(new SuitePubs());
   addTest(SuiteUpnp.suite());
   // TODO add these tests too
   addTest(new SuiteActionables());
   addTest(new SuiteAssets());
   addTest(com.razie.SuiteMedia.suite());
   addTest(SuiteAgent.suite());
   addTest(SuiteDist.suite());
   
   def test1() = 
     // don't touch this line
     addTest(new junit.framework.TestSuite(classOf[com.razie.pub.test.TestAssetMgrTrait]))
     
}

class XNada extends junit.framework.TestCase {
 def testNada : Unit =  {}
}
