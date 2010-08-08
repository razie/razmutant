package com.razie.playground.data;

public interface GraphAssoc<A> {
   Graph<?, A> getANode();

   A getStuff();

   Graph<?, A> getZNode();

   public static class Impl<A> implements GraphAssoc<A> {
      Graph<?, A> a, z;
      A stuff;

      public Impl(Graph<?, A> a, Graph<?, A> z) {
         this.a = a;
         this.z = z;
      }

      public Graph<?, A> getANode() {
         return a;
      }

      public Graph<?, A> getZNode() {
         return z;
      }

      public A getStuff() {
         return stuff;
      }
   }
}
