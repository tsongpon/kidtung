package com.kidtung.dao;

import com.kidtung.domain.TestDomain;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import java.net.UnknownHostException;

/**
 *
 */
public class TestDAO {


    public void save(TestDomain testDomain) throws UnknownHostException {
        DB db = new MongoClient().getDB("mydb");
        JacksonDBCollection<TestDomain, String> coll = JacksonDBCollection.wrap(db.getCollection("col1"), TestDomain.class, String.class);
        WriteResult<TestDomain, String> result = coll.insert(testDomain);
    }
}
