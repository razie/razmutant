/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import razie.assets.AssetActionToInvoke;
import razie.assets.AssetBrief;
import razie.assets.AssetBrief$;
import razie.assets.AssetBriefImpl;
import razie.assets.AssetContext;
import razie.assets.AssetCtxKey;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.FileAssetBrief;
import razie.assets.QueryCriteria;

import com.razie.assets.FileInventory;
import com.razie.pub.assets.JavaAssetMgr;
import razie.base.ActionItem;
import razie.base.scripting.ScriptContext;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import razie.base.ActionToInvoke;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.Comms;
import razie.draw.DrawSequence;
import razie.draw.Drawable;
import com.razie.pub.resources.RazIcons;
import com.razie.pubstage.comms.HtmlContents;
import com.razie.sdk.finders.AssetFinder;

/**
 * movies have specific extensions, descriptions, functionality (i.e. "google")
 * etc
 * 
 */
class SeriesInventory extends JavaSeriesInventory {

   override def getAsset(ref:AssetKey ) : AnyRef = new Series(getBrief(ref))

}

object SeriesInventory extends JavaSeriesInventory {
//   def loadSeries(series:AssetKey) : java.util.Map[AssetKey, String] = super.loadSeries (series)
}