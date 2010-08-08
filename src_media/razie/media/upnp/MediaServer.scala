/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package razie.media.upnp

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
import razie.assets.AssetMgr;

import com.razie.media.config.MediaConfig;
import com.razie.pub.assets._
import com.razie.pub.base.data.HttpUtils;
import razie.base.data.XmlDoc.Reg;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import razie.draw.DrawAccumulator;
import razie.draw.DrawStream;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaResponse;
import com.razie.pub.upnp.DIDLDrawStream;

import com.razie.pub.media._
import razie.assets._

import razie.base.ActionItem;
import razie.base.AttrAccessImpl;
import com.razie.pub.base.log.Log;
import razie.base.ActionToInvoke;
import com.razie.pub.comms.ServiceActionToInvoke;
import razie.draw._
import razie.draw.DrawStream;
import razie.draw.Drawable;
import razie.draw._
import com.razie.pub.resources.RazIcons;
import com.razie.pub.assets._

class MediaServer extends MediaServerService {

   def findContainer(ObjectID:String, BrowseFlag:String, Filter:String, StartingIndex:String,
         RequestedCount:String, next:razie.assets.Affordance) : UpnpContainer = {

      val container : UpnpContainer = ObjectID match {
         case "0" => UpnpServer.root
         case _ => UpnpNodes.get(ObjectID) match {
            case Some(s) => s
            case _ => {
//               result = super.Browse(ObjectID, BrowseFlag, Filter, StartingIndex, RequestedCount)
               null
            }
         }
      }
     
      container
   }

   def toDidl(container:JUpnpContainer, ObjectID:String, BrowseFlag:String, Filter:String, StartingIndex:String,
         RequestedCount:String, next:razie.assets.Affordance) : SoaResponse = {
      var result = new SoaResponse()
      
      val count = if (RequestedCount == null || RequestedCount.length <= 0) 0 else Integer.parseInt(RequestedCount)
      val start = if (StartingIndex == null || StartingIndex.length<=0) 0 else Integer.parseInt(StartingIndex)

      val browseMeta = BrowseFlag.equals("BrowseMetadata");
      val out = new DIDLDrawStream();

      if (container != null) {
         if (browseMeta) container.refreshMeta
         else container.refresh
         
         if (browseMeta) out.write(container.toUpnpXml())
         else container.writeContents(out, start, count)
         
      out.close();
      result.set("NumberReturned", String.valueOf(out.size()));
      val res = out.toString();

      result.set("Result", res);
      result.set("TotalMatches", result.getAttr("NumberReturned"));
      result.set("UpdateID", "10");

      Log.logThis(result.getAttr("Result").toString);
      }      
      
      result
   }

   override def BrowseImpl(ObjectID:String, BrowseFlag:String, Filter:String, StartingIndex:String,
         RequestedCount:String, next:razie.assets.Affordance) : SoaResponse = {
      val container = findContainer(ObjectID, BrowseFlag, Filter, StartingIndex, RequestedCount, next)
      toDidl(container, ObjectID, BrowseFlag, Filter, StartingIndex, RequestedCount, next)
   }

}

class DrawActionables (c:JUpnpContainer, caf:Affordance) extends Drawable{
    
    override def render(technology:Technology, stream:DrawStream) : AnyRef = {
            if (Technology.UPNP.equals(technology)) {
               // THE META upnp
                return c.toUpnpXml();
            } else if (Technology.HTML.equals(technology)) {
                if ("Folder".equals(c.ref.getType())) {
                    var lp = if (c.ref.getLocation().getLocalPath() == null ) "" else c.ref.getLocation()
                            .getLocalPath();

                    lp = lp + (if (lp.length() <= 0 || lp.endsWith("/") || lp.endsWith("\\") ) "" else "/")

                    // TODO reconcile GREF creation and avoid redoing this, use the one from
                    // container...
                    val ref = new AssetKey("Folder", lp + c.ref.getId(), AssetLocation.mutantEnv(c.ref
                            .getLocation().getHost(), ""));

                    val b1 = new ServiceActionToInvoke("cmd", new ActionItem(JUpnpContainer.cmdBROWSE, c.ref
                            .getId()), "ref", ref, "type", c.assetType);

                    return b1.render(technology, stream);
                } else if ("UPNPBROWSER".equals(c.ref.getType())) {
                   val b1 = new ServiceActionToInvoke("media", new ActionItem("Browse", c.ref
                         .getId()), "ref", c.ref, "type", c.assetType);
                    return b1.render(technology, stream);
                }
            }

            c.toString();
    }
   
}
