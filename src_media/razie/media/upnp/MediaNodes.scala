/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.media.upnp

import razie.base._
import com.razie.media.config._
import razie.assets._
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.media._
import com.razie.pub.comms._
import razie.base.data.XmlDoc

import java.io.File;
import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.AssetMgr;

import com.razie.media.config.MediaConfig;
import com.razie.pub.assets.JavaAssetMgr;
import com.razie.pub.base.data.HttpUtils;
import razie.base.data.XmlDoc.Reg;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import razie.draw.DrawAccumulator;
import razie.draw.DrawStream;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaResponse;
import com.razie.pub.upnp.DIDLDrawStream;

object KK {
   def newkey (id:String) = new AssetKey("UPNPBROWSER", id)
}

object UpnpServer {
   val root = new Root(Series :: ByFolder :: ByCategory :: Nil)
}

case class Root (nodes:List[UpnpNode]) 
   extends UpnpContainer (razie.AI("UpnpRoot", "Mutant Jukebox"), KK.newkey("root"), "-1", 1, "movie,music,photo") {

   nodes.foreach {x=>super.addContainer(x.container)}
   
   override def upnpID = "0"
}

object ByCategory extends UpnpContainer(razie.AI("Categories", "Categories"), KK.newkey("Categories"), "0", 1, "movie") {

   override def refreshMeta= childCount = MediaConfig.getInstance.getCategories.size
   
   override def refresh { 
      for (x <- razie.M apply MediaConfig.getInstance.getCategories.keySet()) 
         addContainer (new AssetQueryNode(upnpID, AllOfType("Movie"), null, true, razie.AI(x, x)))
   }
}

class AssetQueryNode (parent:String, criteria:QueryCriteria, env:AssetLocation , recurse:Boolean, title:ActionItem ) 
   extends UpnpContainer(title, KK.newkey(title.name), parent, 1, "movie") {

   override def refreshMeta= childCount = 100
   
   override def refresh { 
      val assets = AssetMgr.query(criteria, env, recurse, new AssetMap())
//      for (x <- razie.RJS apply MediaConfig.singleton.getCategories.values()) 
   }
   
}

object Series extends AssetQueryNode("0", AllOfType("Series"), null, true, razie.AI("Series", "Series")) {

//   override def refresh { }
}

object ByFolder 
   extends UpnpContainer (razie.AI("Folders", "Folders"), KK.newkey("Folders"), "0", 1, "movie") {

   override def refreshMeta=
      childCount = XmlDoc.Reg.doc(MediaConfig.MEDIA_CONFIG).xpl(
               "/config/storage/host[@name='" + Agents.getMyHostName() + "']/media").size();

   override def refresh { 
      clear
      val who = Agents.getMyHostName
      for (e <- razie.M (XmlDoc.Reg.doc(MediaConfig.MEDIA_CONFIG).xpl(
            "/config/storage/host[@name='" +  who + "']/media"))) {
         val dir = e.getAttribute("localdir");
//         val title = new File(dir).getName
         val t = if(e.hasAttribute("type") ) e.getAttribute("type") else "Movie";
         val ref = new AssetKey("Folder", dir, AssetLocation.mutantEnv(who, ""));
         val f = new Folder(this.ref.toUrlEncodedString, dir, "", who, t)
         addContainer(f)
//         parentCache.put(ref, byFolder.ref.toUrlEncodedString());
//         out.write(f);
      }
   }
}

class Folder (parent:String, dir:String, parentDir:String, who:String, mediaType:String)
   extends UpnpContainer (dir, new AssetKey("Folder", dir, AssetLocation.mutantEnv(who, parentDir)),
         parent, 0, mediaType) {

   override def refreshMeta = if (childCount == 0) childCount = 50

   override def refresh = fill (false)
   
   /**
    * @return a (parent) container with containers and items (sub-folders and items)
    */
   def fill (countonly:Boolean) { 
      clear
//   public static JUpnpContainer browseFolder(JUpnpContainer parent, AssetKey ref, DrawStream out, String mediaType, boolean countonly) {

      var count = 0;

      var dir = ref.getId();
      var lp = ref.getLocation().getLocalPath();
      if (lp != null && lp.length() > 0) {
         dir = lp + (if(lp.endsWith("/") || lp.endsWith("\\")) "" else "/") + dir;
      }

      val who = Agents.getMyHostName

      val f = new File(dir);
      val entries = f.listFiles();
      if (entries != null) {
         for (entry <- entries) {
            if (entry.isDirectory()) {
               count=count+1
               val d = entry.getName();
//               val myref = new AssetKey("Folder", d, AssetLocation
//                     .mutantEnv(Agents.getMyHostName(), dir));
               val cont = new Folder(upnpID, d, dir, who, mediaType);
//               out.write(cont);
               if (!countonly)
                  addContainer(cont);
            }
         }
      }

      // now list files...

      val s = mediaType match {
         case "Movie" | "Music" | "Photo" => mediaType
         case _ => "Movie"
      }
      
      val assets = AssetMgr.find(s, AssetLocation.mutantEnv(dir), false)

      for (movie <- assets.values()) {
         movie.setParentID (upnpID);
//         parentCache.put(movie.getKey(), parentID);
         // res.add(movie.toUpnpItem(parentID));
         if (!countonly)
            addItem(movie);
      }

      childCount = count;
   }
}

