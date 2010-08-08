/*
Keystroke and Mouse Event Tutor.

copyright (c) 1999-2007 Roedy Green, Canadian Mind Products
may be copied and used freely for any purpose but military.

Roedy Green
Canadian Mind Products
#101 - 2536 Wark Street
Victoria, BC Canada
V8T 4G8
tel: (250) 361-9093
mailto:roedyg@mindprod.com
http://mindprod.com

version History
1.0 1999-10-16 initial version

1.1 2005-06-30 add stomp bat files,add stomp bat files, avoids dumping
control chars to the console.

1.2 2006-03-06 reformat with IntelliJ, add Javadoc

1.3 2007-05-26 add pad and ican. IntelliJ inspector, reanmed KeyPlay or KeyPlayer.

 */
package com.razie.playground.keys;

import java.awt.Frame;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * CMP KeyPlayer Keystroke and Mouse Event Tutor. Application to let you experiment to learn how mouse and
 * keystroke events work. Output appears on the console. Just click the mouse or hit keystrokes and watch what
 * details of the events generated.
 * 
 * <pre>
 * Compile with:
 * CD \
 * javac.exe com.mindprod.keyplayer.KeyPlayer.java
 * <p/>
 * Execute with:
 * CD \
 * java.exe com.mindprod.keyplayer.KeyPlayer
 * </pre>
 * 
 * @author Roedy Green, Canadian Mind Products
 * @version 1.3, 2007-05-26
 * @since 1999-10-16
 */
public final class KeyPlayer {

   // ------------------------------ FIELDS ------------------------------

   /**
    * undisplayed copyright notice
    * 
    * @noinspection UnusedDeclaration
    */
   public static final String EMBEDDEDCOPYRIGHT = "copyright (c) 1999-2007 Roedy Green, Canadian Mind Products, http://mindprod.com";

   /**
    * date this version released.
    * 
    * @noinspection UnusedDeclaration
    */
   @SuppressWarnings("unused")
   private static final String RELEASEDATE = "2007-05-26";

   /**
    * embedded version string.
    * 
    * @noinspection UnusedDeclaration
    */
   public static final String VERSIONSTRING = "1.3";

   // -------------------------- STATIC METHODS --------------------------

   /**
    * debugging tool to track keystrokes. Whenever you hit a key on the keyboard, three events will show up at
    * your KeyListener, first an event at keyPressed, then at keyTyped, then at keyReleased. If you hold a key
    * down and it repeats, you will see that triple repeated over and over. If you hit the ctrl key and hold
    * it down you will see repeated events at keyPressed and finally one at keyReleased when you let go. There
    * is no event at keyTyped. When you hit Ctrl+C you see keyCode 03, ^C, ETX not the letter C. Java combines
    * the keys for you. It is not your problem to track the ctrl or shift state and modify the meanings of
    * other keystrokes.
    * 
    * @param e
    *           what sort of keystroke typed
    */
   private static void dumpKeyEvent(KeyEvent e) {
      System.out.print(" ID=" + e.getID() + " ");
      switch (e.getID()) {
      case KeyEvent.KEY_TYPED:
         System.out.print("KeyEvent.KEY_TYPED   ");
         break;

      case KeyEvent.KEY_PRESSED:
         System.out.print("KeyEvent.KEY_PRESSED ");
         break;

      case KeyEvent.KEY_RELEASED:
         System.out.print("KeyEvent.KEY_RELEASED");
         break;
      default:
         break;
      }// end switch

      System.out.print("  raw KeyCode=\"" + safe((char) e.getKeyCode()) + "\" " + e.getKeyCode());
      System.out.print("  cooked KeyChar=\"" + safe(e.getKeyChar()) + "\" " + (int) e.getKeyChar());

      int mods = e.getModifiers();
      if ((mods & InputEvent.ALT_MASK) != 0) {
         System.out.print(" ALT_MASK");
      }
      if ((mods & InputEvent.CTRL_MASK) != 0) {
         System.out.print(" CTRL_MASK");
      }
      if ((mods & InputEvent.META_MASK) != 0) {
         System.out.print(" META_MASK");
      }
      if ((mods & InputEvent.SHIFT_MASK) != 0) {
         System.out.print(" SHIFT_MASK");
      }

      System.out.print("toString" + e.toString());
      System.out.println();
   }// end dumpKeyEvent

