package com.razie.comm.test;

import razie.assets.AssetLocation;
import junit.framework.TestCase;

import com.razie.pub.base.log.Log;
import com.razie.sdk.agent.webservice.CmdClient;

public class TestMutant extends TestCase {
   static AssetLocation target = AssetLocation.mutantEnv("localhost", "");

   public static void testEcho() {
      String result = (new CmdClient(target)).execute("echo", "please echo this");
      Log.logThis("CMD_RESULT: " + result);
      assertTrue("not expected...", result.contains("please"));
   }

   public static void testGetlog() {
      String result = (new CmdClient(target)).execute("getlog", "20");
      Log.logThis("CMD_RESULT: " + result);
      assertTrue("not expected...", result.contains("RECEIVED_CMD: getlog"));
   }

   public static void testConfig() {
      String result = (new CmdClient(target)).execute("config", "");
      Log.logThis("CMD_RESULT: " + result);
      assertTrue("not expected...", result.contains("<config>"));
   }

   public static void testStatus() {
      String result = (new CmdClient(target)).execute("status", "");
      Log.logThis("CMD_RESULT: " + result);
      assertTrue("not expected...", result.contains("Mutant"));
   }

   public static void testGET() {
      String result = (new CmdClient(target)).execute("GET", "/dothis/status HTTP/1.0");
      Log.logThis("CMD_RESULT: " + result);
      assertTrue("not expected...", result.contains("Mutant"));
   }

}
