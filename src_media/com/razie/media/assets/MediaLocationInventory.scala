/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media.assets;

import razie.base._
import java.io.File;

import org.w3c.dom.Element;

import com.razie.pub.UnknownRtException
import razie.base.ActionItem
import razie.base.scripting.ScriptContext
import com.razie.pub.base.data.HttpUtils
import razie.base.data.XmlDoc
import com.razie.pub.base.log.Log
import razie.draw.Drawable
import razie.draw.DrawList
import razie.draw.widgets.NavButton
import com.razie.sdk.finders.AssetFinder
import razie.assets._

/**
 * TODO comment
 * 
 */
trait MediaLocationIventory extends AssetInventory {

  override abstract def getAsset(ref:AssetKey):AnyRef = 
	  // TODO cache these for a while or something, eh?
     new MediaLocation (ref)

   override abstract def getBrief(ref:AssetKey ) : AssetBrief = 
     super.getBrief(ref)

    /** list all assets of the given type at the given location */
     //TODO
     
    /** execute command on asset. the asset can be local or remote */
   override abstract def doAction(cmd:String , ref:AssetKey , ctx:ActionContext ):Object =
     super.doAction(cmd, ref, ctx);

   override abstract def getSupportedActions(ref:AssetKey ) : Array[ActionItem] = 
     super.getSupportedActions(ref)

    /** initialize this instance for use with this Meta */
   override abstract def init(meta:Meta ) = 
     super.init(meta)
}
