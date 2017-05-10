package com.tesisnacho.maptest.maptest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.esri.arcgisruntime.mapping.view.MapView;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION ,
            Manifest.permission.ACCESS_FINE_LOCATION};
    int MY_PERMISSIONS_REQUEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //When START Clicked.
    public void startQuest(View view){
        this.checkPermisions(permissions);
    }


    private void checkPermisions(String[] permissions){
       if(hasPermissions(permissions)) {
           this.startGame();
       }
       else {
           ActivityCompat.requestPermissions(this,
                   permissions,
                   MY_PERMISSIONS_REQUEST);
       }
    }

    private boolean hasPermissions(String[] permissions){
        for(String permission : permissions){
            if (ContextCompat.checkSelfPermission(this,
                    permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void startGame(){
        Intent intent = new Intent(this, QuestActivity.class);
        this.setValues(intent);

    }

    private void setValues(Intent intent){
        InputStream open = null;
        try {

            open = this.getResources().getAssets().open("ExternalData.json");
            if(open != null)
            {
                JsonFileManager file = new JsonFileManager(open);
                QuestSettings quest = file.getQuest(1);
                intent.putExtra("quest", quest);
                startActivity(intent);
                finish();
            }
        } catch (IOException e){
            new AlertDialog.Builder(this)
                    .setTitle("FILE CRASH")
                    .setMessage("The sistem found a crash, the app will now close.")
                    .setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if(MY_PERMISSIONS_REQUEST == PackageManager.PERMISSION_GRANTED){
            this.startGame();
        }
        else {
            Log.d("NACHO MESSAGE", "PERMISSION PROBLEM");
        }
    }

}
