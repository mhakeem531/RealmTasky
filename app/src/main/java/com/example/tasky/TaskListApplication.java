package com.example.tasky;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskListApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("tasky.realm")
                .schemaVersion(0)
                //add this line your data will be deleted when migration needed
                .deleteRealmIfMigrationNeeded()
                .build();
        //add this line your data will be delete with every run to your app
       // Realm.deleteRealm(realmConfig);
        Realm.setDefaultConfiguration(realmConfig);
    }


    /**
     * RealmConfiguration realmConfig = new RealmConfiguration.Builder()
     *         .name("tasky.realm")
     *         .schemaVersion(1)
     *         .migration(new Migration())
     *         .build();
     * Realm.setDefaultConfiguration(realmConfig);
     * */

}
