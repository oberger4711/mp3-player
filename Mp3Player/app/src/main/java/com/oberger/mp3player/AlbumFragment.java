package com.oberger.mp3player;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlbumFragment extends Fragment {

    private OnAlbumSelectedListener albumSelectedListener;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance() {
        AlbumFragment fragment = new AlbumFragment();
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
        if (context instanceof OnAlbumSelectedListener) {
            albumSelectedListener = (OnAlbumSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAlbumSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        albumSelectedListener = null;
    }

    public interface OnAlbumSelectedListener {
        // TODO: Update argument type and name
        void onAlbumSelected(final int id);
    }
}
