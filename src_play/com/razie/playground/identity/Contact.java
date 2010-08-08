package com.razie.playground.identity;

import razie.assets.AssetBaseImpl;

import razie.base.ActionItem;
import com.razie.pub.lightsoa.SoaAsset;
import com.razie.pub.lightsoa.SoaAssetHasState;

/**
 * 
 * TODO detailed docs
 * 
 * @author razvanc
 * 
 */
@SoaAsset(meta = Contact.sCLASS, descr = "contact and profile info")
@SoaAssetHasState
public class Contact extends AssetBaseImpl {
   public Contact() {
      super(null);
   }

   public static final String sCLASS = "Contact";
   public static final ActionItem META = new ActionItem(sCLASS, "/public/pics/contacts2.png");
}
