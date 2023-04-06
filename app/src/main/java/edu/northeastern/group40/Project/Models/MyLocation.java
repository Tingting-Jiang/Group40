package edu.northeastern.group40.Project.Models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class MyLocation implements Serializable {

    public String address = null;
    public double latitude = 0;
    public double longitude = 0 ;

    public MyLocation(double latitude, double longitude, Context context) throws IOException {
        this.latitude = latitude;
        this.longitude = longitude;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses  = geocoder.getFromLocation(latitude,longitude, 1);
        address = addresses.get(0).getAddressLine(0);
    }

    public MyLocation(Place place){
        this.address = place.getAddress();
        LatLng latLng = place.getLatLng();
        assert latLng != null;
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public MyLocation() {}

    public double distanceToInMiles(MyLocation other){
        Location start = new Location("start");
        start.setLatitude(latitude);
        start.setLongitude(longitude);

        Location end = new Location("end");
        end.setLatitude(other.latitude);
        end.setLongitude(other.longitude);
        return start.distanceTo(end)/0.000621371;
    }


}
