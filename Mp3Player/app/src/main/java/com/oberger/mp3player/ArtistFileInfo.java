package com.oberger.mp3player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oberger on 4/14/18.
 */

public class ArtistFileInfo {

    private final int id;
    private final String name;
    private final List<AlbumFileInfo> albums;

    public ArtistFileInfo(final int id, final String name, final List<AlbumFileInfo> albums) {
        this.id = id;
        this.name = name;
        this.albums = new ArrayList<>(albums);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<AlbumFileInfo> getAlbums() {
        return Collections.unmodifiableList(albums);
    }
}
