package com.razie.pub.media.players;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import com.razie.media.config.MediaConfig;
import com.razie.pub.base.data.XmlDoc;
import com.razie.pub.base.log.Log;
import com.razie.pub.media.MediaErrors;
import com.razie.pub.base.data._
import com.razie.pub.actionables._
import com.razie.pub.actionables.library._

/**
 * register all players declared in config.xml
 * 
 * TODO reinitialize when xml file changed...
 * 
 * @author razvanc
 * @version $Id$
 */
object PlayerRegistryScala {

  /** load all metas and instantiate all inventories */
  def init():Unit = {
    PlayerRegistry.init (new PlayerReg)
  }	  
}

/** scala version of the player registry - use for additional functionality defined here... */
class PlayerReg extends PlayerRegistry {
   
	override def annotateWithTag(p:SdkPlayer, e:Element):SdkPlayer = {
	  if (e.getNodeName() == "actionables") {
        val hasa = StaticAct.maker(e)
       new ProxyPlayer (p, hasa)
        //ActionableFactory.makeProxy (p, hasa).asInstanceOf[SdkPlayer]
	  } else
		return p;
	}

}