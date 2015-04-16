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

public class PlacesFbParser {
    final String FB_KEY = "1654311001459443";
    final String FB_SECRET_KEY = "6ac98fe6fb7925b337dae35f4f701000";

    /**
     * Public method to parse JSON from URL of Google PLACE API
     * get jsonArray of results
     * set jsonArray to get placesList in private method getPlaces
     *
     * @param jsonObject
     * @return List<HashMap> placesList
     * @see com.my.test_estonia.PlacesTask
     */

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    /**
     * Working with jsonArray of results
     * Parse it with the function getPlace
     * Adding every JSONObject from JSONArray to own field in the <HashMap> placeMap
     * Organization of List with needed results from getPlace to display them in PlacesTask
     *
     * @param jsonArray
     * @return placesList with required fields
     */

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

    /**
     * Parser of json for required fields
     * Getting Facebook likes from function getFBLikes
     * Putting all required results in <HashMap>
     *
     * @param googlePlaceJson
     * @return <HashMap> with all required fields to display
     */

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

    /**
     * Getting Facebook likes
     * Get JSON from URL with help of graph.facebook.com
     * Provide URL "https://graph.facebook.com/" + newName + "?fields=likes&access_token=" + URLEncoder.encode(FB_KEY + "|" + FB_SECRET_KEY, "UTF-8"
     * Get name from JSON of PLACES API for likes parsing, filter it to find in graph.facebook.com, record this in newName
     * make JSON filter to make query faster - fields=likes
     * Adding access_token in this way release_key|secret_key, use URLEncoder to get this symbol "|" in UTF-8
     * Parse JSON and get num of likes in int
     *
     * @param name
     * @return int likes - num likes of given placeName
     */

    private int getFBLikes(String name) {
        JSONObject json;
        String str = "";
        HttpResponse response;
        int likes = 0;
        try {
            String newName = name.replaceAll(" ", "").replaceAll("OÜ", "");
            final String url = "https://graph.facebook.com/" + newName + "?fields=likes&access_token=" + URLEncoder.encode(FB_KEY + "|" + FB_SECRET_KEY, "UTF-8");
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
