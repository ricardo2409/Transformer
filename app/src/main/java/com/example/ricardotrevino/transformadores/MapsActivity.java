package com.example.ricardotrevino.transformadores;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.amazonaws.models.nosql.TransformadoresDO;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    MainActivity mainActivity = new MainActivity();

    Float latitudeValue, longitudeValue;
    String numSerie, marca;
    List<com.amazonaws.models.nosql.TransformadoresDO> resultados = new ArrayList<com.amazonaws.models.nosql.TransformadoresDO>();
    Object[] objeto;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("OnCreate Map");
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        ArrayList<TransformadoresDO> lista = (ArrayList<TransformadoresDO>)args.getSerializable("lista");
        //Obtiene los transformadores
        //objeto = (Object[]) intent.getSerializableExtra("transformadores");
        System.out.println("ESTO ES LO QUE TIENE LA LISTA: " + lista.size());
        //MainActivity.getInstance().print("Esto es lo que saqué de resultados: " + resultados);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("Map Ready");
        //mainActivity.print("Metodo desde Main");
        mMap = googleMap;

        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso Negado", Toast.LENGTH_SHORT).show();
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            //Permission granted
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }

        for(int i = 0; i < objeto.length; i++){
            TransformadoresDO transformador = (TransformadoresDO) objeto[i];
            System.out.println(transformador.getLatitude());
            latitudeValue = transformador.getLatitude().floatValue(); //Get transformador lat
            longitudeValue = transformador.getLongitude().floatValue(); //Get transformador long
            marca = transformador.getMarca();
            LatLng marker = new LatLng(latitudeValue, longitudeValue);
            mMap.addMarker(new MarkerOptions().position(marker).title(marca));
        }
        */

    }


    @Override
    public void onLocationChanged(Location location) {
        //Mover la camara a user location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}