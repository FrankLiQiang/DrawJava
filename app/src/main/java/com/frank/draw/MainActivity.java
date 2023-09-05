package com.frank.draw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.dog).setOnClickListener(this);
        findViewById(R.id.snow).setOnClickListener(this);
        findViewById(R.id.rings).setOnClickListener(this);
        findViewById(R.id.dog_help).setOnClickListener(this);
        findViewById(R.id.snow_help).setOnClickListener(this);
        findViewById(R.id.rings_help).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dog: {
                Intent intent = new Intent(this, DogActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.snow: {
                Intent intent = new Intent(this, SnowActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.rings: {
                Intent intent = new Intent(this, NineRingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.dog_help: {
                Toast.makeText(getApplicationContext(), R.string.dog_help, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.snow_help: {
                Toast.makeText(getApplicationContext(), R.string.snow_help, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.rings_help: {
                Toast.makeText(getApplicationContext(), R.string.rings_help, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}

