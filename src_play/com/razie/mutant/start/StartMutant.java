package com.razie.mutant.start;

import java.io.File;

import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.agent.AgentConfig;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.sdk.TempFilesRazie;

/**
 * just testing upgrade...
 */
public class StartMutant {

   // Listen for incoming connections and handle them
   public static void main(String[] args) {
      Log.program = "StartMutant";

      if (args.length > 0 && args[0].equals("sleep")) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            // ignore
            e.printStackTrace();
         }
      } else {
         Process process = null;

         while (true) {
            try {
               Log.logThis("MUTE...");
               TempFilesRazie.copyFile(AgentConfig.getMutantDir() + "\\upgrade\\razmutant.jar", AgentConfig
                     .getMutantDir()
                     + "\\razmutant.jar");
               Log.logThis("START mutant...");
               process = Runtime.getRuntime().exec("c:\\video\\raz\\srerun.cmd", null,
                     new File("c:\\video\\raz\\"));
               Log.logThis("Started process: " + process);
            } catch (Exception e) {
               Log.logThis("IGNORING: ", e);
            }
            try {
               Thread.sleep(5000);
            } catch (InterruptedException e) {
               Log.logThis("IGNORING: ", e);
            }

            boolean up = true;

            while (up) {
               up = false;
               Log.logThis("Testing mutant...");
               try {
                  ActionToInvoke a = new ServiceActionToInvoke(Agents.me().url, "control", new ActionItem(
                        "Status"));
                  Object oo = a.act(null);
                  String result = oo.toString();
                  // CmdClient client = new CmdClient(AssetLocation.mutantEnv("localhost", ""));
                  // String result = client.execute("status", "");
                  Log.logThis("CMD_RESULT: " + result);
                  if (result.contains("OK")) {
                     up = true;
                  }
               } catch (Throwable e) {
                  Log.logThis("IGNORING: ", e);
               }

               Log.logThis(up ? "mutant up..." : "mutant down...");

               if (!up && process != null) {
                  Log.logThis("destroy mutant...");
                  process.destroy();
               }

               try {
                  Thread.sleep(5000);
               } catch (InterruptedException e) {
                  Log.logThis("IGNORING: ", e);
               }
            }
         }
      }
   }

}
