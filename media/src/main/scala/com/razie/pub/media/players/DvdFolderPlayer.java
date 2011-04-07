/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.pub.media.players;

import razie.actionables.util.WinExec;
import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.assets.AssetBriefImpl;
import razie.assets.FileAssetBrief;
import razie.base.data.XmlDoc;

import com.razie.pub.base.log.Log;

/**
 * TODO play dvd folders...not sure what to execute
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class DvdFolderPlayer implements SdkPlayer {
    private static AssetBrief brief = new AssetBriefImpl();
    static {
        brief.setName("dvdfolder");
        brief.setBriefDesc("DVD Folder Player");
        brief.setIcon("c:/video/wmp.PNG");
    }

    public boolean canPlay(AssetBrief m) {
        return true;
    }

    public AssetBrief getBrief() {
        return brief;
    }

    public PlayerHandle play(AssetBase m) {
       FileAssetBrief fab = (FileAssetBrief)m.getBrief();
        String fname = fab.getFileName();
        String path = fab.getLocalDir() == null ? "" : fab.getLocalDir();

        String reply = "";

        try {
            WinExec.execFile(path + fname);
        } catch (Exception e) {
            Log.logThis(e.toString());
            reply += e.toString();
            return new WinPlayerHandle(reply, m.getBrief());
        }
        return new WinPlayerHandle(PlayerHandle.PLAYING, m.getBrief());
    }

    public PlayerHandle makeHandle() {
        return new WinPlayerHandle(PlayerHandle.PLAYING, null);
    }

    public void init(XmlDoc doc, String xpath) {
        // TODO Auto-generated method stub
    }

}
