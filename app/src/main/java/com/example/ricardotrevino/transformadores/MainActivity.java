package com.example.ricardotrevino.transformadores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.TransformadoresDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.location.LocationRequest;


public class MainActivity extends EasyLocationAppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, Serializable{

    DynamoDBMapper dynamoDBMapper;
    Spinner spinnerTipo, spinnerPoste, spinnerVoltaje;
    ArrayAdapter<String> adapterTipo, adapterPoste, adapterVoltaje;
    String TipoValue, PosteValue, VoltajeValue;
    EditText etMarca, etCapacidad, etNumSerie;
    Button btnGuardar, btnFoto, btnMapa2;
    Float latitudeValue, longitudeValue;
    ImageView ivFoto;
    byte[] byteArray;
    Bitmap bitmap;
    Bitmap bOutput;
    List<com.amazonaws.models.nosql.TransformadoresDO> transformadores;
    ArrayList<TransformadoresDO> lista;
    MapsActivity mapsActivity;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print("OnCreate");
        mainActivity = this;
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

        /*
        try {
            readTransformadores();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    protected void onPostResume() {
        //Pide los transformadores
        super.onPostResume();
        print("OnPostResume");
        /*
        try {
            readTransformadores();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    public void createTransformador(){

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
        transformador.setImagen(byteArray);
        print("Esto tiene lat y long antes de mandarlos: " + latitudeValue +  " " + longitudeValue);
        transformador.setLatitude((double)latitudeValue);//Valor del gps
        transformador.setLongitude((double)longitudeValue);//Valor del gps
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(transformador);
                // Item saved
                showToast("Información Guardada");
                resetValues();
            }
        }).start();
    }

    public void readTransformadores() throws InterruptedException {
        print("readTransformadores");
        new Thread(new Runnable() {
            @Override
            public void run() {
                print("Estoy en el run");
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                transformadores =  dynamoDBMapper.scan(TransformadoresDO.class, scanExpression);

                lista = new ArrayList<TransformadoresDO>();

                print("Este es el tamaño de resultados: " + Integer.toString(transformadores.size()));
                print("Esto tiene Transformadores al hacer el scan: " +  transformadores);
                int cont  = 0;
                byte[] BAimagenTransformador;

                for(TransformadoresDO transformador: transformadores){

                    lista.add(transformador);

                    /*
                    BAimagenTransformador = transformador.getImagen(); //Get transformador image
                    bitmap = BitmapFactory.decodeByteArray(BAimagenTransformador, 0, BAimagenTransformador.length);
                    //Rota la imagen para que se vea normal
                    float degrees = 90;//rotation degree
                    Matrix matrix = new Matrix();
                    matrix.setRotate(degrees);
                    bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //print("Foto Cambiada");
                            //ivFoto.setImageBitmap(bOutput);
                        }
                    });
                    */
                }
                print("Esto tiene la lista: " + lista.size());
                //Fuera del for

            }
        }).start();

        //Cuando acabe que se haga el intent a maps para que se hayan pasado todos los transformadores

        print("Llegue al return");

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
        btnFoto = (Button)findViewById(R.id.btnFoto);
        btnFoto.setOnClickListener(this);
        btnMapa2 = (Button)findViewById(R.id.btnMap2);
        btnMapa2.setOnClickListener(this);
        latitudeValue = (float) 0;
        longitudeValue = (float) 0;
        ivFoto = (ImageView)findViewById(R.id.ivFoto);
        ivFoto.setImageResource(android.R.color.transparent);


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
        switch(v.getId()) {
            case R.id.btnGuardar:
                if(checkEditTexts()){
                    requestSingleLocation();
                }else{
                    showToast("Campo Vacío");
                }
                break;

            case R.id.btnFoto:
                tomarFoto();
                break;

            case R.id.btnMap2:

                createIntent();
                break;
        }
    }

    public void createIntent() {
        Intent intent = new Intent(this, MapsActivity2.class);
        startActivity(intent);
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
        showToast("Permiso Otorgado");
    }

    @Override
    public void onLocationPermissionDenied() {
        showToast("Permiso Negado");
    }

    @Override
    public void onLocationReceived(Location location) {
        //showToast(location.getLatitude() + "," + location.getLongitude());
        latitudeValue = (float)location.getLatitude();
        longitudeValue = (float)location.getLongitude();
        System.out.println("Esto es lat long: " + latitudeValue + " " + longitudeValue);
        createTransformador();//Crea el transformador despues de tener la location
        //readTransformadores();
    }

    @Override
    public void onLocationProviderEnabled() {
        print("Location services are now ON");

    }

    @Override
    public void onLocationProviderDisabled() {
        print("Location services are still Off");
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
        Log.i("hola" , message);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    public void tomarFoto(){
        dispatchTakePictureIntent();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(imageBitmap);
            ivFoto.setRotation(90);

            //Convert bitmap into byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            print("Esto es el byte array: " + byteArray.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void resetValues(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etMarca.setText("");
                etCapacidad.setText("");
                etNumSerie.setText("");
                ivFoto.setImageResource(android.R.color.transparent);
            }
        });

    }
}
