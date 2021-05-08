package com.nikita.receiver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cunoraz.gifview.library.GifView;

public class CastActivity extends AppCompatActivity {

    Button closeBtn;
    LinearLayout connectWaiting;
    GifView waitGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

        InitComponents();
        toggleWaitingVisibility(true);
    }

    private void InitComponents() {
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        connectWaiting = findViewById(R.id.connectWaiting);
        waitGif = findViewById(R.id.waitGif);
        waitGif.setGifResource(R.drawable.wait);
    }

    private void toggleWaitingVisibility(boolean flag) {
        if(flag) {
            connectWaiting.setVisibility(View.VISIBLE);
            waitGif.play();
        } else {
            waitGif.pause();
            connectWaiting.setVisibility(View.GONE);
        }
    }
}