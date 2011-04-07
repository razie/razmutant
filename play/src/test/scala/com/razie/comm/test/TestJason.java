package com.razie.comm.test;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import razie.assets.AssetBrief;
import razie.assets.AssetBrief$;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.InventoryAssetMgr;

import com.razie.pub.assets.JavaAssetMgr;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.sdk.util.Serializer;

public class TestJason extends TestCase {

   public void setUp() {
      // Mutant.init();
      JavaAssetMgr.init(new InventoryAssetMgr());
   }

   public void testPair() throws JSONException {
      AssetKey ref = new AssetKey("Movie", "300.ISO", AssetLocation.mutantEnv(Agents.getMyHostName(),
            "c:/video/"));
      AssetBrief brief = JavaAssetMgr.getBrief(ref);

      JSONObject write = new JSONObject(brief);
      String json = write.toString();

      JSONObject read = new JSONObject(json);
      AssetBrief newbrief = AssetBrief$.MODULE$.fromJson(read);
      logger.log(newbrief.toString());
   }

   public void testBrief() throws JSONException {
      AssetKey ref = new AssetKey("Movie", "300.ISO", AssetLocation.mutantEnv(Agents.getMyHostName(),
            "c:/video/"));
      AssetBrief brief = JavaAssetMgr.getBrief(ref);

      JSONObject write = new JSONObject(brief);
      String json = write.toString();

      JSONObject read = new JSONObject(json);
      AssetBrief newbrief = AssetBrief$.MODULE$.fromJson(read);
      logger.log(newbrief.toString());
   }

   public void testBriefList() throws JSONException {
      AssetMap briefs = JavaAssetMgr.find("Movie", new AssetLocation("c:/video"), false);

      String json = Serializer.assetsToJson(briefs);
      AssetMap newbriefs = Serializer.assetsFromJson(json);

      logger.log(newbriefs.toString());
   }

   static final Log logger = Log.factory.create(TestJason.class.getName());
}
