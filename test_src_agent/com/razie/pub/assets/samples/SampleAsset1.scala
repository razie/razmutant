package com.razie.pub.assets.samples

import com.razie.pub.lightsoa._
import com.razie.assets._
import com.razie.pub.assets._
import com.razie.pub.base._
import com.razie.pub.resources._
import com.razie.pub.agent._
import com.razie.assets._
import com.razie.pub.assets._
import razie.draw._
import com.razie.pub.comms._
import razie.draw.widgets._
import razie.assets._

/** 
 * sample self defined asset implementation 
 */
@SoaAsset(descr = "sample self defined asset", bindings=Array("http"))
class SampleAsset1 extends AssetImpl with HasMeta {

   // this should be a constant
   final val META = "SampleAsset1.razie"
   def metaSpec = new MetaSpec(new Meta (razie.AI cmdicon(META, "/public/pics/web.png"), null))
   
   /* funny initialization after redefining meta() */
   setKey (new AssetKey(META, "the only one", null))
   
   @SoaMethod (descr="test method")
   def testing() = "testing<p>paragraph</p> " + this.getKey
}
