package com.razie.pub.media

import com.razie.pub.plugin._
import com.razie.pub.agent._
import com.razie.pub.base.log._
import com.razie.media._
import com.razie.pub.media.upnp._
import com.razie.pub.media.players._

/** plugin lifecycle class */
class MediaPlugin extends Plugin {
   
  override def loadphase2 = {
   Agent.instance() register new MediaService
   Agent.instance() register new PlayerService
   Agent.instance() register new JukeboxUpnpService
   
   PlayerRegistryScala init;
   }

}
