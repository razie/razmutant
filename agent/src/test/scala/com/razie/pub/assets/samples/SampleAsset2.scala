package com.razie.pub.assets.samples

import com.razie.pub.lightsoa._
import razie.assets._
import com.razie.assets._
import com.razie.pub.assets._
import com.razie.pub.base._
import com.razie.pub.resources._
import com.razie.pub.agent._
import com.razie.assets._
import com.razie.pub.assets._
import razie.draw._
import razie.base._
import com.razie.pub.comms._
import razie.draw.widgets._
import com.razie.pub.webui._

/** 
 * sample self defined asset implementation 
 */
@SoaAsset(descr = "sample self defined asset", bindings=Array("http"))
class SampleAsset2 extends CoolAsset with HasMeta {

   // this should be a constant
   final val META = "SampleAsset2.razie"
   def metaSpec = new MetaSpec(new Meta (razie.AI cmdicon(META, "/public/pics/web.png"), null))
   
   /* funny initialization after redefining meta() */
   setKey (new AssetKey(META, "the only one", null))
   
   override def whoAreYou = razie.Metas.meta(META).id
   
   override def whatAreYouDoing = new ActionItem("Just hanging in the background", RazIcons.UNKNOWN.name)

//   override def paint(ctx:ScriptContext) = myPaint(ctx)
   
   override def render(t:Technology , stream:DrawStream ) : AnyRef = {
//   def myPaint(ctx:ScriptContext) = {
       val details = new AssetActionToInvoke(Agents.me.url, this.getKey(), AssetBrief.DETAILS);
       val testing = new AssetActionToInvoke(Agents.me.url, this.getKey(), razie.AI("testingstring"));
//       new DrawSequence(super.paint(ctx), new DrawLater(details), testing)
       new DrawSequence(super.render(t,stream), new DrawLater(details), testing)
   }

   /** note that this method will override the default that would otherwise call the paint() above */
   @SoaMethod(descr = "paint more details")
   @SoaStreamable
   def details(out:DrawStream ) = {
       out.asInstanceOf[HttpDrawStream] write "sampledetails"
       render(out.getTechnology(), out)
   }

   @SoaMethod (descr="test method")
   def testing() = "testing<p>paragraph</p> " + this.getKey
}
