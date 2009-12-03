package com.razie.dist.db;

import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.assets.AssetBrief$;
import razie.assets.AssetBriefImpl;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;

import com.razie.assets.FileInventory;
import com.razie.pub.base.ActionItem;
import com.razie.pub.base.AttrAccess;
import com.razie.pub.comms.Agents;

/**
 * links are saved in "links" database
 * 
 */
public class DbInventory extends FileInventory {

	public DbInventory() {
	}

	/** use the base and add details */
   public AssetMap queryAll(String meta, AssetLocation env, boolean recurse, AssetMap ret) {

		AttrAccess aa = AgentDb.listLocalDb();
		for (String link : aa.getPopulatedAttr()) {
			AssetBriefImpl b = new AssetBriefImpl();

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
		AssetBriefImpl b = new AssetBriefImpl();
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
	public AssetBase getAsset(AssetKey ref) {
		return AgentDb.db(ref.getId());
	}

	private static final ActionItem[] defaultCmds = { AssetBrief$.MODULE$.DELETE() };
}
