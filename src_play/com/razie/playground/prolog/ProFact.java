package com.razie.playground.prolog;

import com.razie.pub.base.data.MemDb;

public class ProFact {
   ProPredicate predicate;
   Object[] vars;

   protected static MemDb<String, ProFact> allFacts = new MemDb<String, ProFact>();

   public ProFact(ProPredicate p, Object... vars) {
      this.predicate = p;
      this.vars = vars;

      allFacts.put(p.name, this);
   }
}
