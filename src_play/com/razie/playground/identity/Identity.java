package com.razie.playground.identity;

public class Identity {
   public String name; // identitie's name
   public String providerUrl;
   public String username;
   public String password;
   public String token;

   public Identity(String name, String prov, String user, String pwd) {
      this.name = name;
      this.providerUrl = prov;
      this.username = user;
      this.password = pwd;
   }

   /**
    * when using an identity-based service, normally, after login, you get an url with auth code etc
    */
   public String getInitialAccessUrl() {
      return null;
   }
}
