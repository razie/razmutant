package com.razie.comm.test;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import razie.assets.AssetKey;
import razie.assets.AssetLocation;

import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.Comms;

public class TestCmdList extends TestCase {
   public static void main(String[] args) {
      Log.program = "Tester";
   }

   public static void testListJson() throws JSONException {
      String url = "http://localhost:4444/mutant/json/cmd/list/Movie";
      String otherList = (Comms.readUrl(url));

      JSONObject obj = new JSONObject(otherList);

      logger.log(obj.toString());
   }

   public static void testDetailsJson() throws JSONException {
      String url = "http://localhost:4444/mutant/json/cmd/details?ref="
            + new AssetKey("Movie", "300.ISO", AssetLocation.mutantEnv(Agents.getMyHostName(), "c:/video/"))
                  .toUrlEncodedString();
      String otherList = (Comms.readUrl(url));

      JSONObject obj = new JSONObject(otherList);

      logger.log(obj.toString());
   }

   static final Log logger = Log.factory.create(TestCmdList.class.getName());
}
