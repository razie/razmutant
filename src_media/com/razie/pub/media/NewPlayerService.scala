package com.razie.pub.media

import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log.Log;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.comms._
import com.razie.pub.media.players.PlayerHandle;
import com.razie.pub.media.players.PlayerRegistry;

object PlayerHelper {
    var curPlayer:PlayerHandle  = null;
  }
  
/**
 * a command listener listens to commands, executes them and returns an object
 * 
 * @author razvanc
 * 
 */
//TODO @SoaService(){val name = "player", val bindings = Array("http"), val descr = "player control" }
@SoaService(name = "player", bindings = Array("http"), descr = "player control" )
class NewPlayerService extends AgentService {

    def player() : PlayerHandle = PlayerService.curPlayer match {
       case null => throw new IllegalStateException(MediaErrors.ERR_NO_PLAYER)
       case _ => PlayerService.curPlayer
    }

    def player(name : String) : PlayerHandle = {
        if (PlayerService.curPlayer == null) {
            // it's possible - just want to create a new one...
            if (name.length > 0 && PlayerRegistry.getPlayer(name, Agents.me().os, Agents.me().hostname) != null) {
                PlayerService.curPlayer = PlayerRegistry.getPlayer(name, Agents.me().os, Agents.me().hostname).makeHandle();
            } else {
                throw new IllegalStateException(MediaErrors.ERR_NO_PLAYER);
            }
        }
        PlayerService.curPlayer;
    }

    @SoaMethod(descr = "find and display current player", perm = PermType.CONTROL, args = Array("name"))
    def display(name:String) :String={
        player(name).toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "play", perm = PermType.CONTROL)
    def Play() :String={
        player().play();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "stop", perm = PermType.CONTROL)
    def Stop() :String={
        player().stop();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "pause", perm = PermType.CONTROL)
    def Pause() :String={
        player().pause();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "fwd", perm = PermType.CONTROL)
    def Fwd() :String={
        player().fwd();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "rew", perm = PermType.CONTROL)
    def Rew() :String={
        player().rew();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "prev", perm = PermType.CONTROL)
    def Prev() :String={
        player().prev();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "next", perm = PermType.CONTROL)
    def Next() :String={
        player().next();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "mute", perm = PermType.CONTROL)
    def Mute() :String={
        player().mute();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "volup", perm = PermType.CONTROL)
    def Volup() :String={
        player().volup();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    @SoaMethod(descr = "voldown", perm = PermType.CONTROL)
    def Voldown():String ={
        player().voldown();
        return player().toString() + "<p><a href=\"/public\">HOME</a>";
    }

    //static final Log    logger    = Log.factory.create("", PlayerService.class.getName());

}
