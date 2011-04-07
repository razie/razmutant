/**  ____    __    ____  ____  ____,,___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___)/ __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__) \__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)(___/   (__)  (______)(____/    LICENSE.txt
 */
package com.razie.media

import razie.base._
import razie.draw._
import razie.draw.widgets._
import com.razie.media.config._
import razie.base.data._
import com.razie.pub.base._
import razie.base.data.RazElement
import com.razie.pub.base.data._
import com.razie.pub.comms._;

/** url formatter function will turn a category into a url... */
class PaintCategories (todourl: (String)=>(ActionItem, AttrAccess)){

  def paint (e:razie.base.data.RazElement) : Drawable = {
    val ai = new ActionItem(e a "name", e a "pic")
    val tuple = todourl (e a "name")
    new NavButton(ai, new SimpleActionToInvoke(tuple._1, tuple._2))
  }
  
  def paint () : Drawable = {
    val el = RazElement torazdoc XmlDoc.Reg.doc(MediaConfig.MEDIA_CONFIG) xpl "categories/category"
    razie.Draw.layoutTable (3) (el.toList)
//    t.prefCols = 3
   
    // TODO can't i optimize this at all?
    
//    for (cate <- el.toList)
//      t write paint(cate)

//    t
  }
  
}
