package com.razie.assets

import com.razie.dist.db._
import com.razie.pub.draw._
import com.razie.pub.base.data._
import com.razie.pub.draw.widgets._
import com.razie.pub.base._
import com.razie.pub.resources._
import com.razie.pub.assets._
import com.razie.assets._
import org.w3c.dom.Element;
  
class NewXmlAssetInventory extends BaseInventory with ScalaInventory {
   val clsName : String = "?"
   
    /** get an asset by key - it should normally be AssetBase or SdkAsset */
    override def get(ref:AssetKey) = {"" /* TODO actually implement this */}

   /**
    * @param link
    * @return
    */
   private def brief(link:RazElement ):AssetBrief  ={
      val b = new AssetBrief();

      b.setKey(new AssetKey(clsName, link a "key", new AssetLocation()));
      b.player = "internet";
      b.setFileName("");
      //b.setLocalDir(url);
      b.setBriefDesc(link.a("type"));
      b.setLargeDesc(link.a("desc"));

      if (link.a("type").equals("Movie")) {
         b.setImage(RazIcons.TYPE_MOVIE.name());
      } else if (link.a("type").equals("Channel")) {
         b.setImage(RazIcons.TYPE_CHANNEL.name());
      } else if (link.a("type").equals("Site")) {
         b.setImage(RazIcons.TYPE_SITE.name());
      } else if (link.a("type").equals("Stream")) {
         b.setImage(RazIcons.TYPE_STREAM.name());
      }

      b.setName(b.getKey().getId());

      b;
   }

    /**
     * get/make the brief for an asset given its key. The idea around briefs is that I don't always
     * need the full asset - often i can get around by just a proxy brief
     */
    override def getBrief(ref:AssetKey ):AssetBrief  = {
//      // use the list to prevent idiotic issues with duplicates...shift happens...
      val x = AgentDb.deleteme("links")
      val links = AgentDb.db("links").xml().listEntities("/db/link[@url='" + ref.getId() + "']");

      if (links.size() > 0) 
         brief(links.get(0));
      else
    	  null;
   }

  /** list all assets of the given type at the given location */
    override def xfind(ttype:String, env:AssetLocation, recurse:Boolean)
    :scala.collection.mutable.Map[AssetKey, AssetBrief] = {
      val ret = new scala.collection.mutable.HashMap[AssetKey, AssetBrief]()

      AgentDb.db(clsName).xml().listEntities("/db/" + clsName)
      for (val link <- (AgentDb.db(clsName).xml()).asInstanceOf[RazElement].xpl("/db/" + clsName)) {
         val b = brief(link);
         ret.put(b.getKey(), b);
      }

      ret;
      }

    /** execute command on asset. the asset can be local or remote */
    override def doAction(cmd:String , ref:AssetKey , ctx:ScriptContext ):AnyRef = {
      // delegate "delete to super
      super.doAction (cmd,ref,ctx);
    }

    override def getSupportedActions(ref:AssetKey ):Array[ActionItem] = {MyStatics.DFLTCMDS}

    /** get some extra details about an asset */
    override def details(brief:AssetBrief ):Drawable = {
      new DrawText("?")
    }
    
    /** initialize this instance for use with this Meta */
    override def init (meta:AssetMgr.Meta) = {}

   /**
    * destroy, deallocate and remove the asset - must implement auth&auth itself
    * 
    * TODO include in main inv interface as CRUD ops
    */
   override def delete(asset:AssetKey ) = { }

}

private object MyStatics {
   val DFLTCMDS = Array[ActionItem](AssetBrief.PLAY, AssetBrief.DELETE);
}