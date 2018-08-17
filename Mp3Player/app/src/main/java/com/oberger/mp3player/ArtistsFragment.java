package com.oberger.mp3player;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ArtistsFragment extends Fragment {

    private OnArtistSelectedListener artistSelectedListener;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    public static ArtistsFragment newInstance() {
        ArtistsFragment fragment = new ArtistsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArtistSelectedListener) {
            artistSelectedListener = (OnArtistSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArtistSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        artistSelectedListener = null;
    }

    public interface OnArtistSelectedListener {
        // TODO: Update argument type and name
        void onArtistSelected(final int id);
    }
}
