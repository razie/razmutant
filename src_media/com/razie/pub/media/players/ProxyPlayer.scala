/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media.players;

import org.w3c.dom.Element

import razie.actionables.util.WinExec
import com.razie.pub.base.data._
import razie.base.data._
import razie.actionables._
import razie.actionables.library._
import razie.assets._

/**
 * proxy another player with some overrides
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
class ProxyPlayer (val player:SdkPlayer, val hasa:THasActionables) extends SdkPlayer {

    def canPlay(m:AssetBrief):Boolean = player.canPlay(m)

    def getBrief(): AssetBrief = player.getBrief

    def play(m:AssetBase):PlayerHandle = {
        val h = player.play(m)
        ActionableFactory.makeProxy (h, hasa).asInstanceOf[PlayerHandle]
    }

    def makeHandle:PlayerHandle = {
        val h = player.makeHandle()
        ActionableFactory.makeProxy (h, hasa).asInstanceOf[PlayerHandle]
    }

    def init(doc:XmlDoc, xpath:String ) = player.init (doc,xpath)
}
