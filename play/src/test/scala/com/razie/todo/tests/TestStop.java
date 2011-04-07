package com.razie.todo.tests;

import razie.assets.AssetLocation;

import com.razie.pub.base.log.Log;
import com.razie.sdk.agent.webservice.CmdClient;

public class TestStop {
   public static void main(String[] args) {
      Log.program = "Tester";
      CmdClient client = new CmdClient(AssetLocation.mutantEnv("localhost"));
      String result = client.execute("stop", "");
      Log.logThis("CMD_RESULT: " + result);
   }
}
