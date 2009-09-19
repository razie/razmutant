package com.razie.agent.network;

import com.razie.pub.base.ActionItem;
import com.razie.pub.resources.RazIcons;

public class DeviceStaticsJava {
   public static final ActionItem cmdDEVICEINFO  = new ActionItem("deviceInfo", RazIcons.UNKNOWN);

   public static final ActionItem cmdUPGRADETO   = new ActionItem("upgradeTo", RazIcons.UPLOAD);
   public static final ActionItem cmdUPGRADEFROM = new ActionItem("upgradeFrom", RazIcons.DOWNLOAD);
   public static final ActionItem cmdSTOP        = new ActionItem("Stop", RazIcons.POWER);
   public static final ActionItem cmdUPGRADE     = new ActionItem("upgrade", RazIcons.UPGRADE);
   public static final ActionItem cmdDIE         = new ActionItem("Die", RazIcons.POWER);

}
