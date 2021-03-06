package com.oberger.mp3player;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.oberger.mp3player.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PlayerActivity extends AppCompatActivity implements QueueListener {

    public final static String PARAM_NAVIGATION_MODE = "NAV_MODE_ALBUMS";
    public final static String NAVIGATION_MODE_ALBUMS = "NAV_MODE_ALBUMS";
    public final static String NAVIGATION_MODE_SINGLE_TRACKS = "NAV_MODE_SINGLE_TRACKS";

    private final static int PERMISSION_REQUEST_CODE = 1337;

    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        // Check permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PERMISSION_GRANTED) {
            loadNavigationFragment();
        }
        else {
            // Show no permission until it is clear that the app has the permission.
            loadLoadingFragment();
            askPermission();
            // Music will be loaded on callback.
        }
        buttonBack = (Button) findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new BackClickListener());
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
                loadNavigationFragment();
            } else {
                loadNoPermissionFragment();
            }
        }
    }

    private void loadLoadingFragment() {
        final LoadingFragment loadingFragment = new LoadingFragment();
        loadFragmentNoBackStack(loadingFragment);
    }

    private void loadNoPermissionFragment() {
        final NoPermissionFragment noPermissionFragment = new NoPermissionFragment();
        loadFragmentNoBackStack(noPermissionFragment);
    }

    private void loadNavigationFragment() {
        // Permissions are assumed to be checked here.
        final String navMode = getIntent().getStringExtra(PARAM_NAVIGATION_MODE);
        if (NAVIGATION_MODE_ALBUMS.equals(navMode)) {
            final AlbumFragment albumFragment = new AlbumFragment();
            loadFragmentNoBackStack(albumFragment);
        }
        else if (NAVIGATION_MODE_SINGLE_TRACKS.equals(navMode)) {
            final TrackListFragment tracksFragment = new TrackListFragment();
            loadFragmentNoBackStack(tracksFragment);
        }
        else {
            Log.e(this.getClass().getSimpleName(), "Invalid navigation mode in activity intent '" + navMode + "'.");
            returnToMenu();
        }
    }

    private void loadFragmentNoBackStack(final Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.container_main_fragment) == null) {
            // Add.
            fragmentManager.beginTransaction().add(R.id.container_main_fragment, fragment).commitNowAllowingStateLoss();
        }
        else {
            // Replace.
            fragmentManager.beginTransaction().replace(R.id.container_main_fragment, fragment).commitNowAllowingStateLoss();
        }
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void changeQueue(final List<TrackFileInfo> queue) {
        final Intent startIntent = new Intent(this, PlayerService.class);
        startIntent.setAction(PlayerService.ACTION_START);
        startIntent.putExtra(PlayerService.PARAM_NAVIGATION_MODE, getIntent().getStringExtra(PARAM_NAVIGATION_MODE));
        final ArrayList<TrackFileInfo> queueArray = new ArrayList<>(queue);
        startIntent.putParcelableArrayListExtra(PlayerService.PARAM_KEY_QUEUE, queueArray);
        Log.d(this.getClass().getSimpleName(), "Passing queue to service.");
        startService(startIntent);
    }

    private void stopPlayer() {
        final Intent stopIntent = new Intent(this, PlayerService.class);
        stopIntent.setAction(PlayerService.ACTION_STOP);
        Log.d(this.getClass().getSimpleName(), "Stopping service.");
        startService(stopIntent);
    }

    private void returnToMenu() {
        stopPlayer();
        this.finish();
        final Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                returnToMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnToMenu();
    }

    private class BackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            returnToMenu();
        }
    }
}
