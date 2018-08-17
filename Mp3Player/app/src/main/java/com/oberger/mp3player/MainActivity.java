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

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PERMISSION_GRANTED) {
            List<TrackFileInfo> tracks = TrackFileInfoProvider.queryAlbumTracks(this, 0);
            Log.d(this.getClass().getSimpleName(), "Found '" + tracks.size() + "' tracks.");
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
                // TODO: (Important) Query stuff again and update view.
                List<ArtistFileInfo> artist = ArtistFileInfoProvider.queryAllArtists(this);
                Log.d(this.getClass().getSimpleName(), "Found '" + artist.size() + "' artists.");
            }
            else {
                Toast.makeText(getApplicationContext(), "Kein Zugriff auf Songs erlaubt.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
