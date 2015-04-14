package com.my.test_estonia;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesParser {
    final String fbKey = "1654311001459443";
    final String fbKeySecret = "6ac98fe6fb7925b337dae35f4f701000";

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap;

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String likes = "";
        String latitude;
        String longitude;


        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
                likes = Integer.toString(getFBLikes(placeName));
            }

            if (!likes.equalsIgnoreCase("0")) {
                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                googlePlaceMap.put("place_name", placeName);
                googlePlaceMap.put("lat", latitude);
                googlePlaceMap.put("lng", longitude);
                googlePlaceMap.put("likes", likes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private int getFBLikes(String name) {
        JSONObject json;
        String str = "";
        HttpResponse response;
        int likes = 0;
        try {
            String newName = name.replaceAll(" ", "-").replaceAll("OÜ", "");
            final String url = "https://graph.facebook.com/v2.3/" + newName + "?fields=likes&access_token=" + URLEncoder.encode(fbKey + "|" + fbKeySecret, "UTF-8");
            HttpClient myClient = new DefaultHttpClient();
            HttpGet myConnection = new HttpGet(url);

            try {
                response = myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            }
            json = new JSONObject(str);

            if (json.getInt("likes") != 0) {
                likes = json.optInt("likes");
            }

        } catch (final Exception e) {
            return 0;
        }
        return likes;
    }
}
