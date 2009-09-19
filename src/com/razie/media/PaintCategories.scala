package com.razie.media

import com.razie.pub.draw._
import com.razie.pub.draw.widgets._
import com.razie.media.config._
import com.razie.pub.base._
import com.razie.pub.base.data.RazElement
import com.razie.pub.base.data._
import com.razie.pub.comms._;

/** url formatter function will turn a category into a url... */
class PaintCategories (todourl: (String)=>(ActionItem, AttrAccess)){

  def paint (e:com.razie.pub.base.data.RazElement) : Drawable = {
    val ai = new ActionItem(e a "name", e a "pic")
    val tuple = todourl (e a "name")
    new NavButton(ai, new ActionToInvoke(tuple._1, tuple._2))
  }
  
  def paint () : Drawable = {
    val t : DrawTable = new DrawTable()
    t.prefCols = 3
   
    // TODO can't i optimize this at all?
    val el = RazElement torazdoc XmlDoc.Reg.doc(MediaConfig.MEDIA_CONFIG) xpl "categories/category"
    
    for (cate <- el.toList)
      t write paint(cate)

    t
  }
  
}
