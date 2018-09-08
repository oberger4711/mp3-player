package com.oberger.mp3player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SingleTracksArrayAdapter extends ArrayAdapter<TrackFileInfo> {

    private final Context context;
    private final List<TrackFileInfo> tracks;

    public SingleTracksArrayAdapter(final Context context, final List<TrackFileInfo> tracks) {
        super(context, R.layout.single_track_list_item, tracks);
        this.context = context;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final TrackFileInfo track = tracks.get(position);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.single_track_list_item, parent, false);

        final TextView labelTrackName = (TextView) view.findViewById(R.id.label_single_track_name);
        final TextView labelArtistName = (TextView) view.findViewById(R.id.label_single_track_artist_name);

        labelTrackName.setText(track.getTitle());
        labelArtistName.setText(track.getArtist());

        return view;
    }
}
