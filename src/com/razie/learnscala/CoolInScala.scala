package com.razie.learnscala

object CoolInScala extends Application {
 
  println ("lesson1: \n" + lesson1)
  println ("lesson2a: \n" + (lesson2 mkString))
  println ("lesson2b: \n" + lesson2)
  println ("lesson3: \n" + issue3)
 
  def lesson1 = {
    val array = Array("s1", "s2")
    var concat=""
    array foreach (arg => concat += arg)
    concat
  }
  
  def lesson2 = {
    for (i <- 1 to 5; j <- 1 to 5) 
      yield (i +"-" + j + {if(j==5)"\n" else " "}) 
  }
  
  def issue3 = {
    // note that the IF behaves really funny - it actually defined a function...
    for (i <- 1 to 5; j <- 1 to 5) 
      yield (i +"-" + j + (if(j==5)"\n")) 
  }
  
  def playwithxml (file:String) = {
    val root : xml.Elem = xml.XML.loadFile(file)
    root
  }
  
}
