package com.oberger.mp3player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oberger.mp3player.service.PlayerService;

public class PlayerFragment extends Fragment {

    enum EStatus {
        PLAYING,
        PAUSED
    }

    private final PlayerBroadcastReceiver broadcastReceiver;

    private ImageButton buttonPlayPause;
    private TextView labelPlayingTrack;
    private TextView labelPlayingArtist;

    public PlayerFragment() {
        broadcastReceiver = new PlayerBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_player, container, false);
        buttonPlayPause = (ImageButton) view.findViewById(R.id.button_play_pause);
        labelPlayingTrack = (TextView) view.findViewById(R.id.label_playing_track_name);
        labelPlayingArtist = (TextView) view.findViewById(R.id.label_playing_track_artist_name);

        buttonPlayPause.setOnClickListener(new PlayPauseClickListener());
        buttonPlayPause.setAlpha(0.5f);
        buttonPlayPause.setClickable(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, broadcastReceiver.buildIntentFilter());
        requestState(); // Synchronize displayed state with service.
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private void toggleStatus() {
        final Intent intent = new Intent(PlayerService.ACTION_TOGGLE_STATUS);
        getActivity().sendBroadcast(intent);
    }

    private void requestState() {
        final Intent intent = new Intent(PlayerService.ACTION_SEND_STATE);
        getActivity().sendBroadcast(intent);
    }

    private void displayState(final EStatus state, final TrackFileInfo currentTrackOrNull) {
        // Update play / pause button.
        if (currentTrackOrNull == null) {
            buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black));
            buttonPlayPause.setClickable(false);
            buttonPlayPause.setAlpha(0.5f);
        }
        else {
            if (EStatus.PAUSED.equals(state)) {
                buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black));
            } else if (EStatus.PLAYING.equals(state)) {
                buttonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black));
            }
            buttonPlayPause.setClickable(true);
            buttonPlayPause.setAlpha(1.f);
        }
        // Update track info.
        String currentTitle;
        String currentArtist;
        if (currentTrackOrNull != null) {
            currentTitle = currentTrackOrNull.getTitle();
            currentArtist = currentTrackOrNull.getArtist();
        }
        else {
            currentTitle = "";
            currentArtist = "";
        }
        labelPlayingTrack.setText(currentTitle);
        labelPlayingArtist.setText(currentArtist);
    }

    private class PlayPauseClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            toggleStatus();
        }
    }

    private class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PlayerService.ACTION_STATE.equals(intent.getAction())) {
                EStatus state = EStatus.PAUSED;
                final String stateString = intent.getStringExtra(PlayerService.PARAM_KEY_STATE_STATUS);
                if (PlayerService.STATUS_PAUSED.equals(stateString)) {
                    state = EStatus.PAUSED;
                }
                else if (PlayerService.STATUS_PLAYING.equals(stateString)) {
                    state = EStatus.PLAYING;
                }
                else {
                    Log.e(PlayerFragment.this.getClass().getSimpleName(), "Received player state intent with unknown status.");
                }
                final TrackFileInfo currentTrackOrNull = intent.getParcelableExtra(PlayerService.PARAM_KEY_STATE_CURRENT_TRACK);
                displayState(state, currentTrackOrNull);
            }
        }

        public IntentFilter buildIntentFilter() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PlayerService.ACTION_STATE);
            return intentFilter;
        }
    }
}
