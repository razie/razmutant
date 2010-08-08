package com.razie.playground.data;

/**
 * a simplified relational DB schema is a graph of tables and their relationships
 * 
 * @author razvanc
 */
public class DbSchema extends Graph.Impl<DbTable, DbRelationship> {
   public DbSchema() {
      super(null);
   }
}
