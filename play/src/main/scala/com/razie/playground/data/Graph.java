package com.razie.playground.data;

import java.util.ArrayList;
import java.util.List;

import com.razie.pubstage.data.JStructure;

/**
 * see PlainGraph.
 * 
 * This is a particular type of graph: containing subgraphs. In this case the associations at this level are
 * between ME and someone else. I am the aNode for all my associations. The zNode for my associations however
 * can be one of my nodes or sub-nodes. It cannot however be outside this graph.
 * 
 * @author razvanc
 * @param <T>
 */
public interface Graph<T, A> extends JStructure<T> {
   List<Graph<T, A>> getNodes();

   Graph<T, A> getParent();

   List<GraphAssoc<A>> getAssocs();

   public static class Impl<T, A> extends JStructure.Impl<T> implements Graph<T, A> {
      List<Graph<T, A>> nodes = new ArrayList<Graph<T, A>>();
      List<GraphAssoc<A>> assocs = new ArrayList<GraphAssoc<A>>();
      Graph<T, A> parent;

      public Impl(T contents) {
         super(contents);
      }

      public List<GraphAssoc<A>> getAssocs() {
         return assocs;
      }

      public List<Graph<T, A>> getNodes() {
         return nodes;
      }

      public Graph<T, A> getParent() {
         return parent;
      }
   }
}
