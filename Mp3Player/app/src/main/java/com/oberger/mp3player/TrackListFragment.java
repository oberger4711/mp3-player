package com.oberger.mp3player;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.oberger.mp3player.provider.TrackFileInfoProvider;

import java.util.ArrayList;
import java.util.List;


public class TrackListFragment extends Fragment {

    private static final TrackFileInfo DUMMY_TRACK = new TrackFileInfo("4711", "path", "title", "artist");

    private List<TrackFileInfo> allTracks;
    private final List<TrackFileInfo> currentPageTracks;
    private SingleTracksArrayAdapter currentPageTracksAdapter;
    private int indexFirstTrackOnPage;
    private int numberOfTracksPerPage;
    private final TrackLayoutListener trackLayoutListener;
    private Handler handler;

    // UI elements
    private ImageButton buttonPreviousPage;
    private ImageButton buttonNextPage;
    private ListView listViewTracks;
    private RelativeLayout containerPage;

    public TrackListFragment() {
        currentPageTracks = new ArrayList<>();
        currentPageTracks.add(DUMMY_TRACK); // Used for measuring.
        indexFirstTrackOnPage = 0;
        numberOfTracksPerPage = -1;
        trackLayoutListener = new TrackLayoutListener();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allTracks = TrackFileInfoProvider.queryAllTracks(getActivity());
        currentPageTracksAdapter = new SingleTracksArrayAdapter(getActivity(), currentPageTracks);
        handler = new Handler();
    }

    private int measureFirstListItemHeight() {
        assert(listViewTracks != null);
        assert(currentPageTracks.size() != 0);
        final View item = currentPageTracksAdapter.getView(0, null, listViewTracks);
        item.measure(0, 0);
        return item.getMeasuredHeight() + listViewTracks.getDividerHeight();
    }

    private int measurePage() {
        assert(containerPage != null);
        return containerPage.getHeight();
    }

    private boolean deriveNumberOfTracksPerPage() {
        // Derive number of tracks per page.
        final int itemHeight = measureFirstListItemHeight();
        final int listHeight = measurePage();
        final int newNumberOfTracksPerPage = Math.min(4, listHeight / itemHeight);
        boolean changed = newNumberOfTracksPerPage != numberOfTracksPerPage;
        numberOfTracksPerPage = newNumberOfTracksPerPage;
        if (changed) {
            Log.d(this.getClass().getSimpleName(), "item height: " + itemHeight + ", list height: " + listHeight);
            Log.d(this.getClass().getSimpleName(), "tracks per page: " + numberOfTracksPerPage);
        }
        return changed;
    }

    private void displayCurrentAlbum() {
        currentPageTracks.clear();
        final int end = Math.min(indexFirstTrackOnPage + numberOfTracksPerPage, allTracks.size());
        for (int i = indexFirstTrackOnPage; i < end; ++i) {
            currentPageTracks.add(allTracks.get(i));
        }
        currentPageTracksAdapter.notifyDataSetChanged();
        // Update buttons.
        final boolean hasPrevious = indexFirstTrackOnPage > 0;
        final boolean hasNext = indexFirstTrackOnPage + numberOfTracksPerPage < allTracks.size() - 1;
        buttonPreviousPage.setClickable(hasPrevious);
        buttonPreviousPage.setAlpha(hasPrevious ? 1.0f : 0.5f);
        buttonNextPage.setClickable(hasNext);
        buttonNextPage.setAlpha(hasNext ? 1.0f : 0.5f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        buttonPreviousPage = (ImageButton) view.findViewById(R.id.button_previous_page);
        buttonNextPage = (ImageButton) view.findViewById(R.id.button_next_page);
        listViewTracks = (ListView) view.findViewById(R.id.list_view_tracks);
        containerPage = (RelativeLayout) view.findViewById(R.id.layout_track_list_container);

        buttonPreviousPage.setClickable(false);
        buttonNextPage.setClickable(true);

        buttonPreviousPage.setOnClickListener(new PreviousPageClickListener());
        buttonNextPage.setOnClickListener(new NextPageClickListener());
        listViewTracks.setAdapter(currentPageTracksAdapter);
        listViewTracks.getViewTreeObserver().addOnGlobalLayoutListener(trackLayoutListener);

        return view;
    }

    private class TrackLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            if (deriveNumberOfTracksPerPage()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        displayCurrentAlbum();
                    }
                });
            }
        }
    }

    private class PreviousPageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            indexFirstTrackOnPage = Math.max(0, indexFirstTrackOnPage - numberOfTracksPerPage);
            displayCurrentAlbum();
        }
    }

    private class NextPageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            indexFirstTrackOnPage = Math.min(allTracks.size() - 1, indexFirstTrackOnPage + numberOfTracksPerPage);
            displayCurrentAlbum();
        }
    }
}
