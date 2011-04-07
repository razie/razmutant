package com.razie.playground.xx;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;

import com.razie.playground.PieceOfData;

/**
 * to reach objects, we need a manager for the initial creation, deletion etc - inspired from OSS/J and REST
 * 
 * @author razvanc
 * 
 */
public interface NEWAssetInventory {
   AssetBrief getBrief(AssetKey key);

   /** TODO return an Asset class */
   /**
    * REST/XCAP: GET on a ref. this is required so that the remote invokers can retrieve the object to invoke
    * based on its unique URL'd key
    */
   AssetBrief get(AssetKey key);

   /** REST/XCAP: DELETE on a ref */
   AssetBrief remove(AssetKey key);

   /** REST/XCAP: POST on a ref. the data is the contents of the post - normally an xml */
   AssetBrief update(AssetKey key, PieceOfData data);
}
