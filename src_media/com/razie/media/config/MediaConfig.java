/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media.config;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import razie.base.data.XmlDoc;


/**
 * bad name - this is the main configuration for the agent network
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class MediaConfig extends XmlDoc {
    public static final String DFLT_CATALOG = "/cfg/media.xml";
    public static final String MEDIA_CONFIG = "MediaConfig";
    static MediaConfig         singleton    = null;

    /** will reload file if changed ! */
    public static MediaConfig getInstance() {
        if (singleton == null) {
            singleton = new MediaConfig();
            singleton.load(MEDIA_CONFIG, singleton.getClass().getResource(DFLT_CATALOG));
            Reg.docAdd(MEDIA_CONFIG, singleton);
        } else
        	singleton.checkFile();
        return singleton;
    }

    /** assume asset is on HOST server - find local storage from either local or remote path */
    public static String findLocalStorageLocation(String host, String dir) {
        Element e = getInstance().xpe("/config/storage/host[@name='" + host + "']");

        if (dir.startsWith("//")) {
            for (Element maping : XmlDoc.xpl(e, "media")) {
                if (maping.hasAttribute("remote")
                        && (dir.startsWith("//" + host + "/" + maping.getAttribute("remote"))
                        || dir.startsWith(maping.getAttribute("remote")))) {
                    return maping.getAttribute("localdir").replaceAll("\\\\", "/");
                }
            }
        } else {
            // it's already local
            for (Element maping : XmlDoc.xpl(e, "media")) {
                if (maping.hasAttribute("localdir")
                        && dir.startsWith(maping.getAttribute("localdir").replaceAll("\\\\", "/"))) {
                    return maping.getAttribute("localdir").replaceAll("\\\\", "/");
                }
            }
        }

        throw new IllegalArgumentException("can't find local path...host=" + host + " DIR=" + dir);
    }

    public Map<String, Element> categories   = null;
    
    public Map<String, Element> getCategories() {
        if (this.categories == null) {
            // lazy
            this.categories = new HashMap<String, Element>();
            for (Element e : getInstance().xpl("/config/categories/category")) {
                this.categories.put(e.getAttribute("name"), e);
            }
        }

        return this.categories;
    }
}
