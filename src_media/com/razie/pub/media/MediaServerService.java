/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.base.data.XmlDoc.Reg;
import razie.draw.DrawAccumulator;
import razie.draw.DrawStream;

import com.razie.media.config.MediaConfig;
import com.razie.pub.assets.JavaAssetMgr;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaResponse;
import com.razie.pub.upnp.DIDLDrawStream;

/**
 * the upnp mediaserver service
 * 
 * @author razvanc
 * 
 */
public class MediaServerService {
   static JUpnpContainer root = new JUpnpContainer("Mutant Jukebox", null, "-1", 1, "movie,music,photo");
   
   static JUpnpContainer byCategory = new JUpnpContainer("By Category", new AssetKey("UPNPBROWSER",
         "MutantByCat"), "0", 1, "movie");

   static JUpnpContainer byFolder = new JUpnpContainer("By Folder", new AssetKey("UPNPBROWSER",
         "MutantByFolder"), "0", 1, "movie");

   static JUpnpContainer[] rootKids = { byFolder }; // TODO byCategory

   @SuppressWarnings("unchecked")
   @SoaMethod(descr = "browse stuff", args = { "ObjectID", "BrowseFlag", "Filter", "StartingIndex",
         "RequestedCount" })
   public SoaResponse Browse(String ObjectID, String BrowseFlag, String Filter, String StartingIndex,
         String RequestedCount) throws IOException {
      return BrowseImpl(ObjectID, BrowseFlag, Filter, StartingIndex, 
            RequestedCount, razie.assets.NoAffordance$.MODULE$);
   }
   
   public SoaResponse BrowseImpl(String ObjectID, String BrowseFlag, String Filter, String StartingIndex,
         String RequestedCount, razie.assets.Affordance aff) throws IOException {
      SoaResponse result = new SoaResponse();

      int count = RequestedCount == null ? 0 : (RequestedCount.length() > 0 ? Integer
            .parseInt(RequestedCount) : 0);
      int start = StartingIndex == null ? 0 : (StartingIndex.length() > 0 ? Integer.parseInt(StartingIndex)
            : 0);

      boolean browseMeta = BrowseFlag.equals("BrowseMetadata");

      DIDLDrawStream out = new DIDLDrawStream();

      if (browseMeta && ObjectID.equals("0")) {
         // browse the root
         out.write(root.toUpnpXml("0"));
      } else if (ObjectID.equals("0")) {
         // browse the root

         byFolder.childCount = Reg.doc(MediaConfig.MEDIA_CONFIG).xpl(
               "/config/storage/host[@name='" + Agents.getMyHostName() + "']/media").size();

         writesection(out, rootKids, start, count);
      } else if (ObjectID.equals(byCategory.ref.toUrlEncodedString())) {
         // browse below the root, return folders or categories???
      } else if (ObjectID.equals(byFolder.ref.toUrlEncodedString())) {
         // browse below the root, return folders or categories???
         if (browseMeta) {
            byFolder.childCount = Reg.doc(MediaConfig.MEDIA_CONFIG).xpl(
                  "/config/storage/host[@name='" + Agents.getMyHostName() + "']/media").size();
            out.write(byFolder);
         } else
            browseFolders(Agents.getMyHostName(), out);
      } else {
         // the object ID must be aref
         AssetKey ref = AssetKey.fromString(HttpUtils.fromUrlEncodedString(ObjectID));

         if (browseMeta) {
            if ("Folder".equals(ref.getType())) {
               JUpnpContainer c = browseFolder(null, ref, out, "Movie", true); // will only
               // count children
               // UpnpContainer c = new UpnpContainer(ref.getId(), ref, "-1", 1,
               // "movie");
               // c.parentID = parentCache.get(ref) == null ? "-1" :
               // parentCache.get(ref);
               out.write(c);
            } else {
               AssetBrief b = JavaAssetMgr.getBrief(ref);
               b.setParentID (parentCache.get(ref) == null ? "-1" : parentCache.get(ref));
               if (b != null) {
                  out.write(b);
               } else {
               }
            }
         } else {
            if ("Folder".equals(ref.getType())) {
               JUpnpContainer c = browseFolder(null, ref, out, "Movie", false);
               List l = new ArrayList();
               l.addAll(c.getContainers());
               l.addAll(c.getItems());

               writesection(out, l.toArray(), start, count);
            } else {
               // can't be...
            }
         }
      }

      out.close();
      result.setAttr("NumberReturned", String.valueOf(out.size()));
      String res = out.toString();

      result.setAttr("Result", res);
      result.setAttr("TotalMatches", result.getAttr("NumberReturned"));
      result.setAttr("UpdateID", "10");

      Log.logThis((String) result.getAttr("Result"));
      return result;
   }

