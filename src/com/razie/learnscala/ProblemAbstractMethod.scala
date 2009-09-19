package com.razie.learnscala

//import com.razie.pub.base._
import com.razie.pub.assets._

import scala.collection._

object ProblemAbstractMethod extends Application {
  val ac = new ActualClass
  ac.inject("a", "b", "c")
  println ("s")
}

/** register stuff in a map */
class ActualClass {
  
  type TB = String
  type ActionItem = String

  val injectedB = mutable.Map[String,mutable.Map[ActionItem,TB]]()

    def initMap[A] (meta:String, ink:mutable.Map[String,mutable.Map[ActionItem, A]]):mutable.Map[ActionItem,A] = {
      val m = ink.get(meta)
      
      if (m.isEmpty){
        val m:mutable.Map[ActionItem,A] = mutable.Map[ActionItem,A] ()
        ink.put (meta, m)
        m
        }
      else
        m.get
    }
    
  /** new type inject */
  def inject (meta:String, action:String, injected:TB) : Unit = {
      initMap[TB](meta, injectedB).put (action, injected)
  }
}
