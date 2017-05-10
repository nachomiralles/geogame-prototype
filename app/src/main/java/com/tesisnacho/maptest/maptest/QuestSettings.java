package com.tesisnacho.maptest.maptest;

import java.io.Serializable;

/**
 * Created by NachoGeotec on 14/03/2017.
 */

public class QuestSettings implements Serializable{
    private float latitude;
    private float longitude;
    private float distance;
    private int level;

    public QuestSettings(float lat, float lon, float d, int l){
        this.latitude = lat;
        this.longitude = lon;
        this.distance = d;
        this.level = l;
    }

    public float getLatitude(){
        return this.latitude;
    }
    public float getLongitude(){
        return this.longitude;
    }
    public float getDistance(){
        return this.distance;
    }
    public int getLevel() { return this.level; }
}