   protected static void writesection(DrawStream out, Object[] o, int start, int count) {
      int s = start <= 0 ? 0 : (start > o.length ? o.length : start);
      int c = count <= 0 ? o.length : count;

      if (c > o.length)
         c = o.length;

      for (int i = 0; i < c && (i + s) < o.length; i++)
         out.write(o[s + i]);
   }

   public static void browseFolders(String who, DrawAccumulator out) {
      for (Element e : Reg.doc(MediaConfig.MEDIA_CONFIG).xpl(
            "/config/storage/host[@name='" + who + "']/media")) {
         String dir = e.getAttribute("localdir");
         String t = e.hasAttribute("type") ? e.getAttribute("type") : "Movie";
         AssetKey ref = new AssetKey("Folder", dir, AssetLocation.mutantEnv(who, ""));
         JUpnpContainer f = new JUpnpContainer(dir, ref, byFolder.ref.toUrlEncodedString(), 1, t);
         parentCache.put(ref, byFolder.ref.toUrlEncodedString());
         out.write(f);
      }
   }

   /**
    * @return a (parent) container with containers and items (sub-folders and items)
    */
   public static JUpnpContainer browseFolder(JUpnpContainer parent, AssetKey ref, DrawStream out, String mediaType, boolean countonly) {
      if (parent == null) parent = new JUpnpContainer(ref.getId(), ref, "", 0, mediaType);
      parent.parentID = parentCache.get(ref) == null ? "-1" : parentCache.get(ref);

      int count = 0;

      String dir = ref.getId();
      String lp = ref.getLocation().getLocalPath();
      if (lp != null && lp.length() > 0) {
         dir = lp + (lp.endsWith("/") || lp.endsWith("\\") ? "" : "/") + dir;
      }

      String parentID = ref.toUrlEncodedString();

      File f = new File(dir);
      File[] entries = f.listFiles();
      if (entries != null) {
         for (File entry : entries) {
            if (entry.isDirectory()) {
               count++;
               String d = entry.getName();
               AssetKey myref = new AssetKey("Folder", d, AssetLocation
                     .mutantEnv(Agents.getMyHostName(), dir));
               JUpnpContainer cont = new JUpnpContainer(d, myref, parentID, 1, mediaType);
               parentCache.put(myref, parentID);
               out.write(cont);
               if (!countonly)
                  parent.addContainer(cont);
            }
         }
      }

      // now list files...

      AssetLocation env = AssetLocation.mutantEnv(dir);
      AssetMap assets = null;
      if ("Movie".equals(mediaType))
         assets = JavaAssetMgr.find("Movie", env, false);
      else if ("Music".equals(mediaType))
         assets = JavaAssetMgr.find("Music", env, false);
      else if ("Photo".equals(mediaType))
         assets = JavaAssetMgr.find("Photo", env, false);
      else
         assets = JavaAssetMgr.find("Movie", env, false);

      for (AssetBrief movie : assets.jvalues()) {
         movie.setParentID ( parentID);
         parentCache.put(movie.getKey(), parentID);
         // res.add(movie.toUpnpItem(parentID));
         if (!countonly)
            parent.addItem(movie);
      }

      parent.childCount = count;

      return parent;
   }

   @SoaMethod(descr = "guess what", args = {})
   public SoaResponse GetSearchCapabilities() {
      SoaResponse result = new SoaResponse();
      result.setAttr("SearchCaps", "");
      return result;
   }

   @SoaMethod(descr = "guess what", args = {})
   public SoaResponse GetSortCapabilities() {
      SoaResponse result = new SoaResponse();
      result.setAttr("SortCaps", "");
      return result;
   }

   @SoaMethod(descr = "guess what", args = {})
   public SoaResponse GetSystemUpdateID() {
      SoaResponse result = new SoaResponse();
      result.setAttr("Id", "10");
      return result;
   }

   // TODO this cache must expire in say 5 min - right now i'm not removing
   // entries...
   private static Map<AssetKey, String> parentCache = new HashMap<AssetKey, String>();
}
