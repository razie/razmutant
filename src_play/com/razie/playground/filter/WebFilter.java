package com.razie.playground.filter;

import razie.assets.AssetActionToInvoke;
import razie.assets.AssetKey;
import razie.assets.Meta;
import razie.assets.ProxyInventory;
import razie.base.ActionItem;
import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;
import razie.base.life.Being;
import razie.draw.Drawable;
import razie.draw.widgets.DrawForm;

import com.razie.assets.CoolAsset;
import com.razie.pub.lightsoa.SoaAllParms;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaMethod;

/**
 * negociators are used to negociate a logical state in an agent group
 * 
 * TODO detailed docs
 * 
 * @author razvanc
 */
@SoaAsset(meta = WebFilter.sCLASS, bindings = { "http" }, descr = "can filter stuff")
public class WebFilter extends CoolAsset implements Being {
   public static final String sCLASS = "WebFilter.razie";
   public static final ActionItem META = new ActionItem(sCLASS, "/public/pics/IceAgeScrat.png");
   public static Meta MMETA = new Meta(META, "", WebFilter.class.getName(), ProxyInventory.class.getName(),
         "");

   private static final ActionItem MAKENEWSTEP1 = new ActionItem("makeNewStep1");
   private static final ActionItem MAKENEWSTEP2 = new ActionItem("makeNewStep2");

   String url;
   WebFilterInfo filter;

   public WebFilter(String u, WebFilterInfo i) {
      super(new AssetKey(sCLASS, u));
      this.url = u;
      this.filter = i;

      razie.Assets$.MODULE$.manage(this, MMETA);
   }

   /**
    * can create new web filters interactively
    */
   @SoaMethod(descr = "create new web filter interactively")
   public Drawable makeNew() {
      DrawForm df = new DrawForm(MAKENEWSTEP1, new AssetActionToInvoke(this.getKey(), MAKENEWSTEP1),
            new AttrAccessImpl("url", "http://www.google.com", "filter:script", "<write script here>"));
      return df;
   }

   /**
    * can create new web filters interactively
    */
   @SoaMethod(descr = "create new web filter interactively")
   @SoaAllParms
   public Drawable makeNewStep1(AttrAccess args) {
      return new AssetActionToInvoke(this.getKey(), MAKENEWSTEP2);
   }

   /**
    * can create new web filters interactively
    */
   @SoaMethod(descr = "create new web filter interactively")
   @SoaAllParms
   public String makeNewStep2(AttrAccess args) {
      return "ok";
   }

   public ActionItem whatAreYouDoing() {
      return META;
   }

   public ActionItem whoAreYou() {
      return META;
   }

}
