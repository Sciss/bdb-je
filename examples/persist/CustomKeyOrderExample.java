/*-
 * Copyright (C) 2002, 2017, Oracle and/or its affiliates. All rights reserved.
 *
 * This file was distributed by Oracle as part of a version of Oracle Berkeley
 * DB Java Edition made available at:
 *
 * http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/downloads/index.html
 *
 * Please see the LICENSE file included in the top-level directory of the
 * appropriate version of Oracle Berkeley DB Java Edition for a copy of the
 * license and additional information.
 */

package persist;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;

public class CustomKeyOrderExample {

    @Entity
    static class Person {

        @PrimaryKey
        ReverseOrder name;

        Person(String name) {
            this.name = new ReverseOrder(name);
        }

        private Person() {} // For deserialization

        @Override
        public String toString() {
            return name.value;
        }
    }

    @Persistent
    static class ReverseOrder implements Comparable<ReverseOrder> {

        @KeyField(1)
        String value;

        ReverseOrder(String value) {
            this.value = value;
        }

        private ReverseOrder() {} // For deserialization

        public int compareTo(ReverseOrder o) {
            return o.value.compareTo(value);
        }
    }

    public static void main(String[] args)
        throws DatabaseException {

        if (args.length != 2 || !"-h".equals(args[0])) {
            System.err.println
                ("Usage: java " + CustomKeyOrderExample.class.getName() +
                 " -h <envHome>");
            System.exit(2);
        }
        CustomKeyOrderExample example =
            new CustomKeyOrderExample(new File(args[1]));
        example.run();
        example.close();
    }

    private Environment env;
    private EntityStore store;

    private CustomKeyOrderExample(File envHome)
        throws DatabaseException {

        /* Open a transactional Berkeley DB engine environment. */
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);
        env = new Environment(envHome, envConfig);

        /* Open a transactional entity store. */
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);
        store = new EntityStore(env, "TestStore", storeConfig);
    }

    private void run()
        throws DatabaseException {

        PrimaryIndex<ReverseOrder,Person> index =
            store.getPrimaryIndex(ReverseOrder.class, Person.class);

        index.put(new Person("Andy"));
        index.put(new Person("Lisa"));
        index.put(new Person("Zola"));

        /* Print the entities in key order. */
        EntityCursor<Person> people = index.entities();
        try {
            for (Person person : people) {
                System.out.println(person);
            }
        } finally {
            people.close();
        }
    }

    private void close()
        throws DatabaseException {

        store.close();
        env.close();
    }
}
