package com.razie.dist.play.phantom;

import com.razie.dist.db.AgentDb;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.AttrAccess;
import com.razie.pub.events.EvListener;
import com.razie.pub.events.PostOffice;
import com.razie.pubstage.life.Lifegiver;

/**
 * lifecycle listneer to upgrade databases as they're open...
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class AgentPhantomService extends AgentService implements EvListener {
	public static AgentPhantomService singleton = null;

	public AgentPhantomService() {
		if (singleton != null) {
			throw new IllegalStateException(
					"ERR_SVC_INIT AgentPhantomService already initialized!");
		}

		// AssetMgr.register(new Meta(Jumper.META, "",
		// com.razie.sdk.phantom.Jumper.class.getName(),
		// com.razie.sdk.phantom.PhantomInventory.class.getName()));
		Jumper.instance();
		CrazyCar.instance();
		Phantom.instance();

		// HttpAssetSoaBinding.register(Jumper.class);

		// Lifegiver.needstoBreathe(Phantom.instance().key, Phantom.instance());
		Lifegiver.needstoBreathe(Jumper.instance().getKey(), Jumper.instance());
		// Lifegiver.needstoBreathe(CrazyCar.instance().key,
		// CrazyCar.instance());

		PostOffice.register(PostOffice.DFLT_LOCAL_TOPIC, this);
	}

	protected void onShutdown() {
	}

	protected void onStartup() {
	}

	public void eatThis(String srcId, String eventId, AttrAccess info) {
		if (AgentDb.EVT_UPDATEDB.equals(eventId)) {
			// when sync() from notification, will not notify others...
			// TODO optimize - add ver to attrs...
			// TODO not nice poltergeist
			String dbname = (String) info.getAttr("dbname");
			if (dbname.equals("assets")) {
				Jumper.load();
			}
		}
		if (Phantom.EVT_SMTH.equals(eventId)) {
		}
	}

	public String[] interestedIn() {
		return events;
	}

	static final String[] events = { AgentDb.EVT_NEWDB, AgentDb.EVT_REMOVEDB,
			AgentDb.EVT_UPDATEDB, Phantom.EVT_SMTH };
}
