package com.razie.playground.prolog;

public class ProPredicate {
   protected String name;
   private Object[] vars;

   public ProPredicate(String name, Object... vars) {
      this.name = name;
      this.vars = vars;
   }
}
