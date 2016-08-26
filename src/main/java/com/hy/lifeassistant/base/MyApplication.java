package com.hy.lifeassistant.base;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .name("data5.rlm")
                .schemaVersion(5)
                .migration(new RealmMigration() {

                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                    }

                })
                .build();

        Realm.setDefaultConfiguration(config);
    }

}
