/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.media.players;

import razie.actionables.util.WinExec;
import razie.assets.AssetBase;
import razie.assets.AssetBrief;
import razie.assets.AssetBriefImpl;
import razie.assets.AssetKey;
import razie.assets.FileAssetBrief;

import com.razie.pub.base.log.Log;

/**
 * "play" an internet URL in the explorer
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
public class InternetPlayer extends WinFilePlayer implements SdkPlayer {
    private static AssetBrief brief = new AssetBriefImpl(new AssetKey("InternetPlayer"));
    static {
        brief.setBriefDesc("Internet Player");
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
            WinExec.execCmd(shell(), program() + " " + path + fname);
        } catch (Exception e) {
            Log.logThis(e.toString());
            reply += e.toString();
            return new WinPlayerHandle(reply, m.getBrief());
        }
        return new WinPlayerHandle(PlayerHandle.PLAYING, m.getBrief());
    }

}
