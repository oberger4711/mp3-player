package com.oberger.mp3player;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.oberger.mp3player.service.PlayerService;

public class MenuActivity extends AppCompatActivity {

    private Button buttonAllTracks;
    private Button buttonAllAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonAllTracks = (Button) findViewById(R.id.button_all_tracks);
        buttonAllAlbums = (Button) findViewById(R.id.button_all_albums);

        buttonAllTracks.setOnClickListener(new AllTracksClickListener());
        buttonAllAlbums.setOnClickListener(new AllAlbumsClickListener());
    }

    private class AllTracksClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO
        }
    }

    private class AllAlbumsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Intent i = new Intent(MenuActivity.this, PlayerActivity.class);
            startActivity(i);
        }
    }
}
