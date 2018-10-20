package com.mobile.instagram.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;
import java.util.Locale;

public class LocationService {

    private static LocationService instance = null;
    private static Context selfContext;
    private static LocationManager locationManager;
    public static Location location;
    private static Geocoder geocoder;


    /**
     * Singleton implementation
     * @return
     */
    public static LocationService getLocationManager(Context context)     {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private LocationService(Context context )     {
        selfContext = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(context, Locale.getDefault());
    }


    public static String getCity(double lat, double lng){
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String city = addresses.get(0).getLocality();
            return city;
            }catch(Exception e){
            return "";
        }
    }

    public static double[] getCoordinates(){
        location = getLastKnownLocation();
        double[] result = {location.getLatitude(), location.getLongitude()};
        return result;
    }

    public static float distanceFromCurrent(double lat, double lng){
        if (location == null) location = getLastKnownLocation();
        Location dest = new Location("destiny");
        dest.setLatitude(lat);
        dest.setLongitude(lng);
        return location.distanceTo(dest);
    }

    private static Location getLastKnownLocation() {
        Location bestLocation = null;
        List<String> list = locationManager.getProviders(true);
        for (String provider : list) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
