package com.razie.assets

import com.razie.pub.base._
import com.razie.pub.assets._
import com.razie.pub.base.data._

/** allow injection of actions on assets */
class ScalaAssetMgr extends MutantAssetMgr with AssetMgrTrait {

   // TODO move the actual injection mechanism elsewhere, eh?
   //this.inject (new OrganizeMovies)
  
   /** this version takes into account possible injections first */
   override def supportedActionsImpl(ref:AssetKey) : Array[ActionItem] =
      super.supportedActionsImpl(ref) ++  injections (ref.getType())

   /** this version takes into account possible injections first */
   override def doActionImpl(cmd:String, ref:AssetKey, ctx:ScriptContext) : Object = {
      injection (ref.getType(), cmd) match {
         case Some(x) => x(ref, null, cmd, ctx)
         case None => cmd match {
        	 case "details" => detailsImpl (briefImpl(ref))
        	 case _ => super.doActionImpl(cmd,ref,ctx);
         }
      }
	}

   // TODO don't need mutable as retrun type
    def finds(ttype:String, env:AssetLocation , recurse:boolean) : scala.collection.mutable.Map[AssetKey, AssetBrief] = {
      val m = super.findImpl(ttype, env, recurse)
//      val ret = new scala.collection.mutable.HashMap[AssetKey, AssetBrief]()
      RazElement.tomap(m)
    }

}