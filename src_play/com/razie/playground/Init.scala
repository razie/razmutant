package com.razie.playground

import com.razie.pub.agent._
import com.razie.pub.comms._
import com.razie.pub.assets._
import razie.assets._

/** init playground stuff - stupid way to decouple code
 * 
 */
class Init {
   val agent = Agent.instance();

   agent.register(new com.razie.playground.filter.AgentFilterService());
   agent.register(new com.razie.dist.play.negociating.AgentDistService());

   agent.register(new com.razie.sdk.assets.AgentEcosystemService());
   agent.register(new com.razie.dist.play.phantom.AgentPhantomService());
   
   agent.register(new razie.play.dist.robot.AgentRobotService());

   FullAssetMgr.instance inject new com.razie.valueadd.security.SecureDeviceInjection

}
