package com.razie.media;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import razie.assets.AssetActionToInvoke;
import razie.assets.AssetBrief;
import razie.assets.AssetBrief$;
import razie.assets.AssetKey;
import razie.assets.AssetLocation;
import razie.assets.AssetMap;
import razie.assets.QueryCriteria;
import razie.base.ActionContext;
import razie.base.ActionItem;
import razie.base.ActionToInvoke;

import com.razie.assets.FileInventory;
import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.Comms;
import com.razie.pub.resources.RazIcons;
import com.razie.pubstage.comms.HtmlContents;
import com.razie.sdk.finders.AssetFinder;

/**
 * movies have specific extensions, descriptions, functionality (i.e. "google")
 * etc
 * 
 */
public class JavaSeriesInventory extends FileInventory {
   private static final String SERIESLOC = "SERIESLOC";
   public static final String sCLASS_Series = "Series";

   /** use the base and add details */
   @Override
   public AssetMap query(QueryCriteria q, AssetLocation env, boolean recurse, AssetMap res) {
      AssetMap ret = super.query(q, env, recurse, res);

      for (AssetBrief b : ret.jvalues()) {
         // remove .series
         if (b.getName().endsWith(".series")) {
            b.setName(b.getName().replaceFirst("\\.series$", ""));
         }
      }

      return ret;
   }

   @Override
   public AssetBrief getBrief(AssetKey ref) {
      AssetBrief b = super.getBrief(ref);

      // remove .series
      if (b.getName().endsWith(".series")) {
         b.setName(b.getName().replaceFirst("\\.series$", ""));
      }

      if (b.getIcon() == null) {
         // find which finder would accept this and use that icon
         AssetFinder finder = findFinder(ref);
         if (finder != null) {
            b.setIcon(finder.getIcon());
            b.setPlayer(finder.getPlayer());
         }
      }
      return b;
   }

   @Override
   public ActionItem[] getSupportedActions(AssetKey ref) {
      return defaultCmds;
   }

   /** my factory for context actions */
   public static class CtxFactory implements razie.assets.Affordance {
      private AssetKey series;
      private ActionItem item;

      public CtxFactory(ActionItem item, AssetKey series) {
         this.item = item;
         this.series = series;
      }

   @Override
      public ActionToInvoke[] make(AssetKey movie, Object x) {
         ActionToInvoke ati[] = {new AssetActionToInvoke(series, item)};
         ati[0].setAttr("episode", movie);
         return ati;
      }
   }

   @Override
   public Object doAction(String cmd, AssetKey ref, ActionContext ctx) {
      if (cmd.startsWith("reset")) {
         Map<AssetKey, String> map = new HashMap<AssetKey, String>();
         saveSeries(ref, map);
         return "Ok";
      } else if (ACTION_OLD.name.equals(cmd)) {
         AssetKey episode = AssetKey.fromString((String) ctx.getAttr("episode"));
         return SeriesInventory.seriesold(ref, episode);
      } else if (ACTION_NEW.name.equals(cmd)) {
         AssetKey episode = AssetKey.fromString((String) ctx.getAttr("episode"));
         return SeriesInventory.seriesnew(ref, episode);
      }
      return "default-no-goodnik-doAction " + cmd + ", " + ref;
   }

   private static void saveSeries(AssetKey series, Map<AssetKey, String> map) {
      String fname = series.getLocation().getLocalPath() + series.getId();

      Writer output = null;
      try {
         // use buffering
         // FileWriter always assumes default encoding is OK!
         output = new BufferedWriter(new FileWriter(fname));
         output
               .write("HELP: this is a razmutant series file - keep track of the episodes you've seen so far. "
                     + "SERIESLOC is a special stand-in for the location of the series, so you can move them around.");
         output.write("\n");
         for (AssetKey s : map.keySet()) {
            String line = s.toString();
            line = line.replaceAll(series.getLocation().getLocalPath(), SERIESLOC);
            line = HttpUtils.toUrlEncodedString(line);
            output.write(line);
            output.write("\n");
         }
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      } finally {
         // flush and close both "output" and its underlying FileWriter
         if (output != null)
            try {
               output.close();
            } catch (IOException e) {
               Log.logThis("IGNORING: ", e);
            }
      }
   }

   /** @return not null */
   public static Map<AssetKey, String> loadSeries(AssetKey series) {
      String fname = series.getLocation().getLocalPath() + series.getId();
      Map<AssetKey, String> map = new HashMap<AssetKey, String>();

      boolean formatver1 = false;
      boolean formatnoheader = true;

      BufferedReader input = null;
      try {
         // use buffering, reading one line at a time
         // FileReader always assumes default encoding is OK!
         input = new BufferedReader(new FileReader(fname));
         String line = null; // not declared within while loop
         /*
          * readLine is a bit quirky : it returns the content of a line MINUS
          * the newline. it returns null only for the END of the stream. it
          * returns an empty String if two newlines appear in a row.
          */
         while ((line = input.readLine()) != null) {
            line = HttpUtils.fromUrlEncodedString(line);
            if (!line.contains(SERIESLOC) || line.startsWith("<mutant://"))
               formatver1 = true;
            if (line.startsWith("HELP"))
               formatnoheader = false;
            else {
               line = line.replaceAll(SERIESLOC, series.getLocation().getLocalPath());
               map.put(AssetKey.fromString(line), "");
            }
         }
      } catch (FileNotFoundException ex) {
         throw new RuntimeException(ex);
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      } finally {
         try {
            if (input != null) {
               // flush and close both "input" and its underlying
               // FileReader
               input.close();
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      if (formatver1 || formatnoheader) {
         Log.logThis("CONVERTING_SERIES: " + series);
         saveSeries(series, map);
      }

      return map;
   }

   public static String seriesold(AssetKey series, AssetKey movie) {
      if (series.getLocation().isLocal()) {
         Map<AssetKey, String> map = loadSeries(series);
         map.put(movie, "");
         saveSeries(series, map);
         return "Ok...";
      } else {
         String u = Agents.agent(series.getLocation().getHost()).url;
         String url = u + "/mutant/cmd/invcmd/?cmd=seriesold&ref=" + series.toUrlEncodedString()
               + "&episode=" + movie.toUrlEncodedString();
         return HtmlContents.justBody((Comms.readUrl(url)));
      }
   }

   public static String seriesnew(AssetKey series, AssetKey movie) {
      if (series.getLocation().isLocal()) {
         Map<AssetKey, String> map = loadSeries(series);
         map.remove(movie);
         saveSeries(series, map);
         return "Ok...";
      } else {
         String u = Agents.agent(series.getLocation().getHost()).url;
         String url = u + "/mutant/cmd/invcmd/?cmd=seriesnew&ref=" + series.toUrlEncodedString()
               + "&episode=" + movie.toUrlEncodedString();
         return HtmlContents.justBody((Comms.readUrl(url)));
      }
   }

   private static final ActionItem ACTION_OLD = new ActionItem("seriesold", RazIcons.POWER.name());
   private static final ActionItem ACTION_NEW = new ActionItem("seriesnew", RazIcons.POWER.name());
   private static final ActionItem ACTION_RESET = new ActionItem("reset", RazIcons.POWER.name());
   public static final ActionItem cmdUPDATESERIES = new ActionItem("updateseries", RazIcons.POWER.name());
   private static final ActionItem[] defaultCmds = { AssetBrief$.MODULE$.PLAY(), ACTION_RESET };
}
