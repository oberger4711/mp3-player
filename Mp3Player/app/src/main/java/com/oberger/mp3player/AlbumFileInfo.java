package com.oberger.mp3player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oberger on 4/14/18.
 */

public class AlbumFileInfo {

    private final int id;
    private final String name;
    private final String artistName;
    private final List<TrackFileInfo> tracks;

    public AlbumFileInfo(final int id, final String name, final String artistName, final List<TrackFileInfo> tracks) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.tracks = new ArrayList<>(tracks);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public List<TrackFileInfo> getTracks() {
        return Collections.unmodifiableList(tracks);
    }
}
