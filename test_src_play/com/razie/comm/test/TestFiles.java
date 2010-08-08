package com.razie.comm.test;

import razie.assets.AssetLocation;

import com.razie.agent.AgentConfig;
import com.razie.agent.webservice.CmdFilesClient;
import com.razie.pub.base.log.Log;

public class TestFiles {
   // Listen for incoming connections and handle them
   public static void main(String[] args) {
      Log.program = "Tester";
      String mutantdir = AgentConfig.getMutantDir();
      CmdFilesClient.copyFrom(AssetLocation.mutantEnv("localhost", ""), "c:/video/divx.PNG", mutantdir
            + "/divx.FROMREMOTE.PNG");
      CmdFilesClient.copyLocal(mutantdir + "/divx.FROMREMOTE.PNG", mutantdir + "/divx.LOCAL.PNG");
      CmdFilesClient.copyTo(mutantdir + "/divx.LOCAL.PNG", AssetLocation.mutantEnv("192.168.2.201", ""),
            mutantdir + "/divx.TOREMOTE.PNG");
   }
}
