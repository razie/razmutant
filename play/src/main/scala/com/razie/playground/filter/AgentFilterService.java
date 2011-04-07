package com.razie.playground.filter;

import com.razie.pub.agent.AgentService;
import razie.base.AttrAccess;
import razie.draw.DrawStream;
import razie.draw.widgets.DrawToString;

import com.razie.pub.base.data.HttpUtils;
import com.razie.pub.base.log.Log;
import com.razie.pub.lightsoa.SoaAllParms;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.lightsoa.SoaStreamable;
import com.razie.pubstage.comms.HtmlContents;
import com.razie.pubstage.comms.StrFilter;

/**
 * filter web pages based on a regexp
 * 
 * @author razvanc
 * @version $Id$
 * 
 */
@SoaService(name = "filter", bindings = { "http" }, descr = "filtering web pages")
public class AgentFilterService extends AgentService {

   public AgentFilterService() {
      Log.logThis("CREATE_SERVICE " + "AgentFilterService");
   }

   protected void onStartup() {
      new WebFilter("", null);
   }

   protected void onShutdown() {
   }

   public StrFilter findFilter(String url) {
      StrFilter f1 = StrFilter.regexp("src=\"/", "");
      return f1;
   }

   @SoaMethod(descr = "serve a filtered page")
   @SoaAllParms
   @SoaStreamable
   public void filter(DrawStream out, AttrAccess parms) {
      String url = HttpUtils.fromUrlEncodedString((String) parms.getAttr("filterurl"));

      if (!url.startsWith("http://"))
         url = "http://" + url;

      StrFilter f1 = findFilter(url);
      String otherList = new HtmlContents(url, f1).readAll();

      out.write(new DrawToString(otherList));
   }

   static final Log logger = Log.factory.create("", AgentFilterService.class.getName());
}
