package com.razie.playground.data;

import razie.base.AttrAccess;
import razie.base.AttrAccessImpl;

/**
 * this is what an object looks like
 * 
 * @author razvanc
 */
public interface UmlObjectSpec extends AttrAccess {
   UmlClassSpec getClassSpec();

   public String getKey();

   public static class Impl extends AttrAccessImpl implements UmlObjectSpec {
      protected String key;
      protected UmlClassSpec classSpec;

      public Impl(UmlClassSpec cspec, String key, AttrAccess attrs) {
         super(attrs);
         this.classSpec = cspec;
         this.key = key;
      }

      public UmlClassSpec getClassSpec() {
         return classSpec;
      }

      public String getKey() {
         return key;
      }
   }
}
