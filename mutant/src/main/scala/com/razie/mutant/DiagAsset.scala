package com.razie.mutant

import razie.JAS;
import razie.assets.AssetActionToInvoke;
import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.Meta;
import razie.assets.MetaSpec;
import razie.assets.HasMeta;
import razie.assets.ProxyInventory;
import razie.base.ActionItem;
import razie.base.ActionToInvoke;
import razie.draw.DrawSequence;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw.HttpDrawStream;
import razie.draw.Technology;
import razie.draw.widgets.DrawToString;

import com.razie.assets.CoolAsset;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.base.TimeOfDay;
import com.razie.pub.comms.Agents;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pub.resources.RazIcons;
import com.razie.pub.webui.DrawLater;

/**
 * auto-registering diagnostic asset - use to test remotes, management, etc
 *
 * @author razvanc
 * @version $Id$
 *
 */
@SoaAsset(descr = "diagnostic asset") // bindings=Array("http"))
class DiagAsset (k:String) extends CoolAsset with HasMeta {
  final val META = "Diag.razie";
  // this should be a constant
  override val metaSpec = new MetaSpec(
    new Meta(id=razie.AI cmdicon (META, "/public/pics/IceAgeScrat.png"), baseMeta="", 
        assetCls=this.getClass().getName(), inventory=classOf[ProxyInventory].getName(), namespace=""))

  /* funny initialization after redefining meta() */
  setKey(new AssetKey(META, k, null))

  // if not reg yet, reg the class as soa
  AgentHttpService.registerSoaAsset(classOf[DiagAsset]);

  razie.Assets.manage(this)

  override def getBrief() = {
    super.getBrief();
    this.brief.setName("");
    this.brief.setBriefDesc(sayboo());
    this.brief.setLargeDesc("");
    this.brief
  }

  @SoaMethod(descr = "say boo")
  def sayboo(): String = "Hi, I'm the DiagAsset key: " + this.getKey().toString();

  @SoaMethod(descr = "say boo", args = Array("msg"))
  def sayboo2(msg: String): String = "Hi, I'm the DiagAsset key: " + this.getKey().toString() + " msg=" + msg;

  override def render(t: Technology, stream: DrawStream): AnyRef = {
    val ati = new AssetActionToInvoke(Agents.me().url, this.getKey(), AssetBrief.DETAILS);
    new DrawSequence(super.render(t, stream), new DrawLater(ati));
  }

  /** note that this method will override the default that would otherwise call the paint() above */
  @SoaMethod(descr = "paint more details")
  @SoaStreamable
  def details(out: DrawStream) = {
    val d = out.asInstanceOf[HttpDrawStream]
    d.addMeta("<meta http-equiv=\"refresh\" content=\"10\">");
    d.addMeta("<META HTTP-EQUIV=\"Page-Enter\" CONTENT=\"blendTrans (Duration=.5)\">");

    new DrawToString("It is " + TimeOfDay.calcvalue());
  }

  override def whoAreYou = razie.Metas.meta(META).id
  override def whatAreYouDoing = new ActionItem("Just hanging in the background", RazIcons.UNKNOWN.name)
}
