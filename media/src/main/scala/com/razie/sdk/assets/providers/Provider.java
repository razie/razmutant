package com.razie.sdk.assets.providers;

import com.razie.pub.playground.Translator;
import com.razie.pubstage.comms.ObjectStream;
import razie.assets.*;

/**
 * the provider is a provider of assets, channels etc. Think of it as an external website, like
 * youtube.
 * 
 * <p>
 * Translation: for now translation is provided only from external TOWARDS the mutant code.
 * 
 * @stereotype thing
 */

public interface Provider extends AssetBase {
    Translator getBrowseTranslator();

    Translator getListTranslator();

    /** can browse channels, movies, etc */
    ObjectStream browse(String type, String what);

    /** can browse channels, movies, etc 
     * 
     * @param type type of asset, normally Movie
     * @param category optional - category
     * @param channel - optional, the channel
     * @param tags - list of tags, comma-separated
     * @param location - root directory to start the search, if any known...
     * @return
     */
    ObjectStream list(String type, String category, String channel, String tags, String location);

    /** is the provider up and accessible */
    boolean isUp();

    public static abstract class Impl extends AssetBaseImpl implements Provider {
       public Impl () { super(null); }
    }
}
