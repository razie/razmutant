/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.playground.data;

import java.util.Collections;
import java.util.List;

import razie.base.scripting.ScriptContext;
import razie.base.scripting.ScriptFactory;

/**
 * simple method definition
 * 
 * @author razvanc
 * 
 */
public interface MethodDefn {
   public AttrSpec getReturn();

   public String getName();

   public List<AttrSpec> getParms();

   public Object run(ScriptContext ctx);

   /** a method defined dynamically as a script (JS) */
   public static class ScriptedMethod implements MethodDefn {
      String script, name;

      public ScriptedMethod(String name, String script) {
         this.script = script;
      }

      public Object run(ScriptContext ctx) {
         return ScriptFactory.make(null, script).eval(ctx);
      }

      public String getName() {
         return name;
      }

      public List<AttrSpec> getParms() {
         return Collections.EMPTY_LIST;
      }

      public AttrSpec getReturn() {
         return AttrSpec.Impl.A_OBJECT;
      }

      public String toString() {
         return "   " + name + "();";
      }
   }
}
