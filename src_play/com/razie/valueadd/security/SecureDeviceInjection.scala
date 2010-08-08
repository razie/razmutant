/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.valueadd.security

import razie.base.{ActionContext, ActionItem, AttrAccess}
import razie.assets._

//import com.razie.media.ScalaMediaInventoryUtils

/** add an "organize" action to movies 
 
 TODO i don't know if this needs to extend something...but it's usually nice when classes do belong...
 */
class SecureDeviceInjection extends razie.assets.AssetCmdInjection {
   final val SACCEPT = "accept1"
   final val SRESET = "resetSecurity1"
   final val ACCEPT = new ActionItem (SACCEPT)
   final val RESET = new ActionItem (SRESET)
   
   final val INVCMD = new ActionItem ("cmd/invcmd")
   
   val entityTypes = Array("Device")
   val actions = Array(ACCEPT, RESET)
  
   def doAction (entityKey:AssetKey, entity:Referenceable, action:String, ctx:ActionContext) : AnyRef = {
      require(ACCEPT.name == action || RESET.name==action)
  
      action match {
         case SRESET => organize (entityKey)
         case SACCEPT => moveTo (entityKey, ctx)
      }
   }

   def organize (entityKey:AssetKey) : AnyRef = {
      // 1. ask for a category
      val cat = "Action"

//      new PaintCategories (
//         (x) => (INVCMD, new AttrAccess.Impl ("cmd", "moveto", "ref", entityKey, "cat", x))
//      ).paint
"?"   }
  
   /**
    move a movie in a different category, on the same drive

    TODO upgrade to move across drives, somehow
    */
   def moveTo (entityKey:AssetKey, ctx:ActionContext) : AnyRef = {
      val ret = "what? " + " key="+entityKey.toUrlEncodedString + " ctx:"+ctx
      val cat=ctx.getAttr("cat")
"?>"      
   }
  
}
