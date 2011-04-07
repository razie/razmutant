package com.razie.playground;

import java.util.Map;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;

public class AssetCache {
   static AssetCache singleton = new AssetCache();

   public AssetCache instance() {
      return singleton;
   }

   // TripleOptIdx<String, String, AssetKey, SdkAssetBrief> list = new TripleOptIdx<String, String, AssetKey,
   // SdkAssetBrief>();

   Map<AssetKey, AssetBrief> hasList(String type, String location) {
      // return list.getForSecondary(type, location);
      return null;
   }
}
