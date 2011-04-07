/*
 * FileSyncUseCases.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.razie.filesync

/** 
 * this uniquely identifies a location: a directory on a server 
 * 
 * TODO add file system information ?
 */
class FLoc (val directory:String, val url:String) {}

/** these are the uses cases considered for file sync - need test cases */
trait FileSyncUseCases {
 
   /** UC1 - initial sync between two points */
   def uc1InitialSync (l1:FLoc, l2:FLoc);

   /** UC2 - a file in source has been updated*/
   def uc2detectupdate (l1:FLoc, l2:FLoc, pathinsrc:FLoc);
   
   /** UC2 - a file in source has been updated*/
   def uc2updatefile (l1:FLoc, l2:FLoc, pathinsrc:FLoc);

   /** UC3 - a file in dest has been updated*/
   def uc3updatefile (l1:FLoc, l2:FLoc, pathindest:FLoc);

   /** UC4 - a file in both src and dest has been updated*/
   def uc4conflict (l1:FLoc, l2:FLoc, pathinsrc:FLoc);

}
