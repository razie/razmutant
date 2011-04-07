/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.media.players;

import razie.assets.ABDrawable;
import razie.assets.AssetBrief;

import razie.base.ActionItem;
import razie.draw.DetailLevel;
import razie.draw.DrawList;
import razie.draw.Drawable3;
import razie.draw.DrawableSource;
import razie.draw.Technology;
import razie.draw.widgets.NavButton;
import razie.draw.widgets.SimpleButton;

import razie.draw.Renderer;
import com.razie.pub.resources.RazIcons;

/**
 * a generic player handle which handles an instance of a player and can be rendered on a client
 * 
 * @author razvanc
 * @version $Id$
 */
public interface PlayerHandle extends DrawableSource {
    public static final String ERR     = "err";
    public static final String PLAYING = "playing";

    public void play();

    public void pause();

    public void stop();

    public void next();

    public void prev();

    public void fwd();

    public void rew();

    public void volup();

    public void voldown();

    public void mute();

    public String getCurStatus();

    /** return the asset being played */
    public AssetBrief getAssetBrief();

    /**
     * default/simple implementation - doesn't implement any capability but simplifies
     * implementations
     */
    public static class Impl implements PlayerHandle {

        String     curStatus;
        AssetBrief brief;

        public Impl(String status, AssetBrief b) {
            this.curStatus = status;
            this.brief = b;
        }

        public void fwd() {
        }

        public String getCurStatus() {
            return curStatus;
        }

        public void next() {
        }

        public void pause() {
        }

        public void play() {
        }

        public void stop() {
        }

        public void prev() {
        }

        public void rew() {
        }

        public String toString() {
            return (String) Renderer.Helper.draw(makeDrawable(), Technology.HTML, null);
        }

        public Drawable3 makeDrawable() {
            DrawList image = new DrawList();
            image.valign = "top";

            DrawList v1 = new DrawList();
            v1.isVertical = true;
            v1.write(buttons);
            AssetBrief movie = getAssetBrief();

            DrawList vol = new DrawList();
            vol.isVertical = true;
            vol.write(curStatus); // TODO this will be the status - we ignore MT issues for now
            vol.write(new SimpleButton(new ActionItem("volup"), "/mutant/player/Volup"));
            vol.write(new SimpleButton(new ActionItem("voldown"), "/mutant/player/Voldown"));
            vol.write(new SimpleButton(new ActionItem("mute"), "/mutant/player/Mute"));
            vol.write(new NavButton(new ActionItem("Home", "mutant"), "/mutant"));

            if (movie != null) {
                v1.write(new ABDrawable(movie, DetailLevel.FULL));
            }

            image.write(vol);
            image.write(v1);
            return image;
        }

        static DrawList buttons = new DrawList();

        static {
            buttons.write(new NavButton(new ActionItem("prev", RazIcons.SKIP_PREV.name()), "/mutant/player/Prev"));
            buttons.write(new NavButton(new ActionItem("rew", RazIcons.REW.name()), "/mutant/player/Rew"));
            buttons.write(new NavButton(new ActionItem("play", RazIcons.PLAY.name()), "/mutant/player/Play"));
            buttons.write(new NavButton(new ActionItem("pause", RazIcons.PAUSE.name()), "/mutant/player/Pause"));
            buttons.write(new NavButton(new ActionItem("stop", RazIcons.STOP.name()), "/mutant/player/Stop"));
            buttons.write(new NavButton(new ActionItem("fwd", RazIcons.FWD.name()), "/mutant/player/Fwd"));
            buttons.write(new NavButton(new ActionItem("next", RazIcons.SKIP_NEXT.name()), "/mutant/player/Next"));
        }

        public void voldown() {
        }

        public void volup() {
        }

        public void mute() {
        }

        public AssetBrief getAssetBrief() {
            return this.brief;
        }
    }
}
