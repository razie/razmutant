/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media

import razie.base._
import razie.base.scripting._
import java.io.File;

import com.razie.media.config.MediaConfig;
import com.razie.pub.UnknownRtException;
import razie.base.data._
import razie.base.ActionItem;
import razie.assets._

import com.razie.assets._
import com.razie.pub.comms._
import com.razie.pub.assets._
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.base.log.Log
import com.razie.pub.media._
import com.razie.pub.media.players._

class ScalaMediaInventoryBik extends FileInventory {
   
   override def getBrief(ref:AssetKey ) : AssetBrief ={
      val b = super.getBrief(ref)

      val finder = findFinder(ref)
      
      if (finder != null) {
         b.player = finder.getPlayer();
         b.setLargeDesc("Player: " + b.player);
      }

      if (b.getIcon() == null) {
         // find which finder would accept this and use that icon
         if (finder != null) {
            b.setIcon(finder.getIcon());
         }
      }
      return b;
   }

   override def doAction(cmd:String , ref:AssetKey , ctx:ActionContext ):AnyRef =
      if (cmd.startsWith("play")) {
         val ss = cmd.split("/", 2);
         playRef(ss(1), ref);
      } else 
        super.doAction(cmd, ref, ctx);

   /** play a given asset with a preferred player */
   def playRef(prefPlayerNm:String, ref:AssetKey ):Object ={
      val as = new AssetImpl(AssetMgr.getBrief(ref));

      // TODO here trick i'm replacing remote locals with local remotes
      if (as.getKey().getLocation().isRemote()) {
         val path = as.getBrief().asInstanceOf[FileAssetBrief].getLocalDir();
         val host = as.getBrief().getKey().getLocation().getHost();

         val e = XmlDoc.Reg.doc(com.razie.media.config.MediaConfig.MEDIA_CONFIG).xpe(
               "/config/storage/host[@name=\"" + host + "\"]");

         // now, find remote drive mappings
         var break=false
         for (val maping <- com.razie.pub.base.data.RazElement.toraz(e) xpl "media") {
            if (maping.ha("remote") && !break) {
               val s1 = maping.a("localdir").replaceAll("\\\\", "/");
               // hack: if it starts with //, don't add it - this is for maanging remote servers
               val s2 = if (maping.a("remote").startsWith("//"))
                  maping.a("remote");
               else
                  "//" + host + "/" + maping.a("remote");

               if (path.startsWith(s1)) {
                  as.getBrief().asInstanceOf[FileAssetBrief].setLocalDir(path.replaceFirst(s1, s2));
                  break=true;
               }
            }
         }
      }

      var ppName = prefPlayerNm
      var prefPlayer = PlayerRegistry.getPlayer(prefPlayerNm, Agents.me().os, Agents.me().hostname);

      if (prefPlayer == null) {
         val finder = findFinder(as.getKey)
         if (finder != null) {
            ppName = finder.getPlayer()
         }
      }

      ScalaMediaInventoryBik.playAsset (ppName, as)
   }

   override def getSupportedActions(ref:AssetKey): Array[ActionItem] = ScalaMediaInventoryBik.defaultCmds


   // TODO why don't i need this?
//      override def delete(ref:AssetKey ) ={
//      if (ref.getLocation().isRemote()) {
//         // if asset is remote, will delegate to owner
//         ScalaMediaInventoryUtils.delegateCmd(ref.getLocation(), AssetBrief.DELETE, ref);
//      } else {
//         ScalaMediaInventoryUtils.moveToCat(ref, "bin");
//      }
//   }

}

object ScalaMediaInventoryBik {
  val defaultCmds = Array (AssetBrief.PLAY, AssetBrief.DELETE)
   
   /** play a given asset with a preferred player */

   def playAsset(prefPlayerNm:String, as:AssetBase):Object ={
      val prefPlayer = PlayerRegistry.getPlayer(prefPlayerNm, Agents.me().os, Agents.me().hostname);

      if (prefPlayer != null) {
         Log.logThis("MEDIA_FOUND_PLAYER " + prefPlayer.getBrief().getName() + " for " + as.getKey);
         val handle = prefPlayer.play(as);
         PlayerService.curPlayer = handle;
         return handle;
      } else {
         val m = "ERR_MEDIA_NO_PLAYER for " + as.getKey;
         Log.logThis(m);
         return HttpUtils.toUrlEncodedString(m);
      }
   }
   
    def delegateCmd(appEnv:AssetLocation , delete2:ActionItem , ref:AssetKey ) = {
      // TODO Auto-generated method stub
      "TODO...";
   }

   def moveToCat(ref:AssetKey , cat:String ) {
      // 1. find the storage
     val brief = AssetMgr.getBrief(ref) match {
        case b:FileAssetBrief => b
        case _ => throw new IllegalArgumentException ("Can't move non-file asset... "+ref)
     }
     val fullpath = brief.getLocalDir() + brief.getFileName();

     val locationPath = MediaConfig.findLocalStorageLocation(ref.getLocation().getHost(), fullpath);

      val oldfile = new File(fullpath);

      // 2. create a bin if it's not there
      val bin = new File(locationPath + "/" + cat);
      if (!bin.exists()) {
         if (!bin.mkdir()) {
            throw new RuntimeException("could not create directory " + bin.getAbsolutePath());
         }
      }

      // 3. move to bin...
      val newfile = new File(bin, brief.getFileName());

      if (!oldfile.renameTo(newfile))
         throw new UnknownRtException("ERR can't rename "+oldfile.getCanonicalPath + " TO " + newfile.getCanonicalPath);

      // 4. move the pic if any
      val pics = FileInventory.findPicFileName(brief.getLocalDir(), brief.getFileName());
      if (pics != null) {
         val oldfile = new File(brief.getLocalDir() + pics);
         val newfile = new File(bin, pics);

         if (!oldfile.renameTo(newfile))
            throw new UnknownRtException("can't rename PICTURE to bin...");
      }
   }
}
