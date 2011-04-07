package com.razie.filesync

import com.razie.pub.base._
import razie.base._
import com.razie.pub.comms.Agents
import com.razie.pub.base.log._
import com.razie.pub.lightsoa.SoaService
import com.razie.pub.lightsoa.SoaMethod
import com.razie.pub.lightsoa.SoaStreamable
import com.razie.pub.lightsoa.HttpSoaBinding
import razie.draw.DrawStream

/** 
 * deserialize...serialize using fa.toString
 */
object FileAbstract {

    def fromString (s:String) : FileAbstract =  {
      fromAA(JavaAttrAccessImpl.fromJsonString(s))
    }
    
    def fromAA (aa:AttrAccess) : FileAbstract =  {
      val fa=new FileAbstract (aa sa "name", aa sa "path", aa sa "location", java.lang.Boolean.parseBoolean(aa sa "isFolder"), aa sa "lastUpd");
      fa 
    }
    
    def fromFile (f:java.io.File) : FileAbstract =  {
      val fa=new FileAbstract (f.getName(),f.getPath(), Agents.me().name, f.isDirectory, f.lastModified.toString)
      fa 
    }
}

/** 
 * represents a file
 */
class FileAbstract (val name:String, val path:String, val location:String,val isFolder:Boolean,val lastUpd:String) {

  override def toString() :String = {
      val aa=new razie.AA("name", name, "path", path, "location", location, "isFolder", isFolder.toString, "lastUpd", lastUpd);
      aa.toString
    }
  
  def toJson() :String = {
      val aa=razie.AA("name", name, "path", path, "location", location, "isFolder", isFolder.toString, "lastUpd", lastUpd);
      (aa toJson null) toString
    }
  
}
