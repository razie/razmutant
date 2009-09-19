package com.razie.learnscala

object IssueMutableMap extends Application {
  
  println("test1--------------")
  println(test1 mkString ",")
  println (test1.counted mkString ",") 
  
  println("test2--------------")
  println(test2 mkString ",")
  
  def test1 = {
    val m = scala.collection.mutable.Map[Int,(Int,Int)] ()
    
    m.put (1,(2,22))
    m.put (3,(4,44))
    
    m.values
  }
  
  def test2 = {
    val i = test1
    var l : List[(Int,Int)] = Nil
    
    for (x <- i)
      l = x :: l
    
    l
  }
    
}
