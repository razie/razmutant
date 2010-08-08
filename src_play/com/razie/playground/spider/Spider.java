package com.razie.playground.spider;

public class Spider {
   String urlbase;
   String where;
   String urlstart;
   int howmany;
   int actuallySpidered;

   public Spider(String urlbase, String where, String urlstart, int howmany) {
      super();
      this.urlbase = urlbase;
      this.where = where;
      this.urlstart = urlstart;
      this.howmany = howmany;
   }

   public int spider() {
      return 0;
   }

}
