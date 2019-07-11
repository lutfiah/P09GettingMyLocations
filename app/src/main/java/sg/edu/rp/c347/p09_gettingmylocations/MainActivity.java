package sg.edu.rp.c347.p09_gettingmylocations;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnStop, btnCheck;
    TextView tv;
    FusedLocationProviderClient client;
    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnCheck = findViewById(R.id.btnCheck);

        client = LocationServices.getFusedLocationProviderClient(this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()==true) {
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                String msg = "Latitude: " + location.getLatitude() + "\nLongtitude: " + location.getLongitude();
                                tv.setText(msg);
                            } else {
                                String msg = "No last known location found";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent i = new Intent(MainActivity.this, MyService.class);
                    startService(i);
                } else {
                    Toast.makeText(MainActivity.this, "Permission not granted.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);
                }
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission() == true) {
                    File targetFile = new File(folderLocation, "data.txt");

                    if (targetFile.exists() == true) {
                        String data = "";
                        try {
                            FileReader reader = new FileReader(targetFile);
                            BufferedReader br = new BufferedReader(reader);
                            String line = br.readLine();
                            while (line != null) {
                                data += line + "\n";
                                line = br.readLine();
                                Toast.makeText(MainActivity.this, line,
                                        Toast.LENGTH_LONG).show();
                            }
                            br.close();
                            reader.close();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to read!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        tv.setText(data);
                        Log.d ("Content",data);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission not granted.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
            }
        });


    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Write = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Write == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
