package com.razie.learnscala;

public class ABaseJavaClass {
   public void takesOptionalArgs (String...args) {
      System.out.println ("Java printing: ");
      for (String s : args)
         System.out.print (s + " ");
   }
}
