package com.razie.playground.data;

import java.util.List;

/**
 * simple DB table - just a set of typed columns
 * 
 * @author razvanc
 */
public interface DbTable {
   public String getName();

   public List<AttrSpec> getColumns();

   public List<AttrSpec> getKeys();
}
