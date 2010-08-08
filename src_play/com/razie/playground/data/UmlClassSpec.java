package com.razie.playground.data;

import java.util.ArrayList;
import java.util.List;

import razie.base.AttrAccess;

/**
 * this is what a class looks like. Extends AA for static parms.
 * 
 * @author razvanc
 */
public interface UmlClassSpec extends AttrAccess {
   List<AttrSpec> getAttributes();

   UmlClassSpec getSuperClassSpec();

   List<MethodDefn> getMethods();

   public String getName();

   public static class Impl extends razie.WrapAttrAccess implements UmlClassSpec {
      protected List<AttrSpec> attributes = new ArrayList<AttrSpec>();
      protected List<MethodDefn> methods = new ArrayList<MethodDefn>();
      protected String name;
      protected UmlClassSpec superClassSpec;

      public Impl(String name, UmlClassSpec superClassSpec, AttrAccess statics) {
         super(superClassSpec, statics);
         this.name = name;
         this.superClassSpec = superClassSpec;
      }

      public List<AttrSpec> getAttributes() {
         return attributes;
      }

      public List<MethodDefn> getMethods() {
         return methods;
      }

      public String getName() {
         return name;
      }

      public UmlClassSpec getSuperClassSpec() {
         return superClassSpec;
      }

      public String toString() {
         String s = "class " + name + (superClassSpec == null ? "" : " extends " + superClassSpec.getName())
               + " {\n";
         for (String n : getPopulatedAttr())
            s += "   " + n + " = " + getAttr(n) + "\n";
         s += "----------------------\n";
         for (MethodDefn m : getMethods())
            s += m.toString() + "\n";
         s += "----------------------\n";
         for (MethodDefn m : getMethods())
            s += m.toString() + "\n";
         s += "}\n";
         return s;
      }
   }
}
