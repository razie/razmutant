/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.learnscala

object Kuk extends Application {
  val m = new java.util.HashMap[Int,String]()
  
}
trait Centered {
  val center:Point
  
  def offset (offsetBy:Point) = center += offsetBy
}

trait AnotherCentered {
  def center:Point
  def offset (offsetBy:Point) = center += offsetBy
}

class AnotherShape extends AnotherCentered {
  override val center:Point = new Point(0,0)
}