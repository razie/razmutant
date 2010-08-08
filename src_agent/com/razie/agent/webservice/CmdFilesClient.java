package com.razie.agent.webservice;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import razie.assets.AssetLocation;

import com.razie.pub.agent.AgentFileService;
import com.razie.pub.comms.CommRtException;
import com.razie.pub.base.log.Log;

/**
 * helper for file management, using remote mutant sources
 * 
 * @author razvanc
 * 
 */
public class CmdFilesClient {

   public static void remoteCopyDir(String srcDir, AssetLocation target, String targetDir, boolean recurse) {
      // TODO counht files and size and mark % complete while copying
      // TODO also copy only files that actually changed - check date/time?
      File[] files = new File(srcDir).listFiles();
      for (File file : files) {
         String fname = file.getName();

         if (file.isDirectory() && recurse)
            remoteCopyDir(srcDir + "/" + fname, target, targetDir + "/" + fname, true);
         else
            CmdFilesClient.copyTo(srcDir + "/" + fname, target, targetDir + "/" + fname);
      }
   }

   public static boolean copyLocal(String urlSrc, String fileDest) {
      try {
         URL aurl = null;
         if (urlSrc.startsWith("file:") || urlSrc.startsWith("http:")) {
            aurl = new URL(urlSrc);
         } else {
            File file = new File(urlSrc);
            aurl = file.toURL();
         }
         InputStream s = aurl.openStream();
         // do the tragic ...
         File destTmpFile = new File(fileDest + ".TMP");
         if (!destTmpFile.getParentFile().exists())
            destTmpFile.getParentFile().mkdirs();
         destTmpFile.createNewFile();

         FileOutputStream fos = new FileOutputStream(destTmpFile);

         AgentFileService.copyStream(s, fos);

         File destFile = new File(fileDest);
         if (!destTmpFile.renameTo(destFile)) {
            destFile.delete();
            if (!destTmpFile.renameTo(destFile)) {
               throw new CommRtException("Cannot rename...");
            }
         }
      } catch (MalformedURLException e) {
         RuntimeException iex = new IllegalArgumentException();
         iex.initCause(e);
         throw iex;
      } catch (IOException e1) {
         throw new CommRtException("Copy from: " + urlSrc + " to: " + fileDest, e1);
      }

      return true;
   }

   public static boolean copyFrom(AssetLocation src, String urlSrc, String fileDest) {
      try {
         logger.log("CLIENT_EXEC_CMD copyFrom...");
         logger.log("   copyFromRemote [" + src + "]" + urlSrc + " localfile[" + fileDest + "]");

         RazClientSocket server = new RazClientSocket(src.getHost(), Integer.parseInt(src.getPort()));

         // Get input from the client
         DataInputStream in = new DataInputStream(server.getInputStream());
         PrintStream out = new PrintStream(server.getOutputStream());

         out.println("copyFrom " + urlSrc);

         // do the tragic ...
         File destTmpFile = new File(fileDest + ".TMP");
         if (!destTmpFile.getParentFile().exists())
            destTmpFile.getParentFile().mkdirs();
         destTmpFile.createNewFile();
         FileOutputStream fos = new FileOutputStream(destTmpFile);

         AgentFileService.copyStream(in, fos);

         File destFile = new File(fileDest);
         if (!destTmpFile.renameTo(destFile)) {
            destFile.delete();
            if (!destTmpFile.renameTo(destFile)) {
               throw new CommRtException("Cannot rename...");
            }
         }
      } catch (MalformedURLException e) {
         RuntimeException iex = new IllegalArgumentException();
         iex.initCause(e);
         throw iex;
      } catch (IOException e1) {
         throw new CommRtException("Copy from: " + urlSrc + " to: " + fileDest, e1);
      }
      return true;
   }

   public static boolean copyTo(String urlSrc, AssetLocation dest, String fileDest) {
      try {
         logger.log("CLIENT_EXEC_CMD copyTo...");
         logger.log("   copyToRemote [" + dest + "]" + fileDest + " localfile[" + urlSrc + "]");

         URL aurl = null;
         if (urlSrc.startsWith("file:") || urlSrc.startsWith("http:")) {
            aurl = new URL(urlSrc);
         } else {
            File file = new File(urlSrc);
            aurl = file.toURL();
         }
         InputStream s = aurl.openStream();

         // this mutant socket will write password etc
         RazClientSocket server = new RazClientSocket(dest.getHost(), Integer.parseInt(dest.getPort()));

         // Get input from the client
         // DataInputStream in = new DataInputStream(server.getInputStream());
         PrintStream out = new PrintStream(server.getOutputStream());

         // the actual remote command
         out.println("copyTo " + fileDest);

         AgentFileService.copyStream(s, out);

      } catch (MalformedURLException e) {
         RuntimeException iex = new IllegalArgumentException();
         iex.initCause(e);
         throw iex;
      } catch (IOException e1) {
         throw new CommRtException("Copy from: " + urlSrc + " to: " + fileDest, e1);
      }
      return true;
   }

   public static List<String> listFiles(AssetLocation dest) {
      return null;
   }

   static final Log logger = Log.factory.create(CmdFilesClient.class.getName());

}
