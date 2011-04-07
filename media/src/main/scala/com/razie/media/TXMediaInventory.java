package com.razie.media;

import razie.assets.AssetBrief;
import razie.assets.AssetKey;

import com.razie.assets.FileInventory;
import com.razie.pub.comms.Agents;
import com.razie.pub.media.players.PlayerRegistry;
import com.razie.pub.media.players.SdkPlayer;
import com.razie.sdk.finders.AssetFinder;

/**
 * movies have specific extensions, descriptions, functionality (i.e. "google")
 * etc
 * 
 */
public class TXMediaInventory extends FileInventory {

	public AssetBrief getBrief(AssetKey ref) {
		AssetBrief b = super.getBrief(ref);
		addPlayersToDesc(b);

		if (b.getIcon() == null) {
			// find which finder would accept this and use that icon
			AssetFinder finder = findFinder(ref);
			if (finder != null) {
				b.setIcon(finder.getIcon());
				b.setPlayer (finder.getPlayer());
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

}
