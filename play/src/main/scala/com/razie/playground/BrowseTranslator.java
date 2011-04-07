package com.razie.playground;

import com.razie.pub.playground.Translator;
import com.razie.pubstage.comms.ObjectStream;

/**
 * these are translators dedicated to browsing an external source
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public abstract class BrowseTranslator implements Translator {
   public abstract ObjectStream translate(ObjectStream response);
}
