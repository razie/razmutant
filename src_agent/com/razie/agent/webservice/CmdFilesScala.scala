package com.razie.agent.webservice

import java.io.File

object CmdFilesScala extends CmdFilesClient {
  
   /** copy a directory, recursively */
   def copyDir(srcDir:String , targetDir:String , recurse:Boolean, exclusions:Array[String]=Array() ) {
      val td = new File (targetDir)
      if (! td.exists)
         td.mkdirs
         
      // TODO counht files and size and mark % complete while copying
      // TODO also copy only files that actually changed - check date/time?
      val files = new File(srcDir).listFiles();
      for (file <- files) {
         val fname = file.getName();

         if (! exclusions.foldLeft(false){(b:Boolean,s:String)=>b || file.getName.matches(s)}) {
            if (file.isDirectory() && recurse)
               copyDir(srcDir + "/" + fname, targetDir + "/" + fname, true);
            else
               CmdFilesClient.copyLocal(srcDir + "/" + fname, targetDir + "/" + fname);
         }
      }
   }


}
