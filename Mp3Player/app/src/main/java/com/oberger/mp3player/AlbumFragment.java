package com.oberger.mp3player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oberger.mp3player.provider.ArtistFileInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {

    private final List<AlbumFileInfo> albums;
    private int indexCurrentAlbum;
    private QueueListener queueListener;

    // UI elements
    private TextView labelArtist;
    private TextView labelAlbum;
    private TextView labelNumSongs;
    private ImageView imageAlbumCover;
    private ImageButton buttonPreviousAlbum;
    private ImageButton buttonNextAlbum;

    public AlbumFragment() {
        albums = new ArrayList<>();
        indexCurrentAlbum = 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<ArtistFileInfo> artists = ArtistFileInfoProvider.queryAllArtists(getActivity());
        for (final ArtistFileInfo artist : artists) {
            albums.addAll(artist.getAlbums());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_album, container, false);
        labelArtist = (TextView) layout.findViewById(R.id.label_artist);
        labelAlbum = (TextView) layout.findViewById(R.id.label_album);
        labelNumSongs = (TextView) layout.findViewById(R.id.label_number_of_songs);
        imageAlbumCover = (ImageView) layout.findViewById(R.id.image_cover);
        buttonPreviousAlbum = (ImageButton) layout.findViewById(R.id.button_previous_album);
        buttonNextAlbum = (ImageButton) layout.findViewById(R.id.button_next_album);

        imageAlbumCover.setOnClickListener(new PlayAlbumClickListener());
        buttonPreviousAlbum.setOnClickListener(new PreviousAlbumClickListener());
        buttonNextAlbum.setOnClickListener(new NextAlbumClickListener());
        displayCurrentAlbum();
        return layout;
    }

    private void displayCurrentAlbum() {
        if (indexCurrentAlbum < albums.size()) {
            final AlbumFileInfo currentAlbum = albums.get(indexCurrentAlbum);
            labelArtist.setText(currentAlbum.getArtistName());
            labelAlbum.setText(currentAlbum.getName());
            final String numSongsText = getContext().getString(R.string.num_songs, currentAlbum.getTracks().size());
            labelNumSongs.setText(numSongsText);
        }
        final float disabledAlpha = 0.5f; // For graying out buttons.
        final boolean hasPrevious = indexCurrentAlbum > 0;
        final boolean hasNext = indexCurrentAlbum < albums.size();
        buttonPreviousAlbum.setClickable(hasPrevious);
        buttonPreviousAlbum.setAlpha(hasPrevious ? 1.0f : disabledAlpha);
        buttonNextAlbum.setClickable(hasNext);
        buttonNextAlbum.setAlpha(hasNext ? 1.0f : disabledAlpha);
        final Bitmap albumCoverOrNull = loadCurrentAlbumCoverOrNull();
        if (albumCoverOrNull != null) {
            imageAlbumCover.setImageDrawable(new BitmapDrawable(albumCoverOrNull));
        }
        else {
            imageAlbumCover.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_note_black));
        }
    }

    private Bitmap loadCurrentAlbumCoverOrNull() {
        final AlbumFileInfo currentAlbum = albums.get(indexCurrentAlbum);
        final TrackFileInfo firstSong = currentAlbum.getTracks().get(0);
        final MediaMetadataRetriever mm = new MediaMetadataRetriever();
        mm.setDataSource(firstSong.getFilePath());
        byte[] raw = mm.getEmbeddedPicture();
        Bitmap res = null;
        if (raw != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            res = BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
        }
        return res;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: Save currently viewed album id.
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QueueListener) {
            queueListener = (QueueListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAlbumSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        queueListener = null;
    }

    private class PlayAlbumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (queueListener != null) {
                if (indexCurrentAlbum < albums.size()) {
                    final AlbumFileInfo currentAlbum = albums.get(indexCurrentAlbum);
                    queueListener.changeQueue(currentAlbum.getTracks());
                }
            }
        }
    }

    private class PreviousAlbumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            indexCurrentAlbum--;
            displayCurrentAlbum();
        }
    }

    private class NextAlbumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            indexCurrentAlbum++;
            displayCurrentAlbum();
        }
    }

}
