package com.razie.filesync.test

import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.assets._
import org.scalatest.junit._
import com.razie.filesync._
import java.io._
  
/** testing the RazElement */
class TestFileSyncUC extends JUnit3Suite {

   val src:FLoc = new FLoc ("c:/video/temp/src", "localhost")
   val dest:FLoc = new FLoc ("c:/video/temp/dest", "localhost")

   def crfile (path:String, fname:String, contents:String) = {
      val dir = new File (path)
      dir.mkdirs
      val f = new File (path+"/"+fname)
      val out:FileOutputStream = new FileOutputStream(f);
      out.write (contents.getBytes)
      out.close();
      out.flush();
   }
  
   def checkfile (path:String, fname:String, contents:String) = {
      val f = new File (path+"/"+fname)
      expect (true) {f.exists()}
      val out= new BufferedReader(new FileReader(f))
      expect (contents) {out.readLine}
      out.close();
   }
  
   /** create 2 dirs with 2 files each */
   def testACreateFiles = {
     crfile (src.directory, "f1.txt", "f1")
     crfile (src.directory, "f2.txt", "f2")
     crfile (src.directory+"/d1", "f11.txt", "f11")
     crfile (src.directory+"/d1", "f12.txt", "f12")
     crfile (src.directory+"/d2", "f21.txt", "f21")
     crfile (src.directory+"/d2", "f22.txt", "f22")
     
     checkFiles(src, "")
   }
   
   def checkFiles (src:FLoc, changed:String)= {
     checkfile (src.directory, "f1.txt", "f1"+changed)
     checkfile (src.directory, "f2.txt", "f2"+changed)
     checkfile (src.directory+"/d1", "f11.txt", "f11"+changed)
     checkfile (src.directory+"/d1", "f12.txt", "f12"+changed)
     checkfile (src.directory+"/d2", "f21.txt", "f21"+changed)
     checkfile (src.directory+"/d2", "f22.txt", "f22"+changed)
   }
   
   /** UC1 - initial sync between two points */
   def testuc1InitialSync = uc1InitialSync(src, dest)
   
   def uc1InitialSync (l1:FLoc, l2:FLoc) = {
     // assume src files created
   expect ("roota") { "name" }
   }

   /** UC2 - a file in source has been updated*/
   def testuc2InitialSync (l1:FLoc, l2:FLoc, pathinsrc:FLoc) = 
   expect ("roota") { "name" }

   /** UC3 - a file in dest has been updated*/
   def testuc3InitialSync (l1:FLoc, l2:FLoc, pathindest:FLoc) = 
   expect ("roota") { "name" }

   /** UC4 - a file in both src and dest has been updated*/
   def testuc4InitialSync (l1:FLoc, l2:FLoc, pathinsrc:FLoc) = 
   expect ("roota") { "name" }
}
 