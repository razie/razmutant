/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media;

import org.w3c.dom.Element;

import razie.assets.AssetKey;

import com.razie.media.config.MediaConfig;
import com.razie.pub.assets.JavaAssetMgr;
import razie.base.ActionItem;
import razie.base.data.XmlDoc;
import razie.base.data.XmlDoc.Reg;
import razie.draw.ContainerStream;
import razie.draw.DrawStream;
import razie.draw.DrawTable;
import razie.draw.widgets.DrawToString;
import razie.draw.widgets.NavButton;
import razie.draw.widgets.NavLink.Size;
import razie.draw.widgets.NavLink.Style;

import com.razie.pub.comms.Agents;
import com.razie.pub.comms.ServiceActionToInvoke;
import com.razie.pub.media.MediaServerService;
import com.razie.pub.media.JUpnpContainer;
import com.razie.pub.resources.RazIcons;
import com.razie.sdk.assets.providers.MutantProvider;

public class MediaUtils {

	   static public void browse(String protocol, AssetKey ref, String mediaType, DrawStream out) {
		      String host = ref.getLocation().getHost();
		      String dir = ref.getId();

		      out.write("Browsing: " + host + " DIR=" + dir + " - ");

		      if (ref.getLocation().isLocal()) {
		         // aid: include a button to just search all of type from here
		         ActionItem cmdLISTMOVIES = new ActionItem("list", RazIcons.TYPE_MOVIE.name(), "Movies",
		                 "List all movies at this location");
		         ActionItem cmdLISTPHOTO = new ActionItem("list", RazIcons.TYPE_PHOTO.name(), "Photos",
		                 "List all movies at this location");
		         ActionItem cmdLISTMUSIC = new ActionItem("list", RazIcons.TYPE_MUSIC.name(), "Music",
		                 "List all movies at this location");
		         NavButton b = new NavButton(new ServiceActionToInvoke("cmd", cmdLISTMOVIES, "type", "Movie", "location", ref));
		         b.style(Style.JUST_ICON, Size.SMALL);
		         out.write(b);
		         b = new NavButton(new ServiceActionToInvoke("cmd", cmdLISTPHOTO, "type", "Photo", "location", ref));
		         b.style(Style.JUST_ICON, Size.SMALL);
		         out.write(b);
		         b = new NavButton(new ServiceActionToInvoke("cmd", cmdLISTMUSIC, "type", "Music", "location", ref));
		         b.style(Style.JUST_ICON, Size.SMALL);
		         out.write(b);
		      }
		      out.write("<br>");

		      // first, replace remote mapping with local version, if so given
		      if (dir.startsWith("//")) {
		         Element e = Reg.doc(MediaConfig.MEDIA_CONFIG).xpe(
		                 "/config/clouds/cloud/host[@name='" + host + "']");
		         for (Element maping : XmlDoc.xpl(e, "media")) {
		            if (maping.hasAttribute("remote") && dir.startsWith("//" + host + "/" + maping.getAttribute("remote"))) {
		               String s1 = maping.getAttribute("localdir").replaceAll("\\\\", "/");
		               String s2 = "//" + host + "/" + maping.getAttribute("remote");

		               dir = dir.replaceAll(s2, s1);
		               break;
		            }
		         }
		      }

		      if (Agents.getMyHostName().equals(host)) {
		         // local dir list
		         DrawTable folders = new DrawTable(0, 3);
		         out.open(folders);
		         JUpnpContainer c = MediaServerService.browseFolder(null, ref, new ContainerStream(folders), mediaType, false);
		         folders.close();

		         // now local assets
		         out.write("------- Assets --------");
		         JavaAssetMgr.pres().toDrawable(c.getItems(), out, null);
		      } else {
		         // delegate to remote
		         // make sure it's up:
		         MutantProvider mutant = new MutantProvider(host);
		         if (mutant.isUp()) {
		            String otherList = (String) mutant.browse("Movie", dir).read();
		            out.write(new DrawToString(host + ":<br>" + otherList));
		         } else {
		            out.write(host + " - not reacheable...<br>");
		         }
		      }

		   // out.write(reply);
		   }
}
