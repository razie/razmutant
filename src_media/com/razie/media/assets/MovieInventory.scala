package com.razie.media.assets;

import razie.base._
import com.razie.media._
import com.razie.pub.assets._
import com.razie.pub.base._
import razie.assets._

object MovieInventory {
    val sCLASS_MOVIE = "Movie";

  val defaultCmds = Array (AssetBrief.PLAY, AssetBrief.STREAM, AssetBrief.DELETE)
}

/**
 * movies have specific extensions, descriptions, functionality (i.e. "google") etc
 */
class MovieInventory extends ScalaMediaInventoryBik {
   override def getAsset(ref:AssetKey ) : AnyRef = new Movie(getBrief(ref))
   
   override def queryAll(meta:String , env:AssetLocation , recurse:Boolean , toUse:AssetMap ) : AssetMap = {
      val ret = super.queryAll(meta, env, recurse, toUse)
// TODO 2-1 list all movies from all locations - actually move this to the lower level and use the asset type to discover locations
      ret;
   }
    
   override def getSupportedActions(ref:AssetKey): Array[ActionItem] = MovieInventory.defaultCmds
}
