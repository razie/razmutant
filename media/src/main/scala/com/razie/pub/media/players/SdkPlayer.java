/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media.players;

import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.base.data.XmlDoc;


/**
 * plugin interface for a movie player. These are registered in the config.xml. There can be
 * multiple instances of the same class but with different parameters and obviously different names.
 * 
 * @author razvanc
 * 
 */
public interface SdkPlayer {
    /**
     * initiate playing of the given asset
     * 
     * @param m asset to play
     * @return a handle to the player
     */
    public PlayerHandle play(AssetBase m);

    /**
     * if it can play the given asset
     * 
     * @param m asset to play
     * @return true if it can play the given asset
     */
    public boolean canPlay(AssetBrief m);

    /**
     * get a brief for this player
     * 
     * @return a brief for this player
     */
    public AssetBrief getBrief();

    public PlayerHandle makeHandle();

    /**
     * after new() there'll be an init(), based on the respective configuration element, which may
     * 
     * contain extra attributes
     */
    public void init(XmlDoc doc, String xpath);
}
