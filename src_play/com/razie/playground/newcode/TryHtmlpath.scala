package com.razie.playground.newcode

import com.razie.pubstage.comms._
import com.razie.pub.base.data.RazElement
import com.razie.pub.base.data._
import java.net._

object TryHtmlpath {
  def main(args : Array[String]) : Unit = {
    
   val page = new HtmlContents ("http://www.theweathernetwork.com/fourteenday/caon0029").readAll
   println(page)

//   val doc:RazElement = new XmlDoc().load ("14", new URL("http://www.theweathernetwork.com/fourteenday/caon0029"))
//   val o = doc xpe "/html/body/div/div/div[2]/div[3]/div/object"
//   println (o)
   
   
   
  }
}
