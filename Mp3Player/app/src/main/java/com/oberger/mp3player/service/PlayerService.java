package com.oberger.mp3player.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.oberger.mp3player.MainActivity;
import com.oberger.mp3player.R;
import com.oberger.mp3player.TrackFileInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {

    private static final int SERVICE_ID = 1337;
    public static final String ACTION_START = "PlayerService.start";
    public static final String ACTION_STOP = "PlayerService.stop";
    public static final String PARAM_KEY_QUEUE = "PlayerService.queue";

    private Queue<TrackFileInfo> queue;
    private MediaPlayer mediaPlayer;

    public PlayerService() {
        queue = new LinkedList<>();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            if (intent.hasExtra(PARAM_KEY_QUEUE)) {
                // Parse the queue to play back.
                final ArrayList<TrackFileInfo> queue = intent.getParcelableArrayListExtra(PARAM_KEY_QUEUE);
                this.queue = new LinkedList<>(queue);
                // Start playback.
                if (playNextTrack()) {
                    // Start.
                    Log.d(this.getClass().getSimpleName(), "Received start intent. Starting self.");
                    startForeground(SERVICE_ID, buildNotification());
                }
                else {
                    Log.e(this.getClass().getSimpleName(), "Received empty queue for playback.");
                }
            } else {
                Log.e(this.getClass().getSimpleName(), "Received start intent does not have the expected parameter '" + PARAM_KEY_QUEUE + "'.");
            }
        }
        else if (ACTION_STOP.equals(ACTION_STOP)) {
            Log.d(this.getClass().getSimpleName(), "Received stop intent. Stopping self.");
            stopForeground(true);
            stopSelf();
        }
        else {
            Log.e(this.getClass().getSimpleName(), "Received unexpected intent with action '" + intent.getAction() + "'.");
        }
        return START_STICKY;
    }

    private Notification buildNotification() {
        // Show notification that launches the activity on click.
        final Intent launchActivityIntent = new Intent(this, MainActivity.class);
        launchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchActivityIntent, 0);
        final String appName = getString(R.string.app_name);
        final String trackName = queue.isEmpty() ? "" : queue.peek().getTitle();
        return new NotificationCompat.Builder(this)
                .setContentTitle(appName)
                .setTicker(appName)
                .setContentText(trackName)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private boolean playNextTrack() {
        if (queue.isEmpty()) {
            // No tracks left.
            return false;
        }
        final TrackFileInfo nextTrack = queue.poll();
        final String filePath = nextTrack.getFilePath();
        try {
            // Playback.
            if (mediaPlayer.isPlaying()) {
                Log.d(this.getClass().getSimpleName(), "Media Player was already playing. Resetting.");
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d(this.getClass().getSimpleName(), "Media Player started.");
            // Update notification.
            NotificationManagerCompat.from(this).notify(SERVICE_ID, buildNotification());
        } catch (final Exception e) {
            Log.e(this.getClass().getSimpleName(), "Could not playback track '" + filePath + "'. Skipping.");
        }
        return true;
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
}
