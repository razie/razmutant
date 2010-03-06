/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package com.razie.sdk.config;

import java.net.URL;

import razie.base.data.XmlDoc;


/**
 * to load the config for the OM RI - all request types and other entities are
 * configured in here rather than being hardcoded in the RI itself
 * 
 * @version $Revision: 1.3 $
 * @author $Author: razie $
 * @since $Date: 2007/07/11 20:10:25 $
 */
public class UserConfig extends XmlDoc {
	public static final String DFLT_CATALOG = "/cfg/user.xml";
	public static final String XMLDOC = "user.xml";

	public URL getConfigUrl() {
		return getClass().getResource(DFLT_CATALOG);
	}

	public static void init() {
		Reg.registerFactory(XMLDOC, new Factory());
	}

	public static class Factory implements XmlDoc.IXmlDocFactory {
		public XmlDoc make() {
			UserConfig singleton = new UserConfig();
			singleton.load(XMLDOC, singleton.getClass().getResource(DFLT_CATALOG));
			// XmlDoc.docAdd(XMLDOC, singleton);
			return singleton;
		}
	}
}
