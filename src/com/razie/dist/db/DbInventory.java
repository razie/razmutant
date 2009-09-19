package com.razie.dist.db;

import java.util.HashMap;
import java.util.Map;

import com.razie.assets.AssetsInventory;
import com.razie.pub.assets.AssetBrief;
import com.razie.pub.assets.AssetKey;
import com.razie.pub.assets.AssetLocation;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.AttrAccess;
import com.razie.pub.comms.Agents;
import com.razie.sdk.assets.SdkAsset;

/**
 * links are saved in "links" database
 * 
 */
public class DbInventory extends AssetsInventory {

	public DbInventory() {
	}

	/** use the base and add details */
	public Map<AssetKey, AssetBrief> find(String type, AssetLocation env,
			boolean recurse) {
		Map<AssetKey, AssetBrief> ret = new HashMap<AssetKey, AssetBrief>();

		AttrAccess aa = AgentDb.listLocalDb();
		for (String link : aa.getPopulatedAttr()) {
			AssetBrief b = new AssetBrief();

			b.setKey(new AssetKey(AgentDb.sCLASS, link, AssetLocation
					.mutantEnv(Agents.getMyHostName(), "")));
			// b.setIcon(stream.getAttribute("icon"));
			b.setBriefDesc("a database...");

			b.setName(b.getKey().getId());
			ret.put(b.getKey(), b);
		}

		return ret;
	}

	public AssetBrief getBrief(AssetKey ref) {
		AssetBrief b = new AssetBrief();
		b.setKey(ref);

		// b.setIcon(stream.getAttribute("icon"));
		b.setBriefDesc("a database...");

		b.setName(b.getKey().getId());

		return b;
	}

	public ActionItem[] getSupportedActions(AssetKey ref) {
		return defaultCmds;
	}

	public void delete(AssetKey ref) {
		AgentDb.db(ref.getId()).delete(true);
	}

	@Override
	public SdkAsset get(AssetKey ref) {
		return AgentDb.db(ref.getId());
	}

	private static final ActionItem[] defaultCmds = { AssetBrief.DELETE };
}
