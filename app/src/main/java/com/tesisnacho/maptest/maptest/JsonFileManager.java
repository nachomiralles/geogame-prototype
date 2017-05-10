package com.tesisnacho.maptest.maptest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;


public class JsonFileManager {
    private JsonObject jsonData;
    private int numberOfLevels;

    public JsonFileManager(InputStream is) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(new InputStreamReader(is), JsonElement.class);
        jsonData = element.getAsJsonObject();
        numberOfLevels = jsonData.getAsJsonArray("levels").size();
    }

    public QuestSettings getQuest(int level){
        JsonArray levels = jsonData.getAsJsonArray("levels");

        for(int i = 0; i<levels.size(); i++){
            JsonObject elem = levels.get(i).getAsJsonObject();
            int l = elem.get("level").getAsInt();
            if(l==level){
                float lat = elem.get("latitude").getAsFloat();
                float lon = elem.get("longitude").getAsFloat();
                int d = elem.get("distance").getAsInt();
                return new QuestSettings(lat, lon, d, l);
            }
        }
        return null;
    }

    public int getNumberOfLevels(){
        return this.numberOfLevels;
    }
}
