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
import razie.base.data.XmlDoc.Reg;

import com.razie.pub.base.log.Log;

/**
 * this player used for image files, will mount them with Daemon tools and the default system player
 * should play them automatically.
 * 
 * for now it assume WMP is the default player...
 */
public class PlayerDaemonTools implements SdkPlayer {
    private static AssetBrief brief = new AssetBriefImpl();
    static {
        brief.setName("DaemonT");
        brief.setBriefDesc("Daemon Tools Player");
        brief.setIcon("c:/video/wmp.PNG");
    }

    public boolean canPlay(AssetBrief m) {
        return m instanceof FileAssetBrief && ((FileAssetBrief)m).getFileName().toUpperCase().endsWith(".ISO");
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
            String exe = Reg.doc("user.xml").xpa(
                    "/config/tools/tool[@name=\"DaemonTools\"]/prop[@name=\"exe\"]/@value");
            String cmd = exe + " -unmount 0";
            reply += "EXECUTE" + cmd + "\n";
            WinExec.execCmd(cmd);
            Thread.sleep(250);
            WinExec.execCmd(cmd);
            cmd = exe + " -mount 0,\"" + path + fname + "\"";
            reply += "EXECUTE" + cmd + "\n";
            Thread.sleep(250);
            WinExec.execCmd(cmd);
        } catch (Exception e) {
            logger.log(e.toString());
            reply += e.toString();
            return new MyHandler(reply, m.getBrief());
        }
        return new MyHandler(PlayerHandle.PLAYING, m.getBrief());
    }

    public static class MyHandler extends WinPlayerHandle {
        public MyHandler(String x, AssetBrief b) {
            super(x, b);
        }

        public void stop() {
            try {
                String exe = Reg.doc("user.xml").xpa(
                        "/config/tools/tool[@name=\"DaemonTools\"]/prop[@name=\"exe\"]/@value");
                String cmd = exe + " -unmount 0";
            WinExec.execCmd(cmd);
            } catch (Exception e) {
                logger.log(e.toString());
            }
        }
    }

    public PlayerHandle makeHandle() {
        return new MyHandler(PlayerHandle.PLAYING, null);
    }

    public void init(XmlDoc doc, String xpath) {
    }

    static Log logger = Log.factory.create("UTILS", PlayerDaemonTools.class.getName());
}
