package com.razie.dist.db.test;

import com.razie.dist.db.InMemDb;

import junit.framework.TestCase;

public class TestInMemDb extends TestCase {

    private String  newdbname;
    private String  syncdbname;

    public void test1Create() {
        InMemDb source = new InMemDb(newdbname);
        source.setAttr("attr1", "val1");
        InMemDb dest = new InMemDb(newdbname);
        dest.applyDiffs(0, 1, source.getDiffs(0, 1));
        
        assertTrue (dest.getAttr("attr1").equals("val1"));
    }

    public void test2Update() {
        InMemDb source = new InMemDb(newdbname);
        source.setAttr("attr1", "val1");
        InMemDb dest = new InMemDb(newdbname);
        dest.applyDiffs(0, 1, source.getDiffs(0, 1));

        source.setAttr("attr1", "val2");
        dest.applyDiffs(1, 2, source.getDiffs(1,2));
        
        assertTrue (dest.getAttr("attr1").equals("val2"));
    }

}
