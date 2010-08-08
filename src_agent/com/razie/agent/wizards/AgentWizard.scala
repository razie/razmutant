package com.razie.agent.wizards

import java.net._

object Fields {
   val userName = Field ("User Name")
   val cloudName = Field ("Cloud Name")
   val nodeName = Field ("Node Name")
   val nodeType = Field ("Node Type")
   val nodeOS = Field ("Node OS")
   val nodeDir = Field ("Node Dir")
}

case class Field (
  name  : String ,
  ttype : String = "String",
  value : String = ""
)

class Cinfo {
   def in = InetAddress.getLocalHost
   
   def name : String = in.getHostName
   def ip   : String = in.getHostAddress
   def ipv4 : String = ip
   def net : String = if (ipv4 != null && ipv4.length > 7) ipv4.substring(0, 7) else ""
}

case class UseCase (fields : List[Field])

class AgentWizard {
   import Fields._
   
   // this runs on the current computer
    
     
   // first time
   def createUser (uName:String, ctype:String, cname:String, cos:String, cdir:String) {
   }
  
   class createUser2 extends UseCase (userName :: nodeType :: nodeName :: nodeOS :: nodeDir :: Nil)
}

