package com.example.tasky.models;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        /************************************************
         // Version 0
         class Task
         @Required
         @PrimaryKey
         private String id;
         @Required
         private String name;
         private boolean done;

         // Version 1
         class Task
         @Required
         @PrimaryKey
         private String id;
         @Required
         private String name;
         private boolean done;
         private long timestamp;
         ************************************************/
        // Migrate from version 0 to version 1
        if (oldVersion == 0) {
            RealmObjectSchema taskSchema = schema.get("Task");

            /**  transform block
             *  I include it here to show how you would go about setting a value for the field in
             *  already existing objects. transform()runs a transformation function on each RealmObject
             *  instance of the current class. In the above, the existing objects will have a timestamp value of 0.
             * */
            taskSchema.addField("timestamp", long.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("timestamp", 0);
                        }
                    });
            oldVersion += 1;
        }
    }
}