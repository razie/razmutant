package com.razie.agent.network;

import razie.base.ActionItem;
import com.razie.pub.resources.RazIcons;

public class DeviceStaticsJava {
   public static final ActionItem cmdDEVICEINFO  = new ActionItem("deviceInfo", RazIcons.UNKNOWN.name());

   public static final ActionItem cmdUPGRADETO   = new ActionItem("upgradeTo", RazIcons.UPLOAD.name());
   public static final ActionItem cmdUPGRADEFROM = new ActionItem("upgradeFrom", RazIcons.DOWNLOAD.name());
   public static final ActionItem cmdSTOP        = new ActionItem("Stop", RazIcons.POWER.name());
   public static final ActionItem cmdUPGRADE     = new ActionItem("upgrade", RazIcons.UPGRADE.name());
   public static final ActionItem cmdDIE         = new ActionItem("Die", RazIcons.POWER.name());

}
