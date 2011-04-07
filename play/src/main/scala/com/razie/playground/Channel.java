package com.razie.playground;

import razie.assets.AssetBase;
import razie.assets.AssetBaseImpl;
import razie.assets.AssetBrief;

/**
 * @stereotype thing
 */

public interface Channel extends AssetBase {
   public String getProvider();

   public static class Impl extends AssetBaseImpl implements Channel {

      public Impl() {
         super(null);
         // TODO Auto-generated constructor stub
      }

      public String getProvider() {
         // TODO Auto-generated method stub
         return null;
      }

   }
}
