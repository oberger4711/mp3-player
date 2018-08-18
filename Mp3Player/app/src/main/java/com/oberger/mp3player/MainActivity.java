package com.oberger.mp3player;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements AlbumFragment.OnAlbumSelectedListener {

    private final static int PERMISSION_REQUEST_CODE = 1337;

    private void loadAlbumFragment() {
        // Permissions are assumed to be checked here.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PERMISSION_GRANTED) {
            loadAlbumFragment();
        }
        else {
            askPermission();
            // Music will be loaded on callback.
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Our permission
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                loadAlbumFragment();
            }
            else {
                Toast.makeText(getApplicationContext(), "Kein Zugriff auf Songs erlaubt.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAlbumSelected(int id) {
        // TODO: Implement.
        Log.d(this.getClass().getSimpleName(), "Selected album!");
    }
}
