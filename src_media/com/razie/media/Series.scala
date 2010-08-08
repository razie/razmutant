package com.razie.media;

import razie.base._
import razie.assets._
import com.razie.pub.assets._
import com.razie.pub.comms._
import com.razie.pub.resources._

import razie.base.ActionItem;
import com.razie.pub.lightsoa.SoaAsset;
import razie.draw._
import razie.draw.Renderer

/**
 * stands for a movie asset. movies can be played with a player
 * 
 * @author razvanc
 * 
 */
@SoaAsset(descr="series of items")
class Series (myBrief:AssetBrief) extends AssetImpl (myBrief) {
   private var _stuff : AssetMap = null
   private var _oldstuff : AssetMap = null
   private var _newstuff : AssetMap = null
   
   def items : AssetMap    = load._1
   def oldItems : AssetMap = load._2
   def newItems : AssetMap = load._3

   private def load : (AssetMap, AssetMap, AssetMap) = {
      if (_stuff != null) return (_stuff, _oldstuff, _newstuff)
      
      val saw = JavaSeriesInventory.loadSeries(brief.key);

      _stuff = AssetMgr.find("Movie", AssetLocation.mutantEnv((brief.asInstanceOf[FileAssetBrief])
               .getLocalDir()), true);
      
      // reset the movies keys to be context keys
      for (b <- _stuff.values) {
         val c = new AssetCtxKey (b.key.meta, b.key.id, b.key.aloc, new AssetContext().role("series", brief.key));
         b.asInstanceOf[AssetBriefImpl].setKey (c)
      }

     _oldstuff = new AssetMap
     _newstuff = new AssetMap

     // reset the icons for movies i saw already
        for (b <- _stuff.values) {
           if (saw.containsKey(b.getKey())) {
              _oldstuff.put(b.getKey(), b);
           } else {
              _newstuff.put(b.getKey(), b);
           }
        }

      (_stuff, _oldstuff, _newstuff)
   }
   
   override def render(t:Technology , stream:DrawStream ) : AnyRef = {
      razie.Draw.seq(
            super.render(t,stream),
            AssetMgr.pres().toDrawable(newItems.jvalues(), null, Next(Series.ACTION_OLD, key)),
            "-------------OLD STUFF--------------",
            AssetMgr.pres().toDrawable(oldItems.jvalues(), null, Next(Series.ACTION_NEW, key)))
   }
}

   /** my factory for context actions */
case class Next (val item:ActionItem, val series:AssetKey) extends Affordance {

      override def make(k:AssetKey, o:AnyRef) : Array[ActionToInvoke] = {
         val ati = Array[ActionToInvoke](new AssetActionToInvoke(series, item))
         ati(0).set("episode", k)
         ati
      }
   }

object Series {
   val ACTION_OLD = new ActionItem("seriesold", RazIcons.POWER.name);
   val ACTION_NEW = new ActionItem("seriesnew", RazIcons.POWER.name);
}
