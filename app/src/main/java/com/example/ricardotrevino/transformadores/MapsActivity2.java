package com.example.ricardotrevino.transformadores;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Object[] objeto;
    DynamoDBMapper dynamoDBMapper;
    List<com.amazonaws.models.nosql.TransformadoresDO> transformadores;
    ArrayList<com.amazonaws.models.nosql.TransformadoresDO> lista;
    Float latitudeValue, longitudeValue;
    String numSerie, marca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();


        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        try {
            readTransformadores();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

    public void readTransformadores() throws InterruptedException {
        print("readTransformadores");
        new Thread(new Runnable() {
            @Override
            public void run() {
                print("Estoy en el run");
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                transformadores =  dynamoDBMapper.scan(com.amazonaws.models.nosql.TransformadoresDO.class, scanExpression);
                lista = new ArrayList<com.amazonaws.models.nosql.TransformadoresDO>();
                print("Este es el tamaño de resultados: " + Integer.toString(transformadores.size()));
                print("Esto tiene Transformadores al hacer el scan: " +  transformadores);

                for(com.amazonaws.models.nosql.TransformadoresDO transformador: transformadores){
                    lista.add(transformador);
                }
                print("Esto tiene la lista: " + lista.size());
                //Fuera del for

                runOnUiThread(new Runnable() {
                    public void run() {//Crea los markers en el mapa
                        for(int i = 0; i < lista.size(); i++){
                            com.amazonaws.models.nosql.TransformadoresDO transformador = (com.amazonaws.models.nosql.TransformadoresDO) lista.get(i);
                            System.out.println(transformador.getLatitude());
                            latitudeValue = transformador.getLatitude().floatValue(); //Get transformador lat
                            longitudeValue = transformador.getLongitude().floatValue(); //Get transformador long
                            marca = transformador.getMarca();
                            LatLng marker = new LatLng(latitudeValue, longitudeValue);
                            mMap.addMarker(new MarkerOptions().position(marker).title(marca));
                        }
                    }
                });


            }
        }).start();

        //Cuando acabe que se haga el intent a maps para que se hayan pasado todos los transformadores

        print("Llegue al return");

    }
    void print(String message){
        Log.i("hola" , message);
    }
}
