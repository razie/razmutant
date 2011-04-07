package com.razie.agent;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

import com.razie.pub.base.log.Log;

/**
 * manually changing the version before final builds...
 * 
 * the timestamp is added by mprepare.cmd when jaring the code
 * 
 * @author razvanc
 * 
 */
public class Version {

   static String VERSION = "0.1.5";
   static String tstamp = "?";

   public static String getDateTime() {
      if ("?".equals(tstamp)) {
         URL tfile = Version.class.getResource("/timestamp.txt");

         if (tfile != null) {
            DataInputStream is=null;
            try {
               is = new DataInputStream(tfile.openStream());
               tstamp = is.readLine();
               tstamp = tstamp + is.readLine();
            } catch (IOException e) {
               Log.logThis("IGNORING: ", e);
            } finally {
               try {
                  is.close();
               } catch (IOException e) {
                  Log.logThis("IGNORING: ", e);
               }
            }
         } else
            tstamp = "UNLKNOWN-can't find /timestamp.txt";
      }

      return tstamp;
   }

   public static String getVersion() {
      return VERSION + " on " + getDateTime();
   }

}
