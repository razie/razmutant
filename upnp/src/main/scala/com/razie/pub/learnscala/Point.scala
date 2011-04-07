/**
 * Razvan's public code. Copyright 2008 based on Apache license (share alike) see LICENSE.txt for
 * details. No warranty implied nor any liability assumed for this code.
 */
package com.razie.pub.learnscala

class SimplePoint (var x:Int, var y:Int){
  // no overloading - get full class
}

class Point (ax:Int, ay:Int){
  private[this] var ix:Int = ax
  private[this] var iy:Int = ay
  
  def x = ix
  def y = iy
  def x_= (ax:Int) { require(ax>0); ix=ax }
  def y_= (ay:Int) { require(ay>0); iy=ay }
  
  def += (p:Point) = { ix += p.x; iy += p.y }
}

class SimplerPoint (private var ix : Int, private var iy:Int) {
  def x = ix
  def y = iy
  def x_= (ax:Int) { require(ax>0); ix=ax }
  def y_= (ay:Int) { require(ay>0); iy=ay }
}

class SimplestPoint (private var x : Int, private var y:Int) {
  //def x_= (ax:Int) { require(ax>0); ix=ax }
}
