package com.razie.media.assets;

import com.razie.media._
import com.razie.pub.assets._

object MovieInventory {
    val sCLASS_MOVIE = "Movie";
}

/**
 * movies have specific extensions, descriptions, functionality (i.e. "google") etc
 * 
 */
class MovieInventory extends ToDeleteMediaInventory {
    override def get(ref:AssetKey ) :Movie =
        new Movie(getBrief(ref));
}