   /**
    * debugging tool to track mouse clicks.
    * 
    * @param e
    *           what sort of mouse event.
    */
   private static void dumpMouseEvent(MouseEvent e) {
      System.out.print(" ID=" + e.getID() + " ");

      switch (e.getID()) {
      case MouseEvent.MOUSE_CLICKED:
         System.out.print("MouseEvent.MOUSE_CLICKED ");
         break;

      case MouseEvent.MOUSE_ENTERED:
         System.out.print("MouseEvent.MOUSE_ENTERED ");
         break;

      case MouseEvent.MOUSE_EXITED:
         System.out.print("MouseEvent.MOUSE_EXITED  ");
         break;

      case MouseEvent.MOUSE_PRESSED:
         System.out.print("MouseEvent.MOUSE_PRESSED ");
         break;

      case MouseEvent.MOUSE_RELEASED:
         System.out.print("MouseEvent.MOUSE_RELEASED");
         break;

      default:
         break;
      }// end switch

      int mods = e.getModifiers();
      if ((mods & InputEvent.ALT_MASK) != 0) {
         System.out.print(" ALT_MASK");
      }
      if ((mods & InputEvent.BUTTON1_MASK) != 0) {
         System.out.print(" BUTTON1_MASK");
      }
      if ((mods & InputEvent.BUTTON2_MASK) != 0) {
         System.out.print(" BUTTON2_MASK");
      }
      if ((mods & InputEvent.BUTTON3_MASK) != 0) {
         System.out.print(" BUTTON3_MASK");
      }
      if ((mods & InputEvent.CTRL_MASK) != 0) {
         System.out.print(" CTRL_MASK");
      }
      if ((mods & InputEvent.META_MASK) != 0) {
         System.out.print(" META_MASK");
      }
      if ((mods & InputEvent.SHIFT_MASK) != 0) {
         System.out.print(" SHIFT_MASK");
      }
      System.out.print(" (" + e.getX() + "," + e.getY() + ")");
      System.out.println();
   }// end dumpMouseEvent

   /**
    * convert unprintable chars to '.'
    * 
    * @param c
    *           character to test for safety
    * 
    * @return the same character if safe, dot if not.
    */
   private static String safe(char c) {
      if (32 <= c && c <= 254) {
         return String.valueOf(c);
      } else {
         return String.valueOf((int) c);
      }
   }// end safe

   // --------------------------- main() method ---------------------------

   /**
    * main method.
    * 
    * @param args
    *           not used .
    */
   public static void main(String[] args) {
      final Frame frame = new Frame("KEYPLAY " + VERSIONSTRING
            + " Keystroke and Mouse Tutor, output on console");
      frame.setSize(200, 200);
      frame.addKeyListener(new java.awt.event.KeyAdapter() {
         /**
          * Called when key pressed.
          * 
          * @param e
          *           describes key.
          * 
          * @see java.awt.event.KeyAdapter#keyPressed(KeyEvent)
          */
         public void keyPressed(KeyEvent e) {
            System.out.println("At keyPressed");
            dumpKeyEvent(e);
         }

         /**
          * Called when key released.
          * 
          * @param e
          *           describes key.
          * 
          * @see java.awt.event.KeyAdapter#keyReleased(KeyEvent)
          */
         public void keyReleased(KeyEvent e) {
            System.out.println("At keyReleased");
            dumpKeyEvent(e);
         }

         /**
          * Called when key typed.
          * 
          * @param e
          *           describes key.
          * 
          * @see java.awt.event.KeyAdapter#keyTyped(KeyEvent)
          */
         public void keyTyped(KeyEvent e) {
            System.out.println("At keyTyped");
            dumpKeyEvent(e);
         }
      }// end anonymous class
            );// end addKeyListener line

      frame.addMouseListener(new java.awt.event.MouseAdapter() {
         /**
          * Called when mouse clicked.
          * 
          * @param e
          *           describes mouse position.
          * 
          * @see java.awt.event.MouseAdapter#mouseClicked(MouseEvent)
          */
         public void mouseClicked(MouseEvent e) {
            System.out.println("At mouseClicked");
            dumpMouseEvent(e);
         }

         /**
          * Called when mouse enters Component.
          * 
          * @param e
          *           describes mouse position.
          * 
          * @see java.awt.event.MouseAdapter#mouseEntered(MouseEvent)
          */
         public void mouseEntered(MouseEvent e) {
            System.out.println("At mouseEntered");
            dumpMouseEvent(e);
         }

         /**
          * Called when mouse leaves Component.
          * 
          * @param e
          *           describes mouse position.
          * 
          * @see java.awt.event.MouseAdapter#mouseExited(MouseEvent)
          */
         public void mouseExited(MouseEvent e) {
            System.out.println("At mouseExited");
            dumpMouseEvent(e);
         }

         /**
          * Called when mouse button pressed.
          * 
          * @param e
          *           describes mouse position.
          * 
          * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
          */
         public void mousePressed(MouseEvent e) {
            System.out.println("At mousePressed");
            dumpMouseEvent(e);
         }

         /**
          * Called when mouse button released.
          * 
          * @param e
          *           describes mouse position.
          * 
          * @see java.awt.event.MouseAdapter#mouseReleased(MouseEvent)
          */
         public void mouseReleased(MouseEvent e) {
            System.out.println("At mouseReleased");
            dumpMouseEvent(e);
         }
      }// end anonymous class
            );// end addKeyListener line

      frame.addWindowListener(new java.awt.event.WindowAdapter() {
         /**
          * Handle request to shutdown.
          * 
          * @param e
          *           event giving details of closing.
          */
         public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
         }// end WindowClosing
      }// end anonymous class
            );// end addWindowListener line

      frame.validate();
      frame.setVisible(true);
   }// end main
}// end KeyPlayer
