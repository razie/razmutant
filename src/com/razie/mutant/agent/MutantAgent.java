package com.razie.mutant.agent;


import org.w3c.dom.Element;

import com.razie.agent.config.AgentConfig;
import com.razie.agent.network.Devices;
import com.razie.agent.services.AgentControlService;
import com.razie.agent.upnp.AgentUpnpService;
import com.razie.agent.webservice.Server;
import com.razie.assets.*;
import com.razie.dist.db.AgentDbService;
import com.razie.pub.agent.Agent;
import com.razie.pub.agent.AgentFileService;
import com.razie.pub.agent.AgentHttpService;
import com.razie.pub.agent.AgentService;
import com.razie.pub.base.ScriptContext;
import com.razie.pub.base.ScriptFactory;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.AgentCloud;
import com.razie.pub.comms.AgentHandle;
import com.razie.pub.comms.LightAuth;
import com.razie.pub.http.SocketCmdHandler;
import com.razie.pub.lightsoa.HttpSoaBinding;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.media.PlayerService;
import com.razie.pub.media.upnp.JukeboxUpnpService;
import com.razie.pub.webui.MutantPresentation;
import com.razie.sdk.agent.AgentNetworkService;
import com.razie.sdk.config.MutantConfig;

/**
 * this is the full-blown mutant agent, with its specific services etc
 * 
 * @author razvanc
 */
public class MutantAgent extends Agent {

	public MutantAgent(AgentHandle myHandle, AgentCloud homeGroup) {
		super(myHandle, homeGroup);
	}

	/** there is one agent per JVM */
	private static MutantAgent singleton;

	/**
	 * there is one agent per JVM
	 */
	public static MutantAgent getInstance() {
	    Agent x=Agent.instance();
	    
		if (x == null) {
			throw new IllegalStateException("agent NOT intiialized");
		}
		return (MutantAgent)x;
	}

	/** there is one agent per JVM */
	public static void init(MutantAgent m) {
		if (singleton != null) {
			throw new IllegalStateException("agent already intiialized");
		}
		singleton = m;
	}

	/**
	 * called when main() starts up but before onStartup(). Initialize all
	 * services from the configuration file
	 */
	public synchronized void onInit() {
		// some stuff for main()
		LightAuth.init(new LightAuth("mutant"));

		// first the hardcoded services

		// this service is privileged: will start on init to make sure it's up
		Server server = new Server(Integer.parseInt(getHandle().port),
				getMainContext());
		server.initListeners();

		// just because it's a reserved service...
		register(new AgentHttpService(this, server));
		register(new AgentFileService(AgentConfig.getMutantDir()));
		register(new AgentControlService());
		MutantPresentation.addPresentation(MutantPresentation.XMLDOC);

		for (String listenerClass : MutantConfig.getInstance().list(
				"/config/listeners/listener")) {
			try {
				SocketCmdHandler l = (SocketCmdHandler) Class.forName(
						listenerClass).newInstance();
				server.registerCmdListener(l);
				if (l.getClass().getAnnotation(SoaService.class) != null)
					AgentHttpService.registerSoa(new HttpSoaBinding(l));
				Log.logThis("HTTP_INIT_LISTENER " + listenerClass);
			} catch (Exception e) {
				Log.logThis("ERR_HTTP_CANT_INIT_LISTENER " + listenerClass, e);
			}
		}

		hackinit1();
		
		register(new AgentUpnpService(Devices.getInstance()));
		register(new AgentDbService());

		register(new PlayerService());
		register(new AssetService());
		register(new MetaService());

		initialized = true;

		// initialize the JS support - takes a while...
		new Thread(new Runnable() {
			public void run() {
			   // TODO ANOW reinstate this back when scala works
//				ScriptFactory.make(null, "1+2").eval(ScriptContext.Impl.global());
			}
		}).start();

		// initialize rest in separate thread to speed up startup response time
		new Thread(new Runnable() {
			public void run() {
				register(new JukeboxUpnpService());

				// Initialize all services
				for (Element e : MutantConfig.getInstance().listEntities(
						"/config/services/service")) {
					try {
						AgentService svc = (AgentService) Class.forName(
								e.getAttribute("class")).newInstance();
						register(svc);
					} catch (Exception ex) {
						Log.logThis("ERR_CANT_CREATE_SERVICE "
								+ e.getAttribute("class"), ex);
					}
				}
			}
		}).start();
	}

	protected void hackinit1() {
		register(new AgentNetworkService());
	}
	
	/**
	 * main loop - will wait here until the agent is shutdown. Call from your
	 * main() at the end
	 */
	public void keepOnTrucking() {
		// wait for the server to die... it is the first to start and last to
		// die
		// note that shutdown is called from elsewhere
		((AgentHttpService) locateService(AgentHttpService.class
				.getSimpleName())).todoEncapsulateSomehowJoin();
	}
}
