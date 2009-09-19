package com.razie.media

import com.razie.pub.base.AttrAccess
import com.razie.pub.base.ActionItem
import com.razie.pub.base.ScriptContext
import com.razie.pub.assets.AssetKey
import com.razie.pub.assets.Referenceable

//import com.razie.media.ScalaMediaInventoryUtils

/** add an "organize" action to movies 
 
 TODO i don't know if this needs to extend something...but it's usually nice when classes do belong...
 */
class OrganizeMovies extends com.razie.pub.assets.AssetCmdInjector {
   final val SMOVETO = "moveto"
   final val SORGANIZE = "organize"
   final val MOVETO = new ActionItem (SMOVETO)
   final val ORGANIZE = new ActionItem (SORGANIZE)
   
   final val INVCMD = new ActionItem ("cmd/invcmd")
   
   val entityTypes = Array("Movie")
   val actions = Array(ORGANIZE, MOVETO)
  
   def doAction (entityKey:AssetKey, entity:Referenceable, action:String, ctx:ScriptContext) : AnyRef = {
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
         (x) => (INVCMD, new AttrAccess.Impl ("cmd", "moveto", "ref", entityKey, "cat", x))
      ).paint
   }
  
   /**
    move a movie in a different category, on the same drive

    TODO upgrade to move across drives, somehow
    */
   def moveTo (entityKey:AssetKey, ctx:ScriptContext) : AnyRef = {
      val ret = "what? " + " key="+entityKey.toUrlEncodedString + " ctx:"+ctx
      val cat=ctx.getAttr("cat")
      
      if (entityKey.getLocation().isRemote()) {
         // if asset is remote, will delegate to owner
//         ScalaMediaInventoryUtils.delegateCmd(entityKey.getLocation(), MOVETO, entityKey);
         MediaInventory.delegateCmd(entityKey.getLocation(), MOVETO, entityKey);
      } else {
         MediaInventory.moveToCat(entityKey, cat.toString)
//         ScalaMediaInventoryUtils.moveToCat(entityKey, cat.toString)
         "OK. Moved."
      }
   }
  
}
