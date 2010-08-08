/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media.players;

import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.assets.AssetBriefImpl;
import razie.assets.FileAssetBrief;
import razie.base.data.XmlDoc;


/**
 * standard file player: will simply execute the file. You need to map the actual windows player for
 * the file type somewhere
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class SeriesPlayer implements SdkPlayer {
    private static AssetBrief brief   = new AssetBriefImpl();
    protected String          program = "";

    static {
        brief.setName("SeriesPlayer");
        brief.setBriefDesc("Series Player");
        brief.setIcon("c:/video/wmp.PNG");
    }

    public boolean canPlay(AssetBrief m) {
        return m.getKey().getType().equals("Series");
    }

    public AssetBrief getBrief() {
        return brief;
    }

    public PlayerHandle play(AssetBase m) {
       FileAssetBrief fab = (FileAssetBrief)m.getBrief();
        String fname = fab.getFileName();
        String path = fab.getLocalDir() == null ? "" : fab.getLocalDir();

        // TODO find the next in the series, update the series and play it...
        // take into account failed plays, "re-play last one" etc

        return new WinPlayerHandle(PlayerHandle.PLAYING, m.getBrief());
    }

    public PlayerHandle makeHandle() {
        return new WinPlayerHandle(PlayerHandle.PLAYING, null);
    }

    public void init(XmlDoc doc, String xpath) {
    }
}
