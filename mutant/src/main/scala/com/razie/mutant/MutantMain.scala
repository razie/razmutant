package com.razie.mutant

import com.razie.pub.base.log.Log
import com.razie.pub.base.NoStatics
import java.io.IOException;
import razie.base.data._
import com.razie.pub.base._
import com.razie.pub.base.data._
import com.razie.pub.resources.RazIconRes
import com.razie.pub.comms.Agents
import com.razie.pub.agent.Agent
import com.razie.agent.Version
import com.razie.agent._
import com.razie.agent.network.Devices
import com.razie.agent.network.MutantDevices
import com.razie.sdk.config.UserConfig
import com.razie.assets._
import com.razie.mutant._

import com.razie.media.config.MediaConfig
import com.razie.pub.media.players.PlayerRegistryScala
import com.razie.media.MediaService

import com.razie.agent.network._
import com.razie.pub.plugin._
import com.razie.pub.comms._
import com.razie.pub.agent._
import razie.assets._
import razie.base.scripting._

/** starts the mutant agent. */
object MutantMain {

  var mainAgent: MutantAgent = null
  var useLocalIP = false

  /** smart selection of the IP to use from the list of current IPs */
  def selectip(): String = {
    //InetAddress.getLocalHost().getHostAddress();
    val addresses: Seq[String] = FindIp.list

    val cfg: RazElement = AgentConfig.instance() e

    // 1. favor the home network
    val homenet = cfg xpa "/config/network[@home='true']/@ipPrefix"
    val toUse = addresses.find(_.startsWith(homenet) && homenet != "") match {
      case Some(x) => x
      case None => {
        // 2. favor a known network
        // TODO lazy - loop through possible nets
        val nets = cfg xpa "/config/network[@home='false']/@ipPrefix"
        addresses.find(_.startsWith(nets)) match {
          case Some(x) => x
          case None =>
            // 3. i give up: where am i?
            // translate the stupid ipv6 localhost to ipv4
            if ("0:0:0:0:0:0:0:1%1".equals(addresses.head)) "127.0.0.1"
            else addresses.head
        }
      }
    }
    razie.Log("SELECTED_IP: " + toUse + " from list: " + addresses.mkString(","))
    if (useLocalIP) {
      razie.Log("WARN_SELECTED_IP: 127.0.0.1 because of command line argument \"localhost\"")
      "127.0.0.1"
    } else toUse
  }

  def main(args: Array[String]): Unit = {
    println("YO, the mutant is firing up...");

    //         Log.program = "Mutant Version " + Version.getVersion();
    val program = "Mutant Version " + Version.getVersion();
    Log.logThis("Mutant starting: " + program);

    val testing = args contains "test"
    useLocalIP = args contains "localhost"

    startup(testing)
  }

  def startup(testing: Boolean) = {

    // intialize the main agent...assuming name is the same as hostname
    MutantDevices.init(new MutantDevicesScala(), Agents.findMyHostName(testing), selectip);
    val d = Devices.getInstance();

    LightAuth.underLockAndKey {
      LightAuth init "mutant"
    }

    // stupid way to decouple the playground...
    try {
      val p = Class.forName("com.razie.playground.Init1").newInstance();
      Log.logThis("PLAYGROUND_INITIALIZED 1");
    } catch {
      case e: Exception => Log.logThis("WARN_PLAYGROUND_NOT_FOUND", e);
    }

    mainAgent = new MutantAgent(Agents.me(), Agents.homeCloud());
    mainAgent.getContext().enter()
    // initializing the agent will change the root nostatics...
    NoStatics.put(classOf[Devices], d);

    Agents.instance().testing = testing;

    // TODO figure out why i'm looping here?
    while (!Agent.instance().hasStopped()) {
      restart();
    }
  }

  def restart() = {
    this init

    mainAgent onInit;
    mainAgent onStartup;

    AgentHttpService.registerHandler(new com.razie.comm.commands.CmdAssets());

    // plugins are initialized after the agent is up

    // TODO need to find all plugins nicely
    val hardcoded = Map(
      ("plugin_razmedia.xml" -> classOf[MediaConfig]),
      ("plugin_assets_tests.xml" -> classOf[AssetsConfig]),
      ("plugin_base_assets.xml" -> classOf[AssetsConfig]))
    hardcoded.elements.foreach(x => Plugins.init(x._2.getResource("/plugins/" + x._1)))

    try {
      // TODO when in eclipse i probably want to load the xmls from classpath not disk
      val urls = Plugins.findAll(new java.io.File(Agents.me().localdir + "/plugins").toURL)
      Log.logThis("FOUND_PLUGINS: " + urls.mkString)
      urls.filter(x => !hardcoded.contains(new java.io.File(x.toURI()).getName)).foreach(Plugins init _)
    }

    // this is phase2, all known plugins are loaded
    Plugins.allPlugins foreach (_ loadphase2)

    //		   mainAgent.register(new WebUiService());

    mainAgent register new NewAgentNetworkService
    mainAgent register new com.razie.comm.commands.AgentServiceLinks

    //         mainAgent register new MutantScriptable

    // stupid way to decouple the playground...
    try {
      val p = Class.forName("com.razie.playground.Init").newInstance();
      Log.logThis("PLAYGROUND_INITIALIZED ");
    } catch {
      case e: Exception => Log.logThis("WARN_PLAYGROUND_NOT_FOUND", e);
    }

    // initialize diagnostics
    new DiagAsset("1");
    new DiagAsset("2");
    new DiagAsset("3");

    mainAgent register new com.razie.mutant.AgentDebugService
    mainAgent register new SampleScalaAgentService

    mainAgent join
  }

  def init() = {
    HtmlRenderUtils.setTheme(new MutantDarkTheme());

    ScriptFactory.init(new ScalaScriptFactory(ScriptFactory.singleton, true));

    UserConfig init;
    AssetsConfig getInstance;

    AssetMgr init new FullAssetMgr

    Devices getInstance;

    try {
      RazIconRes.init();
      RazIconRes.getPictureService = "/public/pics/";
    } catch {
      case e: IOException =>
        throw new RuntimeException(e);
    }
  }

}
