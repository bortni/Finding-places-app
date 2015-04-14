package com.my.test_estonia;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class RoadBuilderTask extends AsyncTask<Void, Integer, Boolean> {
    private static final String TOAST_MSG = "Calculating";
    private static final String TOAST_ERR_MAJ = "Impossible to trace Itinerary";
    private Context context;
    private GoogleMap gMap;
    private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();

    public RoadBuilderTask(final Context context, final GoogleMap gMap) {
        this.context = context;
        this.gMap = gMap;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, TOAST_MSG, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        JSONObject json = null;
        String str = "";
        HttpResponse response;
        try {
            final String url = "http://maps.googleapis.com/maps/api/directions/json?sensor=false&language=en&origin=Narva&destination=Tallin";
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
                return false;
            }
            JSONArray jsonRoutes = json.getJSONArray("routes");
            JSONArray jsonSteps = jsonRoutes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            for (int i = 0; i < jsonSteps.length(); i++) {
                String points = jsonSteps.getJSONObject(i).getJSONObject("polyline").optString("points");
                decodePolylines(points);
            }
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    private void decodePolylines(final String encodedPoints) {
        int index = 0;
        int lat = 0, lng = 0;
        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (!result) {
            Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();
        } else {
            final PolylineOptions polylines = new PolylineOptions();
            polylines.color(Color.BLUE);
            for (final LatLng latLng : lstLatLng) {
                polylines.add(latLng);
            }
            final MarkerOptions markerA = new MarkerOptions();
            markerA.position(lstLatLng.get(0));
            markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            final MarkerOptions markerB = new MarkerOptions();
            markerB.position(lstLatLng.get(lstLatLng.size() - 1));
            markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(lstLatLng.size() / 2 + 300), 7));
            gMap.addMarker(markerA);
            gMap.addPolyline(polylines);
            gMap.addMarker(markerB);
        }
    }
}
