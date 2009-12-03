package com.razie.bootstrap.assets.network

import com.razie.pub.lightsoa.SoaAsset
import com.razie.assets._
import com.razie.pub.base.data._
import razie.assets._

@SoaAsset(meta = "Network", descr = "?")
class Network (val e:RazElement) extends AssetImpl {
   def name = e a "name"
   def ipPrefix = e a "ipPrefix"
   def proxy = e a "proxy"
}
