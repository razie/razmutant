/**
 * Razvan's code. Copyright 2008 based on Apache (share alike) see LICENSE.txt for details.
 */
package com.razie.pub.media.players;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import razie.assets.AssetBrief;

/**
 * generic player handle for windows - will simulate key events, assuming a player of some kind is
 * running and can understand them
 * 
 * TODO implement
 * 
 * @author razvanc
 * @version $Id$
 */
public class WinPlayerHandle extends PlayerHandle.Impl implements PlayerHandle {

    public WinPlayerHandle(String status, AssetBrief b) {
        super(status, b);
    }

    public void play() {
        playCKey(KeyEvent.VK_P);
    }

    public void pause() {
        playCKey(KeyEvent.VK_P);
    }

    public void stop() {
        playCKey(KeyEvent.VK_S);
    }

    public void prev() {
        playCKey(KeyEvent.VK_B);
    }

    public void next() {
        playCKey(KeyEvent.VK_F);
    }

    public void fwd() {
        playSCKey(KeyEvent.VK_F);
    }

    public void rew() {
        playSCKey(KeyEvent.VK_B);
    }

    public void voldown() {
        playKey(KeyEvent.VK_F8);
    }

    public void volup() {
        playKey(KeyEvent.VK_F9);
    }

    public void mute() {
        playKey(KeyEvent.VK_F7);
    }

    protected boolean playKey(int key) {
        try {
            Robot r = new Robot();
            r.keyPress(key);
            r.keyRelease(key);
        } catch (AWTException e) {
            return false;
        }
        return true;
    }

    protected boolean playCKey(int key) {
        try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(key);
            r.keyRelease(key);
            r.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException e) {
            return false;
        }
        return true;
    }

    protected boolean playSCKey(int key) {
        try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_SHIFT);
            r.keyPress(key);
            r.keyRelease(key);
            r.keyRelease(KeyEvent.VK_SHIFT);
            r.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException e) {
            return false;
        }
        return true;
    }

}
