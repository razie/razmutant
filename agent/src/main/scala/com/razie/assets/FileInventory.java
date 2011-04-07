package com.razie.assets;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import razie.assets.AssetBrief;
import razie.assets.AssetImpl;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.BaseInventory;
import razie.assets.FileAssetBrief;
import razie.assets.FileAssetBriefImpl;
import razie.assets.Meta;

import com.razie.pub.assets.JavaAssetInventory;
import razie.base.ActionItem;
import com.razie.pub.base.NoStatic;
import com.razie.pub.base.NoStaticSafe;
import com.razie.pub.base.NoStatics;
import com.razie.pub.base.files.SSFilesRazie;
import com.razie.pub.base.log.Log;
import com.razie.pub.cfg.XmlConfigProcessor;
import com.razie.pub.cfg.XmlConfigProcessors$;
import com.razie.pubstage.data.JStrucList;
import com.razie.sdk.finders.AssetFinder;

/**
 * 
 * base implementation for file-based assets (i.e. music etc)
 * 
 */
@NoStaticSafe
public class FileInventory extends BaseInventory implements JavaAssetInventory {

   public FileInventory() {
      // there's no harm registering the same thing multiple times...they're both NoStatics
      XmlConfigProcessors$.MODULE$.put("assetfinder", finders.get());
   }

   public static FileInventory instance() {
      return (FileInventory) NoStatics.get(FileInventory.class);
   }

   protected AssetFinder findFinder(AssetKey ref) {
      String fullPath = ref.getId();
      if (ref.getLocation().getLocalPath() != null) {
         fullPath = ref.getLocation().getLocalPath();
         if (! fullPath.endsWith("/"))
            fullPath = fullPath+"/";
         fullPath = fullPath+ref.getId();
      }
      File f = new File(fullPath);

      for (AssetFinder finder : getFilters(ref.getType())) {
         if (finder.filter.accept(f)) {
            return finder;
         }
      }
      return null;
   }

   protected static AssetBrief setPicIfAny(FileAssetBrief b) {
      String pics = findPicFileName(b.getLocalDir(), b.getFileName());

      if (pics != null) {
         ((AssetBrief)b).setIcon(b.getLocalDir() + pics);
         ((AssetBrief)b).setImage(b.getLocalDir() + pics);
      }

      return (AssetBrief)b;
   }

   public static String findPicFileName(String getLocalDir, String getFileName) {
      // try to find an icon
      String pics = getFileName.replaceFirst("\\.[a-zA-Z0-9]+$", ".jpg");
      String pic = getLocalDir + pics;

      // windows crap
      // if (pic.startsWith("/")) {
      // pic = "\\" + pic.replaceAll("/", "\\\\");
      // }

      if (new File(pic).exists()) {
         return pics;
      } else {
         pics = getFileName.replaceFirst("\\.[a-zA-Z0-9]+$", ".png");
         pic = getLocalDir + pics;
         if (new File(pic).exists()) {
            return pics;
         } else {
            // WHS makes these thumbnails
            pics = "." + getFileName + ".jpg";
            pic = getLocalDir + pics;
            if (new File(pic).exists()) {
               return pics;
            }
         }
      }

      return null;
   }

   /**
    * part of discovering assets - a way to filter files in any selection dialog/finder
    * 
    * @return can be null
    */
   /* default */List<AssetFinder> getFilters(String type) {
      List<AssetFinder> l = finders.get().fileFiltersRegistry.get(type);
      return l != null ? l : Collections.EMPTY_LIST;
   }


//   @Override
//   public scala.Option getAsset (AssetKey k) {
//      Object o = javaGetAsset (k);
//      if (o == null) 
//         return scala.None$.MODULE$;
//      else
//         return new scala.Some(o);
//   }

   /** get an asset by key - it should normally be AssetBase or SdkAsset */
   @Override
   public Object getAsset(AssetKey ref) {
      return new AssetImpl (getBrief(ref));
   }

   /** queries can run in the background, they are multithreaded safe etc */
   /** default find works off the defined asset finders... */
   public AssetMap queryAll(String meta, AssetLocation env, boolean recurse, AssetMap toUse) {
      AssetMap ret = dfltfind(meta, env, false, false, recurse);

      for (AssetBrief b : ret.jvalues()) {
         if (b instanceof FileAssetBrief)
            setPicIfAny((FileAssetBrief)b);
      }
      return ret;
   }

   /** default assumes the ref contains a filename */
   public AssetBrief getBrief(AssetKey ref) {
      FileAssetBriefImpl b = new FileAssetBriefImpl();
      b.setFileName(ref.getId());
      b.setLocalDir(ref.getLocation().getLocalPath());
      b.setName(nameFromFile(b.getFileName()));
      b.setKey(ref);
      b.setFileSize((new File(b.getLocalDir() + b.getFileName()).length()));
      b.setBriefDesc(SSFilesRazie.niceFileSize(b.getFileSize()));
      return this.setPicIfAny(b);
   }

   public ActionItem[] getSupportedActions(AssetKey ref) {
      return ActionItem.NOACTIONS;
   }

   /**
    * the preferred naming convention is polish: ThisIsTheMovie
    * 
    * NOTE this is overwritten in the different assets to accomodate common names
    */
   private String nameFromFile(String fileName) {
      // cut the extension
      String name = fileName.replaceFirst("\\.[A-Za-z0-9]*$", "");

      // special chars
      name = name.replaceAll("[_\\[\\]\\(\\)]", " ");

      // TODO find caps/lower and insert spaces

      return name;
   }

