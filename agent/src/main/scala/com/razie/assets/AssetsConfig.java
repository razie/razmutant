/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.assets;

import razie.base.data.XmlDoc;

/**
 * bad name - this is the main configuration for the agent network
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class AssetsConfig extends XmlDoc {
    public static final String DFLT_CATALOG = "/cfg/assets.xml";
    public static final String ASSETS_CONFIG = "AssetsConfig";
    static AssetsConfig         singleton    = null;

    public static AssetsConfig getInstance() {
        if (singleton == null) {
            singleton = new AssetsConfig();
            singleton.load(ASSETS_CONFIG, singleton.getClass().getResource(DFLT_CATALOG));
            Reg.docAdd(ASSETS_CONFIG, singleton);
        } else
        	singleton.checkFile();
        return singleton;
    }
}
