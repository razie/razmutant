package com.razie.assets;

import razie.assets.ABDrawable;
import razie.assets.AssetBrief;
import razie.assets.AssetBriefImpl;
import razie.assets.AssetImpl;
import razie.assets.AssetKey;
import razie.assets.AssetMgr;

import razie.draw.DetailLevel;
import razie.draw.DrawList;
import razie.draw.DrawSequence;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw._
import razie.base.life.Being;

/**
 * a cool asset can get its own brief and can paint itself
 * 
 * @author razvanc
 * @version $Id$
 */
abstract class CoolAsset (ref:AssetKey) extends AssetImpl(ref) with Being {

   /** be sure to set the key before using it, eh? */

   def this() = this (null)

   override def getBrief() = {
      brief.setBriefDesc(this.whatAreYouDoing().label);
      brief
   }

   /** overwriting because cool assets generally do not have a brief until requested... */
   override def getKey() = nkey;

   /** paint self */
   override def render(t:Technology , stream:DrawStream ) : AnyRef = {
      val movie = getBrief();

      val vert = new DrawList();
      vert.isVertical = true;
      val horiz = new DrawList();

      horiz.write(new ABDrawable (movie, DetailLevel.FULL))

      // add more links...
      vert.write(horiz);

      vert
   }
}
