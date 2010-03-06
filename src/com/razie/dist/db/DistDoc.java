/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package com.razie.dist.db;

/**
 * simple replicated distributed document - MODEL
 * 
 * This is the interface to implement by actual document, i.e. XML documents.
 * 
 * A distributed replicated document will replicate itself on several nodes, all
 * instances being kept in sync. Synchronization occurs on the fly, if all
 * instances are connected or whenever an instance connects to the network.
 * 
 * Changes can thus hapen off-line or on-line.
 * 
 * This implementation supports different format changes for the documents...
 * 
 * <h2>Implementation</h2>
 * 
 * You must implement everything about change management: keep current version,
 * track changes between versions and apply diffs.
 * 
 * <h2>Versioning</h2>
 * 
 * You can see that the diffs between versions are embedded in the document -
 * there is no separate versioning system, much like a wiki page. You can
 * reconstruct or "play the timeline" of any document.
 * 
 * Up to the local "lastSyncVersion", the versions are global, shared by all
 * instances. After that, the versions are local and have not yet been
 * propagated out. You can tell if a document is up-to-date because the two are
 * the same number, i.e. there have been no local changes.
 * 
 * <h2>Conflict resolution</h2>
 * 
 * Conflicts are detected by comparing the current local versions of the
 * different documents, versus their lastSyncVer. IF the instance TO BE updated
 * has a differnet current version from its lastSyncVer, it means it has evolved
 * independently in the mean time.
 * 
 * TODO future actual conflict resolution, with selectable strategies - not handled yet.
 * 
 * @author razvanc
 */
public interface DistDoc {
	/**
	 * extract differences between two versions. Note that if there is a
	 * transaction in progress, this is assumed NOT to include those diffs.
	 * 
	 * @param fromVersion
	 *            < getVersion()
	 * @param toVersion
	 *            <= getVersion()
	 * @return NULL if you don't have the complete list of diffs between the two
	 *         versions OR an object you're prepared to accept in
	 *         {@link #applyDiffs(int, int, String)}
	 */
	String getDiffs(int fromVersion, int toVersion);

	/**
	 * apply differences to update to a higher version
	 * 
	 * @param fromVersion
	 *            == getVersion()
	 * @param toVersion
	 *            > getVersion()
	 * @param diffs
	 * @throws RuntimeException
	 *             for anything that went wrong
	 */
	void applyDiffs(int fromVersion, int toVersion, String diffs);

	/**
	 * purge old differences when no longer neccessary
	 * 
	 * @param fromVersion
	 *            == getVersion()
	 * @param toVersion
	 *            > getVersion()
	 * @param diffs
	 */
	void purgeDiffs(int fromVersion, int toVersion);

	/**
	 * current version of the local instance. If different, will be asked to
	 * sync. if doc is brand new, should be -1. First change will make this 0...
	 */
	int getLocalVersion();

	/**
	 * current version when the local instance was last sync'd, regardless of
	 * where it was sync'd from.
	 */
	int getLastSyncVersion();

	/**
	 * create this doc. creating a doc includes creating files or whatnot for it
	 */
	// void create();
	/**
	 * load or reload this doc from the local storage. If it was loaded, reload
	 * last image
	 */
	// void reload();
	/** close resources associated with this doc */
	// void close();
	/** final delete of this doc - it will not exist anymore after this */
	// void delete();
	/** the current format version number */
	int getFormatVersion();

	/**
	 * upgrade from one version to the next. You must write in code the format
	 * upgrade
	 */
	void upgradeFormat(int fromFormatVer, int toFromatVer);
}
