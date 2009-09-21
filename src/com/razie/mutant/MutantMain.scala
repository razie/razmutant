package com.razie.mutant

import com.razie.pub.base.log.Log
import com.razie.pub.base.NoStatics
import java.io.IOException;
import com.razie.pub.base.data._
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.resources.RazIconRes
import com.razie.pub.comms.Agents
import com.razie.pub.agent.Agent
import com.razie.agent.Version
import com.razie.agent.config.AgentConfig
import com.razie.agent.network.Devices
import com.razie.mutant.agent.MutantAgent
import com.razie.agent.network.MutantDevices
import com.razie.sdk.config.UserConfig
import com.razie.assets._
import com.razie.assets.config.AssetsConfig
import com.razie.mutant._
import com.razie.mutant.MutantDarkTheme

import com.razie.media.config.MediaConfig
import com.razie.pub.media.players.PlayerRegistryScala
import com.razie.media.MediaService

import com.razie.agent.network._
import com.razie.pub.plugin._


/** starts the mutant agent. */
object MutantMain {
  
   var mainAgent:MutantAgent=null;

   /** smart selection of the IP to use from the list of current IPs */
   def selectip () : String = {
      //InetAddress.getLocalHost().getHostAddress();
      val addresses:List[String] = FindIp.list

      val cfg:RazElement = AgentConfig.instance() e

      // 1. favor the home network
      val homenet = cfg xpa "/config/network[@home='true']/@ipPrefix"
      addresses.find (_.startsWith(homenet) && homenet != "") match {
         case Some(x) => x
         case None => {
               // 2. favor a known network
               // TODO lazy - loop through possible nets
               val nets = cfg xpa "/config/network[@home='false']/@ipPrefix"
               addresses.find (_.startsWith(nets)) match {
                  case Some(x) => x
                  case None =>
                     // 3. i give up: where am i?
                     // translate the stupid ipv6 localhost to ipv4
                     if ("0:0:0:0:0:0:0:1%1".equals(addresses.head)) "127.0.0.1" else addresses.head
               }
            }
         }
      }
   
      def main(args : Array[String]) : Unit = {
         println("YO, the mutant is firing up...");
   
         Log.program = "Mutant Version " + Version.getVersion();
         Log.logThis("Mutant starting: " + Log.program);
        
         val testing = (args.length > 0 && args(0).equals("test"));

         startup (testing)
      }

      def sayboo () = {
        println ("Mutant says boo...")
      }
      
      def startup(testing : boolean) = {
         // intialize the main agent...assuming name is the same as hostname
         MutantDevices.init(new MutantDevicesScala(), Agents.findMyHostName(testing), selectip);
         val d = Devices.getInstance();
         mainAgent = new MutantAgent(Agents.me(), Agents.homeCloud());
         // initializing the agent will change the root nostatics...
         //  NoStatics.put(Devices.class, d);
         NoStatics.put(classOf[Devices], d);
         //  NoStatics.put(Class.forName("com.razie.agent.network.Devices"), d);
         MutantAgent.init(mainAgent);

         Agents.instance().testing = testing;

         // TODO figure out why i'm looping here?
         while (!Agent.instance().hasStopped()) {
            restart();
         }
      }
  
      def restart() ={
         this init

         mainAgent onInit;
         mainAgent onStartup;

//		   mainAgent.register(new WebUiService());

         mainAgent register new NewAgentNetworkService
         
//         mainAgent register new MutantScriptable
         
         mainAgent register new AgentMutantService
         mainAgent register new com.razie.mutant.AgentDebugService
         mainAgent register new SampleScalaAgentService

         mainAgent keepOnTrucking
      }
 
      def init() = {
         HtmlRenderUtils.setTheme(new MutantDarkTheme());
         
         ScriptFactory.init (new ScriptFactoryScala(ScriptFactory.singleton, true));

         UserConfig init;
         AssetsConfig getInstance;
         
         MutantAssetMgr init new ScalaAssetMgr
        
         // intiailizig all plugins
         
         // TODO need to find all plugins nicely
         Plugins init classOf[MediaConfig].getResource ("/plugins/plugin_media.xml")
         Plugins init classOf[AssetsConfig].getResource ("/plugins/plugin_assets_tests.xml")
         Plugins init classOf[AssetsConfig].getResource ("/plugins/plugin_base_assets.xml")
         
//         val pl = Plugins.findAll(new java.net.URL("" + "/plugins")).foreach (Plugins init _)
         
         // this is phase2, all known plugins are loaded
         Plugins.allPlugins foreach (_ loadphase2)
         
         PlayerRegistryScala init;
         Devices getInstance;

         try {
            RazIconRes.init();
            RazIconRes.getPictureService = "/mutant/pics/";
         } catch {
            case e : IOException =>
               throw new RuntimeException(e);
         }
      }

   }