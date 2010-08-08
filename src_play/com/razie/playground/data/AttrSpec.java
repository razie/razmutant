package com.razie.playground.data;

/**
 * very simple attribute specification - name and type.
 * 
 * Types are not bound to anything, but i strongly recommend these: "string", "int", "float", "boolean",
 * "datetime"
 * 
 * @author razvanc
 */
public interface AttrSpec {
   public String getType();

   public String getName();

   public class Impl implements AttrSpec {

      private String name;
      private String type;

      public Impl(String name, String type) {
         this.name = name;
         this.type = type;
      }

      public String getName() {
         return name;
      }

      public String getType() {
         return type;
      }

      public static final AttrSpec A_STRING = new Impl("String", "string");
      public static final AttrSpec A_INT = new Impl("int", "int");
      public static final AttrSpec A_FLOAT = new Impl("float", "float");
      public static final AttrSpec A_DATETIME = new Impl("datetime", "datetime");
      public static final AttrSpec A_BOOLEAN = new Impl("bool", "bool");
      public static final AttrSpec A_OBJECT = new Impl("object", "object");
   }
}
