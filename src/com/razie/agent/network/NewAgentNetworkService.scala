package com.razie.agent.network

import com.razie.pub.agent._
import com.razie.pub.assets.Meta
import com.razie.assets._
import com.razie.pub.assets._

class NewAgentNetworkService extends AgentService {

  override def onStartup = {
    AgentHttpService.registerSoaAsset(classOf[DeviceScala]);

    AssetMgr.instance().register(DeviceStuff.META);
  }
}
