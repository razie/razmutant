/**  ____    __    ____  ____  ____/___     ____  __  __  ____
 *  (  _ \  /__\  (_   )(_  _)( ___) __)   (  _ \(  )(  )(  _ \           Read
 *   )   / /(__)\  / /_  _)(_  )__)\__ \    )___/ )(__)(  ) _ <     README.txt
 *  (_)\_)(__)(__)(____)(____)(____)___/   (__)  (______)(____/   LICENESE.txt
 */
package com.razie.dist.db.test;

import junit.framework.TestCase;

import com.razie.dist.db.XmlDb;
import razie.base.AttrAccessImpl;
import com.razie.pub.base.log.Log;

public class TestXmlDb extends TestCase {
    static String newdbname  = null;
    static String syncdbname = null;

    public void setUp() {
        if (newdbname == null) {
            newdbname = "testdb-1";
            syncdbname = newdbname + "_copy";
        }
    }

    public void test1Create() {
        new XmlDb();
    }

    public void test2Add() {
        XmlDb db = new XmlDb();
        db.initialize("");
        db.add("/db", "node1", new AttrAccessImpl("attr1", "val1", "attr2", "val2"));

        XmlDb db2 = new XmlDb();
        db2.initialize("");
        db2.applyDiffs(0, 1, db.getDiffs(0, 1));

        String s = db2.xpa("/db/node1/@attr1");
        assertTrue("val1".equals(s));
    }

    public void test4Update() {
        XmlDb db = new XmlDb();
        db.initialize("");
        db.add("/db", "node1", new AttrAccessImpl("attr1", "val1", "attr2", "val2"));
        db.add("/db/node1", "newnode", new AttrAccessImpl("attr3", "newval3"));

        XmlDb db2 = new XmlDb();
        db2.initialize("");
        db2.applyDiffs(0, 1, db.getDiffs(0, 1));

        db.setAttr("/db/node1", "attr1", "newval1");
        db2.applyDiffs(1, 2, db.getDiffs(1, 2));

        String s = db2.xpa("/db/node1/@attr1");
        assertTrue("newval1".equals(s));
    }

    public void test4Remove() {
        XmlDb db = new XmlDb();
        db.initialize("");
        db.add("/db", "node1", new AttrAccessImpl("attr1", "val1", "attr2", "val2"));
        db.add("/db/node1", "newnode", new AttrAccessImpl("attr3", "newval3"));

        XmlDb db2 = new XmlDb();
        db2.initialize("");
        db2.applyDiffs(0, 1, db.getDiffs(0, 1));

        db.delete("/db/node1", "newnode", new AttrAccessImpl("attr3", "newval3"));
        db2.applyDiffs(1, 2, db.getDiffs(1, 2));

        assertTrue(db2.xpl("/db/node1/newnode").size() == 0);
    }

    static final Log logger = Log.factory.create(TestXmlDb.class.getName());
}
