package com.oberger.mp3player.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.oberger.mp3player.PlayerActivity;
import com.oberger.mp3player.R;
import com.oberger.mp3player.TrackFileInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {

    private static final int SERVICE_ID = 1337;

    public static final String ACTION_START = "com.oberger.mp3player.service.PlayerService.start";
    public static final String ACTION_STOP = "com.oberger.mp3player.service.PlayerService.stop";
    public static final String ACTION_TOGGLE_STATUS = "com.oberger.mp3player.service.PlayerService.toggle";
    public static final String ACTION_SEND_STATE = "com.oberger.mp3player.service.PlayerService.sendState";
    public static final String PARAM_KEY_QUEUE = "com.oberger.mp3player.service.PlayerService.queue";
    public static final String PARAM_NAVIGATION_MODE = "com.oberger.mp3player.service.PlayerService.navigationMode";

    public static final String ACTION_STATE = "com.oberger.mp3player.service.PlayerService.state";
    public static final String PARAM_KEY_STATE_CURRENT_TRACK = "com.oberger.mp3player.service.PlayerService.currentTrack";
    public static final String PARAM_KEY_STATE_STATUS = "com.oberger.mp3player.service.PlayerService.status";

    public static final String STATUS_PLAYING = "PLAYING";
    public static final String STATUS_PAUSED = "PAUSED";

    private final PlayerBroadcastReceiver broadcastReceiver;
    private Queue<TrackFileInfo> queue;
    private String navigationMode;
    private TrackFileInfo currentTrack;
    private final MediaPlayer mediaPlayer;

    public PlayerService() {
        broadcastReceiver = new PlayerBroadcastReceiver();
        queue = new LinkedList<>();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            if (intent.hasExtra(PARAM_KEY_QUEUE)) {
                if (intent.hasExtra(PARAM_NAVIGATION_MODE)) {
                    navigationMode = intent.getStringExtra(PARAM_NAVIGATION_MODE);
                    // Parse the queue to play back.
                    final ArrayList<TrackFileInfo> queue = intent.getParcelableArrayListExtra(PARAM_KEY_QUEUE);
                    this.queue = new LinkedList<>(queue);
                    // Start playback.
                    if (playNextTrack()) {
                        Log.d(this.getClass().getSimpleName(), "Received start intent. Starting self.");
                        // Register intent receiver.
                        registerReceiver(broadcastReceiver, broadcastReceiver.buildIntentFilter());
                        // Start.
                        startForeground(SERVICE_ID, buildNotification());
                    } else {
                        Log.e(this.getClass().getSimpleName(), "Received empty queue for playback.");
                    }
                }
                else {
                    Log.e(this.getClass().getSimpleName(), "Received start intent does not have the expected parameter '" + PARAM_NAVIGATION_MODE + "'.");
                }
            } else {
                Log.e(this.getClass().getSimpleName(), "Received start intent does not have the expected parameter '" + PARAM_KEY_QUEUE + "'.");
            }
        }
        else if (ACTION_STOP.equals(ACTION_STOP)) {
            Log.d(this.getClass().getSimpleName(), "Received stop intent. Stopping self.");
            mediaPlayer.reset();
            stopForeground(true);
            stopSelf();
        }
        else {
            Log.e(this.getClass().getSimpleName(), "Received unexpected intent with action '" + intent.getAction() + "'.");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (final IllegalArgumentException e) {
        }
        super.onDestroy();
    }

    private Notification buildNotification() {
        // Touch intent.
        final Intent launchActivityIntent = new Intent(this, PlayerActivity.class);
        launchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.d(this.getClass().getSimpleName(), "Putting extra navigation mode '" + navigationMode + "'.");
        launchActivityIntent.putExtra(PlayerActivity.PARAM_NAVIGATION_MODE, navigationMode);
        final PendingIntent pendingLaunchActivityIntent = PendingIntent.getActivity(this, 1337331, launchActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        /*
        // Swipe intent.
        final Intent killServiceIntent = new Intent(this, PlayerService.class);
        killServiceIntent.setAction(ACTION_STOP);
        final PendingIntent pendingKillServiceIntent = PendingIntent.getService(this, 0, killServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        */
        // Information.
        final String appName = getString(R.string.app_name);
        final String trackName = currentTrack != null ? currentTrack.getTitle() : "";
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_music_note_black)
                .setContentTitle(appName)
                .setTicker(appName)
                .setContentText(trackName)
                .setContentIntent(pendingLaunchActivityIntent)
                .setOngoing(true)
                .build();
    }

    private boolean playNextTrack() {
        if (queue.isEmpty()) {
            // No tracks left.
            return false;
        }
        currentTrack = queue.poll();
        final String filePath = currentTrack.getFilePath();
        try {
            // Playback.
            if (mediaPlayer.isPlaying()) {
                Log.d(this.getClass().getSimpleName(), "Media Player was already playing. Resetting.");
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d(this.getClass().getSimpleName(), "Media Player started.");
            updateState();
        } catch (final Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "Could not playback track '" + filePath + "'. Skipping. " + e.getMessage());
            currentTrack = null;
            mediaPlayer.reset();
        }
        return true;
    }

    private void updateState() {
        updateNotification();
        sendState();
    }

    private void updateNotification() {
        NotificationManagerCompat.from(this).notify(SERVICE_ID, buildNotification());
    }

    private void sendState() {
        final Intent stateIntent = new Intent(ACTION_STATE);
        if (currentTrack != null) {
            stateIntent.putExtra(PARAM_KEY_STATE_CURRENT_TRACK, currentTrack);
            if (mediaPlayer.isPlaying()) {
                stateIntent.putExtra(PARAM_KEY_STATE_STATUS, STATUS_PLAYING);
            }
            else {
                stateIntent.putExtra(PARAM_KEY_STATE_STATUS, STATUS_PAUSED);
            }
        }
        else {
            stateIntent.putExtra(PARAM_KEY_STATE_STATUS, STATUS_PAUSED);
        }
        sendBroadcast(stateIntent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!playNextTrack()) {
            Log.d(this.getClass().getSimpleName(), "Played last track. Stopping self.");
            stopForeground(true);
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This is not a bound service.
        return null;
    }

    private class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SEND_STATE.equals(intent.getAction())) {
                sendState();
            }
            else if (ACTION_TOGGLE_STATUS.equals(intent.getAction())) {
                togglePlayPause();
            }
        }

        public IntentFilter buildIntentFilter() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SEND_STATE);
            intentFilter.addAction(ACTION_TOGGLE_STATUS);
            return intentFilter;
        }
    }

    private void togglePlayPause() {
        if (currentTrack != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
        else {
            Log.e(PlayerService.this.getClass().getSimpleName(), "Tried to toggle status without current track.");
        }
        updateState();
    }
}
