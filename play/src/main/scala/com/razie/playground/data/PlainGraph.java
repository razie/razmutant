package com.razie.playground.data;

import java.util.ArrayList;
import java.util.List;

import com.razie.pubstage.data.JStructure;

/**
 * a graph is a set of nodes and a set of associations between those nodes. What we have here is the basic
 * notion of a graph. Thus we will not make any assumptions about the association ends, yet. They can be
 * between my nodes or their nodes.
 * 
 * As a matter of convention, the associations can be stored in both places, but they MUST be available in the
 * aNode or one of its parents.
 * 
 * @author razvanc
 * 
 * @param <T>
 */
public interface PlainGraph<T, A> extends JStructure<T> {
   List<PlainGraph<T, A>> getNodes();

   List<GraphAssoc<A>> getAssocs();

   public static class Impl<T, A> extends JStructure.Impl<T> implements PlainGraph<T, A> {
      List<PlainGraph<T, A>> nodes = new ArrayList<PlainGraph<T, A>>();
      List<GraphAssoc<A>> assocs = new ArrayList<GraphAssoc<A>>();

      public Impl(T contents) {
         super(contents);
      }

      public List<GraphAssoc<A>> getAssocs() {
         return assocs;
      }

      public List<PlainGraph<T, A>> getNodes() {
         return nodes;
      }
   }
}
