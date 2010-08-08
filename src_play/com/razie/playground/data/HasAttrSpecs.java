package com.razie.playground.data;

import java.util.ArrayList;
import java.util.List;

public interface HasAttrSpecs {
   List<AttrSpec> getAttrSpecs();

   public static class Impl implements HasAttrSpecs {
      private List<AttrSpec> attrSpecs = new ArrayList<AttrSpec>();

      public List<AttrSpec> getAttrSpecs() {
         return attrSpecs;
      }
   }
}
