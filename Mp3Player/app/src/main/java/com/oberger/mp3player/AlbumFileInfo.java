package com.oberger.mp3player;

/**
 * Created by oberger on 4/14/18.
 */

public class AlbumFileInfo {

    private final int id;
    private final String name;
    private final String artistName;

    public AlbumFileInfo(final int id, final String name, final String artistName) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
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
}
