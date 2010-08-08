package com.razie.playground.ibrowser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import razie.base.data.RiXmlUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Comms;

public class HtmlCapture {

   /**
    * extract the matching string
    * 
    * @param s
    *           the string to lookup
    * @param pre
    *           the preceding pattern
    * @param matching
    *           the pattern to extract, after the prePattern
    * @param post
    *           i don't know if i need this
    * @return
    */
   protected static String getMatching(String s, String pre, String matching, String post) {
      // get the first cell
      String res = "";
      String[] b = s.split(pre, 2);

      if (b.length > 1) {
         // found the pre, look for the matching
         Matcher m = Pattern.compile(matching).matcher(b[1]);
         if (m.find()) {
            res = m.group(0);
         }
      }

      return res;
   }

   /**
    * get the value from a cell in a table, where the previous column contains the description mentioned
    * 
    * <p>
    * It will strip all leading/trailing WS
    * 
    * @param root
    * @param desc
    * @return
    */
   public static String getCellWithDesc(String root, String desc, String cellFormat) {
      // get the first cell containing the description
      Matcher m = Pattern.compile("<td.*>" + desc.trim() + "</td>").matcher(root);
      String b[] = Pattern.compile("<td.*>" + desc.trim() + "</td>").split(root, 2);

      // the next cell must be the one we care about
      if (b.length > 1) {
         return getMatching(b[1], "<td[^>]*>", "[0-9]+[0-9,]*", null);
      }

      return "";
   }

   /**
    * get the value from a cell in a table, where the previous column contains the description mentioned
    * 
    * <p>
    * It will strip all leading/trailing WS
    * 
    * @param root
    * @param desc
    * @return
    */
   public static String getCellWithDesc(Element root, String desc) {
      List<Element> nodes = RiXmlUtils.getNodeList(root, "td", null);

      Log.logThis("there are " + nodes.size() + " cells to search...");

      int found = 0;
      for (Element e : nodes) {
         if (desc.equals(RiXmlUtils.getOptNodeVal(e))) {
            found = 1;
         }
         if (found > 0 && found++ == 2) {
            return RiXmlUtils.getOptNodeVal(e);
         }
      }

      return "";
   }

   public static Map<String, String> getCellsWithDesc(Element root, String[] descs) {
      Map<String, String> ret = new HashMap<String, String>();

      return ret;
   }

   public static void main(String[] argv) {
      String desc = "Shares Short";
      // Element page;
      // try {
      // page = XmlUtils.readXmlRoot(new URL(
      // "http://www.shortsqueeze.com/index.php?symbol=ffh&submit=Enter"), false);
      // } catch (MalformedURLException e) {
      // throw new CfgRtException(e);
      // }
      //        
      // String value = getCellWithDesc (page, "Shares Short");
      // Log.log (value);
      //
      // Map<String,String> values = getCellsWithDesc (page, new String[]{"1", "2"});
      // Log.log (values.toString());

      // String page = FilesUtils.readUrl("http://www.shortsqueeze.com/index.php?symbol=ffh&submit=Enter");
      String page = Comms.readUrl("C:\\Documents and Settings\\razvanc\\Desktop\\page.html");
      String value = getCellWithDesc(page, desc, null);
      Log.logThis(desc + "=" + value);
   }
}
