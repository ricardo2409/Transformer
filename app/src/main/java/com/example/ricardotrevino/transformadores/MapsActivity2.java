package com.example.ricardotrevino.transformadores;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.TransformadoresDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    Object[] objeto;
    DynamoDBMapper dynamoDBMapper;
    List<com.amazonaws.models.nosql.TransformadoresDO> transformadores;
    ArrayList<com.amazonaws.models.nosql.TransformadoresDO> lista;
    Float latitudeValue, longitudeValue;
    String numSerie, marca, capacidad, tipo, poste, voltaje, aparato;
    LocationManager locationManager;
    LatLng latLng;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    byte[] BAimagenTransformador;
    Bitmap bitmap, bOutput;
    //InfoWindow
    ImageView ivInfo;
    List<Marker> transformadorMarkers = new ArrayList<Marker>();
    List<Marker> indicadorMarkers = new ArrayList<Marker>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ivInfo = (ImageView)findViewById(R.id.ivInfo);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Conexión a base de datos
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

        //Get user location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission denied
        } else {
            //Permission granted
            //Checa si la red está permitida
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                System.out.println("GPS");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        float speed = location.getSpeed();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        latLng = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        LatLng latLng2 = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng2, 10);
                        //mMap.animateCamera(cameraUpdate);
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
                });
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                System.out.println("Network");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        float speed = location.getSpeed();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        System.out.println("Estas son las coordenadas: " + latitude + " , " + longitude + " , " + speed);
                        latLng = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
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
                });
            }
        }

        try {
            readAparatos();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permiso Otorgado", Toast.LENGTH_SHORT).show();
            //Permission granted
        } else {
            //Permission denied
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
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
        mMap.setOnInfoWindowClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else{
            mMap.setMyLocationEnabled(true);
        }
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                String[] splitArray = marker.getTitle().split(",");
                String auxAparato = splitArray[0];
                System.out.println("Esto tiene auxaparato: " + auxAparato);

                if(auxAparato.equals("Transformador")){
                    System.out.println("Transformador");

                    View v = getLayoutInflater().inflate(R.layout.custominfo, null);
                    v.setLayoutParams(new RelativeLayout.LayoutParams(650, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    ImageView ivFoto = (ImageView)v.findViewById(R.id.ivInfo);
                    String id = marker.getSnippet();
                    TextView tvMarca = (TextView)v.findViewById(R.id.tvMarcaInfo);
                    TextView tvNumSerie = (TextView)v.findViewById(R.id.tvNumSerieInfo);
                    TextView tvCapacidad = (TextView)v.findViewById(R.id.tvCapacidad);
                    TextView tvTipo = (TextView)v.findViewById(R.id.tvTipo);
                    TextView tvPoste = (TextView)v.findViewById(R.id.tvPoste);
                    TextView tvVoltaje = (TextView)v.findViewById(R.id.tvVoltaje);
                    System.out.println("Esto tiene split: " + splitArray);
                    //Get transformador image with marker id
                    for(int i = 0; i < lista.size(); i++) {
                        com.amazonaws.models.nosql.TransformadoresDO transformador = (com.amazonaws.models.nosql.TransformadoresDO) lista.get(i);
                        String transformadorID = transformador.getUserId();
                        if(id.equals(transformadorID)){
                            System.out.println("Es el mismo");
                            BAimagenTransformador = transformador.getImagen(); //Get transformador image
                            bitmap = BitmapFactory.decodeByteArray(BAimagenTransformador, 0, BAimagenTransformador.length);
                            //Rota la imagen para que se vea normal
                            float degrees = 90;//rotation degree
                            Matrix matrix = new Matrix();
                            matrix.setRotate(degrees);
                            bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            ivFoto.setImageBitmap(bOutput);
                        }else{
                            System.out.println("No es el mismo");
                        }
                    }
                    tvMarca.setText(splitArray[1]);
                    tvCapacidad.setText(splitArray[2]);
                    tvNumSerie.setText(splitArray[3]);
                    tvTipo.setText(splitArray[4]);
                    tvPoste.setText(splitArray[5]);
                    tvVoltaje.setText(splitArray[6]);
                    return v;

                }else if(auxAparato.equals("Indicador de Falla")){
                    System.out.println("Indicador");

                    View v = getLayoutInflater().inflate(R.layout.custominfoindicador, null);
                    v.setLayoutParams(new RelativeLayout.LayoutParams(200, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    ImageView ivFoto = (ImageView)v.findViewById(R.id.ivInfoIndicador);
                    //Get transformador image with marker id
                    String id = marker.getSnippet();

                    for(int i = 0; i < lista.size(); i++) {
                        com.amazonaws.models.nosql.TransformadoresDO transformador = (com.amazonaws.models.nosql.TransformadoresDO) lista.get(i);
                        String transformadorID = transformador.getUserId();
                        if(id.equals(transformadorID)){
                            System.out.println("Es el mismo");
                            BAimagenTransformador = transformador.getImagen(); //Get transformador image
                            bitmap = BitmapFactory.decodeByteArray(BAimagenTransformador, 0, BAimagenTransformador.length);
                            //Rota la imagen para que se vea normal
                            float degrees = 90;//rotation degree
                            Matrix matrix = new Matrix();
                            matrix.setRotate(degrees);
                            bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            ivFoto.setImageBitmap(bOutput);
                        }else{
                            System.out.println("No es el mismo");
                        }
                    }
                    return v;
                }

                return null;
            }
        });
    }

    public void readAparatos() throws InterruptedException {
        print("readAparatos");
        new Thread(new Runnable() {
            @Override
            public void run() {
                print("Estoy en el run");
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                //dynamoDBMapper.load(TransformadoresDO.class, "101");
                transformadores =  dynamoDBMapper.scan(TransformadoresDO.class, scanExpression);
                lista = new ArrayList<TransformadoresDO>();
                print("Este es el tamaño de resultados: " + Integer.toString(transformadores.size()));
                print("Esto tiene Transformadores al hacer el scan: " +  transformadores);

                for(TransformadoresDO transformador: transformadores){
                    lista.add(transformador);
                }
                print("Esto tiene la lista: " + lista.size());

                runOnUiThread(new Runnable() {
                    public void run() {//Crea los markers en el mapa

                        for(int i = 0; i < lista.size(); i++){
                            TransformadoresDO transformador = (TransformadoresDO) lista.get(i);
                            System.out.println(transformador.getLatitude());
                            aparato = transformador.getAparato();
                            System.out.println("Esto es aparato: " + aparato);
                            latitudeValue = transformador.getLatitude().floatValue(); //Get transformador lat
                            longitudeValue = transformador.getLongitude().floatValue(); //Get transformador long
                            marca = transformador.getMarca();
                            int auxCapacidad = transformador.getCapacidad().intValue();
                            capacidad = Integer.toString(auxCapacidad);
                            int auxNumSerie = transformador.getNumserie().intValue();//Double to int to string
                            numSerie = Integer.toString(auxNumSerie);
                            tipo = transformador.getTipo();
                            poste = transformador.getPoste();
                            voltaje = transformador.getVoltaje().toString();
                            String id = transformador.getUserId();
                            LatLng marker = new LatLng(latitudeValue, longitudeValue);

                            if(aparato.equals("Transformador")){
                                Marker transformadorMarker = mMap.addMarker(new MarkerOptions().position(marker).title(aparato + "," + marca + "," + capacidad + "," + numSerie + "," + tipo + "," + poste + "," + voltaje ).snippet(id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                transformadorMarkers.add(transformadorMarker);
                            }else if(aparato.equals("Indicador de Falla")){
                                Marker indicadorMarker = mMap.addMarker(new MarkerOptions().position(marker).title(aparato + "," + marca + "," + capacidad + "," + numSerie + "," + tipo + "," + poste + "," + voltaje ).snippet(id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                indicadorMarkers.add(indicadorMarker);
                            }
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        for(int i = 0; i < lista.size(); i++) {
            String id = marker.getSnippet();
            com.amazonaws.models.nosql.TransformadoresDO transformador = (com.amazonaws.models.nosql.TransformadoresDO) lista.get(i);
            String transformadorID = transformador.getUserId();
            if(id.equals(transformadorID)){
                System.out.println("Es el mismo Click");
                BAimagenTransformador = transformador.getImagen(); //Get transformador image Byte Array
                Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra("foto", BAimagenTransformador);
                startActivity(intent);
            }else{
                System.out.println("No es el mismo click");
            }
        }
    }
}
