package com.tesisnacho.maptest.maptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;


public class SuccessActivity extends AppCompatActivity {

    private QuestSettings previousQuest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        previousQuest = (QuestSettings) intent.getSerializableExtra("quest");
    }

    public void nextLevel(View view){
        Intent intent = new Intent(this, QuestActivity.class);
        try {
            InputStream open = this.getResources().getAssets().open("ExternalData.json");
            JsonFileManager file = new JsonFileManager(open);
            //TODO If it is last level, show GAME OVER
            if(this.previousQuest.getLevel()==file.getNumberOfLevels()){
                Intent lastIntent = new Intent(this, GameFinished.class);
                startActivity(lastIntent);
            }

            else{
                QuestSettings quest = file.getQuest(this.previousQuest.getLevel()+1);
                intent.putExtra("quest", quest);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        startActivity(intent);
        finish();
    }
}
