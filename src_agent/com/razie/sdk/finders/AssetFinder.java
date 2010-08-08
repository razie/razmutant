package com.razie.sdk.finders;

import java.io.File;
import java.io.FileFilter;

import org.w3c.dom.Element;

import razie.assets.AssetKey;
import razie.assets.AssetLocation;

import razie.assets.*;
import com.razie.pub.base.files.SSFilesRazie;
import com.razie.pub.base.log.Log;

/**
 * these can find assets of different types. Can also extract briefs from a ref
 */
public class AssetFinder {

	protected String assetType;

	private Element element;

	private String player;

	public AssetFinder() {
	}

	/**
	 * c-tor from xml config
	 * 
	 * @param e is the <assetfinder> element 
	 */
	public void init(Element e) {
		this.canRemote = false;
		this.recurse = true;
		this.assetType = e.getAttribute("type");
		this.player = e.getAttribute("player");
		this.element = e;

		if (e.hasAttribute("extension")) {
			String ext = e.getAttribute("extension");
			SSFilesRazie.ORFileFilter f = new SSFilesRazie.ORFileFilter();
			f.add(new SSFilesRazie.RegExpFileFilter(".*\\."
					+ ext.toLowerCase()));
			f.add(new SSFilesRazie.RegExpFileFilter(".*\\."
					+ ext.toUpperCase()));

			this.filter = new AFFileFilter(f);
		}
	}

	public AssetBrief getBrief(File file) {
		FileAssetBriefImpl b = new FileAssetBriefImpl();

		b.setKey(new AssetKey(this.assetType, file.getName(), AssetLocation
				.mutantEnv(file.getParent())));
		b.setPlayer (player);
		b.setFileName(b.getKey().getId());
		b.setLocalDir(b.getKey().getLocation().getLocalPath());
		b.setIcon(element.getAttribute("icon"));

		b.setFileSize(file.length());
		b.setBriefDesc(SSFilesRazie.niceFileSize(b.getFileSize()));
		
		// TODO add type to desc. because of upnp validation, this desc must be identical to AssetsInventory.getBrief()
		//b.setBriefDesc(SSFilesRazie.niceFileSize(b.size) + " - "
			//	+ element.getAttribute("name"));

		b.setName(nameFromFile(b.getFileName()));
		return b;
	}

	protected String nameFromFile(String fileName) {
		// cut the extension
		String name = fileName.replaceFirst("\\.[A-Za-z0-9]*$", "");

		// underscores
		name = name.replaceAll("_", " ");

		// TODO find caps/lower and insert spaces

		return name;
	}

	public String toString() {
		return "AssetFinder {canRemote=" + canRemote + ", recurse=" + recurse
				+ ", filter: " + (filter == null ? "null" : filter.toString());
	}

	/**
	 * file filter wrapper to extract info from the asset finder
	 */
	class AFFileFilter implements FileFilter {

		private FileFilter proxy;

		public AFFileFilter(FileFilter proxy) {
			this.proxy = proxy;
		}

		public boolean accept(File pathname) {
			if (proxy.accept(pathname)) {
				return true;
			}
			return false;
		}

		public Object clone() {
			return new AFFileFilter(this.proxy);
		}

		public String toString() {
			return "AssetFinder.AFFileFilter "
					+ Log.tryToString("", this.proxy);
		}
	}

	/** the file filter */
	public FileFilter filter;

	/** if the asset can be remote */
	public boolean canRemote;

	/** should recurse in directories */
	public boolean recurse;

	/** if the asset can be remote */
	public final static boolean FIND_REMOTE = true;

	public final static boolean FIND_ANY = true;

	/** should recurse in directories */
	public final static boolean FIND_RECURSIVE = true;

	public String getIcon() {
		return element.getAttribute("icon");
	}

	public String getPlayer() {
		return player;
	}
}
