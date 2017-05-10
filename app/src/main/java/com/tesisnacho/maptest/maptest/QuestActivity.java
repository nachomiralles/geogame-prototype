package com.tesisnacho.maptest.maptest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.squareup.otto.Subscribe;

import es.kibu.geoapis.api.Tracker;
import es.kibu.geoapis.data.DataUtils;
import es.kibu.geoapis.metrics.sdk.api.AndroidMetricsApi;
import es.kibu.geoapis.metrics.sdk.api.events.MetricsEventsReceiver;
import es.kibu.geoapis.metrics.sdk.data.providers.AndroidDataSubmitter;

public class QuestActivity extends AppCompatActivity  implements MetricsEventsReceiver.SubmitActionReceiver{

    public static final String TAG = "QuestActivity";
    private Point objective;
    private Graphic myPositionGraphic = null;
    private Point myPositionPoint = null;
    private SimpleMarkerSymbol userPositionSymbol;
    private Graphic userPositionGraphic = null;

    private MapView mMapView;
    private SpatialReference wgs84;
    private GraphicsOverlay graphicsOverlay;
    private int walkedDistance = 0;
    private TextView questStatus;
    private QuestSettings quest;
    Tracker tracker;
    private Handler locationTrackerTimeManager;
    private int FRECUENCY = 3000;
    private int WALKED_DISTANCE_MINIMUM_THRESHOLD = 8;
    private int WALKED_DISTANCE_MAXIMUM_THRESHOLD = 50;
    private int FENCE_DISTANCE_RADIUS = 50;
    private Point lastPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic code.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        Intent intent = getIntent();
        this.quest = (QuestSettings) intent.getSerializableExtra("quest");

        this.wgs84 = SpatialReferences.getWgs84();
        this.objective = new Point(this.quest.getLongitude(), this.quest.getLatitude(), this.wgs84);
        ((TextView) findViewById(R.id.questInfo)).setText("QUEST: You have to walk " + (int)this.quest.getDistance() +
                " meters and THEN go to the red point. If you go before you get the distance, the boos will win you!!");


        questStatus = (TextView) findViewById(R.id.questStatus);
        questStatus.setText("Current: " + this.walkedDistance + "/" + (int)this.quest.getDistance());

        mMapView = (MapView) findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS, this.quest.getLatitude(), this.quest.getLongitude(), 14);
        mMapView.setMap(map);

        this.graphicsOverlay = addGraphicsOverlay(mMapView);
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 12);
        final Graphic graphic = new Graphic(this.objective, symbol);
        this.graphicsOverlay.getGraphics().add(graphic);

        AndroidMetricsApi.registerReceiver(this);
        tracker = AndroidMetricsApi.getInstance().getDefaultTracker();

        locationTrackerTimeManager = new Handler();
        locationTrackerTimeManager.postDelayed(new Runnable(){
            public void run(){
                tracker.send("movement");
                //this.updatePoint(fakeData.getNextPoint());
                locationTrackerTimeManager.postDelayed(this, FRECUENCY);
            }

            private void updatePoint(Point nextPoint) {
                if(userPositionGraphic!=null){
                    userPositionGraphic=new Graphic(nextPoint, userPositionSymbol);
                    graphicsOverlay.getGraphics().add(graphic);
                }
                else{
                    userPositionSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 12);
                    userPositionGraphic=new Graphic(nextPoint, userPositionSymbol);
                    graphicsOverlay.getGraphics().add(graphic);
                }
            }
        }, FRECUENCY);
    }

    private void endQuest() {
        if(this.walkedDistance >= this.quest.getDistance()){
            this.startSuccessActivity();
        }
        else this.startFailureActivity();
    }

    private void startFailureActivity() {
        Intent intent = new Intent(this, FailureActivity.class);
        startActivity(intent);
        finish();
    }

    private void startSuccessActivity() {
        Intent intent = new Intent(this, SuccessActivity.class);
        intent.putExtra("quest", this.quest);
        startActivity(intent);
        finish();
    }

    private void showLocationInMap(DataUtils.Location location){
        this.myPositionPoint = new Point(location.longitude, location.latitude, this.wgs84);
        if(this.myPositionGraphic ==null){
            SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 12);
            this.myPositionGraphic = new Graphic(this.myPositionPoint, symbol);
            this.graphicsOverlay.getGraphics().add(this.myPositionGraphic);
            this.lastPoint = new Point(this.myPositionPoint.getX(), this.myPositionPoint.getY(), this.wgs84);
        }
        else{
            this.myPositionGraphic.setGeometry(this.myPositionPoint);
            this.checkWalkedDistance();
        }
        this.mMapView.setViewpointCenterAsync(this.myPositionPoint);
    }

    private double calculateDistance(Point p1, Point p2){
        GeodeticDistanceResult geodeticDistanceResult = GeometryEngine.distanceGeodetic(p1,
                p2,
                new LinearUnit(LinearUnitId.METERS),
                new AngularUnit(AngularUnitId.DEGREES),
                GeodeticCurveType.GEODESIC);
        return geodeticDistanceResult.getDistance();
    }

    private void checkWalkedDistance() {
        double auxiliarWalkedDistance = this.calculateDistance(this.myPositionPoint, this.lastPoint);
        if(auxiliarWalkedDistance>this.WALKED_DISTANCE_MINIMUM_THRESHOLD && auxiliarWalkedDistance<WALKED_DISTANCE_MAXIMUM_THRESHOLD){
            this.walkedDistance+=auxiliarWalkedDistance;
            this.lastPoint = new Point(this.myPositionPoint.getX(), this.myPositionPoint.getY(), this.wgs84);
            questStatus.setText("Current: " + this.walkedDistance + "/" + (int)this.quest.getDistance());
            this.checkIfFinished();
        }
    }

    private void checkIfFinished(){
        double distanceToObjective = this.calculateDistance(this.myPositionPoint, this.objective);
        if(distanceToObjective<this.FENCE_DISTANCE_RADIUS){
            this.endQuest();
        }
    }

    private GraphicsOverlay addGraphicsOverlay(MapView mapView) {
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(0, graphicsOverlay);
        return graphicsOverlay;
    }

    @Subscribe
    @Override
    public void onMetricsDataSubmitted(AndroidDataSubmitter.SubmissionDataEvent event) {
        if (event.getStage() == AndroidDataSubmitter.SubmissionStage.SENT) {
            DataUtils.Location location = event.getLocation();
            this.showLocationInMap(location);
            Log.d(TAG, String.format("Location: %s", location));
        }
    }
}