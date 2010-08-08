/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.assets;

import razie.assets.AssetKey;
import razie.assets.AssetKey$;
import razie.base.AttrAccess;
import razie.base.scripting.ScriptContext;
import razie.base.scripting.ScriptContextImpl;
import razie.base.scripting.ScriptFactory;
import razie.draw.DrawSequence;
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw.DrawableSource;
import razie.draw.Renderer;
import razie.draw.Technology;

import com.razie.pub.agent.AgentService;
import com.razie.pub.assets.JavaAssetMgr;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.PermType;
import com.razie.pub.lightsoa.HttpAssetSoaBinding;
import com.razie.pub.lightsoa.SoaAllParms;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaMethodSink;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.lightsoa.SoaStreamable;

/**
 * a command listener listens to commands, executes them and returns an object
 * 
 * @author razvanc
 * 
 */
@SoaService(name = "assets", bindings = { "http" }, descr = "asset ecosystem and services")
public class AssetService extends AgentService {

   @Override
   protected void onStartup() {
      
   }

   /** list some assets directly to the output stream */
   @SoaMethod(descr = "list assets of type to stream", perm = PermType.VIEW, args = { "type", "location",
         "recurse" })
   @SoaStreamable
   public void listLocal(DrawStream out, String type, String location, String recurse) {
      HttpAssetSoaBinding.listLocal(type, location, Boolean.parseBoolean(recurse), out);
   }

   @SoaMethod(descr = "details for the given asset", perm = PermType.VIEW, args = { "ref", "series" })
   public Object details(String ref, String series) {
      AssetKey kref = AssetKey$.MODULE$.fromString(ref);
      AssetKey ks = series == null ? null : AssetKey$.MODULE$.fromString(series);
      Object amovie = JavaAssetMgr.getAsset(kref);

      if (amovie instanceof Drawable || amovie instanceof DrawableSource) {
         DrawSequence ds = new DrawSequence();
         Object o = Renderer.Helper.draw(amovie, Technology.HTML, ds);
         if (o != null) {
            ds.write(o);
         }
         return ds;
      } else
//         return JavaAssetMgr.details(JavaAssetMgr.brief(kref));
         return "NO DETAILS....WHAT ??? the asset is not drawable - heh"; // TODO - some generic painting?
   }

   /** list some assets directly to the output stream */
   @SoaMethod(descr = "list assets of type to stream", perm = PermType.VIEW, args = { "type", "location",
         "recurse" })
   @SoaStreamable
   @SoaMethodSink
   @SoaAllParms
   public void sink(DrawStream out, AttrAccess parms) {
      // for now just /assets/TYPE
      HttpAssetSoaBinding.listLocal(parms.sa(SoaMethodSink.SOA_METHODNAME), "", true, out);
   }

   static final Log logger = Log.factory.create("", AssetService.class.getName());

   @Override
   protected void onShutdown() {
   }
}
