package com.my.test_estonia;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class PlacesTask extends AsyncTask<Void, Integer, List<HashMap<String, String>>> {

    private static final String TOAST_MSG = "Scanning";
    final String GOOGLE_KEY = "AIzaSyC7diRPdlsoo__jVA4wGSGYm30Ya6Pd2nI";
    final String latitude = "59.365455";
    final String longtitude = "26.432159";
    private Context context;
    private GoogleMap gMap;

    public PlacesTask(final Context context, final GoogleMap gMap) {
        this.context = context;
        this.gMap = gMap;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, TOAST_MSG, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(Void... params) {
        JSONObject json = null;
        String str = "";
        HttpResponse response;
        List<HashMap<String, String>> googlePlacesList = null;
        PlacesParser placeJsonParser = new PlacesParser();
        try {
            final String url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + latitude + "," + longtitude + "&radius=5000&types=restaurant&sensor=true&key=" + GOOGLE_KEY;
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url);

            try {
                response = myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            }
            json = new JSONObject(str);
            final String status = json.optString("status");
            if (!"OK".equals(status)) {
                return null;
            }
            googlePlacesList = placeJsonParser.parse(json);

            return googlePlacesList;
        } catch (final Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {

        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            if (!googlePlace.isEmpty()) {
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " " + " Facebook likes:" + googlePlace.get("likes"));
                gMap.addMarker(markerOptions);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
        }
    }
}
