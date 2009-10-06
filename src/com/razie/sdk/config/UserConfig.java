/*
 * $Id: RiCatalog.java,v 1.3 2007/07/11 20:10:25 razie Exp $
 * 
 * Copyright 2006 - 2007 The Members of the OSS through Java(TM) Initiative. All rights reserved.
 * Use is subject to license terms.
 */
package com.razie.sdk.config;

import java.net.URL;

import com.razie.pub.base.data.XmlDoc;
import com.razie.pub.webui.MutantPresentation;

/**
 * to load the config for the OM RI - all request types and other entities are
 * configured in here rather than being hardcoded in the RI itself
 * 
 * @version $Revision: 1.3 $
 * @author $Author: razie $
 * @since $Date: 2007/07/11 20:10:25 $
 */
public class UserConfig extends XmlDoc {
	public static final String DFLT_CATALOG = "/user.xml";
	public static final String XMLDOC = MutantPresentation.XMLDOC;

	public URL getConfigUrl() {
		return getClass().getResource(DFLT_CATALOG);
	}

	public static void init() {
		Reg.registerFactory(XMLDOC, new Factory());
	}

	public static class Factory implements IXmlDocFactory {
		public XmlDoc make() {
			UserConfig singleton = new UserConfig();
			singleton.load(XMLDOC, singleton.getClass().getResource(
					DFLT_CATALOG));
			// XmlDoc.docAdd(XMLDOC, singleton);
			return singleton;
		}
	}
}