   /**
    * default implementation for list all entities of a certain class, or all if the class is null. Note that
    * if the class is null, only the high-level entities are listed and not their children.
    * 
    * <p>
    * Please use the constants at the bottom when invoking this....
    * 
    * @param entityClassNm
    *           a certain class to list or null
    * @param env
    *           the appenv to search in (remote or local or null)
    * @param canRemote
    *           true if it can access remote assets
    * @param findAnyClass
    *           if true, will use generic file search and list all assets of all types
    * @param recurse
    *           if true, will search recursively
    * @return Map<GRef, Object> all entities of a certain class, or all if the class is null. Return may be
    *         empty, but never null.
    */
   public AssetMap dfltfind(String entityClassNm, AssetLocation env, boolean canRemote,
         boolean findAnyClass, boolean recurse) {
      AssetMap map = new AssetMap();

      // 1. make sure i have a location to search
      if (env == null) {
         logger.log("Cannot get info without environment details.");
         return map;
      }
      AssetKey ref = new AssetKey(entityClassNm, null, env);
      try {
         findAssets(env, findAnyClass, recurse, map, ref);
      } catch (Exception e) {
         // will just ignore for now...quite usual for Swing, i take it?
      }
      return map;
   }

   /**
    * this is if you only need to implement local list all - remote will be taken care of the default impl. In
    * that case, just overwrite this method and leave the find default
    * 
    */
   public AssetMap findAssets(AssetLocation env, boolean findAnyClass, boolean recurse,
         AssetMap map, AssetKey ref) throws IOException {

      if (getFilters(ref.getType()) != null) {
         SSFilesRazie.ORFileFilter filter = new SSFilesRazie.ORFileFilter();
         for (AssetFinder af : getFilters(ref.getType())) {
            filter.add(af.filter);
         }
         JStrucList<File> files = SSFilesRazie.listFiles(env.getLocalPath(), filter, recurse);
         listFileAssets(map, ref, files.elements());
      } else {
         logger.log("Local filter missing for: " + ref.getType());
         if (findAnyClass) {
            logger.log("ERR_CANNOT find any type of asset yet...");
         }
      }

      return map;
   }

   /**
    * use like:
    * 
    * <pre>
    * List files;
    * files = SmpFiles.findFiles(env.localPath, &quot;.*lam.*\\.xml&quot;);
    * listFileAssets(map, ref, files);
    * </pre>
    * 
    * @param map
    *           will add refs to this map
    * @param ref
    *           the ref originally used to search
    * @param files
    *           List<File> the list of files i found
    * @return
    * @throws IOException
    */
   protected AssetMap listFileAssets(AssetMap map, AssetKey ref,
         List<File> files) throws IOException {
      for (File file : files) {
         String fpath = file.getParent();
         String fname = file.getName();
         String meta = ref.getType();
         AssetBrief brief = null;

         // only when looking for plain files - change their ref.
         // TODO not actually a good idea, because they can be remote, in
         // which case i want to
         // download them and then treat them as another class...deh
         if (finders.get().fileFiltersRegistry.size() > 0) {
            for (String ecn : finders.get().fileFiltersRegistry.keySet()) {
               if (getFilters(ecn) != null) {
                  for (AssetFinder af : getFilters(ecn)) {
                     FileFilter filter = af.filter;
                     if (filter != null && filter.accept(file)) {
                        meta = ecn;
                        brief = af.getBrief(file);
                        break;
                     }
                  }
               }

               if (brief != null) {
                  // found it...stop
                  break;
               }
            }
         }
         AssetKey newref = new AssetKey(meta, fname, AssetLocation.mutantEnv(fpath));

         if (brief != null) {
            map.put(newref, brief);
         } else {
            Log.logThis("ERR_CANT_FIND_FINDER...screwY!");
         }
      }
      return map;
   }

   static final Log logger = Log.factory.create(FileInventory.class.getName());

   public static class AssetFinders implements XmlConfigProcessor {
      public void eat(Element e) {
         registerFinder(e);
      }

      /**
       * register a new asset finder. The problem is that not all inventories are derived from here
       * 
       * @param entityClassNm
       *           the entity class name for this finder
       * @param finder
       *           the finder for these entities
       */
      public void registerFinder(Element e) {
         try {
            AssetFinder finder = (AssetFinder) Class.forName(e.getAttribute("finder"), true,
                  ClassLoader.getSystemClassLoader()).newInstance();
            finder.init(e);
            registerFileFilter(e.getAttribute("type"), finder);

            logger.trace(3, "CREATE_FINDER " + e.getAttribute("finder"));
         } catch (Exception ex) {
            logger.alarm("ERR_CANT_CREATE_FINDER " + e.getAttribute("finder"), ex);
         }
      }

      /**
       * register a new asset finder. The problem is that not all inventories are derived from here
       * 
       * @param entityClassNm
       *           the entity class name for this finder
       * @param finder
       *           the finder for these entities
       */
      public void registerFileFilter(String type, AssetFinder finder) {
         List<AssetFinder> l = fileFiltersRegistry.get(type);
         if (l == null) {
            l = new ArrayList<AssetFinder>();
         }
         l.add(finder);
         fileFiltersRegistry.put(type, l);
      }

      /**
       * Map<String entClassNm, AssetFinder> add in here any filters for classes you know. If any file
       * browsed/looked at meets any of these filters, it's known to the system and descriptions, available
       * actions etc will change.
       * 
       * These filter instances better be mtsafe.
       */
      protected Map<String, List<AssetFinder>> fileFiltersRegistry = new HashMap<String, List<AssetFinder>>();

   }

   protected static NoStatic<AssetFinders> finders = new NoStatic<AssetFinders>("AssetFinders",
         new AssetFinders());

   @Override
   public void init(Meta meta) { /* empty */
   }
}
