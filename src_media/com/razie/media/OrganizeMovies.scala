/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media

import razie.base.AttrAccess
import razie.base.AttrAccessImpl
import razie.base.ActionItem
import razie.base.ActionContext
import razie.assets._

//import com.razie.media.ScalaMediaInventoryUtils

/** add an "organize" action to movies 
 
 TODO i don't know if this needs to extend something...but it's usually nice when classes do belong...
 */
class OrganizeMovies extends razie.assets.AssetCmdInjection {
   final val SMOVETO = "moveto"
   final val SORGANIZE = "organize"
   final val MOVETO = new ActionItem (SMOVETO)
   final val ORGANIZE = new ActionItem (SORGANIZE)
   
   final val INVCMD = new ActionItem ("cmd/invcmd")
   
   val entityTypes = Array("Movie")
   val actions = Array(ORGANIZE, MOVETO)
  
   def doAction (entityKey:AssetKey, entity:Referenceable, action:String, ctx:ActionContext) : AnyRef = {
      require(ORGANIZE.name == action || MOVETO.name==action)
  
      action match {
         case SORGANIZE => organize (entityKey)
         case SMOVETO => moveTo (entityKey, ctx)
      }
   }

   def organize (entityKey:AssetKey) : AnyRef = {
      // 1. ask for a category
      val cat = "Action"

      new PaintCategories (
         (x) => (INVCMD, new AttrAccessImpl ("cmd", "moveto", "ref", entityKey, "cat", x))
      ).paint
   }
  
   /**
    move a movie in a different category, on the same drive

    TODO upgrade to move across drives, somehow
    */
   def moveTo (entityKey:AssetKey, ctx:ActionContext) : AnyRef = {
      val ret = "what? " + " key="+entityKey.toUrlEncodedString + " ctx:"+ctx
      val cat=ctx.getAttr("cat")
      
      if (entityKey.getLocation().isRemote()) {
         // if asset is remote, will delegate to owner
         ScalaMediaInventoryBik.delegateCmd(entityKey.getLocation(), MOVETO, entityKey);
      } else {
         ScalaMediaInventoryBik.moveToCat(entityKey, cat.toString)
         "OK. Moved."
      }
   }
  
}
