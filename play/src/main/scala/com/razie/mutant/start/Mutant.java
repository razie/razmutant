package com.razie.mutant.start;

import java.io.IOException;

import com.razie.agent.AgentConfig;
import com.razie.pub.base.log.Log;
import com.razie.sdk.TempFilesRazie;

/**
 */
public class Mutant {

   public static void OLDmain(String[] args) throws IOException {

      if (args.length > 0 && args[0].equals("sleep")) {
         try {
            Log.logThis("SLEEP...");
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            Log.logThis("", e);
         }
      } else if (args.length > 0 && args[0].equals("mute")) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            // ignore
            Log.logThis(e.toString());
         }
         Log.logThis("MUTE...");
         TempFilesRazie.copyFile(AgentConfig.getMutantDir() + "\\upgrade\\razmutant.jar", AgentConfig
               .getMutantDir()
               + "\\razmutant.jar");
         try {
            Runtime.getRuntime().exec(AgentConfig.getMutantDir() + "\\srerun.cmd");
         } catch (IOException e) {
            Log.logThis(e.toString());
         }
      } else {

         // if (upgraded) {
         // try {
         // Log.logThis("sleep...");
         // Thread.sleep(1000);
         // } catch (InterruptedException e) {
         // Log.logThis(e.toString());
         // }
         // try {
         // Log.logThis("restart...");
         // Runtime.getRuntime()
         // .exec("c:\\video\\razmutant\\smute.cmd");
         // } catch (IOException e) {
         // Log.logThis(e.toString());
         // }
         // }
      }
   }

}
