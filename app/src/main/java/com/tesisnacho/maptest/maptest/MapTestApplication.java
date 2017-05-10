package com.tesisnacho.maptest.maptest;

import android.app.Application;
import android.util.Log;

import es.kibu.geoapis.metrics.sdk.api.AndroidMetricsApi;
import es.kibu.geoapis.metrics.sdk.api.MetricsConfig;
import es.kibu.geoapis.metrics.sdk.api.events.MetricsEventsReceiver;
import es.kibu.geoapis.metrics.sdk.api.events.UserEvent;

/**
 * Created by NachoGeotec on 27/03/2017.
 */

public class MapTestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final String user = "user1";
        final String session = "session7";
//        final String application = "app-23f5644a1706b6ba";
        final String application = "app-47f2e1b5a3c44404";

        AndroidMetricsApi.init(this, new MetricsConfig() {
            public String getUser() {
                return user;
            }

            public String getApplication() {
                return application;
            }

            public String getSession() {
                return session;
            }
        });

//        AndroidMetricsApi.registerReceiver(new MetricsEventsReceiver.UserMetricsEventsReceiver() {
//            @Override
//            public void onMetricsUserEvent(UserEvent userEvent) {
//                Log.d("RECEIVED", userEvent.getData());
//            }
//        });

    }
}
