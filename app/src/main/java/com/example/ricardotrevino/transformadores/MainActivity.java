package com.example.ricardotrevino.transformadores;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    DynamoDBMapper dynamoDBMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //createTransformador();
        readTransformadores();
    }

    public void createTransformador(){
        final TransformadoresDO transformador = new TransformadoresDO();
        transformador.setUserId("1");
        transformador.setItemId("1");
        transformador.setInfo("Esto es info");
        transformador.setLatitude(25.654028);
        transformador.setLongitude(-100.266437);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(transformador);
                // Item saved
            }
        }).start();

    }

    public void readTransformadores() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                TransformadoresDO transformador = dynamoDBMapper.load(TransformadoresDO.class, "1", "1");

                // Item read
                Log.d("Info: ", transformador.getInfo());

                TransformadoresDO Lista = dynamoDBMapper.scan(TransformadoresDO.class, new DynamoDBScanExpression());

            }
        }).start();
    }
}
