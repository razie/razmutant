package com.razie.playground;

import razie.assets.AssetLocation;

/**
 * this represents a piece of data - normally a String, that we got from someplace
 * 
 * @author razvanc
 * 
 */
public class PieceOfData {
   /** simple mime type of the data */
   public String mimeType;

   /** for string data, give my hints about the structure, if known. i.e. "nvp" etc */
   public String structure;

   // only one of the following 3 must be populated
   public String stringData;
   public String blobData;
   public AssetLocation source;
}
