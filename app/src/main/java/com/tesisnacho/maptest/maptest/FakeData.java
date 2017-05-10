package com.tesisnacho.maptest.maptest;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.util.Random;

/**
 * Created by NachoGeotec on 30/03/2017.
 */

public class FakeData {
    private float latitude = 39.98f;
    private float longitude = -0.05f;
    private SpatialReference wgs84;
    Random randomGenerator = new Random();

    public Point getNextPoint(){
        wgs84 = SpatialReferences.getWgs84();
        latitude+= nextFloat(-0.05f, 0.05f);
        longitude+= nextFloat(-0.05f, 0.05f);
        return new Point(longitude, latitude, wgs84);
    }

    private float nextFloat(float min, float max)
    {
        return min + randomGenerator.nextFloat() * (max - min);
    }


}
