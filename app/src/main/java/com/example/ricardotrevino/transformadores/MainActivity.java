package com.example.ricardotrevino.transformadores;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.security.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.location.LocationRequest;


public class MainActivity extends EasyLocationAppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    DynamoDBMapper dynamoDBMapper;
    Spinner spinnerTipo, spinnerPoste, spinnerVoltaje;
    ArrayAdapter<String> adapterTipo, adapterPoste, adapterVoltaje;
    String TipoValue, PosteValue, VoltajeValue;
    EditText etMarca, etCapacidad, etNumSerie;
    Button btnGuardar;
    Float latitudeValue, longitudeValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initValues();
        setSpinners();
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


    }

    public void createTransformador(){

        if(checkEditTexts()){

            final com.amazonaws.models.nosql.TransformadoresDO transformador = new com.amazonaws.models.nosql.TransformadoresDO();
            String uuid= UUID.randomUUID().toString();//unique ids
            String uuid2= UUID.randomUUID().toString();
            transformador.setUserId(uuid);
            transformador.setItemId(uuid2);
            transformador.setCapacidad(Double.valueOf(etCapacidad.getText().toString()));
            transformador.setPoste(PosteValue);
            transformador.setVoltaje(Double.valueOf(VoltajeValue));
            transformador.setNumserie(Double.valueOf(etNumSerie.getText().toString()));
            transformador.setMarca(etMarca.getText().toString());
            transformador.setTipo(TipoValue);
            print("Esto tiene lat y long antes de mandarlos: " + latitudeValue +  " " + longitudeValue);
            transformador.setLatitude((double)latitudeValue);//Valor del gps
            transformador.setLongitude((double)longitudeValue);//Valor del gps
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dynamoDBMapper.save(transformador);
                    // Item saved
                    //showToast("Información Guardada");
                }
            }).start();


        }else{
            showToast("Campo vacío");
        }
    }

    public void readTransformadores() {
        print("readTransformadores");
        new Thread(new Runnable() {
            @Override
            public void run() {
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                List<com.amazonaws.models.nosql.TransformadoresDO> transformadores =  dynamoDBMapper.scan(com.amazonaws.models.nosql.TransformadoresDO.class, scanExpression);
                int cont  = 0;
                print(Integer.toString(transformadores.size()));
                
                for(com.amazonaws.models.nosql.TransformadoresDO transformador: transformadores){
                    cont ++;
                    System.out.println(cont + " " + transformador.getUserId());
                }
            }
        }).start();
    }

    public void setSpinners(){
        String[] itemsTipo = {"Monofásico", "Trifásico"};
        String[] itemsVoltaje = {"13.8", "34.5"};
        String[] itemsPoste = {"Madera", "Metal"};
        spinnerTipo = (Spinner)findViewById(R.id.spinnerTipo);
        spinnerPoste = (Spinner)findViewById(R.id.spinnerPoste);
        spinnerVoltaje = (Spinner)findViewById(R.id.spinnerVoltaje);

        adapterTipo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsTipo);
        spinnerTipo.setAdapter(adapterTipo);
        spinnerTipo.setOnItemSelectedListener(this);

        adapterPoste = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsPoste);
        spinnerPoste.setAdapter(adapterPoste);
        spinnerPoste.setOnItemSelectedListener(this);

        adapterVoltaje = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsVoltaje);
        spinnerVoltaje.setAdapter(adapterVoltaje);
        spinnerVoltaje.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinnerTipo)
        {
            TipoValue = parent.getSelectedItem().toString();
        }
        else if(parent.getId() == R.id.spinnerPoste)
        {
            PosteValue = parent.getSelectedItem().toString();

        }else if(parent.getId() == R.id.spinnerVoltaje)
        {
            VoltajeValue = parent.getSelectedItem().toString();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void initValues(){
        TipoValue = "Trifásico";
        PosteValue = "Madera";
        VoltajeValue = "13.8";
        etMarca = (EditText)findViewById(R.id.etMarca);
        etCapacidad = (EditText)findViewById(R.id.etCapacidad);
        etNumSerie = (EditText)findViewById(R.id.etNumSerie);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);
        latitudeValue = (float) 0;
        longitudeValue = (float) 0;
    }

    public boolean checkEditTexts(){
        if(etMarca.getText().toString().matches("") && etCapacidad.getText().toString().matches("") && etNumSerie.getText().toString().matches("")){
            System.out.println("Vacio");
            return false;
        }else{
            System.out.println("No Vacio");

            return true;
        }
    }

    @Override
    public void onClick(View v) {
        requestSingleLocation();
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationPermissionGranted() {
        showToast("Location Permission Granted");
    }

    @Override
    public void onLocationPermissionDenied() {
        showToast("Location Permission Denied");
    }

    @Override
    public void onLocationReceived(Location location) {
        showToast(location.getLatitude() + "," + location.getLongitude());
        latitudeValue = (float)location.getLatitude();
        longitudeValue = (float)location.getLongitude();
        System.out.println("Esto es lat long: " + latitudeValue + " " + longitudeValue);
        createTransformador();//Crea el transformador despues de tener la location
        //readTransformadores();
    }

    @Override
    public void onLocationProviderEnabled() {
        showToast("Location services are now ON");

    }

    @Override
    public void onLocationProviderDisabled() {
        showToast("Location services are still Off");
    }

    public void requestSingleLocation(){
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(3000)
                .build();
        requestSingleLocationFix(easyLocationRequest);
    }

    void print(String message){
        System.out.println(message);
    }
}
