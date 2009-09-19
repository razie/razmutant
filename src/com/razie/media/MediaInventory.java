package com.razie.media;

import java.io.File;

import org.w3c.dom.Element;

import com.razie.assets.Asset;
import com.razie.assets.AssetsInventory;
import com.razie.assets.EntityAction;
import com.razie.media.config.MediaConfig;
import com.razie.pub.UnknownRtException;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.assets.AssetMgr;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.data.XmlDoc;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.ActionToInvoke;
import com.razie.pub.comms.Agents;
import com.razie.pub.draw.DrawList;
import com.razie.pub.draw.widgets.NavButton;
import com.razie.pub.media.PlayerService;
import com.razie.pub.media.players.PlayerHandle;
import com.razie.pub.media.players.PlayerRegistry;
import com.razie.pub.media.players.SdkPlayer;
import com.razie.sdk.finders.AssetFinder;

/**
 * movies have specific extensions, descriptions, functionality (i.e. "google")
 * etc
 * 
 */
public class MediaInventory extends AssetsInventory {

	public AssetBrief getBrief(AssetKey ref) {
		AssetBrief b = super.getBrief(ref);
		addPlayersToDesc(b);

		if (b.getIcon() == null) {
			// find which finder would accept this and use that icon
			AssetFinder finder = findFinder(ref);
			if (finder != null) {
				b.setIcon(finder.getIcon());
				b.player = finder.getPlayer();
			}
		}
		return b;
	}

	protected static void addPlayersToDesc(AssetBrief b) {
		String desc = "Players: ";
		for (SdkPlayer player : PlayerRegistry.getPlayers(Agents.me().os, Agents.me().hostname).values()) {
			if (player.canPlay(b)) {
				desc += player.getBrief().getName() + ", ";
				break;
			}
		}
		b.setLargeDesc(desc);
	}

	public Movie get(AssetKey ref) {
		return new Movie(getBrief(ref));
	}

	/** play a given asset with a preferred player */
	protected Object playAsset(String prefPlayerNm, AssetKey ref) {
		Asset as = new Asset(AssetMgr.brief(ref));

		// TODO here trick i'm replacing remote locals with local remotes
		if (as.getKey().getLocation().isRemote()) {
			String path = as.getBrief().getLocalDir();
			String host = as.getBrief().getKey().getLocation().getHost();

			Element e = XmlDoc.Reg.doc(MediaConfig.MEDIA_CONFIG).getEntity(
					"/config/storage/host[@name=\"" + host + "\"]");

			// now, find remote drive mappings
			for (Element maping : XmlDoc.listEntities(e, "media")) {
				if (maping.hasAttribute("remote")) {
					String s1 = maping.getAttribute("localdir").replaceAll("\\\\", "/");
					String s2 = "//" + host + "/" + maping.getAttribute("remote");

					if (path.startsWith(s1)) {
						as.getBrief().setLocalDir(path.replaceFirst(s1, s2));
						break;
					}
				}
			}
		}

		SdkPlayer prefPlayer = PlayerRegistry.getPlayer(prefPlayerNm, Agents.me().os, Agents.me().hostname);

		if (prefPlayer == null) {
			AssetFinder finder = findFinder(ref);
			if (finder != null) {
				prefPlayer = PlayerRegistry.getPlayer(finder.getPlayer(), Agents.me().os, Agents.me().hostname);
			}
		}

		if (prefPlayer != null) {
			Log.logThis("FOUND_PLAYER " + prefPlayer.getBrief().getName() + " for " + ref);
			PlayerHandle handle = prefPlayer.play(as);
			PlayerService.curPlayer = handle;
			return handle;
		} else {
			String m = "ERR_NO_PLAYER for " + ref;
			Log.logThis(m);
			return m;
		}
	}

	@Override
	public Object doAction(String cmd, AssetKey ref, ScriptContext ctx) {
		if (cmd.startsWith("play")) {
			String[] ss = cmd.split("/", 2);
			return playAsset(ss[1], ref);
		}

		return super.doAction(cmd, ref, ctx);
	}

	public Object confirmDelete(AssetKey ref) {
		DrawList list = new DrawList();
		list.write(new NavButton(ActionItem.WARN, ""));
		list.write("Confirm deletion below or click BACK...");
		ActionToInvoke ati = new EntityAction(AssetBrief.DELETE, ref);
		ati.setAttr("confirmed", "yes");
		list.write(ati);
		return list;
	}

	public void delete(AssetKey ref) {
		if (ref.getLocation().isRemote()) {
			// if asset is remote, will delegate to owner
			delegateCmd(ref.getLocation(), AssetBrief.DELETE, ref);
		} else {
			moveToCat(ref, "bin");
		}
	}

	public static void moveToCat(AssetKey ref, String cat) {
		// 1. find the storage
		Asset as = new Asset(AssetMgr.brief(ref));
		String fullpath = as.getBrief().getLocalDir() + as.getBrief().getFileName();

		String locationPath = MediaConfig.findLocalStorageLocation(ref.getLocation().getHost(), fullpath);

		File oldfile = new File(fullpath);

		// 2. create a bin if it's not there
		File bin = new File(locationPath + "/" + cat);
		if (!bin.exists()) {
			if (!bin.mkdir()) {
				throw new RuntimeException("could not create directory " + bin.getAbsolutePath());
			}
		}

		// 3. move to bin...
		File newfile = new File(bin, as.getBrief().getFileName());

		if (!oldfile.renameTo(newfile))
			throw new UnknownRtException("can't rename to bin...");
	}

	public static Object delegateCmd(AssetLocation appEnv, ActionItem delete2, AssetKey ref) {
		// TODO Auto-generated method stub
		return "TODO...";
	}

}
