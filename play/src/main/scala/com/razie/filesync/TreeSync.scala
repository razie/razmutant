package com.razie.filesync

import com.razie.pubstage.data._

// result of a node comparison
class IsSameOrNewer 
case class IsSame extends IsSameOrNewer  // still the same node, no sync needed
case class IsNewer extends IsSameOrNewer // one node is newer - should be obvious in context which one
case class IsOlder extends IsSameOrNewer // other node is newer - should be obvious in context which one
case class IsDifferent extends IsSameOrNewer // one node is different but can't tell which is newer...most likely a conflict
case class NoMatch extends IsSameOrNewer // doesn't match - when comparing two nodes
case class Missing extends IsSameOrNewer // a node is not in the other tree - should be obvious in context
case class Unknown extends IsSameOrNewer // for a folder, it means i should compare its contents

/** 
 * this includes enough information to compare two instances of the same entity. 
 * 
 * it normally includes hashcodes, date/time last modified, whathaveyou
 */
trait TSBrief {
   def compare (other:TSBrief) : IsSameOrNewer
}

/** an action on a tree node: copy,move,compare,diff,remove */
abstract class TSStrategy {
   def execute (cmd:TSAct)
}

case class TSPath (val root:SyncTree[TSBrief], val path:Seq[TSBrief]) {
   override def clone () : TSPath = TSPath (root, Seq (path:_*))
}
   
case class TSContext (
      val src:TSBrief, 
      val dest:TSBrief, 
      val srcPath:TSPath, 
      val destPath:TSPath) {
   override def clone () : TSContext = TSContext (src, dest, srcPath.clone, destPath.clone)
}

abstract class TSAct ( ctx : TSContext ) {
}

case class TSAdd     ( ctx:TSContext ) extends TSAct (ctx)
case class TSRemove  ( ctx:TSContext ) extends TSAct (ctx)
case class TSUpdate  ( ctx:TSContext ) extends TSAct (ctx)
case class TSUpdate2 ( ctx:TSContext ) extends TSAct (ctx)


class SyncTree[T<:TSBrief] (root:T) (f: TreeStruc[T] => List[TreeStruc[T]]) extends LazyTreeBuilder[T] (root)(f) {
//   w T <: TSBrief
}

object SyncTree {
   
   def syncNode[T<:TSBrief] (src:SyncTree[T], dest:SyncTree[T], path:TSPath) : Seq[TSAct] = {
      val ctx = TSContext (src.contents, dest.contents, path, path)
      src.contents.compare (dest.contents) match {
         case IsNewer() => Seq(TSUpdate(ctx))
         case IsOlder() => Seq(TSUpdate2(ctx))
      }
   }
   
   def sync[T<:TSBrief] (src:SyncTree[T], dest:SyncTree[T]) : Seq[TSAct] = {
      
//      for (a <- src.)

      Seq()
   }
}
