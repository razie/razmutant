/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.media.players;

import org.w3c.dom.Element

import com.razie.pub.WinExec
import com.razie.pub.assets.AssetBase
import com.razie.pub.assets.AssetBrief
import com.razie.pub.base.log.Log
import com.razie.pub.actionables._
import com.razie.pub.actionables.library._

/**
 * proxy another player with some overrides
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
class ProxyPlayer (val player:SdkPlayer, val hasa:THasActionables) extends SdkPlayer {

    def canPlay(m:AssetBrief):boolean = player.canPlay(m)

    def getBrief(): AssetBrief = player.getBrief

    def play(m:AssetBase):PlayerHandle = {
        val h = player.play(m)
        ActionableFactory.makeProxy (h, hasa).asInstanceOf[PlayerHandle]
    }

    def makeHandle:PlayerHandle = {
        val h = player.makeHandle()
        ActionableFactory.makeProxy (h, hasa).asInstanceOf[PlayerHandle]
    }

    def init(config:Element ) = player.init (config)
}
