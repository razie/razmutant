package com.razie.playground.keys;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.razie.pub.base.log.Log;

public class Keys {

   public static void main(String args[]) throws Exception {
      playKey('g');
      KeyAdapter ka;
      KeyListener kl;
   }

   public static void playKey(int key) throws AWTException {
      Robot r = new Robot();
      r.keyPress(KeyEvent.VK_CONTROL);
      r.keyPress(KeyEvent.VK_P);
      r.keyRelease(KeyEvent.VK_P);
      r.keyRelease(KeyEvent.VK_CONTROL);
   }

   static final Log logger = Log.factory.create(Keys.class.getName());

}
