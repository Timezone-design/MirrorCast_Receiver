package com.nikita.receiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.nikita.receiver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    AppCompatButton startBtn;
    AppCompatButton exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                OnBackPressed();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        InitComponents();
    }

    private void InitComponents() {
        startBtn = findViewById(R.id.startBtn);
        exitBtn = findViewById(R.id.exitBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkWifiOn()) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("ATTENTION");
                    alert.setMessage("The WIFI is not activated. Please check the WIFI state.");
                    alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBackPressed();
            }
        });
    }

    private boolean checkWifiOn() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
            return true;
        return false;
    }

    // Override the OnBackPressed
    public void OnBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("ALERT");
        alert.setMessage("Do you really want to exit?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}