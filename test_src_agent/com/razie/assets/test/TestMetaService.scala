package com.razie.assets.test

import com.razie.pub.agent._
import com.razie.pub.agent.test._
import com.razie.agent._
import com.razie.assets._
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.pub.base.log._
import com.razie.pub.comms._
import com.razie.pub.assets._
import razie.actionables._
import razie.actionables.library._
import org.scalatest.junit._
import razie.assets._

/** test asset metas */
class TestMetaService extends JUnit3Suite {

   var agent:razie.SimpleAgent = null

   override def setUp = {
      LightAuth.init()

      agent = razie.SimpleAgent.local("4446").onInit
      agent inContext {
         AssetMgr.init (new InventoryAssetMgr)
         agent register (new MetaService)
         
      } 
   }

   def todotestBlankAsset = expect (true) {
      // TODO 2-2 build test
      
//      val action = new AssetActionToInvoke(agent.handle.url, ak1.getKey(), razie.AI("sayhi"));
//      val result:String = (action act null).asInstanceOf[String]
      
//      val action2 = new AssetActionToInvoke(agent.handle.url, ak2.getKey(), razie.AI("say"), new AttrAccessImpl("what", "kuku"));
//      val result2:String = (action2 act null).asInstanceOf[String]
//      (result contains "hi")  && (result2 contains "kiki2")
   }

   override def tearDown = {
      agent.onShutdown
      agent.join
      super.tearDown
   }
}
