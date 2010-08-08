package com.razie.pub.media;

import razie.draw.Drawable;
import razie.draw.widgets.DrawToString;

import com.razie.pub.agent.AgentService;
import com.razie.pub.base.log.Log;
import com.razie.pub.comms.Agents;
import com.razie.pub.comms.PermType;
import com.razie.pub.lightsoa.SoaMethod;
import com.razie.pub.lightsoa.SoaService;
import com.razie.pub.media.players.PlayerHandle;
import com.razie.pub.media.players.PlayerRegistry;

/**
 * a command listener listens to commands, executes them and returns an object
 * 
 * @author razvanc
 * 
 */
@SoaService(name = "player",  bindings={"http"}, descr = "player control")
public class PlayerService extends AgentService {
    public static PlayerHandle curPlayer = null;

	@Override
    protected void onStartup() {
    }
    
    private PlayerHandle player(String... name) {
        if (PlayerService.curPlayer == null) {
            // it's possible - just want to create a new one...
            if (name.length > 0 && PlayerRegistry.getPlayer(name[0], Agents.me().os, Agents.me().hostname) != null) {
                PlayerService.curPlayer = PlayerRegistry.getPlayer(name[0], Agents.me().os, Agents.me().hostname).makeHandle();
            } else {
                throw new IllegalStateException(MediaErrors.ERR_NO_PLAYER);
            }
        }
        return PlayerService.curPlayer;
    }

    @SoaMethod(descr = "find and display current player", perm = PermType.CONTROL, args = { "name" })
    public Drawable display(String name) {
        return new DrawToString(player(name).toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "play", perm = PermType.CONTROL)
    public Drawable Play() {
        player().play();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "stop", perm = PermType.CONTROL)
    public Drawable Stop() {
        player().stop();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "pause", perm = PermType.CONTROL)
    public Drawable Pause() {
        player().pause();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "fwd", perm = PermType.CONTROL)
    public Drawable Fwd() {
        player().fwd();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "rew", perm = PermType.CONTROL)
    public Drawable Rew() {
        player().rew();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "prev", perm = PermType.CONTROL)
    public Drawable Prev() {
        player().prev();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "next", perm = PermType.CONTROL)
    public Drawable Next() {
        player().next();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "mute", perm = PermType.CONTROL)
    public Drawable Mute() {
        player().mute();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "volup", perm = PermType.CONTROL)
    public Drawable Volup() {
        player().volup();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    @SoaMethod(descr = "voldown", perm = PermType.CONTROL)
    public Drawable Voldown() {
        player().voldown();
        return new DrawToString(player().toString() + "<p><a href=\"/public\">HOME</a>");
    }

    static final Log    logger    = Log.factory.create("", PlayerService.class.getName());

    protected void onShutdown() {
    }
}
